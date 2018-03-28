package com.example.otkyu.h30fenrir.asynchronous.api.model;

import com.example.otkyu.h30fenrir.asynchronous.api.secret.AccessKey;
import com.example.otkyu.h30fenrir.model.CheckModel;

import java.io.Serializable;

/**
 * Created by YukiOtake on 2018/01/25 025.
 * ぐるなびAPIを使用する際のパラメータを保管
 */

public class GnaviRequestEntity implements Serializable {
    private double[] gps;
    private String range, freeword, offsetPage, page,lunch;

    public GnaviRequestEntity() {
        gps = new double[2];//座標データ(0=lat,1=lon)
        range = null;//検索範囲
        freeword = null;//検索キーワード
        offsetPage = "1";//ページ数
        page = "20";//1ページに何項目表示するか
        lunch="0";//ランチ営業をしているか(1=している)
    }

    public double[] getGps() {
        return gps;
    }

    public void setGps(double[] gps) {
        this.gps = gps;
    }

    public void setRange(String range) {
        switch (range) {
            case "300m":
                range = "1";
                break;
            case "500m":
                range = "2";
                break;
            case "1km":
                range = "3";
                break;
            case "2km":
                range = "4";
                break;
            case "5km":
                range = "5";
                break;
            default:
                range = "2";
        }
        this.range = range;
    }

    public void setFreeword(String freeword) {
        this.freeword = freeword;
    }

    public void setOffsetPage(String offsetPage) {
        this.offsetPage = offsetPage;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getAPIUrl() {
        String url = "https://api.gnavi.co.jp/RestSearchAPI/20150630/";
        StringBuffer uri = new StringBuffer();
        uri.append(url);
        uri.append(getFormat());
        uri.append(getAccessKey());
        uri.append(getInputCoordinatesMode());
        uri.append(getLat());
        uri.append(getLon());
        uri.append(getRange());
        uri.append(getHitPerPage());
        uri.append(getOffsetPage());
        uri.append(getLunch());
        uri.append(getFreeword());
        return String.valueOf(uri);
    }

    private String getFormat() {  // 返却形式
        String format = "json";
        return "?format=" + format;
    }
    private String getAccessKey() {// アクセスキー
        AccessKey accessKey = new AccessKey();
        String acckey = accessKey.getKey();//please show "./Secret/readme.txt"
        return "&keyid=" + acckey;
    }

    private String getInputCoordinatesMode() {//入力測地系タイプを変更
        int mode = 2;
        return "&input_coordinates_mode=" + mode;
    }

    private String getLat() {//緯度
        String lat = String.valueOf(gps[0]);
        return "&latitude=" + lat;
    }

    private String getLon() {// 経度
        String lon = String.valueOf(gps[1]);
        return "&longitude=" + lon;
    }

    private String getRange() {// 範囲
        return "&range=" + range;
    }

    private String getHitPerPage() {//出力数
        String hitPerPage = page;
        return "&hit_per_page=" + hitPerPage;
    }

    private String getOffsetPage() {//ページ数
        return "&offset_page=" + offsetPage;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    private String getLunch(){//昼営業あるか
        return "&lunch="+lunch;
    }

    private String getFreeword() {//フリーワード検索
        CheckModel checkModel = new CheckModel();
        boolean freewordFlag = checkModel.isCheckNull(freeword);
        if (freewordFlag) {
            return "&freeword=" + freeword;
        }
        return "";
    }
}