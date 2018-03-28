package com.example.otkyu.h30fenrir.asynchronous.api.model;

import android.util.Log;

import com.example.otkyu.h30fenrir.model.Check;
import com.example.otkyu.h30fenrir.model.StringChange;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by YukiOtake on 2018/01/25 025.
 * ぐるなびAPIを利用した時の返り値を格納
 * Check.java をメソッドごとに読んでいるのはそうしないとアクティビティ間での受け渡しができなかったから:http://jazzguitar7.blog118.fc2.com/blog-entry-10.html
 * ディープコピーの参考:https://qiita.com/SUZUKI_Masaya/items/8da8c0038797f143f5d3
 */

public class GnaviResultEntity implements Serializable, Cloneable {//参照できるように,ディープコピーするために
    private String name, nameKana, address, tel, opentime, howGo, genre, homePage;
    private String[] img, storeOpen, storeClose;
    private boolean openTimeFlag;

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
        storeOpen = new String[2];//open時間(中休憩or土日)
        storeClose = new String[2];//close時間(中休憩or土日)
        openTimeFlag = false;//開店時間があるか(true=ある)
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
        opentime = check.checkString(opentime);//中身があるか確認
        if (!opentime.equals("登録されていません")) {
            setOpenTimeFlag(true);//ある場合はフラグを立てる
        } else {
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

    private void setStoreTime() {//開店・閉店時間のセッター
        String[] hoge = opentime.split("～", 0);//～で開店時間の範囲を取得
        String[] fuga = null;
        boolean two = false;//開店情報が2つあるか
        if (hoge.length >= 3) {//中休み情報有り
            two = true;//開店情報が2つある
            fuga = hoge[hoge.length - 2].split("\n", 0);
        }
        if (two) {//中休みor土日情報あり(開店情報が2つある)
            storeOpen[0] = doSubStringSetting(hoge[0], true);//開店時間
            storeClose[0] = doSubStringSetting(fuga[0], false);//閉店時間
            storeOpen[1] = doSubStringSetting(fuga[fuga.length - 1], true);//開店時間
            storeClose[1] = doSubStringSetting(hoge[hoge.length - 1], false);//閉店時間
        } else {//中休み情報無し
            storeOpen[0] = doSubStringSetting(hoge[0], true);
            storeClose[0] = doSubStringSetting(hoge[1], false);//閉店時間
        }
    }

    private String doSubStringSetting(String str, boolean fast) {//時間の形式に合わせてごみを取り除く(抜き取り対象文字列,開店時間=true:閉店時間=false)
        StringChange stringChange = new StringChange();//クラス呼び出し
        if (fast) {//開店時間が対象ならば
            str = stringChange.doSubStringFast(str.indexOf(")"), str);//対象文字列から")"以上切りすて(後ろ側が残る)
        } else {//閉店時間が対象ならば
            str = stringChange.doSubStringLast(str.indexOf("("), str);//対象文字列から"("以下切りすて(前側が残る)
        }
        str = stringChange.doSubStringFast(str.indexOf(" "), str);//対象文字列からスペース以上を切りすて(後ろ側が残る)
        str = stringChange.doSubStringFast(str.indexOf("："), str);//対象文字列から：(全角)以上を切りすて(後ろ側が残る)
        return str;//成形された時間を返す
    }

    public String[] getStoreOpen() {
        return storeOpen;
    }

    public String[] getStoreClose() {
        return storeClose;
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
            gnaviResultEntity.openTimeFlag = this.openTimeFlag;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return gnaviResultEntity;
    }
}