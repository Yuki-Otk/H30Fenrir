package com.example.otkyu.h30fenrir;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.otkyu.h30fenrir.view.CasarealRecycleViewAdapter;
import com.example.otkyu.h30fenrir.asynchronous.api.GnaviAPI;
import com.example.otkyu.h30fenrir.asynchronous.api.model.GnaviRequestEntity;
import com.example.otkyu.h30fenrir.asynchronous.api.model.GnaviResultEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by YukiOtake on 2018/01/24 024.
 * src:https://akira-watson.com/android/activity-2.html
 * src:https://qiita.com/so-ma1221/items/d1b84bf764bf82fe1ac3
 * src:https://akira-watson.com/android/scrollview.html
 */

public class ShowListActivity extends AppCompatActivity {

    private Button backPageButton, nextPageButton, returnButton;
    private GnaviRequestEntity gnaviRequestEntity;
    private static final String REQUEST_KEY = "gnaviRequestEntity";
    private CasarealRecycleViewAdapter adapter;
    private List<GnaviResultEntity> listAPICopy = GnaviAPI.getGnaviResultEntityList();//検索結果のコピー
    private List<GnaviResultEntity> listAPI = new ArrayList<>();//検索結果
    private boolean sprinnerFlag, modeFlag;
    private final SimpleDateFormat hourMinutesFormat = new SimpleDateFormat("HH:mm");//時間扱い時のフォーマット(時:分)
    private final SimpleDateFormat yearMonthDayFormat = new SimpleDateFormat("yyyy/MM/dd");//時間扱い時のフォーマット(年/月/日)
    private final SimpleDateFormat yearMonthDayHourMinutesFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm");//時間扱い時のフォーマット(年/月/日-時:分)
    private Spinner spinner;
    private TextView resultTextView;
    private RecyclerView recyclerView;

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
        init();//viewの読み込み
        readListAPI();//検索結果
        onSelectSprinner();//spinnerのイベント
        onReturnButton();//ReturnButtonのイベント
        makeList();//list表示
        doCheckButton();//next/backButtonを有効無効にする
        onNextPageButton();//nextPageButtonのイベント
        onBackPageButton();//backPageButtonのイベント
        //アクションバーの左のbackボタン
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        sprinnerFlag = false;
    }

    private void init() {
        spinner = findViewById(R.id.time_spinner);//Sprinner(プルダウン)
        //下のボタン群
        backPageButton = findViewById(R.id.backPage_button);
        nextPageButton = findViewById(R.id.nextPage_button);
        returnButton = findViewById(R.id.backHome_button);
        resultTextView = findViewById(R.id.result_textView);//検索結果の数とかを表示
        recyclerView = findViewById(R.id.casareal_recyclerView);//list
        modeFlag = GnaviAPI.isModeFlag();//制限モードかのフラグ(true=制限モード)
    }

    private void readListAPI() {//検索結果のListをディープコピーする
        listAPI.clear();
        for (int i = 0; i < listAPICopy.size(); i++) {
            listAPI.add(listAPICopy.get(i).clone());
        }
    }

    private void onNextPageButton() {//nextPageButtonのイベント
        nextPageButton.setOnClickListener(new View.OnClickListener() {//次のページを表示
            @Override
            public void onClick(View v) {
                int newPage = GnaviAPI.getPageNum() + 1;
                reload(newPage);
            }
        });
    }

    private void onBackPageButton() {//backPageButtonのイベント
        backPageButton.setOnClickListener(new View.OnClickListener() {//前のページを表示
            @Override
            public void onClick(View v) {
                int newPage = GnaviAPI.getPageNum() - 1;
                reload(newPage);
            }
        });
    }

    private void onReturnButton() {//ReturnButtonのイベント
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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


    private void onSelectSprinner() {//sprinnerを変更したとき
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {//プルダウンで変更されたとき
                String select = (String) adapterView.getSelectedItem();
                reload(select);//指定のもので再描画
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {//選択されなかったとき

            }
        });
    }

    private void reload(int newPage) {//再描画(ページ指定)//overload//APIを呼び直す
        GnaviAPI gnaviAPI = new GnaviAPI();
        System.out.println("new page is " + newPage);
        gnaviRequestEntity.setOffsetPage(String.valueOf(newPage));
        gnaviAPI.setModeFlag(modeFlag);
        gnaviAPI.setGnaviRequestEntity(gnaviRequestEntity);
        gnaviAPI.execute();
        while (true) {//api結果取得するまでweit
            if (gnaviAPI.isFinishFlag()) {
                break;
            }
        }
        listAPICopy = GnaviAPI.getGnaviResultEntityList();
        readListAPI();
        spinner.setSelection(0);
        makeList();
        doCheckButton();
    }

    private void reload(String lunch) {//再描画(lunch)//overload//APIを呼び直す
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
                doCheckOpen(getHourMinutes());//現在時刻で判定
                break;
            case "昼営業あり"://昼営業
                doCheckOpen("12:00");//12時に開店しているか
                break;
            case "夜営業あり"://夜営業
                doCheckOpen("18:00");//18時に開店しているか
//                doCheckWriteOpenTime();
                break;
        }
        if (!flag) {
            Toast.makeText(ShowListActivity.this, "このページのみの絞り込みです", Toast.LENGTH_LONG).show();
        }
        makeList();
        doCheckButton();
    }

    private void doCheckWriteOpenTime() {//営業時間について確認する用
        for (int i = 0; i < listAPI.size(); i++) {
            if (!listAPI.get(i).isOpenTimeFlag()) {
                listAPI.remove(i);
                i--;
            } else {
                Log.d("hoge", String.valueOf(i));
                String[] hoge = listAPI.get(i).getStoreOpen();
                String[] fuga = listAPI.get(i).getStoreClose();
                Log.d("hoge", listAPI.get(i).getName());
            }
        }
    }

    private String getHourMinutes() {//現在の時刻(時:分)をformatに従って取得
        Calendar calendar = Calendar.getInstance();//現在時刻を取得
        Log.d("time", hourMinutesFormat.format(calendar.getTime()));
        return hourMinutesFormat.format(calendar.getTime());
    }

    private String getYearMonthDay() {//現在の時刻(日)をformatに従って取得
        Calendar calendar = Calendar.getInstance();//現在時刻を取得
        return yearMonthDayFormat.format(calendar.getTime());
    }

    private void doCheckOpen(String time) {//指定時刻が閉店中ならば削除
        for (int i = 0; i < listAPI.size(); i++) {
            if (listAPI.get(i).isOpenTimeFlag()) {//営業時間が登録されているか
                try {
                    if (isCheckOpenTime(time, i)) {//指定時刻が開店しているか
                        continue;//ここで切り上げる !=break
                    }
                } catch (Exception e) {
                    Log.d("error", String.valueOf(e));
                }
            }
            listAPI.remove(i);//listの中身削除
            i--;//削除したためindexも減らす
        }
    }

    private boolean isCheckOpenTime(String time, int index) throws ParseException {//引き数の時間は営業時間か
        time = getYearMonthDay() + "-" + time;//時間に日付を追加
        String[] storeOpen = listAPI.get(index).getStoreOpen();//listから開店時間を取得
        String[] storeClose = listAPI.get(index).getStoreClose();//listから閉店時間を取得
        Date date = yearMonthDayHourMinutesFormat.parse(time);//指定時間
        for (int i = 0; i < 2; i++) {
            try {//片方だけでもあっていれば検索に適応するようにforの中に例外
                boolean nextDay = isCheckNextDay(storeClose[i]);//翌日まで営業している店か
                if (nextDay) {//翌日まで営業
                    storeClose[i] = storeClose[i].substring(1, storeClose[i].length());//"翌"の文字を切り捨て
                }
                Date open = yearMonthDayHourMinutesFormat.parse(getYearMonthDay() + "-" + storeOpen[i]);//開店時間
                Date close = yearMonthDayHourMinutesFormat.parse(getYearMonthDay() + "-" + storeClose[i]);//閉店時間
                if (nextDay) {
                    close = doAddDay(close);//日付を1日増やす
                    if (isCheckAmPm(date)) {
                        date = doAddDay(date);
                    }
                }
                int diffOpen = date.compareTo(open);//open時間との比較
                int diffClose = date.compareTo(close);//close時間との比較
                if (diffOpen >= 0 && diffClose < 0) {//開店時間以降で閉店時間以前か
                    return true;//営業時間なう
                }
            } catch (Exception e) {
                Log.d("error", String.valueOf(e));
            }
        }
        return false;//営業時間ではない
    }

    private boolean isCheckNextDay(String str) {//次の日が指定されていないか
        if (str.indexOf("翌") == 0) {
            return true;
        }
        return false;
    }

    private boolean isCheckAmPm(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (calendar.get(Calendar.AM_PM) == 0) {
            return true;
        }
        return false;
    }

    private Date doAddDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }


    private void doCheckButton() {//next/backButtonを有効無効にする
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
        //もしもrequestNumが0なら営業時間の抜き出しの時にエラーが起きてる可能性大
        int pageMax = (int) Math.ceil((double) total / requestNum);
        String resultStr = "合計" + total + "件\t" + page + "/" + pageMax + "ページ目(" + (requestNum * page - requestNum + 1) + "～" + (requestNum * (page - 1) + dataNum) + "件表示)";
        //layout
        resultTextView.setText(resultStr);
        adapter = new CasarealRecycleViewAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);//listを表示
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setList(listAPI);
        adapter.setModeFlag(modeFlag);
        adapter.setResources(getResources());
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
