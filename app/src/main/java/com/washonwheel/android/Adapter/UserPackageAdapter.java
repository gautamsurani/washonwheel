package com.washonwheel.android.Adapter;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.washonwheel.android.Pojo.UserPackageList;
import com.washonwheel.android.Pojo.UserPackageServiceList;
import com.washonwheel.android.R;
import com.washonwheel.android.Util.SpacesItemDecorationGrid;

import java.util.List;

/**
 * Created by welcome on 12-12-2017.
 */

public class UserPackageAdapter extends RecyclerView.Adapter<UserPackageAdapter.ViewHolder> {

    List<UserPackageList> bean;
    Activity context;
    private OnItemClickListener mOnItemClickListener;
    List<UserPackageServiceList> userPackageServiceLists;
    UserPackServiceAdapter userPackServiceAdapter;

    RecyclerView.RecycledViewPool viewPool;

    interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListenersxdxsdx) {
        this.mOnItemClickListener = mItemClickListenersxdxsdx;
    }

    public UserPackageAdapter(Activity context, List<UserPackageList> bean) {
        this.context = context;
        this.bean = bean;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView rvCouponList;

        TextView tvName, tvDate;

        ViewHolder(View itemView) {
            super(itemView);

            rvCouponList = itemView.findViewById(R.id.rvCouponList);

            tvName = itemView.findViewById(R.id.tvName);

            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }

    @Override
    public UserPackageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_package_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final UserPackageAdapter.ViewHolder holder, final int position) {

        final UserPackageList userPackageList = bean.get(holder.getAdapterPosition());

        holder.tvName.setText(userPackageList.getPackage_name());

        if (userPackageList.getPackage_expiry_date().equals("")) {
            holder.tvDate.setText("Expiry Date : NA");
        } else {
            holder.tvDate.setText("Expiry Date : " + userPackageList.getPackage_expiry_date());
        }

        userPackageServiceLists = userPackageList.getUserPackageServiceLists();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3, LinearLayoutManager.VERTICAL, false);
        holder.rvCouponList.setHasFixedSize(true);
        holder.rvCouponList.setLayoutManager(gridLayoutManager);
        holder.rvCouponList.addItemDecoration(new SpacesItemDecorationGrid(10));
        userPackServiceAdapter = new UserPackServiceAdapter(context, userPackageServiceLists);
        holder.rvCouponList.setAdapter(userPackServiceAdapter);

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