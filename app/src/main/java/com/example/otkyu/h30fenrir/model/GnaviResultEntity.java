package com.example.otkyu.h30fenrir.model;

/**
 * Created by YukiOtake on 2018/01/25 025.
 * ぐるなびAPIを利用した時の返り値を格納
 */

public class GnaviResultEntity {
    private String name = null, nameKana = null, address = null, tel = null, opentime = null, howGo = null;
    private String[] img = new String[2];

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameKana() {
        return nameKana;
    }

    public void setNameKana(String nameKana) {
        this.nameKana = nameKana;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getOpentime() {
        return opentime;
    }

    public void setOpentime(String opentime) {
        this.opentime = opentime;
    }

    public String getHowGo() {
        return howGo;
    }

    public void setHowGo(String howGo) {
        this.howGo = howGo;
    }

    public String[] getImg() {
        return img;
    }

    public void setImg(String[] img) {
        String url = "https://developer.android.com/_static/0d76052693/images/android/touchicon-180.png?hl=ja";
//        String url="https://uds.gnst.jp/rest/img/c4bdzhxp0000/t_0000.jpg";
        String error = "登録されていません";
        if (img[0].equals(error)) {
            img[0] = url;
        }
        if (img[1].equals(error)) {
            img[1] = url;
        }
        this.img = img;
    }
}
