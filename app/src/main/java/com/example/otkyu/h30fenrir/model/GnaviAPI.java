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
    private double[] gps;
    private static List<GnaviResultEntity> list;
//    private static Integer requestNum;
    private static boolean finishFlag;
    private static boolean resultFlag;

    public GnaviAPI() {
        gps = new double[2];
        list = new ArrayList<>();
//        requestNum = 0;
        finishFlag = false;
        resultFlag = false;
    }

    private void useApi() {
        // アクセスキー
//        String acckey = "input your accesskey";
        AccessKey accessKey = new AccessKey();
        String acckey = accessKey.getKey();//please show "./Secret/readme.txt"
        // 緯度
//        String lat = "35.670082";
        String lat = String.valueOf(gps[0]);
//        String lat = "37.358386";
        // 経度
//        String lon = "139.763267";
        String lon = String.valueOf(gps[1]);
//        String lon = "140.383444";
//        System.out.println("!!!!GPS is " + gps[0] + ":" + gps[1]);
        // 範囲
        String range = "1"; //大学の近くにお店がなかった orz
        // 返却形式
        String format = "json";
        //出力数
        String hitPerPage = "20";
//        requestNum = Integer.valueOf(hitPerPage);

        //ページ数
        String offsetPage = "1";
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
            }
//            String hitcount = "total:" + nodeList.path("total_hit_count").asText();
            String hitcount = "total:" + total;
            System.out.println(hitcount);
            //restのみ取得
            JsonNode restList = nodeList.path("rest");
            Iterator<JsonNode> rest = restList.iterator();
            //店舗番号、店舗名、最寄の路線、最寄の駅、最寄駅から店までの時間、店舗の小業態を出力
            int count = 0;
            while (rest.hasNext()) {
                GnaviResultEntity gnaviResultEntity = new GnaviResultEntity();
                JsonNode r = rest.next();
                String id = r.path("id").asText();
                String name = r.path("name").asText();
                String line = r.path("access").path("line").asText();
                String station = r.path("access").path("station").asText();
                String walk = r.path("access").path("walk").asText() + "分";
                String address=r.path("address").asText();//住所
                String nameKana=r.path("name_kana").asText();//ナマエ
                String howGo=line+station+"から徒歩"+walk;//行き方
                String tel=r.path("tel").asText();//電話番号
                String opentime=r.path("opentime").asText();//営業時間
                String[] img=new String[2];
                img[0]=r.path("shop_image1").asText();
                img[1]=r.path("shop_image2").asText();
                String categorys = "";
                for (JsonNode n : r.path("code").path("category_name_s")) {
                    categorys += n.asText();
                }
                System.out.println(id + "¥t" + name + "¥t" + line + "¥t" + station + "¥t" + walk + "¥t" + categorys + "count=" + count);
                gnaviResultEntity.setName(name);
                gnaviResultEntity.setAddress(address);
                gnaviResultEntity.setNameKana(nameKana);
                gnaviResultEntity.setOpentime(opentime);
                gnaviResultEntity.setTel(tel);
                gnaviResultEntity.setHowGo(howGo);
                gnaviResultEntity.setImg(img);
                list.add(gnaviResultEntity);
//                list.get(count).setName(name);
                System.out.println(count+":"+gnaviResultEntity.getName());
                count++;
            }
            finishFlag = true;
        }
    }

    private static boolean isCheckInteger(String a) {//数字にできるか判定
        try {
            Integer hoge = Integer.valueOf(a);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected String doInBackground(String... integers) {
        useApi();
        return null;
    }

    public void setGps(double[] gps) {
        this.gps = gps;
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
}
