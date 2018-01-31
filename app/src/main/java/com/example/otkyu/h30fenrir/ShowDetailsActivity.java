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
    private TextView nameTextView, genreTextView, telTextView, addressTextView, opentimeTextView, howGoTextView;
    private ImgAsyncTaskHttpRequest imgAsyncTaskHttpRequest;
    private ImageView imageView;
    private int index,count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_details_show);
        setContentView(R.layout.activity_details_show_scroll);//スクロールできるように変更

        Intent intent = getIntent();
        index = intent.getIntExtra("index", 0);
//        System.out.println("get index="+index);
        init();
        setAll(index,count);

        Button backButton = findViewById(R.id.back_button);//listに戻る
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button changeButton = findViewById(R.id.change_button);//画像を切り替える
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                setAll(index,count);
            }
        });

    }

    private void init() {
        nameTextView = (TextView) findViewById(R.id.name_textView);
        genreTextView = (TextView) findViewById(R.id.genre_textView);
        telTextView = (TextView) findViewById(R.id.tel_textView);
        addressTextView = (TextView) findViewById(R.id.address_textView);
        opentimeTextView = (TextView) findViewById(R.id.opentime_textView);
        howGoTextView = (TextView) findViewById(R.id.howGo_textView);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    private void setAll(int index,int count) {
        List<GnaviResultEntity> list = GnaviAPI.getList();
        String name=list.get(index).getName()+"("+list.get(index).getNameKana()+")";
        nameTextView.setText(name);
        genreTextView.setText(list.get(index).getGenre());
        telTextView.setText(list.get(index).getTel());
        addressTextView.setText(list.get(index).getAddress());
        opentimeTextView.setText(list.get(index).getOpentime());
        howGoTextView.setText(list.get(index).getHowGo());
        String[] temp = list.get(index).getImg();
        String url = temp[count%2];
        imgAsyncTaskHttpRequest = new ImgAsyncTaskHttpRequest();
        imgAsyncTaskHttpRequest.setListener(createListener());
        imgAsyncTaskHttpRequest.execute(url);
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