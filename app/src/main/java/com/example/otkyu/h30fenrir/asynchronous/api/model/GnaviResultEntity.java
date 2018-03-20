package com.example.otkyu.h30fenrir.asynchronous.api.model;

import com.example.otkyu.h30fenrir.model.Check;

/**
 * Created by YukiOtake on 2018/01/25 025.
 * ぐるなびAPIを利用した時の返り値を格納
 */

public class GnaviResultEntity {
    private String name = null, nameKana = null, address = null, tel = null, opentime = null, howGo = null, genre = null, homePage = null;
    private String[] img = new String[2];
    private Check check=new Check();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name=check.checkString(name);
        this.name = name;
    }

    public String getNameKana() {
        return nameKana;
    }

    public void setNameKana(String nameKana) {
        nameKana=check.checkString(nameKana);
        this.nameKana = nameKana;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        address=check.checkString(address);
        String[] temp = address.split(" ");
        address = "";
        for (int i = 0; i < temp.length; i++) {
            address = address + "\n" + temp[i];
        }
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        tel=check.checkString(tel);
        this.tel = tel;
    }

    public String getOpentime() {
        return opentime;
    }

    public void setOpentime(String opentime) {
        opentime=check.checkString(opentime);
        String[] temp = opentime.split("<BR>");
        String str = "";
        for (int i = 0; i < temp.length; i++) {
            str = str + "\n" + temp[i];
        }
        this.opentime = str;
    }

    public String getHowGo() {
        return howGo;
    }

    public void setHowGo(String howGo) {
        howGo=check.checkString(howGo);
        this.howGo = howGo;
    }

    public String[] getImg() {
        return img;
    }

    public void setImg(String[] img) {
        img[0]=check.checkString(img[0]);
        img[1]=check.checkString(img[1]);
        String url1 = "https://developer.android.com/_static/0d76052693/images/android/touchicon-180.png?hl=ja";
        String url2 = "https://raw.githubusercontent.com/Yuki-Otk/H30Fenrir/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png";
        String error = "登録されていません";
        if (img[0].equals(error)) {
            img[0] = url1;
        }
        if (img[1].equals(error)) {
            img[1] = url2;
        }
        this.img = img;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        genre=check.checkString(genre);
        this.genre = genre;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        homePage=check.checkString(homePage);
        this.homePage = homePage;
    }
}