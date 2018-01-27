package com.example.otkyu.h30fenrir.Model;

/**
 * Created by YukiOtake on 2018/01/25 025.
 * ぐるなびAPIを利用した時の返り値を格納
 */

public class GnaviResultEntity {
    private String name=null,nameKana=null,address=null,tel=null,opentime=null,howGo=null;
    private String[] img=new String[2];

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
        this.img = img;
    }
}
