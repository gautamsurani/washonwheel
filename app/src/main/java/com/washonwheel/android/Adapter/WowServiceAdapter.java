package com.washonwheel.android.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.washonwheel.android.Activity.WowServiceDetailActivity;
import com.washonwheel.android.Pojo.WowServiceModel;
import com.washonwheel.android.R;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by welcome on 12-12-2017.
 */

public class WowServiceAdapter extends RecyclerView.Adapter<WowServiceAdapter.ViewHolder> {

    List<WowServiceModel> bean = new ArrayList<>();
    Activity context;
    private OnItemClickListener mOnItemClickListener;


    interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListenersxdxsdx) {
        this.mOnItemClickListener = mItemClickListenersxdxsdx;
    }

    public WowServiceAdapter(Activity context, List<WowServiceModel> bean) {

        this.context = context;
        this.bean = bean;


    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        ImageView ivServiceIMG;

        LinearLayout ll_Main;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivServiceIMG = itemView.findViewById(R.id.ivServiceIMG);
            ll_Main = itemView.findViewById(R.id.ll_Main);
        }
    }

    @Override
    public WowServiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.wow_service_item, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final WowServiceAdapter.ViewHolder holder, final int position) {
        final WowServiceModel newbean = bean.get(holder.getAdapterPosition());

        holder.tvName.setText(newbean.getName());

        Glide.with(context)
                .load(newbean.getImage())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.default_icon)
                .into(holder.ivServiceIMG);

        holder.ll_Main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WowServiceDetailActivity.class);
                intent.putExtra("ID", newbean.getID());
                context.startActivity(intent);
                context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    @Override
    public int getItemCount() {
        return bean.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}