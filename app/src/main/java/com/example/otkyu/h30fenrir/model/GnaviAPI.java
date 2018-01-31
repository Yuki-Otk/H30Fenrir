package com.example.otkyu.h30fenrir.model;
/**
 * Created by YukiOtake on 2018/01/23 023.
 */

import android.os.AsyncTask;

import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.otkyu.h30fenrir.model.secret.AccessKey;
import com.fasterxml.jackson.databind.*;

/*******************************************************************************
 * ぐるなびWebサービスのレストラン検索APIで緯度経度検索を実行しパースするプログラム
 * 注意：緯度、経度、範囲は固定で入れています。
 *　　　　アクセスキーはアカウント登録時に発行されたキーを指定してください。
 *      JsonをパースするためにライブラリにJacksonを追加しています。
 ******************************************************************************/

public class GnaviAPI extends AsyncTask<String, String, String> {
    //    private double[] gps;
    private GnaviRequestEntity gnaviRequestEntity;
    private static List<GnaviResultEntity> list;
    //    private static Integer requestNum;
    private static boolean finishFlag;
    private static boolean resultFlag;
    private static int totalNum, pageNum, dataNum, requestNum;

    public GnaviAPI() {
//        gps = new double[2];
//        gnaviRequestEntity=new GnaviRequestEntity();
        list = new ArrayList<>();
//        requestNum = 0;
        finishFlag = false;
        resultFlag = false;
        totalNum = 0;
        pageNum = 0;
        dataNum = 0;
        requestNum = 0;
    }

    private void useApi() {
        // アクセスキー
//        String acckey = "input your accesskey";
        AccessKey accessKey = new AccessKey();
        String acckey = accessKey.getKey();//please show "./Secret/readme.txt"
        double[] gps = getGnaviRequestEntity().getGps();
        // 緯度
        String lat = String.valueOf(gps[0]);
        // 経度
        String lon = String.valueOf(gps[1]);
        // 範囲
        String range = getGnaviRequestEntity().getRange();
//        System.out.println("GnaviApi range is "+range);
        // 返却形式
        String format = "json";
        //出力数Integer.valueOf(hitPerPage);
//        String hitPerPage = "20";
        String hitPerPage=gnaviRequestEntity.getPage();
        requestNum = Integer.parseInt(hitPerPage);
        //ページ数
//        String offsetPage = "1";
        String offsetPage = getGnaviRequestEntity().getOffsetPage();
        pageNum = Integer.parseInt(offsetPage);
        //フリーワード
        String freeword = getGnaviRequestEntity().getFreeword();
        boolean freewordFlag = isCheckNull(freeword);
        // エンドポイント
        String gnaviRestUri = "https://api.gnavi.co.jp/RestSearchAPI/20150630/";
        String prmFormat = "?format=" + format;
        String prmKeyid = "&keyid=" + acckey;
        String prmInputCoordinatesMode = "&input_coordinates_mode=2";//入力測地系タイプを変更
        String prmLat = "&latitude=" + lat;
        String prmLon = "&longitude=" + lon;
        String prmRange = "&range=" + range;
        String prmHitPerPage = "&hit_per_page=" + hitPerPage;
        String prmOffsetPage = "&offset_page=" + offsetPage;

        // URI組み立て
        StringBuffer uri = new StringBuffer();
        uri.append(gnaviRestUri);
        uri.append(prmFormat);
        uri.append(prmKeyid);
        uri.append(prmInputCoordinatesMode);
        uri.append(prmLat);
        uri.append(prmLon);
        uri.append(prmRange);
        uri.append(prmHitPerPage);
        uri.append(prmOffsetPage);
        System.out.println("freeword flag=" + freewordFlag);
        if (freewordFlag) {
            String prmFreeword = "&freeword=" + freeword;
            uri.append(prmFreeword);
        }

        System.out.println("url=" + uri);
        // API実行、結果を取得し出力
        getNodeList(uri.toString());
    }

    private static void getNodeList(String url) {
        try {
            URL restSearch = new URL(url);
            HttpURLConnection http = (HttpURLConnection) restSearch.openConnection();
            http.setRequestMethod("GET");
            http.connect();
//            System.out.println("uer="+url);
            //Jackson
            ObjectMapper mapper = new ObjectMapper();
            viewJsonNode(mapper.readTree(http.getInputStream()));

        } catch (Exception e) {
            //TODO: 例外を考慮していません
            System.out.println("error");
            System.out.println(e);
        }
    }

