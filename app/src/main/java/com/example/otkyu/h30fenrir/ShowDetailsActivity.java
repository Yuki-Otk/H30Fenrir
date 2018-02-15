package com.example.otkyu.h30fenrir;

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
    private String webUrl = null,data=null,imgUrl=null,telNum=null;

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
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
//                startActivity(intent);


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
        imgUrl=url;
        webUrl = list.get(index).getHomePage();
        telNum=list.get(index).getTel();
        data=list.get(index).getName()+"("+list.get(index).getNameKana()+")\n"+list.get(index).getHowGo()+"\n"+webUrl;

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
                String chooserTitle="共有する",subject="ぐるなび店舗情報";
                share(ShowDetailsActivity.this,chooserTitle,subject,data, Uri.parse(imgUrl));
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
        Uri uri=Uri.parse("tel:"+telNum);
        Intent intent=new Intent(Intent.ACTION_DIAL,uri);
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