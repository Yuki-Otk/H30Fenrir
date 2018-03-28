package com.example.otkyu.h30fenrir.model;

/**
 * Created by YukiOtake on 2018/03/28 028.
 */

public class StringChange {
    public String doSubStringFast(int num, String str) {//文字列の後ろ側を抜き取り(基準点,対象文字列)
        if (num != -1) {//基準点の文字列が検索できていなかった
            str  = str.substring(num+1, str.length());//文字列後ろ側を抜き取り
        }
        return str.trim();//文字列前後の空白等の制御文字を捨てる
    }

    public String doSubStringLast(int num, String str) {//文字列の前側を抜き取り(基準点,対象文字列)
        if (num != -1) {//基準点の文字列が検索できていなかった
            str= str.substring(0, num);//文字列前側を抜き取り
        }
        return str.trim();//文字列前後の空白等の制御文字を捨てる
    }
}
