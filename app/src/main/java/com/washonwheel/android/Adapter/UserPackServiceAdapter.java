package com.washonwheel.android.Adapter;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.washonwheel.android.Pojo.UserPackageServiceList;
import com.washonwheel.android.R;

import java.util.ArrayList;
import java.util.List;


/*
 * Created by welcome on 12-12-2017.
 */

public class UserPackServiceAdapter extends RecyclerView.Adapter<UserPackServiceAdapter.ViewHolder> {

    private List<UserPackageServiceList> bean = new ArrayList<>();
    Activity context;
    private OnItemClickListener mOnItemClickListener;

    interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListenersxdxsdx) {
        this.mOnItemClickListener = mItemClickListenersxdxsdx;
    }

    UserPackServiceAdapter(Activity context, List<UserPackageServiceList> bean) {
        this.context = context;
        this.bean = bean;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvType;

        RelativeLayout ll_Main;

        LinearLayout llUsed;

        ViewHolder(View itemView) {
            super(itemView);

            ll_Main = itemView.findViewById(R.id.ll_Main);

            llUsed = itemView.findViewById(R.id.llUsed);

            tvName = itemView.findViewById(R.id.tvName);

            tvType = itemView.findViewById(R.id.tvType);

        }
    }

    @Override
    public UserPackServiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_pack_ser_item, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final UserPackServiceAdapter.ViewHolder holder, final int position) {
        final UserPackageServiceList userPackageServiceList = bean.get(holder.getAdapterPosition());

        /*ScaleAnimation fade_in = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(500);     // animation duration in milliseconds
        fade_in.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
        holder.ll_Main.startAnimation(fade_in);*/

        holder.tvName.setText(userPackageServiceList.getService_name());

        holder.tvType.setText(userPackageServiceList.getTypes());

        if (userPackageServiceList.getUse_status().equals("Yes")) {
            holder.llUsed.setVisibility(View.VISIBLE);
        } else if(userPackageServiceList.getUse_status().equals("No")){
            holder.llUsed.setVisibility(View.GONE);
        }

        holder.ll_Main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipIt(v);
            }
        });

    }

    private void flipIt(final View viewToFlip) {
        ObjectAnimator flip = ObjectAnimator.ofFloat(viewToFlip, "rotationX", 0f, 360f);
        flip.setDuration(1500);
        flip.start();
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