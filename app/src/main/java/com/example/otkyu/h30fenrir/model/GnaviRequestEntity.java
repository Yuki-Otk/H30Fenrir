package com.example.otkyu.h30fenrir.model;

import java.io.Serializable;

/**
 * Created by YukiOtake on 2018/01/25 025.
 * ぐるなびAPIを使用する際のパラメータを保管
 */

public class GnaviRequestEntity implements Serializable {
    private double[] gps;
    private String range, freeword;

    public GnaviRequestEntity() {
        gps = new double[2];
        range = null;
        freeword = null;
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
        if (range.equals("300m")) {
            range = "1";
        } else if (range.equals("500m")) {
            range = "2";
        } else if (range.equals("1km")) {
            range = "3";
        } else if (range.equals("2km")) {
            range = "4";
        } else if (range.equals("5km")) {
            range = "5";
        } else {
            range = "2";
        }
//        System.out.println("change range is "+range);
        this.range = range;
    }

    public String getFreeword() {
        return freeword;
    }

    public void setFreeword(String freeword) {
        this.freeword = freeword;
    }
}
