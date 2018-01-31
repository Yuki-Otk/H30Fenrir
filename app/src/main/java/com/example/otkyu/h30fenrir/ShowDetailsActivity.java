package com.example.otkyu.h30fenrir;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.otkyu.h30fenrir.model.GnaviAPI;
import com.example.otkyu.h30fenrir.model.GnaviResultEntity;
import com.example.otkyu.h30fenrir.model.ImgAsyncTaskHttpRequest;

import java.util.List;

/**
 * Created by YukiOtake on 2018/01/28 028.
 */

public class ShowDetailsActivity extends AppCompatActivity {
    private TextView nameTextView, nameKanaTextView, telTextView, addressTextView, opentimeTextView, howGoTextView;
    private ImgAsyncTaskHttpRequest imgAsyncTaskHttpRequest;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_show);


        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        int index = intent.getIntExtra("index", 0);
//        System.out.println("get index="+index);

        String url = "https://developer.android.com/_static/0d76052693/images/android/touchicon-180.png?hl=ja";
        imageView = (ImageView) findViewById(R.id.imageView);
        imgAsyncTaskHttpRequest = new ImgAsyncTaskHttpRequest();
        imgAsyncTaskHttpRequest.setListener(createListener());
        imgAsyncTaskHttpRequest.execute(url);

        init();
        setAll(index);

    }

    private void init() {
        nameTextView = (TextView) findViewById(R.id.name_textView);
        nameKanaTextView = (TextView) findViewById(R.id.nameKana_textView);
        telTextView = (TextView) findViewById(R.id.tel_textView);
        addressTextView = (TextView) findViewById(R.id.address_textView);
        opentimeTextView = (TextView) findViewById(R.id.opentime_textView);
        howGoTextView = (TextView) findViewById(R.id.howGo_textView);
    }

    private void setAll(int n) {
        List<GnaviResultEntity> list = GnaviAPI.getList();
        nameTextView.setText(list.get(n).getName());
        nameKanaTextView.setText(list.get(n).getNameKana());
        telTextView.setText(list.get(n).getTel());
        addressTextView.setText(list.get(n).getAddress());
        opentimeTextView.setText(list.get(n).getOpentime());
        howGoTextView.setText(list.get(n).getHowGo());
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