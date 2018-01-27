package com.example.otkyu.h30fenrir;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.otkyu.h30fenrir.model.CasarealRecycleViewAdapter;
import com.example.otkyu.h30fenrir.model.GnaviAPI;
import com.example.otkyu.h30fenrir.model.GnaviResultEntity;

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

        List<GnaviResultEntity> list = GnaviAPI.getList();
        System.out.println("list size=" + list.size());
        for (int i = 0; i < list.size(); i++) {
            System.out.println("name is=" + list.get(i).getName());
        }
        //layout
        RecyclerView rv = (RecyclerView) findViewById(R.id.casarealRecyclerView);
        CasarealRecycleViewAdapter adapter = new CasarealRecycleViewAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setHasFixedSize(true);

        rv.setLayoutManager(llm);

        rv.setAdapter(adapter);


    }


}
