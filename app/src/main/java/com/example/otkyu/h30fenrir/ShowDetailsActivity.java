package com.example.otkyu.h30fenrir;

//import android.app.ActionBar;

import android.app.Application;
import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.otkyu.h30fenrir.asynchronous.api.GnaviAPI;
import com.example.otkyu.h30fenrir.asynchronous.api.model.GnaviRequestEntity;
import com.example.otkyu.h30fenrir.asynchronous.api.model.GnaviResultEntity;
import com.example.otkyu.h30fenrir.asynchronous.img.ImgAsyncTaskHttpRequest;

import java.util.List;

/**
 * Created by YukiOtake on 2018/01/28 028.
 */

public class ShowDetailsActivity extends AppCompatActivity {
    private TextView nameTextView, genreTextView, telTextView, addressTextView, opentimeTextView, howGoTextView, nameKanaTextView,holidayTextView;
    private ImgAsyncTaskHttpRequest imgAsyncTaskHttpRequest;
    private ImageView imageView;
    private int count = 0;
    private String webUrl = null, data = null, imgUrl = null, telNum = null;
    private static final String LIST_KEY = "LIST_KEY";
    private GnaviResultEntity gnaviResultEntity;

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
        init();
        setAll(count);

        Button changeButton = (Button) findViewById(R.id.change_button);//画像を切り替える
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                setAll(count);
            }
        });
        //backButton
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void init() {
        nameTextView =  findViewById(R.id.name_textView);
        nameKanaTextView =  findViewById(R.id.nameKana_textView);
        genreTextView =  findViewById(R.id.genre_textView);
        telTextView =  findViewById(R.id.tel_textView);
        addressTextView =  findViewById(R.id.address_textView);
        opentimeTextView =  findViewById(R.id.opentime_textView);
        howGoTextView =  findViewById(R.id.howGo_textView);
        imageView =  findViewById(R.id.imageView);
        holidayTextView=findViewById(R.id.holiday_textView);
    }

    private void setAll(int count) {
        nameTextView.setText(gnaviResultEntity.getName());
        nameKanaTextView.setText(gnaviResultEntity.getNameKana());
        genreTextView.setText(gnaviResultEntity.getGenre());
        telTextView.setText(gnaviResultEntity.getTel());
        addressTextView.setText(gnaviResultEntity.getAddress());
        opentimeTextView.setText(gnaviResultEntity.getOpentime());
        howGoTextView.setText(gnaviResultEntity.getHowGo());
        holidayTextView.setText(gnaviResultEntity.getHoliday());
        String[] temp = gnaviResultEntity.getImg();
        String url = temp[count % 2];
        imgAsyncTaskHttpRequest = new ImgAsyncTaskHttpRequest();
        imgAsyncTaskHttpRequest.setListener(createListener());
        imgAsyncTaskHttpRequest.execute(url);
        imgUrl = url;
        webUrl = gnaviResultEntity.getHomePage();
        telNum = gnaviResultEntity.getTel();
        data = gnaviResultEntity.getName() + "(" + gnaviResultEntity.getNameKana() + ")\n" + gnaviResultEntity.getHowGo() + "\n" + webUrl;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.tel_menu:
                callTell();
                return true;
            case R.id.web_menu:
                showWeb();
                return true;
            case R.id.share_menu:
                String chooserTitle = "共有する", subject = "ぐるなび店舗情報";
                share(ShowDetailsActivity.this, chooserTitle, subject, data, Uri.parse(imgUrl));
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void share(Activity activity, String chooserTitle, String subject, String text, Uri uri) {
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(activity);
        builder.setChooserTitle(chooserTitle);
        builder.setSubject(subject);
        builder.setText(text);
        builder.setStream(uri);
        builder.setType("image/jpeg");
        builder.startChooser();
    }

    private void callTell() {
        Uri uri = Uri.parse("tel:" + telNum);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(intent);
    }

    private void showWeb() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        imgAsyncTaskHttpRequest.setListener(null);
        super.onDestroy();
    }

    private ImgAsyncTaskHttpRequest.Listener createListener() {
        return new ImgAsyncTaskHttpRequest.Listener() {
            @Override
            public void onSuccess(Bitmap bmp) {
                imageView.setImageBitmap(bmp);
            }
        };
    }
}