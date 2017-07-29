package com.kozlovsky.fix_contact;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Anton on 14.01.2017.
 */
public class Model {
    public boolean LOG=false;
    public boolean DELETE_DOUBLE =true;

    public static String COUNTRY_FORMAT = "\\+.*";
    public static String UKR_FORMAT = "\\+38.*";
    public static String FORMAT_SPACE = "\\+?.* .*";
    public static String FORMAT_NOTHING = "\\+?\\d{10,}";
    public static String FORMAT_TIRE = "\\+?.*-.*";

    public ArrayList<Integer> symbol_enter_pos_country = new ArrayList<Integer>(){{add(4);add(7);add(11);}};
    public ArrayList<Integer> symbol_enter_pos_not_country = new ArrayList<Integer>(){{add(3);add(7);}};

    public  String TARGET_FORM = "FORMAT_TIRE";


    private String file;
    private ArrayList<String> lines;

    public Model(String file){
        this.file = file;
    }

    public Model(){
        this.file = "def";
    }

    public void fixContactFile(){

        if (openAndrFile()){
            changeLines(true);
            if (DELETE_DOUBLE) {
                deleteDublContact();

                ContactManadger cont = new ContactManadger(lines);

                lines = cont.getNewLines();
            }
            writeAndrFile();
        }
    }

    private String getFixedNumber(String TARGET_FORMAT, String num){
        String res="";

        String CUR_FORMAT = (num.matches(FORMAT_TIRE))?FORMAT_TIRE:(num.matches(FORMAT_SPACE))?FORMAT_SPACE:(num.matches(FORMAT_NOTHING))?FORMAT_NOTHING:"IGNOR";

        if (CUR_FORMAT.equals(TARGET_FORMAT) ){
            return num;
        }

        if (CUR_FORMAT.equals("IGNOR") || num.length()<8){
            if (LOG){
                System.out.println("incorrect format in num [ignored this number] -> " + num);
            }
            return num;
        }

        res = deleteSymbol(num," ");
        res = deleteSymbol(res,"-");
        res = addUkraineCountyFormat(res);



        String sym;
        if (TARGET_FORMAT.equals("FORMAT_TIRE"))
            sym = "-";
        else
            sym = " ";

        if (TARGET_FORMAT.equals("FORMAT_TIRE") || TARGET_FORMAT.equals("FORMAT_SPACE")){    //+380 96 925 0697
            if (res.matches(COUNTRY_FORMAT) && res.matches(UKR_FORMAT)){
                for (Integer i: symbol_enter_pos_country) {
                    if (res.length()>i)
                        res = res.substring(0,i)+sym+res.substring(i,res.length());
                    else
                        return res+"|";
                }
                return res;
            }
            else {
                for (Integer i: symbol_enter_pos_not_country) {
                    if (res.length()>i && res.matches(UKR_FORMAT))
                        res = res.substring(0,i)+sym+res.substring(i-1,res.length()-1);
                    else
                        return res;
                }
                return res;
            }
        }

        return res;
    }

    private String deleteSymbol(String num, String sym){
        return num.replaceAll(sym,"");
    }

    private String addUkraineCountyFormat(String num){
        if (num.matches(COUNTRY_FORMAT)){
            return num;
        }
        else {
            if (!num.matches("0(99||95||93||67||68||50||63||93||98||97||96).*")){
                if (LOG) System.out.println(num + " is not Ukraine format, ignored add +38");
                return num;
            }
            else
                return "+38"+num;
        }
    }

    public boolean openAndrFile(){
        try {

            File f = new File(Environment.getExternalStorageDirectory(), file);//changes here
            if(f.exists()) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));

            StringBuilder out = new StringBuilder();
                ArrayList<String> lines = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            this.lines = lines;
            return true;
            }
        }catch (Exception e){
            System.out.printf("ERROR " + e.getMessage());

            return false;
        }
        return false;
    }

    public boolean openFile(){
        try {


            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            ArrayList<String> lines = new ArrayList<String>();
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            this.lines = lines;



            return true;
        }catch (Exception e){
            System.out.printf("ERROR " + e.getMessage());

            return false;
        }
    }

    public String getPATHandr;

    private void writeAndrFile(){
        try {
            String filePath = "/";

            if (file.matches(".*/.*")) {
                filePath += file.substring(0, file.lastIndexOf('/')+1);
                file = file.substring( file.lastIndexOf('/')+1);
            }

            File f = new File(Environment.getExternalStorageDirectory(), filePath+"res"+file);//changes here

            getPATHandr = Environment.getExternalStorageDirectory()+filePath+"res"+file;

                FileWriter fw = new FileWriter(f);
                for (String line : lines) {


                        fw.append(line);
                        fw.append("\n");

                }

                fw.flush();
                fw.close();

        }
        catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    private void writeFile(){
        try {
            FileWriter fw=new FileWriter("res-"+TARGET_FORM+"-"+file);
            for (String line: lines){
                //if(!line.equals("")){
                    fw.append(line);
                    fw.append("\n");
                //}
            }

            fw.flush();
            fw.close();
        }
        catch (Exception ex) {
            System.out.println(ex.toString()); //чтобы хоть что-то знать о возможной ошибке
        }
    }

    public void showLines(ArrayList<String> lines){
        for (String s: lines){
            System.out.println(s);
        }
    }

    private ArrayList<Integer> delLines;

    private void deleteDublContact(){
        try {
            for (int i = 0; i < delLines.size(); i++) {
                lines.set((int) delLines.get(i),"");
            }
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    private void changeLines(boolean mode){
        ArrayList<String> unicLines = new ArrayList<>();
        delLines = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String reg = "(TEL;(CELL:|HOME:|WORK:))(.+)";
            Pattern p = Pattern.compile(reg);
            Matcher m;
            if (lines.get(i).matches(reg)){
                m = p.matcher(lines.get(i));
                if (m.find())
                    if (LOG) System.out.println(getFixedNumber(TARGET_FORM,m.group(3)));

                String fixedNum = getFixedNumber(TARGET_FORM,m.group(3));

                if (!unicLines.contains(fixedNum)) {
                    unicLines.add(fixedNum);
                    if (LOG) System.out.println("add in unic list -> " + fixedNum);
                }
                else {
                    delLines.add(i);
                    if (LOG) System.out.println("delete unic index -> " + i);
                }

                lines.set(i,m.group(1)+fixedNum);
            }
        }
    }


    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}

