package com.kozlovsky.fix_contact;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import static com.kozlovsky.fix_contact.Model.FORMAT_NOTHING;
import static com.kozlovsky.fix_contact.Model.FORMAT_SPACE;
import static com.kozlovsky.fix_contact.Model.FORMAT_TIRE;

public class MainActivity extends AppCompatActivity {

    Button bt, bt1;
    EditText txtView,txtView1;
    TextView status;

    Model model;
    CheckBox ch;

    boolean delete=true;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("all");
        startActivityForResult(pickIntent, 666);*/



        bt = (Button) findViewById(R.id.button);
        bt1 = (Button) findViewById(R.id.button3);
        txtView = (EditText) findViewById(R.id.editText) ;
        txtView1 = (EditText) findViewById(R.id.editText3) ;
        status = (TextView) findViewById(R.id.textView4) ;
        status.setText("Для создания файла контактов нажмите 'Выбрать файл', выберите файл с контактами верного формата и нажмите 'Починить контакты'");

        //txtView1.setText(getExternalStorageDirectory().toString());



        RadioButton rN = (RadioButton)findViewById(R.id.radioButton);
        RadioButton rS = (RadioButton)findViewById(R.id.radioButton2);
        RadioButton rT = (RadioButton)findViewById(R.id.radioButton3);

        ch = (CheckBox)findViewById(R.id.checkBox2);

        ch.setOnClickListener(checkListener);
        rN.setOnClickListener(radioButtonClickListener);
        rS.setOnClickListener(radioButtonClickListener);
        rT.setOnClickListener(radioButtonClickListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, 100);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CALL_LOG}, 100);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }

        try{
            showFormat();


            }catch (Exception e)
        {
            txtView.setEnabled(false);
            System.out.println(e.getMessage());
        }

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                }

                Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickIntent.setType("*/*");
                startActivityForResult(pickIntent, 10);






            }
        });

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtView1.getText()!=null && txtView1.getText().toString().matches(".*vcf")) {


                    model = new Model(txtView1.getText().toString());

                    System.out.println("new model with name " + txtView1.getText().toString());

                    if (model.openAndrFile()){
                        model.LOG = false;
                        model.TARGET_FORM = myForm;
                        model.DELETE_DOUBLE = delete;

                        model.fixContactFile();

                        status.setText("Контакты записаны в " + model.getPATHandr);
                    }
                    else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Неправильный путь к файлу контактов .vcf",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                    }
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Введите путь к файлу контактов .vcf",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }
            }
        });


    }

    String myForm="FORMAT_NOTHING";

    View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RadioButton rb = (RadioButton)v;
            switch (rb.getId()) {
                case R.id.radioButton: myForm = "FORMAT_NOTHING";
                    break;
                case R.id.radioButton2: myForm = "FORMAT_SPACE";
                    break;
                case R.id.radioButton3: myForm = "FORMAT_TIRE";
                    break;


                default:
                    break;
            }
        }
    };

    View.OnClickListener checkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            delete = ch.isChecked();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // super.onActivityResult(requestCode, resultCode, data);

        if (data!=null) {
            Uri currFileURI = data.getData();
            String path = currFileURI.getPath();

            txtView1.setText(path.substring(txtView1.getText().toString().indexOf("primary:")+19));
        }

    }

    public void showFormat(){

        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,null, null, null);

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        while (managedCursor.moveToNext()) {
            String phNum = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);

            String CUR_FORMAT = (phNum.matches(FORMAT_TIRE))?" c тире":(phNum.matches(FORMAT_SPACE))?"с пробелами":(phNum.matches(FORMAT_NOTHING))?" cлитно":" невозможно определить";


            txtView.setText(phNum + CUR_FORMAT);

            if (phNum.length() > 8)
                break;
        }
    }
}
