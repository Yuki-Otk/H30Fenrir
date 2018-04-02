package com.example.otkyu.h30fenrir;

import android.app.Application;
import android.graphics.Matrix;
import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.otkyu.h30fenrir.asynchronous.api.GnaviAPI;
import com.example.otkyu.h30fenrir.asynchronous.api.model.GnaviResultEntity;
import com.example.otkyu.h30fenrir.asynchronous.img.ImgAsyncTaskHttpRequest;

/**
 * Created by YukiOtake on 2018/01/28 028.
 */

public class ShowDetailsActivity extends AppCompatActivity {
    private TextView nameTextView, genreTextView, telTextView, addressTextView, opentimeTextView, howGoTextView, nameKanaTextView, holidayTextView, prTextView;
    private ImgAsyncTaskHttpRequest imgAsyncTaskHttpRequest;
    private final int IMAGEVIEW_NUM = 2;//ImageViewの使用する数を固定値にしておく
    private ImageView[] imageViews = new ImageView[IMAGEVIEW_NUM];
    private String webUrl = null, data = null, imgUrl = null, telNum = null;
    private static final String LIST_KEY = "LIST_KEY";//intentのkey
    private GnaviResultEntity gnaviResultEntity;
    private String[] img;

    public static Intent createIntent(GnaviResultEntity object, Application activity) {//画面遷移の取得
        Intent intent = new Intent(activity, ShowDetailsActivity.class);
        intent.putExtra(LIST_KEY, object);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_show_scroll);//スクロールできるように変更

        gnaviResultEntity = (GnaviResultEntity) getIntent().getSerializableExtra(LIST_KEY);//引き数取得
        init();//viewの初期化
        setAll();//viewにデータをセット
        ActionBar actionBar = getSupportActionBar();//アクションバーにをセット
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void init() {//viewのセット
        nameTextView = findViewById(R.id.name_textView);
        nameKanaTextView = findViewById(R.id.nameKana_textView);
        genreTextView = findViewById(R.id.genre_textView);
        telTextView = findViewById(R.id.tel_textView);
        addressTextView = findViewById(R.id.address_textView);
        opentimeTextView = findViewById(R.id.opentime_textView);
        howGoTextView = findViewById(R.id.howGo_textView);
        imageViews[0] = findViewById(R.id.imageView1);
        imageViews[1] = findViewById(R.id.imageView2);
        holidayTextView = findViewById(R.id.holiday_textView);
        prTextView = findViewById(R.id.pr_textView);
    }

    private void setAll() {//dataSet
        nameTextView.setText(gnaviResultEntity.getName());
        nameKanaTextView.setText(gnaviResultEntity.getNameKana());
        genreTextView.setText(gnaviResultEntity.getGenre());
        telTextView.setText(gnaviResultEntity.getTel());
        addressTextView.setText(gnaviResultEntity.getAddress());
        opentimeTextView.setText(gnaviResultEntity.getOpentime());
        howGoTextView.setText(gnaviResultEntity.getHowGo());
        holidayTextView.setText(gnaviResultEntity.getHoliday());
        prTextView.setText(gnaviResultEntity.getPr());
        img = gnaviResultEntity.getImg();
        if (img[0] != null) {//画像情報が登録されているなら
            if (!GnaviAPI.isModeFlag()) {//制限モードでないなら読み込む
                imgAsyncTaskHttpRequest = new ImgAsyncTaskHttpRequest();
                imgAsyncTaskHttpRequest.setListener(createListener());
                imgAsyncTaskHttpRequest.execute(img[0], String.valueOf(0));//urlを引き数にして画像取得
            }
            imgUrl = img[0];
        }
        webUrl = gnaviResultEntity.getHomePage();
        telNum = gnaviResultEntity.getTel();
        data = gnaviResultEntity.getName() + "(" + gnaviResultEntity.getNameKana() + ")\n" + gnaviResultEntity.getHowGo() + "\n" + webUrl;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//アクションバーにメニューを表示させる
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//アクションバーのメニューを選択した時のイベント
        switch (item.getItemId()) {
            case R.id.tel_menu:
                callTell();//電話をかける
                return true;
            case R.id.web_menu:
                showWeb();//ブラウザ表示
                return true;
            case R.id.share_menu:
                String chooserTitle = "共有する", subject = "ぐるなび店舗情報";
                share(ShowDetailsActivity.this, chooserTitle, subject, data, imgUrl);//share
                return true;
            case android.R.id.home:
                finish();//左上の戻るボタン
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void share(Activity activity, String chooserTitle, String subject, String text, String uri) {//シェアする
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(activity);
        builder.setChooserTitle(chooserTitle);
        builder.setSubject(subject);
        builder.setText(text+"\n"+uri);
        builder.setType("text/plain");
        builder.startChooser();
    }

    private void callTell() {//電話をかける
        Uri uri = Uri.parse("tel:" + telNum);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(intent);
    }

    private void showWeb() {//ブラウザを開く
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
        startActivity(intent);
    }

    private ImgAsyncTaskHttpRequest.Listener createListener() {//画像の取得
        return new ImgAsyncTaskHttpRequest.Listener() {
            @Override
            public void onSuccess(Bitmap bitmap, int index) {
                imageViews[index].setImageBitmap(doExpansion(bitmap));//詳細画面ではオリジナルの画像を拡大して表示する
                if (index < imageViews.length - 1) {
                    if (img[index + 1] != null) {
                        imgAsyncTaskHttpRequest = new ImgAsyncTaskHttpRequest();
                        imgAsyncTaskHttpRequest.setListener(createListener());
                        imgAsyncTaskHttpRequest.execute(img[index + 1], String.valueOf(index + 1));//追加で画像を読み込む
                    }
                }
            }

            private Bitmap doExpansion(Bitmap bitmap) {//画像を拡大する
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;//端末の画面の幅
                float magnification = ((float) width / bitmap.getWidth()) / 4 * 3;//画面幅75%程度の拡大率にする
                Matrix matrix = new Matrix();
                matrix.setScale(magnification, magnification);//拡大率をセット
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);//拡大した画像を書き出し
                return bitmap;
            }
        };
    }
}