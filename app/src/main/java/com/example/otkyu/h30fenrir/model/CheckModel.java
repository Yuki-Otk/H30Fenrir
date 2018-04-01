package com.example.otkyu.h30fenrir.model;

/**
 * Created by YukiOtake on 2018/02/14 014.
 */

public class CheckModel {
    public String doCheckString(String str) {//APIに登録されているか判定
        try {
            int hoge = str.indexOf("{");
            int fuga = str.indexOf("}");
            if (hoge == 0 && fuga == 1) {
                str="";
            }
        } catch (Exception e) {
            return str;
        }
        if (str.equals("") || str.equals("から分")) {
            return "登録されていません";
        }
        return str;
    }

    public boolean isCheckInteger(String str) {//数字にできるか判定
        try {
            Integer hoge = Integer.valueOf(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCheckNull(String str) {
        if (str == null) {
            return false;
        }
        if (str.equals("")) {
            return false;
        }
        return true;
    }
    public boolean isCheckNullArray(String[] strings){
        boolean flag=false;
        for(int i=0;i<strings.length;i++){
            if (strings[i]!=null){
                flag=true;
            }
        }
        return flag;
    }

}
