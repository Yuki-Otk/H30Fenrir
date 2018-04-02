package com.example.otkyu.h30fenrir.asynchronous.api.model;

import android.util.Log;

import com.example.otkyu.h30fenrir.model.CheckModel;
import com.example.otkyu.h30fenrir.model.ChangeModel;

import java.io.Serializable;

/**
 * Created by YukiOtake on 2018/01/25 025.
 * ぐるなびAPIを利用した時の返り値を格納
 * CheckModel.java をメソッドごとに読んでいるのはそうしないとアクティビティ間での受け渡しができなかったから:http://jazzguitar7.blog118.fc2.com/blog-entry-10.html
 * ディープコピーの参考:https://qiita.com/SUZUKI_Masaya/items/8da8c0038797f143f5d3
 */

public class GnaviResultEntity implements Serializable, Cloneable {//参照できるように,ディープコピーするために
    private String name, nameKana, address, tel, opentime, howGo, genre, homePage, holiday, pr;
    private String[] img, storeOpen, storeClose;
    private boolean openTimeFlag, modeFlag;

    public GnaviResultEntity() {//コンストラクタ
        name = null;//店名
        nameKana = null;//テンメイ
        address = null;//住所
        tel = null;//電話番号
        opentime = null;//営業時間
        howGo = null;//行き方
        genre = null;//ジャンル
        homePage = null;//ぐるなびのサイトURL
        img = new String[2];//詳細画像2枚
        openTimeFlag = false;//開店時間があるか(true=ある)
        modeFlag = false;//節約モードならtrue
        pr = null;//紹介文
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        CheckModel checkModel = new CheckModel();
        name = checkModel.doCheckString(name);
        this.name = name;
    }

    public String getNameKana() {
        return nameKana;
    }

    public void setNameKana(String nameKana) {
        CheckModel checkModel = new CheckModel();
        nameKana = checkModel.doCheckString(nameKana);
        this.nameKana = nameKana;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        CheckModel checkModel = new CheckModel();
        address = checkModel.doCheckString(address);
        String[] temp = address.split(" ");//空白に改行を入れる
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
        CheckModel checkModel = new CheckModel();
        tel = checkModel.doCheckString(tel);
        this.tel = tel;
    }

    public String getOpentime() {
        return opentime;
    }

    public void setOpentime(String opentime) {
        CheckModel checkModel = new CheckModel();
        opentime = checkModel.doCheckString(opentime);//中身があるか確認
        if (opentime.equals("登録されていません")) {
            setOpenTimeFlag(false);//ない場合はフラグをしまう
            this.opentime = opentime;
            return;//登録されていなければ強制終了
        }
        setOpenTimeFlag(true);//ある場合はフラグを立てる
        opentime = opentime.replace("<BR>", "\n");//<BR>を\nに置き換え
        opentime = opentime.replace("、", "\n");//、を\nに置き換え
        this.opentime = opentime;
        setStoreTime();//開店時間と閉店時間のセット
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
        CheckModel checkModel = new CheckModel();
        howGo = checkModel.doCheckString(howGo);
        this.howGo = howGo;
    }

    public String[] getImg() {
        return img;
    }

    public void setImg(String[] img) {
        CheckModel checkModel = new CheckModel();
        String error = "登録されていません";
        for (int i = 0; i < img.length; i++) {
            img[i] = checkModel.doCheckString(img[i]);
            if (img[i].equals(error)) {//もし画像が登録されていなければnullを代入
                img[i] = null;
            }
        }
        this.img = img;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        CheckModel checkModel = new CheckModel();
        genre = checkModel.doCheckString(genre);
        this.genre = genre;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        CheckModel checkModel = new CheckModel();
        homePage = checkModel.doCheckString(homePage);
        this.homePage = homePage;
    }

    private void setStoreTime() {//開店・閉店時間のセッター
        ChangeModel changeModel = new ChangeModel();
        CheckModel checkModel = new CheckModel();
        String[] piyo = opentime.split("\n");//\nでsplit
        piyo = changeModel.doSubStringsFast("：", piyo);//全角セミコロンがある場合はそれより前を捨てる(配列の中身すべてで)
        storeOpen = new String[piyo.length];//open時間(改行の数だけ配列を宣言)
        storeClose = new String[piyo.length];//close時間(改行の数だけ配列を宣言)
        for (int i = 0; i < piyo.length; i++) {//改行の数だけループ
            String[] hoge = piyo[i].split("～", 0);//～で開店時間の範囲を取得
            if (hoge.length >= 2) {//前と後ろの2つはないと時間の情報が格納されていないと判定
                storeOpen[i] = hoge[hoge.length - 2];//後ろからのほうが精度が高いため
                storeClose[i] = hoge[hoge.length - 1];
            }
        }
        if (!checkModel.isCheckNullArray(storeClose) && !checkModel.isCheckNullArray(storeOpen)) {//storeOpen/Closeどちらも中身がnullならば
            return;
        }
        storeClose = changeModel.doSubStringsLast("(", storeClose);//(よりも後ろ側を捨てる
        String[] data = {" ", "：", "(", ")"};//ごみであるこのことの多い文字
        for (int i = 0; i < data.length; i++) {
            storeOpen = changeModel.doSubStringsFast(data[i], storeOpen);//data[i]よりも前を捨てる
            storeClose = changeModel.doSubStringsFast(data[i], storeClose);
        }
    }

    public String[] getStoreOpen() {
        return storeOpen;
    }

    public String[] getStoreClose() {
        return storeClose;
    }

    public String getHoliday() {
        return holiday;
    }

    public void setHoliday(String holiday) {
        CheckModel checkModel = new CheckModel();
        holiday = checkModel.doCheckString(holiday);
        holiday = holiday.replace("<BR>", "\n");//<BR>を\nに置き換え
        holiday = holiday.replace("、", "\n");//、を\nに置き換え
        this.holiday = holiday;
    }

    public void setPr(String pr) {
        CheckModel checkModel = new CheckModel();
        pr = checkModel.doCheckString(pr);
        pr = pr.replace("<BR>", "\n");//<BR>を\nに置き換え
        pr = pr.replace("、", "\n");//、を\nに置き換え
        this.pr = pr;
    }

    public boolean isModeFlag() {
        return modeFlag;
    }

    public void setModeFlag(boolean modeFlag) {
        this.modeFlag = modeFlag;
    }

    public String getPr() {
        return pr;
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
            gnaviResultEntity.storeOpen = this.storeOpen;
            gnaviResultEntity.storeClose = this.storeClose;
            gnaviResultEntity.holiday = this.holiday;
            gnaviResultEntity.openTimeFlag = this.openTimeFlag;
            gnaviResultEntity.modeFlag = this.modeFlag;
            gnaviResultEntity.pr = this.pr;
        } catch (CloneNotSupportedException e) {
            Log.d("error", String.valueOf(e));
        }
        return gnaviResultEntity;
    }
}