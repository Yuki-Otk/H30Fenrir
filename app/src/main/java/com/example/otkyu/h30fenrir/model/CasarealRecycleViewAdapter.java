package com.example.otkyu.h30fenrir.model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.otkyu.h30fenrir.R;

import java.util.List;

/**
 * Created by YukiOtake on 2018/01/27 027.
 */

public class CasarealRecycleViewAdapter extends RecyclerView.Adapter<CasarealViewHolder> {
    private List<GnaviResultEntity> list;

    public CasarealRecycleViewAdapter() {
        this.list=GnaviAPI.getList();
    }
    @Override
    public CasarealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.format_row, parent,false);
        CasarealViewHolder vh = new CasarealViewHolder(inflate);
        return vh;
    }

    @Override
    public void onBindViewHolder(CasarealViewHolder holder, int position) {
        holder.titleView.setText(list.get(position).getName());
        holder.detailView.setText(list.get(position).getNameKana());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
