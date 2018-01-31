package com.example.otkyu.h30fenrir;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.otkyu.h30fenrir.model.CasarealRecycleViewAdapter;
import com.example.otkyu.h30fenrir.model.GnaviAPI;
import com.example.otkyu.h30fenrir.model.GnaviRequestEntity;
import com.example.otkyu.h30fenrir.model.GnaviResultEntity;
import com.example.otkyu.h30fenrir.model.ImgAsyncTaskHttpRequest;

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

        List<GnaviResultEntity> list = GnaviAPI.getList();
        System.out.println("list size=" + list.size());
        makeList();
        checkButton();

        //next and back
        nextPageButton.setOnClickListener(new View.OnClickListener() {//次のページを表示
            @Override
            public void onClick(View v) {
                int newPage = GnaviAPI.getPageNum() + 1;
                redraw(newPage);
            }
        });
        backPageButton.setOnClickListener(new View.OnClickListener() {//前のページを表示
            @Override
            public void onClick(View v) {
                int newPage = GnaviAPI.getPageNum() - 1;
                redraw(newPage);
            }
        });
    }

    private void redraw(int newPage) {
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
//        System.out.println("total");
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
//                Toast.makeText(ShowListActivity.this, String.valueOf(list.get(view.getId()).getName()), Toast.LENGTH_SHORT).show();
                int index = view.getId();
//                System.out.println("index=" + index);
                Intent intent = new Intent(getApplication(), ShowDetailsActivity.class);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        });
        //img
//        imageView=(ImageView)findViewById(R.id.imageView);
    }

    @Override
    protected void onDestroy() {
//        imgAsyncTaskHttpRequest.setListener(null);//多分listenerがずっと生き続けるためもったいない？
        super.onDestroy();
    }
}
