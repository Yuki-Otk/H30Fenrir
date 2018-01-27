package com.example.otkyu.h30fenrir;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.otkyu.h30fenrir.Model.GnaviAPI;
import com.example.otkyu.h30fenrir.Model.GnaviRequestEntity;
import com.example.otkyu.h30fenrir.Model.GnaviResultEntity;

import java.util.List;

/**
 * Created by YukiOtake on 2018/01/24 024.
 * src:https://akira-watson.com/android/activity-2.html
 * src:
 */

public class ShowListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_showlist);

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        Intent intent = getIntent();
//        GnaviRequestEntity gnaviRequestEntity = (GnaviRequestEntity) intent.getSerializableExtra("entity");
//        double[] gps = gnaviRequestEntity.getGps();
//        System.out.println("get data is" + gps[0] + ":" + gps[1]);
//        GnaviAPI gnaviAPI = new GnaviAPI(gps);
//        gnaviAPI.execute();

//        List<GnaviResultEntity> list = GnaviAPI.getList();
//        System.out.println("list size="+list.size());
        List<GnaviResultEntity> list = GnaviAPI.getList();
        System.out.println("list size="+list.size());
        for (int i=0;i<list.size();i++){
            System.out.println("name is="+list.get(i).getName());
        }


    }


}
