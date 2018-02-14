package com.example.otkyu.h30fenrir.view;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.otkyu.h30fenrir.R;
import com.example.otkyu.h30fenrir.asynchronous.api.GnaviAPI;
import com.example.otkyu.h30fenrir.asynchronous.img.ImgAsyncTaskHttpRequest;
import com.example.otkyu.h30fenrir.asynchronous.api.model.GnaviResultEntity;

import java.util.List;

/**
 * Created by YukiOtake on 2018/01/27 027.
 */

public class CasarealRecycleViewAdapter extends RecyclerView.Adapter<CasarealViewHolder> {
    private List<GnaviResultEntity> list;
    private View.OnClickListener listener;
    private ImgAsyncTaskHttpRequest imgAsyncTaskHttpRequest;

    public CasarealRecycleViewAdapter() {
        this.list = GnaviAPI.getGnaviResultEntityList();
    }

    @Override
    public CasarealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.format_row, parent, false);
        CasarealViewHolder vh = new CasarealViewHolder(inflate);
        return vh;
    }

    @Override
    public void onBindViewHolder(CasarealViewHolder holder, int position) {
        String name = list.get(position).getName();
        String nameKana = list.get(position).getNameKana();
        String genre = list.get(position).getGenre();
        String howGo = list.get(position).getHowGo();
        String[] img = list.get(position).getImg();
        String title = name + "(" + genre + ")";
        holder.titleView.setText(title);
        holder.detailView.setText(howGo);
        //img
//        String url="https://developer.android.com/_static/0d76052693/images/android/touchicon-180.png?hl=ja";
        String url = img[0];
        imgAsyncTaskHttpRequest = new ImgAsyncTaskHttpRequest();
        imgAsyncTaskHttpRequest.setListener(createListener(holder));
        imgAsyncTaskHttpRequest.execute(url);


        holder.linearLayout.setId(holder.getAdapterPosition());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
            }
        });
    }

    private ImgAsyncTaskHttpRequest.Listener createListener(final CasarealViewHolder holder) {
        return new ImgAsyncTaskHttpRequest.Listener() {
            @Override
            public void onSuccess(Bitmap bmp) {
                holder.imageView.setImageBitmap(bmp);
            }
        };
    }

    public void setOnItemClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
