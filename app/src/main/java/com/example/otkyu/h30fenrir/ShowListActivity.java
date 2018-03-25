package com.example.otkyu.h30fenrir;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private static final String REQUEST_KEY = "gnaviRequestEntity";
    ImgAsyncTaskHttpRequest imgAsyncTaskHttpRequest;
    private CasarealRecycleViewAdapter adapter;


    public static Intent createIntent(GnaviRequestEntity object, Application activity) {
        Intent intent = new Intent(activity, ShowListActivity.class);
        intent.putExtra(REQUEST_KEY, object);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_show);
        gnaviRequestEntity = (GnaviRequestEntity) getIntent().getSerializableExtra(REQUEST_KEY);
        //Sprinner(プルダウン)
        Spinner spinner = (Spinner) findViewById(R.id.time_spinner);
        doSelectSprinner(spinner);
        //下のボタン群
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
        //backButton
        //backButton
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void doSelectSprinner(Spinner spinner) {//sprinnerを変更したとき
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {//プルダウンで変更されたとき
                String select = (String) adapterView.getSelectedItem();
                Toast.makeText(ShowListActivity.this, select, Toast.LENGTH_SHORT).show();
                doSelectCheck(select);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {//選択されなかったとき

            }
        });
    }
    private void doSelectCheck(String select){//sprinnerで指定したものを分岐
        switch (select){
            case "さらに絞り込み":

                break;
            case "開店中":

                break;
            case "昼営業あり":
                reload("1");//昼営業有の結果に変更
                break;
            case "夜営業あり":
                break;
        }
    }
    private void reload(int newPage) {//再描画(ページ指定)//overload
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
    private void reload(String lunch) {//再描画(lunch)//overload
        GnaviAPI gnaviAPI = new GnaviAPI();
        gnaviRequestEntity.setLunch(lunch);//lunch時間のみ
        //TODO;営業時間でもAPIに登録されていないと蹴られてしまう
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

    private void checkButton() {//next/backButtonを有効無効にする
        //TODO;件数が0だと戻るボタンがアクティブになる
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
                startActivity(ShowDetailsActivity.createIntent(index,getApplication()));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
