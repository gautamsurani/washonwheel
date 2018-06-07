package com.washonwheel.android.Adapter;

import android.app.Activity;
import android.content.ClipData;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.washonwheel.android.Pojo.PackDataList;
import com.washonwheel.android.Pojo.PackService;
import com.washonwheel.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by welcome on 12-12-2017.
 */

public class PackDataAdapter extends RecyclerView.Adapter<PackDataAdapter.ViewHolder> {

    List<PackDataList> bean ;
    Activity context;
    private OnItemClickListener mOnItemClickListener;
    List<PackService> packServices;
    LinearLayoutManager mLayoutManager;
    CouponAdapter couponAdapter;

    RecyclerView.RecycledViewPool viewPool;

    interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListenersxdxsdx) {
        this.mOnItemClickListener = mItemClickListenersxdxsdx;
    }

    public PackDataAdapter(Activity context, List<PackDataList> bean) {
        this.context = context;
        this.bean = bean;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView rvCouponList;

        TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);

            rvCouponList = itemView.findViewById(R.id.rvCouponList);

            tvName = itemView.findViewById(R.id.tvName);
        }
    }

    @Override
    public PackDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pack_data_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PackDataAdapter.ViewHolder holder, final int position) {

        final PackDataList packDataList = bean.get(holder.getAdapterPosition());

        holder.tvName.setText(packDataList.getPackage_name());

        packServices = packDataList.getPackServices();

        mLayoutManager = new LinearLayoutManager(context);
        holder.rvCouponList.setLayoutManager(mLayoutManager);
        holder.rvCouponList.setHasFixedSize(true);
        couponAdapter = new CouponAdapter(context, packServices);
        holder.rvCouponList.setAdapter(couponAdapter);

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