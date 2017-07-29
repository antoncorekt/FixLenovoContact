package com.kozlovsky;

/**
 * Created by Anton on 15.01.2017.
 */
public class Contact {

    private String name;
    private String last_name="";
    private String tel;
    private int begin, end;

    public Contact(int begin){
        this.begin = begin;
    }

    public Contact(){

    }


    public boolean isAvaibaleContact(){
        return (end!=0)&&(name!=null);
    }

    public boolean isContactInLines(int t){
        return (t>=begin)&&(t<=end);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAllName(){
        return name + " " + last_name;
    }

    @Override
    public String toString() {
        return "[" + begin+";"+end+"]   tel: " + tel + "   name: " + name;
    }
}
