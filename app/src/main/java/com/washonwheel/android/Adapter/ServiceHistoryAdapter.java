package com.washonwheel.android.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.washonwheel.android.Activity.HistoryDetailActivity;
import com.washonwheel.android.Pojo.ServiceHistoryModel;
import com.washonwheel.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by welcome on 12-12-2017.
 */

public class ServiceHistoryAdapter extends RecyclerView.Adapter<ServiceHistoryAdapter.ViewHolder> {

    List<ServiceHistoryModel> bean = new ArrayList<>();
    Activity context;
    private OnItemClickListener mOnItemClickListener;


    interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListenersxdxsdx) {
        this.mOnItemClickListener = mItemClickListenersxdxsdx;
    }

    public ServiceHistoryAdapter(Activity context, List<ServiceHistoryModel> bean) {

        this.context = context;
        this.bean = bean;


    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTextRad, tvTextGreen, tvAmount, tvDate, tvBookingId;

        LinearLayout main_content;

        ViewHolder(View itemView) {
            super(itemView);
            tvTextRad = itemView.findViewById(R.id.tvTextRad);
            tvTextGreen = itemView.findViewById(R.id.tvTextGreen);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            main_content = itemView.findViewById(R.id.main_content);
        }
    }

    @Override
    public ServiceHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_service_history, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ServiceHistoryAdapter.ViewHolder holder, final int position) {
        final ServiceHistoryModel newbean = bean.get(holder.getAdapterPosition());

        /*ScaleAnimation fade_in = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(500);     // animation duration in milliseconds
        fade_in.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
        holder.main_content.startAnimation(fade_in);*/

        holder.tvDate.setText(newbean.getService());
        holder.tvBookingId.setText(newbean.getLeadno());
        holder.tvAmount.setText(context.getResources().getString(R.string.Rs) + " " + newbean.getTotal());
        if (newbean.getStatus().equals("Canceled")) {
            holder.tvTextRad.setVisibility(View.VISIBLE);
            holder.tvTextGreen.setVisibility(View.GONE);
            holder.tvTextRad.setText(newbean.getStatus());
            holder.tvTextGreen.setText(newbean.getStatus());
        } else {
            holder.tvTextRad.setVisibility(View.GONE);
            holder.tvTextGreen.setVisibility(View.VISIBLE);
            holder.tvTextRad.setText(newbean.getStatus());
            holder.tvTextGreen.setText(newbean.getStatus());
        }
        holder.main_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HistoryDetailActivity.class);
                intent.putExtra("id", newbean.getLeadID());
                context.startActivity(intent);
                context.overridePendingTransition(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);
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