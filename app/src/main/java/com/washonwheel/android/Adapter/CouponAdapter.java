package com.washonwheel.android.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.washonwheel.android.Activity.CheckoutActivity;
import com.washonwheel.android.Pojo.PackService;
import com.washonwheel.android.R;
import com.washonwheel.android.Util.AppConstant;
import com.washonwheel.android.Util.AppPersistance;
import com.washonwheel.android.Util.AppPreference;
import com.washonwheel.android.Util.RequestMethod;
import com.washonwheel.android.Util.RestClient;
import com.washonwheel.android.Util.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by welcome on 12-12-2017.
 */

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.ViewHolder> {

    List<PackService> bean = new ArrayList<>();
    Activity context;
    private OnItemClickListener mOnItemClickListener;
    String resMessage = "", resCode = "";
    ProgressDialog progressDialog;
    String pack_ser_id, UserId, Car_Type, BookService, final_discount, discount_service_id;

    interface OnItemClickListener {
        void onItemClick(int position, View view, int i);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListenersxdxsdx) {
        this.mOnItemClickListener = mItemClickListenersxdxsdx;
    }

    public CouponAdapter(Activity context, List<PackService> bean) {
        this.context = context;
        this.bean = bean;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        Button btnApply;

        LinearLayout ll_Main;

        ViewHolder(View itemView) {
            super(itemView);

            btnApply = itemView.findViewById(R.id.btnApply);

            tvName = itemView.findViewById(R.id.tvName);

            ll_Main = itemView.findViewById(R.id.ll_Main);

        }
    }

    @Override
    public CouponAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_item, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final CouponAdapter.ViewHolder holder, final int position) {
        final PackService packService = bean.get(holder.getAdapterPosition());

        /*ScaleAnimation fade_in = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(500);     // animation duration in milliseconds
        fade_in.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
        holder.ll_Main.startAnimation(fade_in);*/

        holder.tvName.setText(packService.getShort_desc());

        holder.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pack_ser_id = bean.get(position).getPack_ser_id();
                UserId = Utils.getUserId(context);
                Car_Type = AppPreference.getPreference(context, AppPersistance.keys.ModelType);
                BookService = AppPreference.getPreference(context, AppPersistance.keys.BookService);
                new GetCouponData().execute();
            }
        });

    }

    private class GetCouponData extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.show();
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_COUPON_DATA + UserId
                    + "&car_type=" + Car_Type
                    + "&book_service=" + BookService
                    + "&pack_ser_id=" + pack_ser_id;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPI);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String Register = restClient.getResponse();
                Log.e("Register", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");

                        if (resCode.equalsIgnoreCase("0")) {
                            final_discount = jsonObjectList.getString("final_discount");
                            discount_service_id = jsonObjectList.getString("discount_service_id");
                        } else {
                            final_discount = "0";
                            discount_service_id = "";
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();

            CheckoutActivity.final_discount = final_discount;
            CheckoutActivity.discount_service_id = discount_service_id;
            CheckoutActivity.Coupon_msg = resMessage;
            CheckoutActivity.Coupon_code = resCode;

            context.finish();

            if (resCode.equalsIgnoreCase("0")) {

            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
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