package com.example.otkyu.h30fenrir;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.otkyu.h30fenrir.view.CasarealRecycleViewAdapter;
import com.example.otkyu.h30fenrir.asynchronous.api.GnaviAPI;
import com.example.otkyu.h30fenrir.asynchronous.api.model.GnaviRequestEntity;
import com.example.otkyu.h30fenrir.asynchronous.api.model.GnaviResultEntity;
import com.example.otkyu.h30fenrir.asynchronous.img.ImgAsyncTaskHttpRequest;

import java.util.List;

/**
 * Created by YukiOtake on 2018/01/24 024.
 * src:https://akira-watson.com/android/activity-2.html
 * src:https://qiita.com/so-ma1221/items/d1b84bf764bf82fe1ac3
 * src:https://akira-watson.com/android/scrollview.html
 */

public class ShowListActivity extends AppCompatActivity {

    private Button backPageButton, nextPageButton;
    private GnaviRequestEntity gnaviRequestEntity;
    ImgAsyncTaskHttpRequest imgAsyncTaskHttpRequest;
    private CasarealRecycleViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_show);
        gnaviRequestEntity = (GnaviRequestEntity) getIntent().getSerializableExtra("gnaviRequestEntity");

        backPageButton = (Button) findViewById(R.id.backPage_button);
        nextPageButton = (Button) findViewById(R.id.nextPage_button);
        Button returnButton = (Button) findViewById(R.id.backHome_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.shop_linearLayout);

        List<GnaviResultEntity> list = GnaviAPI.getGnaviResultEntityList();
        System.out.println("list size=" + list.size());
        makeList();
        checkButton();

        //next and back
        nextPageButton.setOnClickListener(new View.OnClickListener() {//次のページを表示
            @Override
            public void onClick(View v) {
                int newPage = GnaviAPI.getPageNum() + 1;
                reload(newPage);
            }
        });
        backPageButton.setOnClickListener(new View.OnClickListener() {//前のページを表示
            @Override
            public void onClick(View v) {
                int newPage = GnaviAPI.getPageNum() - 1;
                reload(newPage);
            }
        });
    }

    private void reload(int newPage) {
        GnaviAPI gnaviAPI = new GnaviAPI();
        System.out.println("new page is " + newPage);
        gnaviRequestEntity.setOffsetPage(String.valueOf(newPage));
        gnaviAPI.setGnaviRequestEntity(gnaviRequestEntity);
        gnaviAPI.execute();
        while (true) {//api結果取得するまでweit
            if (GnaviAPI.isFinishFlag()) {
                break;
            }
        }
        makeList();
        checkButton();
    }

    private void checkButton() {
        int total = GnaviAPI.getTotalNum(), page = GnaviAPI.getPageNum(), requestNum = GnaviAPI.getRequestNum();
        //backPageButtonの有効化・無効化
        if (GnaviAPI.getPageNum() == 1) {
            backPageButton.setEnabled(false);
        } else {
            backPageButton.setEnabled(true);
        }
        //nextPageButtonの有効化・無効化
        double temp = Math.ceil((double) total / requestNum);
        int pageMax = (int) temp;
        System.out.println("pageMax=" + pageMax);
        if (page == pageMax) {
            nextPageButton.setEnabled(false);
        } else {
            nextPageButton.setEnabled(true);
        }
    }

    private void makeList() {
        int total = GnaviAPI.getTotalNum(), page = GnaviAPI.getPageNum(), dataNum = GnaviAPI.getDataNum(), requestNum = GnaviAPI.getRequestNum();
        double temp = Math.ceil((double) total / requestNum);
        int pageMax = (int) temp;
        String resultStr = "合計" + total + "件\t" + page + "/" + pageMax + "ページ目(" + (requestNum * page - requestNum + 1) + "～" + (requestNum * (page - 1) + dataNum) + "件表示)";
        //layout
        TextView resultTextView = (TextView) findViewById(R.id.result_textView);
        resultTextView.setText(resultStr);
        RecyclerView rv = (RecyclerView) findViewById(R.id.casareal_recyclerView);
        adapter = new CasarealRecycleViewAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = view.getId();
                Intent intent = new Intent(getApplication(), ShowDetailsActivity.class);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
