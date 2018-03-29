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
            index=Integer.parseInt(strings[1]);
        }catch (Exception e){//引き数渡し忘れた時用
            index=0;
        }
        return downloadImage(strings[0]);
    }


    @Override
    protected void onProgressUpdate(Void... progress) {// 途中経過をメインスレッドに返す
    }

    @Override
    protected void onPostExecute(Bitmap bmp) {// 非同期処理が終了後、結果をメインスレッドに返す
        if (listener != null) {
            listener.onSuccess(bmp,index);
        }
    }


    private Bitmap downloadImage(String address) {
        Bitmap bmp = null;

        final StringBuilder result = new StringBuilder();

        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(address);
            urlConnection = (HttpURLConnection) url.openConnection();// HttpURLConnection インスタンス生成
            // タイムアウト設定
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(20000);
            // リクエストメソッド
            urlConnection.setRequestMethod("GET");
            // リダイレクトを自動で許可しない設定
            urlConnection.setInstanceFollowRedirects(false);
            // ヘッダーの設定(複数設定可能)
            urlConnection.setRequestProperty("Accept-Language", "jp");
            // 接続
            urlConnection.connect();
            int resp = urlConnection.getResponseCode();
            switch (resp) {
                case HttpURLConnection.HTTP_OK:
                    InputStream is = null;
                    try {
                        is = urlConnection.getInputStream();
                        bmp = BitmapFactory.decodeStream(is);
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
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
            Log.d("debug", "downloadImage error");
            e.printStackTrace();
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


    public interface Listener {
        void onSuccess(Bitmap bmp,int index);
    }
}
