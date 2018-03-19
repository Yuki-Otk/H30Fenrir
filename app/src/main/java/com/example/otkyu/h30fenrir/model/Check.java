package com.example.otkyu.h30fenrir.model;

/**
 * Created by YukiOtake on 2018/02/14 014.
 */

public class Check {
    public boolean isCheckNull(String str) {
        if (str == null) {
            return false;
        }
        if (str.equals("")) {
            return false;
        }
        return true;
    }
    public boolean isCheckInteger(String str) {//数字にできるか判定
        try {
            Integer hoge = Integer.valueOf(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
