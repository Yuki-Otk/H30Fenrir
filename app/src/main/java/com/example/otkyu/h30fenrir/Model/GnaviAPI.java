package com.example.otkyu.h30fenrir.Model;

/**
 * Created by otkyu on 2018/01/23 023.
 */

import android.os.AsyncTask;

import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Iterator;

import com.example.otkyu.h30fenrir.Model.Secret.AccessKey;
import com.fasterxml.jackson.databind.*;

/*******************************************************************************
 * ぐるなびWebサービスのレストラン検索APIで緯度経度検索を実行しパースするプログラム
 * 注意：緯度、経度、範囲は固定で入れています。
 *　　　　アクセスキーはアカウント登録時に発行されたキーを指定してください。
 *      JsonをパースするためにライブラリにJacksonを追加しています。
 ******************************************************************************/

public class GnaviAPI extends AsyncTask<String, String,String> {
    private void useApi(){
        // アクセスキー
//        String acckey = "input your accesskey";
        AccessKey accessKey=new AccessKey();
        String acckey=accessKey.getKey();//please show "./Secret/readme.txt"
        // 緯度
        String lat = "35.670082";
        // 経度
        String lon = "139.763267";
        // 範囲
        String range = "1";
        // 返却形式
        String format = "json";
        // エンドポイント
        String gnaviRestUri = "https://api.gnavi.co.jp/RestSearchAPI/20150630/";
        String prmFormat = "?format=" + format;
        String prmKeyid = "&keyid=" + acckey;
        String prmLat = "&latitude=" + lat;
        String prmLon = "&longitude=" + lon;
        String prmRange = "&range=" + range;

        // URI組み立て
        StringBuffer uri = new StringBuffer();
        uri.append(gnaviRestUri);
        uri.append(prmFormat);
        uri.append(prmKeyid);
        uri.append(prmLat);
        uri.append(prmLon);
        uri.append(prmRange);

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
            String hitcount = "total:" + nodeList.path("total_hit_count").asText();
            System.out.println(hitcount);
            //restのみ取得
            JsonNode restList = nodeList.path("rest");
            Iterator<JsonNode> rest = restList.iterator();
            //店舗番号、店舗名、最寄の路線、最寄の駅、最寄駅から店までの時間、店舗の小業態を出力
            while (rest.hasNext()) {
                JsonNode r = rest.next();
                String id = r.path("id").asText();
                String name = r.path("name").asText();
                String line = r.path("access").path("line").asText();
                String station = r.path("access").path("station").asText();
                String walk = r.path("access").path("walk").asText() + "分";
                String categorys = "";

                for (JsonNode n : r.path("code").path("category_name_s")) {
                    categorys += n.asText();
                }
                System.out.println(id + "¥t" + name + "¥t" + line + "¥t" + station + "¥t" + walk + "¥t" + categorys);
            }
        }
    }

    @Override
    protected String doInBackground(String... integers) {
        useApi();
        return null;
    }
}
