package com.example.otkyu.h30fenrir.asynchronous.api;
/**
 * Created by YukiOtake on 2018/01/23 023.
 * url:http://android.swift-studying.com/entry/20151024/1445697702
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import com.example.otkyu.h30fenrir.asynchronous.api.model.GnaviRequestEntity;
import com.example.otkyu.h30fenrir.asynchronous.api.model.GnaviResultEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GnaviAPI extends AsyncTask<String, String, String> {
    private GnaviRequestEntity gnaviRequestEntity;
    private static List<GnaviResultEntity> gnaviResultEntityList;
    private static boolean finishFlag, modeFlag, resultFlag;
    private static int totalNum, pageNum, dataNum, requestNum;

    public GnaviAPI() {//コンストラクタ
        gnaviResultEntityList = new ArrayList<>();
        finishFlag = false;
        resultFlag = false;
        totalNum = 0;//総検索数
        pageNum = 0;//現在のページ
        dataNum = 0;//表示数
        requestNum = 0;//表示項目数
        modeFlag = false;//検索モード(制限モードならtrue)
    }

    private void getNodeList(String url) {// API実行、結果を取得し出力
        try {
            URL restSearch = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) restSearch.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();//接続
            //jsonの解読
            JSONObject jsonObject = getJson(httpURLConnection);
            openJson(jsonObject);
        } catch (Exception e) {
            Log.d("error", String.valueOf(e));
            finishFlag = true;//修了のフラグ
        }
    }

    private JSONObject getJson(HttpURLConnection httpURLConnection) throws IOException, JSONException {//結果のjsonを取得
        BufferedInputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            if (length > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
        JSONObject jsonObject = new JSONObject(new String(outputStream.toByteArray()));//データをJSONObjectに変換
        return jsonObject;
    }

    private void openJson(JSONObject jsonObject) throws JSONException {//jsonを展開してみて件数等を確認する
        try {
            totalNum = jsonObject.getInt("total_hit_count");//総検索数
            pageNum = jsonObject.getInt("page_offset");//現在のページ
            requestNum = jsonObject.getInt("hit_per_page");//表示項目数
            JSONObject rest;//レストラン情報格納
            resultFlag = true;//検索結果はある！
            try {//検索結果が2以上の時
                JSONArray rests = jsonObject.getJSONArray("rest");
                for (int i = 0; i < rests.length(); i++) {//ヒット数ループ
                    rest = rests.getJSONObject(i);
                    gnaviResultEntityList.add(getGnaviResultEntity(rest));//entityにセットしたのをリストに追加する
                }
                dataNum = rests.length();//表示数
            } catch (JSONException e) {//検索結果が1の時
                rest = jsonObject.getJSONObject("rest");
                gnaviResultEntityList.add(getGnaviResultEntity(rest));//entityにセットしたのをリストに追加する
                dataNum = 1;//表示数
            }
        } catch (JSONException e) {//検索結果がないとき
            Log.d("error", "検索結果なし");
        }
        finishFlag = true;//終了のフラグ
    }

    private GnaviResultEntity getGnaviResultEntity(JSONObject rest) throws JSONException {//検索結果を展開してGnaviResultEntityに代入
        //jsonの展開
        GnaviResultEntity gnaviResultEntity = new GnaviResultEntity();
        //展開結果を保存
        gnaviResultEntity.setName(rest.getString("name"));//店名
        gnaviResultEntity.setAddress(rest.getString("address"));//住所
        gnaviResultEntity.setNameKana(rest.getString("name_kana"));//テンメイ
        gnaviResultEntity.setOpentime(rest.getString("opentime"));//営業時間
        gnaviResultEntity.setTel(rest.getString("tel"));//電話番号
        gnaviResultEntity.setGenre(rest.getString("category"));//カテゴリ
        gnaviResultEntity.setHomePage(rest.getString("url"));//ホームページ
        gnaviResultEntity.setHoliday(rest.getString("holiday"));//休日);
        gnaviResultEntity.setPr(rest.getJSONObject("pr").getString("pr_short"));//紹介文
        String[] img = new String[2];
        img[0] = rest.getJSONObject("image_url").getString("shop_image1");//画像1
        img[1] = rest.getJSONObject("image_url").getString("shop_image2");//画像2
        String line = rest.getJSONObject("access").getString("line");//沿線
        String station = rest.getJSONObject("access").getString("station");//最寄り駅
        String walk = rest.getJSONObject("access").getString("walk") + "分";//徒歩何分
        String howGo = line + station + "から" + walk;//行き方
        gnaviResultEntity.setHowGo(howGo);
        gnaviResultEntity.setImg(img);
        return gnaviResultEntity;
    }

    @Override
    protected String doInBackground(String... integers) {
        String url = gnaviRequestEntity.getAPIUrl();// URI組み立て
        getNodeList(url);// API実行、結果を取得し出力
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public static List<GnaviResultEntity> getGnaviResultEntityList() {
        return gnaviResultEntityList;
    }

    public boolean isFinishFlag() {
        return finishFlag;
    }

    public boolean isResultFlag() {
        return resultFlag;
    }

    public void setGnaviRequestEntity(GnaviRequestEntity gnaviRequestEntity) {
        this.gnaviRequestEntity = gnaviRequestEntity;
    }

    public static int getTotalNum() {
        return totalNum;
    }

    public static int getPageNum() {
        return pageNum;
    }

    public static int getDataNum() {
        return dataNum;
    }

    public static int getRequestNum() {
        return requestNum;
    }

    public static boolean isModeFlag() {
        return modeFlag;
    }

    public void setModeFlag(boolean modeFlag) {
        GnaviAPI.modeFlag = modeFlag;
    }
}
