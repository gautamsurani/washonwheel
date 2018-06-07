package com.washonwheel.android.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.washonwheel.android.Adapter.ProductDetailServiceAdapter;
import com.washonwheel.android.Pojo.BookDetailService;
import com.washonwheel.android.R;
import com.washonwheel.android.Util.AppConstant;
import com.washonwheel.android.Util.Global;
import com.washonwheel.android.Util.RequestMethod;
import com.washonwheel.android.Util.RestClient;
import com.washonwheel.android.Util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryDetailActivity extends AppCompatActivity {
    TextView tvTitle;
    Activity context;
    Global global;
    RecyclerView rvProductDetailList;
    String UserId, LeadId = "";
    ProgressDialog progressDialog;
    String resMessage = "", resCode = "";
    List<BookDetailService> bookDetailServices = new ArrayList<>();
    String leadID, serviceDATE, serviceTIME, discount_type, discount_msg, payment_type,
            subtotal, discount, wallet, grandtotal, name, email, phone, address, car_no, status;

    TextView tvDate, tvStatus, tvAmount, tvNumber, tvEmail, tvAddress, tvVehicleNo, tvSubTotal, tvDiscount, tvWowWallet,
            tvPayType, tvGrandTotal, tvTime, tvDType, tvDMsg;

    LinearLayoutManager mLayoutManager;

    ProductDetailServiceAdapter productDetailServiceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        context = this;
        global = new Global(context);

        init();

        tvDate = findViewById(R.id.tvDate);
        tvStatus = findViewById(R.id.tvStatus);
        tvAmount = findViewById(R.id.tvAmount);
        tvNumber = findViewById(R.id.tvNumber);
        tvEmail = findViewById(R.id.tvEmail);
        tvAddress = findViewById(R.id.tvAddress);
        tvVehicleNo = findViewById(R.id.tvVehicleNo);
        tvSubTotal = findViewById(R.id.tvSubTotal);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvWowWallet = findViewById(R.id.tvWowWallet);
        tvPayType = findViewById(R.id.tvPayType);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);
        tvTime = findViewById(R.id.tvTime);
        tvDType = findViewById(R.id.tvDType);
        tvDMsg = findViewById(R.id.tvDMsg);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            LeadId = bundle.getString("id");
        }

        UserId = Utils.getUserId(context);

        if (global.isNetworkAvailable()) {
            new GetDetail().execute();
        } else {
            retryInternet();
        }

        mLayoutManager = new LinearLayoutManager(context);
        rvProductDetailList.setLayoutManager(mLayoutManager);
        rvProductDetailList.setHasFixedSize(true);
        productDetailServiceAdapter = new ProductDetailServiceAdapter(context, bookDetailServices);
        rvProductDetailList.setAdapter(productDetailServiceAdapter);

    }

    @SuppressLint("StaticFieldLeak")
    private class GetDetail extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_LEAD_DETAIL + LeadId;

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
                Log.e("API", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("booking_detail");
                            if (jsonArray != null && jsonArray.length() != 0) {

                                JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                                leadID = jsonObjectList.getString("leadID");
                                serviceDATE = jsonObjectList.getString("serviceDATE");
                                serviceTIME = jsonObjectList.getString("serviceTIME");
                                discount_type = jsonObjectList.getString("discount_type");
                                discount_msg = jsonObjectList.getString("discount_msg");
                                payment_type = jsonObjectList.getString("payment_type");
                                subtotal = jsonObjectList.getString("subtotal");
                                discount = jsonObjectList.getString("discount");
                                wallet = jsonObjectList.getString("wallet");
                                grandtotal = jsonObjectList.getString("grandtotal");
                                status = jsonObjectList.getString("status");

                                JSONArray jsonArray2 = jsonObjectList.getJSONArray("user_detail");
                                if (jsonArray2 != null && jsonArray2.length() != 0) {
                                    JSONObject jsonObjectList1 = jsonArray2.getJSONObject(0);
                                    name = jsonObjectList1.getString("name");
                                    email = jsonObjectList1.getString("email");
                                    phone = jsonObjectList1.getString("phone");
                                    address = jsonObjectList1.getString("address");
                                    car_no = jsonObjectList1.getString("car_no");
                                }

                                JSONArray jsonArray1 = jsonObjectList.getJSONArray("services");
                                if (jsonArray1 != null && jsonArray1.length() != 0) {
                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                        JSONObject jsonObjectList1 = jsonArray1.getJSONObject(j);
                                        String sr = jsonObjectList1.getString("sr");
                                        String service_name = jsonObjectList1.getString("service_name");
                                        String service_price = jsonObjectList1.getString("service_price");
                                        bookDetailServices.add(new BookDetailService(sr, service_name, service_price));
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (resCode.equalsIgnoreCase("0")) {
                productDetailServiceAdapter.notifyDataSetChanged();
                tvDate.setText(serviceDATE);
                tvStatus.setText(status);
                tvAmount.setText(getResources().getString(R.string.Rs) + " " + grandtotal);
                tvNumber.setText(phone);
                tvEmail.setText(email);
                tvAddress.setText(address);
                tvVehicleNo.setText(car_no);
                tvSubTotal.setText(getResources().getString(R.string.Rs) + " " + subtotal);
                tvDiscount.setText("- " + getResources().getString(R.string.Rs) + " " + discount);
                tvWowWallet.setText("- " + getResources().getString(R.string.Rs) + " " + wallet);
                tvPayType.setText(payment_type);
                tvGrandTotal.setText(getResources().getString(R.string.Rs) + " " + grandtotal);
                tvTime.setText(serviceTIME);
                tvDMsg.setText(discount_msg);
                tvDType.setText(discount_type);
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        tvTitle = findViewById(R.id.toolbar_title);
        rvProductDetailList = findViewById(R.id.rvProductDetailList);
        tvTitle.setText("Booking History");
        Toolbar toolbar = findViewById(R.id.tool_bar);
        assert toolbar != null;
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);

    }

    public void retryInternet() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    new GetDetail().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }
}
