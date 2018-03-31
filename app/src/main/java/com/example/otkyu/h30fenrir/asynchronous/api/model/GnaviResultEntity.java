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
    private String name, nameKana, address, tel, opentime, howGo, genre, homePage,holiday;
    private String[] img, storeOpen, storeClose;
    private boolean openTimeFlag,modeFlag;

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
        storeOpen = new String[5];//open時間(中休憩or土日)
        storeClose = new String[5];//close時間(中休憩or土日)
        openTimeFlag = false;//開店時間があるか(true=ある)
        modeFlag=false;//節約モードならtrue
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        CheckModel checkModel = new CheckModel();
        name = checkModel.checkString(name);
        this.name = name;
    }

    public String getNameKana() {
        return nameKana;
    }

    public void setNameKana(String nameKana) {
        CheckModel checkModel = new CheckModel();
        nameKana = checkModel.checkString(nameKana);
        this.nameKana = nameKana;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        CheckModel checkModel = new CheckModel();
        address = checkModel.checkString(address);
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
        CheckModel checkModel = new CheckModel();
        tel = checkModel.checkString(tel);
        this.tel = tel;
    }

    public String getOpentime() {
        return opentime;
    }

    public void setOpentime(String opentime) {
        CheckModel checkModel = new CheckModel();
        opentime = checkModel.checkString(opentime);//中身があるか確認
        if (!opentime.equals("登録されていません")) {
            setOpenTimeFlag(true);//ある場合はフラグを立てる
        } else {
            this.opentime=opentime;
            return;//登録されていなければ強制終了
        }
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
        howGo = checkModel.checkString(howGo);
        this.howGo = howGo;
    }

    public String[] getImg() {
        return img;
    }

    public void setImg(String[] img) {
        CheckModel checkModel = new CheckModel();
        String error = "登録されていません";
        for(int i=0;i<img.length;i++){
            img[i]=checkModel.checkString(img[i]);
            if (img[i].equals(error)){//もし画像が登録されていなければnullを代入
                img[i]=null;
            }
        }
        this.img = img;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        CheckModel checkModel = new CheckModel();
        genre = checkModel.checkString(genre);
        this.genre = genre;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        CheckModel checkModel = new CheckModel();
        homePage = checkModel.checkString(homePage);
        this.homePage = homePage;
    }

    private void setStoreTime() {//開店・閉店時間のセッター
        ChangeModel changeModel=new ChangeModel();
        String[] piyo=opentime.split("\n");//\nでsplit
        piyo=changeModel.doSubStringsFast("：",piyo);
        for(int i=0;i<piyo.length;i++){
            String[] hoge=piyo[i].split("～", 0);//～で開店時間の範囲を取得
            if (hoge.length>=2) {
                storeOpen[i] = hoge[hoge.length - 2];
                storeClose[i] = hoge[hoge.length - 1];
            }
        }
        storeClose=changeModel.doSubStringsLast("(",storeClose);
        String[] data={" ","：","(",")"};
        for(int i=0;i<data.length;i++){
            storeOpen=changeModel.doSubStringsFast(data[i],storeOpen);
            storeClose=changeModel.doSubStringsFast(data[i],storeClose);
        }
        Log.d("hoge",storeOpen[0]);
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
        holiday = checkModel.checkString(holiday);
        holiday = holiday.replace("<BR>", "\n");//<BR>を\nに置き換え
        holiday = holiday.replace("、", "\n");//、を\nに置き換え
        this.holiday = holiday;
    }

    public boolean isModeFlag() {
        return modeFlag;
    }

    public void setModeFlag(boolean modeFlag) {
        this.modeFlag = modeFlag;
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
            gnaviResultEntity.holiday=this.holiday;
            gnaviResultEntity.openTimeFlag = this.openTimeFlag;
            gnaviResultEntity.modeFlag=this.modeFlag;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return gnaviResultEntity;
    }
}