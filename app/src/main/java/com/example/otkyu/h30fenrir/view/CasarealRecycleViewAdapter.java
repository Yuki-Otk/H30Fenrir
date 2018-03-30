package com.example.otkyu.h30fenrir.view;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.otkyu.h30fenrir.R;
import com.example.otkyu.h30fenrir.asynchronous.api.GnaviAPI;
import com.example.otkyu.h30fenrir.asynchronous.img.ImgAsyncTaskHttpRequest;
import com.example.otkyu.h30fenrir.asynchronous.api.model.GnaviResultEntity;

import java.util.List;

/**
 * Created by YukiOtake on 2018/01/27 027.
 */

public class CasarealRecycleViewAdapter extends RecyclerView.Adapter<CasarealRecycleViewAdapter.CasarealViewHolder> {
    private List<GnaviResultEntity> list;
    private View.OnClickListener listener;
    private boolean modeFlag;
    private ImgAsyncTaskHttpRequest imgAsyncTaskHttpRequest;

    public CasarealRecycleViewAdapter() {//コンストラクタ
        list = getList();
        modeFlag=false;
    }


    @Override
    public CasarealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.format_row, parent, false);
        CasarealViewHolder casarealViewHolder = new CasarealViewHolder(inflate);
        return casarealViewHolder;
    }

    @Override
    public void onBindViewHolder(CasarealViewHolder holder, int position) {//listの表示設定
        String name = list.get(position).getName();
        String nameKana = list.get(position).getNameKana();
        String genre = list.get(position).getGenre();
        String howGo = list.get(position).getHowGo();
        String[] img = list.get(position).getImg();
        String title = name + "(" + genre + ")";
        holder.titleView.setText(title);
        holder.detailView.setText(howGo);
        //img
        String url = img[0];
        if (url != null) {//画像情報が登録されていないなら読み込まない
            if (!modeFlag) {//制限モードでなければ
                imgAsyncTaskHttpRequest = new ImgAsyncTaskHttpRequest();
                imgAsyncTaskHttpRequest.setListener(createListener(holder));
                imgAsyncTaskHttpRequest.execute(url);
            }
        }
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
            public void onSuccess(Bitmap bitmap, int index) {
                holder.imageView.setImageBitmap(doResize(bitmap));//画像をリサイズしてセット
            }
            private Bitmap doResize(Bitmap bitmap){//周りの画像に合わせてアスペクト比も無視してリサイズ
                Bitmap original=  ((BitmapDrawable)holder.imageView.getDrawable()).getBitmap();//現在登録されている画像を読み込む
                bitmap=Bitmap.createScaledBitmap(bitmap,original.getWidth(),original.getHeight(),false);//新しい画像を現在のアスペクト比に変換
                return bitmap;
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

    class CasarealViewHolder extends RecyclerView.ViewHolder {
        private TextView titleView;
        private TextView detailView;
        private LinearLayout linearLayout;
        private ImageView imageView;

        private CasarealViewHolder(View itemView) {
            super(itemView);
            titleView =  itemView.findViewById(R.id.title);
            detailView =  itemView.findViewById(R.id.detail);
            linearLayout =  itemView.findViewById(R.id.shop_linearLayout);
            imageView =  itemView.findViewById(R.id.imageView);
        }
    }

    private List<GnaviResultEntity> getList() {
        return list;
    }

    public void setList(List<GnaviResultEntity> list) {
        this.list = list;
    }
    public void setModeFlag(boolean modeFlag) {
        this.modeFlag = modeFlag;
    }
}