package com.example.otkyu.h30fenrir.Model;

import java.io.Serializable;

/**
 * Created by YukiOtake on 2018/01/25 025.
 */

public class GnaviEntity implements Serializable {
    private double[] gps=new double[2];

    public double[] getGps() {
        return gps;
    }

    public void setGps(double[] gps) {
        this.gps = gps;
    }
}
