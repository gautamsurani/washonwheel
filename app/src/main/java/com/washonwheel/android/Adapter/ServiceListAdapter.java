package com.washonwheel.android.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.washonwheel.android.Activity.SelectServiceActivity;
import com.washonwheel.android.Pojo.ServiceList;
import com.washonwheel.android.R;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by welcome on 12-12-2017.
 */

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.ViewHolder> {

    List<ServiceList> bean = new ArrayList<>();
    Activity context;
    private OnItemClickListener mOnItemClickListener;

    interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListeners) {
        this.mOnItemClickListener = mItemClickListeners;
    }

    public ServiceListAdapter(Activity context, List<ServiceList> bean) {
        this.context = context;
        this.bean = bean;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPrice;

        CheckBox cbService;

        LinearLayout ll_Main;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            cbService = itemView.findViewById(R.id.cbService);
            ll_Main = itemView.findViewById(R.id.ll_Main);
        }
    }

    @Override
    public ServiceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_item, parent, false);

        return new ViewHolder(v);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ServiceListAdapter.ViewHolder holder, final int position) {
        final ServiceList newBean = bean.get(holder.getAdapterPosition());

        holder.tvName.setText(newBean.getService_name());
        holder.tvPrice.setText(context.getResources().getString(R.string.Rs) + " " + newBean.getService_price());
        holder.ll_Main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.cbService.isChecked()) {
                    holder.cbService.setChecked(false);
                } else {
                    holder.cbService.setChecked(true);
                }
            }
        });
        holder.cbService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    SelectServiceActivity.Amount = SelectServiceActivity.Amount + Integer.parseInt(newBean.getService_price());

                    SelectServiceActivity.tvTotalAmount.setText(context.getResources().getString(R.string.Rs) + " " + String.valueOf(SelectServiceActivity.Amount));

                    if (SelectServiceActivity.BookService.equals("")) {
                        SelectServiceActivity.BookService = newBean.getServiceID();
                    } else {
                        SelectServiceActivity.BookService = SelectServiceActivity.BookService + "," + newBean.getServiceID();
                    }
                } else {
                    SelectServiceActivity.Amount = SelectServiceActivity.Amount - Integer.parseInt(newBean.getService_price());
                    SelectServiceActivity.tvTotalAmount.setText(context.getResources().getString(R.string.Rs) + " " + String.valueOf(SelectServiceActivity.Amount));

                    if (SelectServiceActivity.BookService.contains(",")) {
                        SelectServiceActivity.BookService =
                                SelectServiceActivity.BookService.replace("," + newBean.getServiceID(), "");
                    } else {
                        SelectServiceActivity.BookService =
                                SelectServiceActivity.BookService.replace(newBean.getServiceID(), "");
                    }
                }
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