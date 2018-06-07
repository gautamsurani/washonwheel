package com.washonwheel.android.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.washonwheel.android.Pojo.VehicleList;
import com.washonwheel.android.R;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by Welcome on 25-01-2018.
 */

public class VehicleListAdapter extends RecyclerView.Adapter<VehicleListAdapter.ViewHolder> {

    private List<VehicleList> vehicleLists = new ArrayList<>();
    Activity context;
    OnItemClickListener mOnItemClickListener;
    private String Delete;

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListenersxdxsdx) {
        this.mOnItemClickListener = mItemClickListenersxdxsdx;
    }

    public VehicleListAdapter(Activity context, List<VehicleList> vehicleLists, String Delete) {
        this.context = context;
        this.vehicleLists = vehicleLists;
        this.Delete = Delete;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCarNo;

        ImageView ivDelete;

        LinearLayout ll_Main;

        ViewHolder(View itemView) {
            super(itemView);
            tvCarNo = itemView.findViewById(R.id.tvCarNo);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            ll_Main = itemView.findViewById(R.id.ll_Main);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_list_item, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final VehicleList vehicleList = vehicleLists.get(holder.getAdapterPosition());

        holder.tvCarNo.setText(vehicleList.getCar_no());

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(position, v, 2);
            }
        });

        if (Delete.equals("No")) {
            holder.ivDelete.setVisibility(View.GONE);
        }

        holder.ll_Main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(position, v, 3);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicleLists.size();
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