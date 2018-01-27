package com.example.otkyu.h30fenrir.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.otkyu.h30fenrir.R;

/**
 * Created by YukiOtake on 2018/01/27 027.
 */

public class CasarealViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView detailView;
    public CasarealViewHolder(View itemView) {
        super(itemView);
        titleView = (TextView) itemView.findViewById(R.id.title);
        detailView = (TextView) itemView.findViewById(R.id.detail);
    }
}
