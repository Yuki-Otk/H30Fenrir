package com.example.otkyu.h30fenrir;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.otkyu.h30fenrir.asynchronous.api.GnaviAPI;
import com.example.otkyu.h30fenrir.asynchronous.api.model.GnaviResultEntity;
import com.example.otkyu.h30fenrir.asynchronous.img.ImgAsyncTaskHttpRequest;

import java.util.List;

/**
 * Created by YukiOtake on 2018/01/28 028.
 */

public class ShowDetailsActivity extends AppCompatActivity {
    private TextView nameTextView, genreTextView, telTextView, addressTextView, opentimeTextView, howGoTextView, nameKanaTextView;
    private ImgAsyncTaskHttpRequest imgAsyncTaskHttpRequest;
    private ImageView imageView;
    private int index, count = 0;
    private String homePage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_show_scroll);//スクロールできるように変更

        Intent intent = getIntent();
        index = intent.getIntExtra("index", 0);
        init();
        setAll(index, count);

        Button backButton = (Button) findViewById(R.id.back_button);//listに戻る
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button changeButton = (Button) findViewById(R.id.change_button);//画像を切り替える
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                setAll(index, count);
            }
        });
        Button jumpButton = (Button) findViewById(R.id.jump_button);
        jumpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(homePage));
                startActivity(intent);
            }
        });
    }

    private void init() {
        nameTextView = (TextView) findViewById(R.id.name_textView);
        nameKanaTextView = (TextView) findViewById(R.id.nameKana_textView);
        genreTextView = (TextView) findViewById(R.id.genre_textView);
        telTextView = (TextView) findViewById(R.id.tel_textView);
        addressTextView = (TextView) findViewById(R.id.address_textView);
        opentimeTextView = (TextView) findViewById(R.id.opentime_textView);
        howGoTextView = (TextView) findViewById(R.id.howGo_textView);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    private void setAll(int index, int count) {
        List<GnaviResultEntity> list = GnaviAPI.getGnaviResultEntityList();
        nameTextView.setText(list.get(index).getName());
        nameKanaTextView.setText(list.get(index).getNameKana());
        genreTextView.setText(list.get(index).getGenre());
        telTextView.setText(list.get(index).getTel());
        addressTextView.setText(list.get(index).getAddress());
        opentimeTextView.setText(list.get(index).getOpentime());
        howGoTextView.setText(list.get(index).getHowGo());
        String[] temp = list.get(index).getImg();
        String url = temp[count % 2];
        imgAsyncTaskHttpRequest = new ImgAsyncTaskHttpRequest();
        imgAsyncTaskHttpRequest.setListener(createListener());
        imgAsyncTaskHttpRequest.execute(url);
        homePage = list.get(index).getHomePage();
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