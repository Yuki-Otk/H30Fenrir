package com.example.otkyu.h30fenrir.model;

import java.io.Serializable;

/**
 * Created by YukiOtake on 2018/01/25 025.
 * ぐるなびAPIを使用する際のパラメータを保管
 */

public class GnaviRequestEntity implements Serializable {
    private double[] gps=new double[2];

    public double[] getGps() {
        return gps;
    }

    public void setGps(double[] gps) {
        this.gps = gps;
    }
}
