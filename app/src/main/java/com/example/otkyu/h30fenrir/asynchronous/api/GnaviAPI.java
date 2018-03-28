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
    private static boolean finishFlag;
    private static boolean resultFlag;
    private static int totalNum, pageNum, dataNum, requestNum;

    public GnaviAPI() {
        gnaviResultEntityList = new ArrayList<>();
        finishFlag = false;
        resultFlag = false;
        totalNum = 0;//総検索数
        pageNum = 0;//現在のページ
        dataNum = 0;//表示数
        requestNum = 0;//表示項目数
    }

    private void useApi() {
        // URI組み立て
        String url = gnaviRequestEntity.getAPIUrl();
        System.out.println("url=" + url);
        // API実行、結果を取得し出力
        getNodeList(url);
    }

    private void getNodeList(String url) {
        try {
            URL restSearch = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) restSearch.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            //jsonの解読
            JSONObject jsonObject = getJson(httpURLConnection);
            openJson(jsonObject);
        } catch (Exception e) {
            //TODO: 例外を考慮していません
            Log.d("error", String.valueOf(e));
            finishFlag = true;
        }
    }

    private JSONObject getJson(HttpURLConnection httpURLConnection) throws IOException, JSONException {//結果のjsonを取得
        String result = null;
        BufferedInputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            if (length > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
        JSONObject jsonObject = new JSONObject(new String(outputStream.toByteArray()));
        return jsonObject;
    }

    private void openJson(JSONObject jsonObject) throws JSONException {//jsonを展開してみて件数等を確認する
        try {
            totalNum = jsonObject.getInt("total_hit_count");//総検索数
            pageNum = jsonObject.getInt("page_offset");//現在のページ
            requestNum = jsonObject.getInt("hit_per_page");//表示項目数
            JSONObject rest;
            resultFlag = true;//検索結果はある！
            try {//検索結果が2以上の時
                JSONArray rests = jsonObject.getJSONArray("rest");
                for (int i = 0; i < rests.length(); i++) {//ヒット数ループ
                    rest = rests.getJSONObject(i);
                    gnaviResultEntityList.add(getGnaviResultEntity(rest));
                }
                dataNum = rests.length();//表示数
            } catch (JSONException e) {//検索結果が1の時
                rest = jsonObject.getJSONObject("rest");
                gnaviResultEntityList.add(getGnaviResultEntity(rest));
                dataNum = 1;//表示数
            }

        } catch (JSONException e) {//検索結果がないとき
            Log.d("ERROR", "検索結果なし");
        }
        finishFlag = true;
    }

    private GnaviResultEntity getGnaviResultEntity(JSONObject rest) throws JSONException {//検索結果を展開してGnaviResultEntityに代入
        //jsonの展開
        GnaviResultEntity gnaviResultEntity = new GnaviResultEntity();
        String id = rest.getString("id");
        String name = rest.getString("name");
        String address = rest.getString("address");//住所
        String nameKana = rest.getString("name_kana");//ナマエ
        String tel = rest.getString("tel");//電話番号
        String opentime = rest.getString("opentime");//営業時間
        String homePage = rest.getString("url");//ホームページ
        String categorys = rest.getString("category");
        String[] img = new String[2];
        img[0] = rest.getJSONObject("image_url").getString("shop_image1");
        img[1] = rest.getJSONObject("image_url").getString("shop_image2");
        String line = rest.getJSONObject("access").getString("line");
        String station = rest.getJSONObject("access").getString("station");
        String walk = rest.getJSONObject("access").getString("walk") + "分";
        String howGo = line + station + "から" + walk;//行き方
        //展開結果を保存
        gnaviResultEntity.setName(name);
        gnaviResultEntity.setAddress(address);
        gnaviResultEntity.setNameKana(nameKana);
        gnaviResultEntity.setOpentime(opentime);
        gnaviResultEntity.setTel(tel);
        gnaviResultEntity.setHowGo(howGo);
        gnaviResultEntity.setImg(img);
        gnaviResultEntity.setGenre(categorys);
        gnaviResultEntity.setHomePage(homePage);
        return gnaviResultEntity;
    }

    @Override
    protected String doInBackground(String... integers) {
        useApi();
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.d("hoge", "onProgressUpdate");
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public static List<GnaviResultEntity> getGnaviResultEntityList() {
        return gnaviResultEntityList;
    }

    public static boolean isFinishFlag() {
        return finishFlag;
    }

    public static boolean isResultFlag() {
        return resultFlag;
    }

    public GnaviRequestEntity getGnaviRequestEntity() {
        return gnaviRequestEntity;
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
}
