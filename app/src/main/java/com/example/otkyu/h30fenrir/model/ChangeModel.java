package com.example.otkyu.h30fenrir.model;

/**
 * Created by YukiOtake on 2018/03/28 028.
 */

public class ChangeModel {
    public String[] doSubStringsFast(String find,String[] strings) {//文字列の後ろ側を抜き取り(基準,対象文字配列)
        for(int i=0;i<strings.length;i++) {
            try {
                int num = strings[i].indexOf(find);
                if (num != -1) {//基準点の文字列が検索できていなかった
                    strings[i] = strings[i].substring(num + 1, strings[i].length());//文字列後ろ側を抜き取り
                }
                strings[i] = strings[i].trim();//文字列前後の空白等の制御文字を捨てる
            }
            catch (Exception e){
                return strings;
            }
        }
        return strings;
    }
    public String[] doSubStringsLast(String find, String[] strings) {//文字列の前側を抜き取り(基準,対象文字配列)
        for(int i=0;i<strings.length;i++) {
            try {
            int num=strings[i].indexOf(find);
            if (num != -1) {//基準点の文字列が検索できていなかった
                strings[i] = strings[i].substring(0, num);//文字列前側を抜き取り
            }
            strings[i]=strings[i].trim();//文字列前後の空白等の制御文字を捨てる
            }catch (Exception e){
                return strings;
            }
        }
        return strings;
    }
}
