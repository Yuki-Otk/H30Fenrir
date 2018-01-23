package com.example.otkyu.h30fenrir;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.otkyu.h30fenrir.Model.GnaviAPI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GnaviAPI gnaviAPI=new GnaviAPI();
//        System.out.println("in!!");
        gnaviAPI.execute(null,null,null);//GPSデータでも入れる
//        System.out.println("out!!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
