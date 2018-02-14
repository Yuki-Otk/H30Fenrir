package com.example.otkyu.h30fenrir.asynchronous.api.model;

/**
 * Created by YukiOtake on 2018/01/25 025.
 * ぐるなびAPIを利用した時の返り値を格納
 */

public class GnaviResultEntity {
    private String name = null, nameKana = null, address = null, tel = null, opentime = null, howGo = null, genre = null, homePage = null;
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
        String[] temp = address.split(" ");
        address = "";
        for (int i = 0; i < temp.length; i++) {
            address = address + "\n" + temp[i];
        }
//        int num=address.indexOf("町");
//        if(num==-1){
//            num=address.indexOf("市");
//        }
//        StringBuilder stringBuilder=new StringBuilder();
//        stringBuilder.append(address);
//        stringBuilder.insert(num+1,"\n");
//        this.address = new String(stringBuilder);
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
        this.howGo = howGo;
    }

    public String[] getImg() {
        return img;
    }

    public void setImg(String[] img) {
        String url1 = "https://developer.android.com/_static/0d76052693/images/android/touchicon-180.png?hl=ja";
        String url2 = "https://raw.githubusercontent.com/Yuki-Otk/H30Fenrir/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png";
//        String url="https://uds.gnst.jp/rest/img/c4bdzhxp0000/t_0000.jpg";
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
        this.genre = genre;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }
}