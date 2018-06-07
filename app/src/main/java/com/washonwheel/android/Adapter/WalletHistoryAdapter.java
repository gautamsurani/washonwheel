package com.washonwheel.android.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.washonwheel.android.Pojo.TransactionData;
import com.washonwheel.android.R;

import java.util.List;


public class WalletHistoryAdapter extends RecyclerView.Adapter<WalletHistoryAdapter.ViewHolder> {

    Context context;
    Activity main;
    List<TransactionData> listData;
    LayoutInflater inflater;
    private OnItemClickListener mOnItemClickListener;


    public WalletHistoryAdapter(Activity activity, Context context, List<TransactionData> bean) {

        this.main = activity;
        this.listData = bean;
        this.context = context;
        this.inflater = (LayoutInflater.from(context));


    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public WalletHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wallethistory, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final WalletHistoryAdapter.ViewHolder holder, final int position) {
        final TransactionData bean = listData.get(position);

        holder.tvTransactiondate.setText(bean.getTransactionDate());
        holder.tvTransactionTitle.setText(bean.getRemark());

        if (bean.getType().equalsIgnoreCase("blue")) {
            holder.tvTransactionAmount.setText(bean.getSymbol() + " " + context.getResources().getString(R.string.Rs) + " " + bean.getAmount());
            holder.tvTransactionAmount.setTextColor(Color.GREEN);
            Glide.with(context)
                    .load(R.drawable.ic_bluedot)
                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.leogo)
                    .into(holder.imgTransactionStatus);
        } else {
            holder.tvTransactionAmount.setText(bean.getSymbol() + " " + context.getResources().getString(R.string.Rs) + " " + bean.getAmount());
            holder.tvTransactionAmount.setTextColor(ContextCompat.getColor(context, R.color.red));
            Glide.with(context)
                    .load(R.drawable.ic_reddot)
                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.leogo)
                    .into(holder.imgTransactionStatus);
        }

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTransactiondate, tvTransactionTitle, tvTransactionAmount;
        ImageView imgTransactionStatus;

        public ViewHolder(View v) {
            super(v);
            tvTransactiondate = v.findViewById(R.id.tvTransactiondate);
            tvTransactionTitle = v.findViewById(R.id.tvTransactionTitle);
            tvTransactionAmount = v.findViewById(R.id.tvTransactionAmount);
            imgTransactionStatus = v.findViewById(R.id.imgTransactionStatus);

        }

    }

}







