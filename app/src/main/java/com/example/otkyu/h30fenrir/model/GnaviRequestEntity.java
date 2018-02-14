package com.example.otkyu.h30fenrir.asynchronous.api.model;

import java.io.Serializable;

/**
 * Created by YukiOtake on 2018/01/25 025.
 * ぐるなびAPIを使用する際のパラメータを保管
 */

public class GnaviRequestEntity implements Serializable {
    private double[] gps;
    private String range, freeword, offsetPage, page;

    public GnaviRequestEntity() {
        gps = new double[2];
        range = null;
        freeword = null;
        offsetPage = "1";
        page = "20";
    }

    public double[] getGps() {
        return gps;
    }

    public void setGps(double[] gps) {
        this.gps = gps;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
//        System.out.println("get range is "+range);
        switch (range){
            case "300m":
                range="1";
                break;
            case "500m":
                range="2";
                break;
            case "1km":
                range="3";
                break;
            case "2km":
                range="4";
                break;
            case "5km":
                range="5";
                break;
            default:
                range="2";
        }
        this.range = range;
    }

    public String getFreeword() {
        return freeword;
    }

    public void setFreeword(String freeword) {
        this.freeword = freeword;
    }

    public String getOffsetPage() {
        return offsetPage;
    }

    public void setOffsetPage(String offsetPage) {
        this.offsetPage = offsetPage;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
}