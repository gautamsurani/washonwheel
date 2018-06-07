package com.washonwheel.android.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.washonwheel.android.Pojo.BookDetailService;
import com.washonwheel.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by welcome on 12-12-2017.
 */

public class ProductDetailServiceAdapter extends RecyclerView.Adapter<ProductDetailServiceAdapter.ViewHolder> {

    List<BookDetailService> bean = new ArrayList<>();
    Activity context;
    private OnItemClickListener mOnItemClickListener;

    interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListenersxdxsdx) {
        this.mOnItemClickListener = mItemClickListenersxdxsdx;
    }

    public ProductDetailServiceAdapter(Activity context, List<BookDetailService> bean) {
        this.context = context;
        this.bean = bean;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPrice;

        LinearLayout ll_Main;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ll_Main = itemView.findViewById(R.id.ll_Main);
        }
    }

    @Override
    public ProductDetailServiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_detail_service_item, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ProductDetailServiceAdapter.ViewHolder holder, final int position) {
        final BookDetailService newbean = bean.get(holder.getAdapterPosition());

        /*ScaleAnimation fade_in = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(500);     // animation duration in milliseconds
        fade_in.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
        holder.ll_Main.startAnimation(fade_in);*/

        holder.tvName.setText(newbean.getService_name());
        holder.tvPrice.setText(context.getResources().getString(R.string.Rs) + " " + newbean.getService_price());

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