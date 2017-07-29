package com.kozlovsky.fix_contact;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

;

/**
 * Created by Anton on 18.01.2017.
 */
public class ContactManadger implements Iterable <com.kozlovsky.Contact>, Iterator<com.kozlovsky.Contact> {

    private ArrayList<com.kozlovsky.Contact> contacts;
    private ArrayList<String > lines;

    public ContactManadger(ArrayList<com.kozlovsky.Contact> contacts, int empty){
        this.contacts = contacts;
    }

    public ContactManadger(ArrayList<String> lines){
        this.lines = lines;
        this.contacts = linesToContact(lines);

        System.out.println("-------- create contact manadger --------");
        System.out.println("lines: " + lines.size() + " contacts: " + contacts.size());
        System.out.println("-----------------------------------------");

        deleteContactWhithEmptyNum();
        //deleteContactWhithEmptyNum();
       // deleteContactWhithEmptyNum();
       // deleteContactWhithEmptyNum();
    }

    public ArrayList<com.kozlovsky.Contact> linesToContact(ArrayList<String> lines) {
        ArrayList<com.kozlovsky.Contact> res = new ArrayList<>();

        String tel_reg = "(TEL;(CELL:|HOME:|WORK:))(.+)";
        String name_reg = "N;.*(CHARSET=)(.*;).*:(.*)";

        Pattern pat_tel = Pattern.compile(tel_reg);
        Pattern pat_name = Pattern.compile(name_reg);
        Matcher m;
        int i=0;
        while( i < lines.size()) {
            if (lines.get(i).matches("BEGIN.*")) {

                com.kozlovsky.Contact t = new com.kozlovsky.Contact(i);

                int j=i;
                while (!lines.get(i).matches("END.*")){

                    if (lines.get(i).matches(tel_reg)){
                        m = pat_tel.matcher(lines.get(i));

                        if (m.find())
                            t.setTel(m.group(3));

                    }

                    if (lines.get(i).matches(name_reg) ){
                        m = pat_name.matcher(lines.get(i));

                        if (m.find())
                        {
                            String s = m.group(3);
                            t.setName( m.group(3));
                            /*s=s.replaceAll("=","");
                            // s=s.replaceFirst(";",",");

                            s=s.replaceAll(";","");

                            // s=s.replaceAll(",",";");
                            // System.out.println(s);

                            byte[] bytes ;

                            try {
                                bytes = DatatypeConverter.parseHexBinary(s);

                                try {
                                    s = new String(bytes, "UTF-8");
                                } catch (Exception e) {
                                    System.out.println(e);
                                }
                                t.setName(s);
                            }
                            catch (Exception e){

                            }*/
                        }
                    }

                    i++;
                    j++;
                }
                t.setEnd(j);

                res.add(t);
            }
            i++;
        }


        return res;
    }

    public void deleteContactWhithEmptyNum(){
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getTel()==null || contacts.get(i).getTel().equals("")){
                System.out.println("remoove");

                for (int j = contacts.get(i).getBegin(); j <= contacts.get(i).getEnd(); j++) {
                    lines.set(j,"");
                }

                contacts.remove(i);
            }
        }
    }


    public ArrayList<com.kozlovsky.Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<com.kozlovsky.Contact> contacts) {
        this.contacts = contacts;
    }

    private int count = 0;

    @Override
    public Iterator iterator() {
        return this;
    }


    @Override
    public boolean hasNext() {
        return count < contacts.size();
    }

    @Override
    public com.kozlovsky.Contact next() {
        if (this.hasNext())
            return contacts.get(count++);
        else
            throw new NoSuchElementException();
    }

    @Override
    public void remove() {

    }

    public static String decompose(String s) {
        return java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+","");
    }

    public ArrayList<String> getNewLines() {return lines;}
}
