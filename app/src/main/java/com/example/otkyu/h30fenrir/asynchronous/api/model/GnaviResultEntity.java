package com.example.otkyu.h30fenrir.asynchronous.api.model;

import com.example.otkyu.h30fenrir.model.Check;

import java.io.Serializable;

/**
 * Created by YukiOtake on 2018/01/25 025.
 * ぐるなびAPIを利用した時の返り値を格納
 * Check.java をメソッドごとに読んでいるのはそうしないとアクティビティ間での受け渡しができなかったから:http://jazzguitar7.blog118.fc2.com/blog-entry-10.html
 * ディープコピーの参考:https://qiita.com/SUZUKI_Masaya/items/8da8c0038797f143f5d3
 */

public class GnaviResultEntity implements Serializable, Cloneable {//参照できるように,ディープコピーするために
    private String name, nameKana, address, tel, opentime, howGo, genre, homePage;
    private String[] img;
    private boolean openTimeFlag;

    public GnaviResultEntity() {
        name = null;
        nameKana = null;
        address = null;
        tel = null;
        opentime = null;
        howGo = null;
        genre = null;
        homePage = null;
        img = new String[2];
        openTimeFlag = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Check check = new Check();
        name = check.checkString(name);
        this.name = name;
    }

    public String getNameKana() {
        return nameKana;
    }

    public void setNameKana(String nameKana) {
        Check check = new Check();
        nameKana = check.checkString(nameKana);
        this.nameKana = nameKana;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        Check check = new Check();
        address = check.checkString(address);
        String[] temp = address.split(" ");
        for (int i = 0; i < temp.length; i++) {
            if (i == 0) {
                address = temp[i];
            } else {
                address = address + "\n" + temp[i];
            }
        }
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        Check check = new Check();
        tel = check.checkString(tel);
        this.tel = tel;
    }

    public String getOpentime() {
        return opentime;
    }

    public void setOpentime(String opentime) {
        Check check = new Check();
        opentime = check.checkString(opentime);
        if (!opentime.equals("登録されていません")) {
            setOpenTimeFlag(true);
        }
        String[] temp = opentime.split("<BR>", -1);
        String str = "";
        if (temp.length == 1) {
            this.opentime = opentime;
            return;
        }
        for (int i = 0; i < temp.length; i++) {
            str = str + "\n" + temp[i];
        }
        this.opentime = str;
    }

    public boolean isOpenTimeFlag() {
        return openTimeFlag;
    }

    private void setOpenTimeFlag(boolean openTimeFlag) {
        this.openTimeFlag = openTimeFlag;
    }

    public String getHowGo() {
        return howGo;
    }

    public void setHowGo(String howGo) {
        Check check = new Check();
        howGo = check.checkString(howGo);
        this.howGo = howGo;
    }

    public String[] getImg() {
        return img;
    }

    public void setImg(String[] img) {
        Check check = new Check();
        img[0] = check.checkString(img[0]);
        img[1] = check.checkString(img[1]);
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
        Check check = new Check();
        genre = check.checkString(genre);
        this.genre = genre;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        Check check = new Check();
        homePage = check.checkString(homePage);
        this.homePage = homePage;
    }

    @Override
    public GnaviResultEntity clone() {//ディープコピー
        GnaviResultEntity gnaviResultEntity = null;
        try {
            gnaviResultEntity = (GnaviResultEntity) super.clone();
            gnaviResultEntity.name = this.name;
            gnaviResultEntity.nameKana = this.nameKana;
            gnaviResultEntity.address = this.address;
            gnaviResultEntity.tel = this.tel;
            gnaviResultEntity.opentime = this.opentime;
            gnaviResultEntity.howGo = this.howGo;
            gnaviResultEntity.genre = this.genre;
            gnaviResultEntity.homePage = this.homePage;
            gnaviResultEntity.img = this.img;
            gnaviResultEntity.openTimeFlag = this.openTimeFlag;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return gnaviResultEntity;
    }
}