    private static void viewJsonNode(JsonNode nodeList) {
        if (nodeList != null) {
            //トータルヒット件数
            String total = nodeList.path("total_hit_count").asText();
//            Integer total= Integer.valueOf(nodeList.path("total_hit_count").asText());
            if (isCheckInteger(total)) {//数字にできるか判定
                resultFlag = true;//検索結果はある！
                totalNum = Integer.parseInt(total);
            } else {
                System.out.println("検索結果なし");
                finishFlag = true;
                return;//検索結果がなければ終了
            }
//            String hitcount = "total:" + nodeList.path("total_hit_count").asText();
            String hitcount = "total:" + total;
            int pageOffset = nodeList.path("page_offset").asInt();
            int hitPerPage = nodeList.path("hit_per_page").asInt();
            System.out.println(hitcount);
            //restのみ取得
            JsonNode restList = nodeList.path("rest");
            Iterator<JsonNode> rest = restList.iterator();
            int count = 0;
            int max;
            if (totalNum > pageOffset * hitPerPage) {
                max = hitPerPage;
            } else {
                max = totalNum - (pageOffset - 1) * hitPerPage;
            }
            System.out.println("page=" + pageOffset);
            System.out.println("hitPage=" + hitPerPage);
            System.out.println("max=" + max);
            System.out.println("rest=" + restList.get(0));
            //店舗番号、店舗名、最寄の路線、最寄の駅、最寄駅から店までの時間、店舗の小業態を出力
//            while (rest.hasNext()) {
            if (max == 1) {//暫定実行停止中
                resultFlag = false;
                finishFlag = true;
                return;
            }
            for (int i = 0; i < max; i++) {
                GnaviResultEntity gnaviResultEntity = new GnaviResultEntity();
                JsonNode r = rest.next();
                String id = r.path("id").asText();
                String name = r.path("name").asText();
                String line = r.path("access").path("line").asText();
                String station = r.path("access").path("station").asText();
                String walk = r.path("access").path("walk").asText() + "分";
                String address = r.path("address").asText();//住所
                String nameKana = r.path("name_kana").asText();//ナマエ
                String howGo = line + station + "から" + walk;//行き方
                String tel = r.path("tel").asText();//電話番号
                String opentime = r.path("opentime").asText();//営業時間
                String homePage = r.path("url").asText();//homePage
                String[] img = new String[2];
                img[0] = r.path("image_url").path("shop_image1").asText();
                img[1] = r.path("image_url").path("shop_image2").asText();
                String categorys = "";
                for (JsonNode n : r.path("code").path("category_name_s")) {
                    categorys += n.asText();
                }
                System.out.println(id + "¥t" + name + "¥t" + line + "¥t" + station + "¥t" + walk + "¥t" + categorys + "count=" + count);
//                System.out.println(count+":"+gnaviResultEntity.getName());
//                System.out.println("opentime='"+opentime+"'");
                name = checkString(name);
                gnaviResultEntity.setName(name);
                address = checkString(address);
                gnaviResultEntity.setAddress(address);
                nameKana = checkString(nameKana);
                gnaviResultEntity.setNameKana(nameKana);
                opentime = checkString(opentime);
                gnaviResultEntity.setOpentime(opentime);
                tel = checkString(tel);
                gnaviResultEntity.setTel(tel);
                howGo = checkString(howGo);
                gnaviResultEntity.setHowGo(howGo);
                img[0] = checkString(img[0]);
                img[1] = checkString(img[1]);
                System.out.println("img url=" + img[0]);
                gnaviResultEntity.setImg(img);
                categorys=checkString(categorys);
                gnaviResultEntity.setGenre(categorys);
                homePage=checkString(homePage);
                gnaviResultEntity.setHomePage(homePage);
                list.add(gnaviResultEntity);
//                list.get(count).setName(name);
                count++;
                System.out.println("img url=" + img[0]);
            }
            dataNum = count;
            finishFlag = true;
        }
    }

    private static String checkString(String str) {
        if (str.equals("") || str.equals("から分")) {
            return "登録されていません";
        }
        return str;
    }

    private static boolean isCheckInteger(String str) {//数字にできるか判定
        try {
            Integer hoge = Integer.valueOf(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isCheckNull(String str) {
        if (str == null) {
            return false;
        }
        if (str.equals("")) {
            return false;
        }
        return true;
    }

    @Override
    protected String doInBackground(String... integers) {
        useApi();
        return null;
    }

    public static List<GnaviResultEntity> getList() {
//        for(int i=0;i<list.size();i++){
//            System.out.println("list "+i+"="+list.get(i).getName());
//        }
        return list;
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
