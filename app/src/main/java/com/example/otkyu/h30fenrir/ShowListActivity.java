package com.example.otkyu.h30fenrir;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.otkyu.h30fenrir.model.CasarealRecycleViewAdapter;
import com.example.otkyu.h30fenrir.model.GnaviAPI;
import com.example.otkyu.h30fenrir.model.GnaviResultEntity;

import java.util.List;

/**
 * Created by YukiOtake on 2018/01/24 024.
 * src:https://akira-watson.com/android/activity-2.html
 * src:https://qiita.com/so-ma1221/items/d1b84bf764bf82fe1ac3
 */

public class ShowListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_show);

        Button returnButton = (Button) findViewById(R.id.backHome_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayout linearLayout =(LinearLayout) findViewById(R.id.shop_linearLayout);
        Button backPageButton = (Button) findViewById(R.id.backPage_button);
        Button nextPageButton=(Button)findViewById(R.id.nextPage_button);
        final List<GnaviResultEntity> list = GnaviAPI.getList();
        System.out.println("list size=" + list.size());
//        for (int i = 0; i < list.size(); i++) {
//            System.out.println("name is=" + list.get(i).getName());
//        }
        int total=GnaviAPI.getTotalNum(),page=GnaviAPI.getPageNum(),dataNum=GnaviAPI.getDataNum();
        String resultStr = "合計" + total + "件\t" + page + "ページ目(" + (dataNum*page-dataNum)+"～"+dataNum*page + "件表示)";
        //backPageButtonの有効化・無効化
        if(GnaviAPI.getPageNum()==1){
            backPageButton.setEnabled(false);
        }
        else{
            backPageButton.setEnabled(true);
        }
        //nextPageButtonの有効化・無効化
//        System.out.println("total");
        double temp=Math.ceil((double) total/dataNum);
        int pageMax= (int) temp;
        System.out.println("pageMax="+pageMax);
        if (page==pageMax){
            nextPageButton.setEnabled(false);
        }
        else{
            nextPageButton.setEnabled(true);
        }


        //layout
        TextView resultTextView = (TextView) findViewById(R.id.result_textView);
        resultTextView.setText(resultStr);
        RecyclerView rv = (RecyclerView) findViewById(R.id.casareal_recyclerView);
        CasarealRecycleViewAdapter adapter = new CasarealRecycleViewAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(ShowListActivity.this, String.valueOf(list.get(view.getId()).getName()), Toast.LENGTH_SHORT).show();
                int index = view.getId();
//                System.out.println("index=" + index);
                Intent intent = new Intent(getApplication(), ShowDetailsActivity.class);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        });
    }
}
