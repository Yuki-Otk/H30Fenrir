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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private CasarealRecycleViewAdapter adapter;
    private final List<GnaviResultEntity> listAPICopy = GnaviAPI.getGnaviResultEntityList();//検索結果のコピー(変更不可)
    private List<GnaviResultEntity> listAPI = new ArrayList<>();//検索結果
    private boolean sprinnerFlag = false;

    public static Intent createIntent(GnaviRequestEntity object, Application activity) {//画面遷移の取得
        Intent intent = new Intent(activity, ShowListActivity.class);
        intent.putExtra(REQUEST_KEY, object);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_show);
        gnaviRequestEntity = (GnaviRequestEntity) getIntent().getSerializableExtra(REQUEST_KEY);//引き数取得
        readListAPI();//検索結果

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
        makeList();//list表示
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

    private void readListAPI() {//検索結果のListをディープコピーする
        listAPI.clear();
        for (int i = 0; i < listAPICopy.size(); i++) {
            listAPI.add(listAPICopy.get(i).clone());
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
                doSelectCheck(select);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {//選択されなかったとき

            }
        });
    }

    private void doSelectCheck(String select) {//sprinnerで指定したものを分岐
        reload(select);//指定のもので再描画

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
        readListAPI();//検索結果を初期に戻す
        //TODO;営業時間でもAPIに登録されていないと蹴られてしまう
        boolean flag = false;
        switch (lunch) {
            case "さらに絞り込み"://初期に戻す
                if (sprinnerFlag) {
                    Toast.makeText(ShowListActivity.this, "検索結果を元に戻します", Toast.LENGTH_SHORT).show();
                }
                sprinnerFlag = true;
                flag = true;
                break;
            case "開店中"://開店中
                doCheckOpen("");//TODO:現在時刻をセット
                break;
            case "昼営業あり"://昼営業
                doCheckOpen("12:00");
                break;
            case "夜営業あり"://夜営業
                doCheckOpen("18:00");
                break;
        }
        if (!flag) {
            Toast.makeText(ShowListActivity.this, "このページのみの絞り込みです", Toast.LENGTH_SHORT).show();
            flag = false;
        }
        makeList();
        checkButton();
    }

    private void doCheckOpen(String time) {//指定時刻が閉店中ならば削除
        for (int i = 0; i < listAPI.size(); i++) {
            if (listAPI.get(i).isOpenTimeFlag()) {//営業時間が登録されている
                if (isCheckOpenTime(time,i)) {//指定時刻が開店している
                    continue;//ここで切り上げる !=break
                }
            }
            listAPI.remove(i);//listの中身削除
            i--;//削除したためindexも減らす
        }
    }

    private boolean isCheckOpenTime(String time,int index) {//引き数の時間は営業時間か
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");//format
        String[] storeOpen=listAPI.get(index).getStoreOpen();
        String[] storeClose=listAPI.get(index).getStoreClose();
        try {
            Date date = simpleDateFormat.parse(time);//指定時間
            for (int i = 0; i < 2; i++) {
                Date open = simpleDateFormat.parse( storeOpen[i]);//開店時間
                Date close = simpleDateFormat.parse(storeClose[i]);//閉店時間
                int diffOpen = date.compareTo(open);//open時間との比較
                int diffClose = date.compareTo(close);//close時間との比較
                if (diffOpen >= 0 && diffClose < 0) {//開店時間以降で閉店時間以前か
                    return true;
                }
            }
        } catch (Exception e) {
            Log.d("error", String.valueOf(e));
        }
        return false;
    }

    private void checkButton() {//next/backButtonを有効無効にする
        int total = GnaviAPI.getTotalNum(), page = GnaviAPI.getPageNum(), requestNum = GnaviAPI.getRequestNum();
        //backPageButtonの有効化・無効化
        if (GnaviAPI.getPageNum() <= 1) {
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

    private void makeList() {//list表示の設定
        int total = GnaviAPI.getTotalNum(), page = GnaviAPI.getPageNum(), dataNum = GnaviAPI.getDataNum(), requestNum = GnaviAPI.getRequestNum();
        int pageMax = (int) Math.ceil((double) total / requestNum);
        String resultStr = "合計" + total + "件\t" + page + "/" + pageMax + "ページ目(" + (requestNum * page - requestNum + 1) + "～" + (requestNum * (page - 1) + dataNum) + "件表示)";
        //layout
        TextView resultTextView = (TextView) findViewById(R.id.result_textView);
        resultTextView.setText(resultStr);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.casareal_recyclerView);
        adapter = new CasarealRecycleViewAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setList(listAPI);
        adapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = view.getId();
                startActivity(ShowDetailsActivity.createIntent(listAPI.get(index), getApplication()));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
