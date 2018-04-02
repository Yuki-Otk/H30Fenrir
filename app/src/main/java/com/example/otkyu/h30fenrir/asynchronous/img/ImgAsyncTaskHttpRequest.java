package com.example.otkyu.h30fenrir.asynchronous.img;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by YukiOtake on 2018/01/29 029.
 * src:https://akira-watson.com/android/httpurlconnection-get.html
 */

public class ImgAsyncTaskHttpRequest extends AsyncTask<String, Void, Bitmap> {
    private Listener listener;
    private int index;

    @Override
    protected Bitmap doInBackground(String... strings) {//引き数は2つ(画像URL,index)
        try {
            index = Integer.parseInt(strings[1]);//第2引き数を数字に変換
        } catch (Exception e) {//引き数渡し忘れた時用
            index = 0;//0で初期化
        }
        return downloadImage(strings[0]);//第1引き数を返す
    }


    @Override
    protected void onProgressUpdate(Void... progress) {// 途中経過をメインスレッドに返す
    }

    @Override
    protected void onPostExecute(Bitmap bmp) {// 非同期処理が終了後、結果をメインスレッドに返す
        if (listener != null) {
            listener.onSuccess(bmp, index);
        }
    }


    private Bitmap downloadImage(String address) {//画像をダウンロード
        Bitmap bmp = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(address);
            urlConnection = (HttpURLConnection) url.openConnection();// HttpURLConnection インスタンス生成
            // タイムアウト設定
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(20000);
            urlConnection.setRequestMethod("GET");// リクエストメソッド
            urlConnection.setInstanceFollowRedirects(false);// リダイレクトを自動で許可しない設定
            urlConnection.setRequestProperty("Accept-Language", "jp");// ヘッダーの設定(複数設定可能)
            urlConnection.connect();// 接続
            int resp = urlConnection.getResponseCode();//結果
            switch (resp) {
                case HttpURLConnection.HTTP_OK://取得できたら
                    InputStream is = null;
                    try {
                        is = urlConnection.getInputStream();
                        bmp = BitmapFactory.decodeStream(is);
                        is.close();
                    } catch (IOException e) {
                        Log.d("error", String.valueOf(e));
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.d("error", String.valueOf(e));
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return bmp;
    }
    public void setListener(Listener listener) {
        this.listener = listener;
    }
    public interface Listener {//読み終わったら
        void onSuccess(Bitmap bmp, int index);
    }
}
