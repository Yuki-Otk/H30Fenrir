package com.example.otkyu.h30fenrir.model;

/**
 * Created by YukiOtake on 2018/02/14 014.
 */

public class CheckModel {
    public String doCheckString(String str) {//APIに登録されているか判定
        try {
            int hoge = str.indexOf("{");
            int fuga = str.indexOf("}");
            if (hoge == 0 && fuga == 1) {//何も登録されていない
                str = "";
            }
        } catch (Exception e) {
            return str;
        }
        if (str.equals("") || str.equals("から分")) {
            return "登録されていません";
        }
        return str;
    }

    public boolean isCheckNull(String str) {//nullか判定
        if (str == null) {//nullならtrue
            return false;
        }
        return true;
    }

    public boolean isCheckNullArray(String[] strings) {//配列の中身がnullしかないかどうか
        boolean flag = false;
        for (int i = 0; i < strings.length; i++) {
            if (strings[i] != null) {//1つでもnullでなければtrue
                flag = true;
            }
        }
        return flag;
    }
}
