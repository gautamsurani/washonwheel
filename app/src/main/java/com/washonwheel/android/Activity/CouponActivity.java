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

import com.washonwheel.android.Adapter.PackDataAdapter;
import com.washonwheel.android.Pojo.PackDataList;
import com.washonwheel.android.Pojo.PackService;
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

public class CouponActivity extends AppCompatActivity {

    TextView tvTitle;

    Activity context;

    Global global;

    ProgressDialog progressDialog;

    String UserId, car_type, book_service, resMessage = "", resCode = "";

    List<PackDataList> packDataLists = new ArrayList<>();

    RecyclerView rvCoupon;

    PackDataAdapter packDataAdapter;

    LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        context = this;
        global = new Global(context);
        init();

        rvCoupon = findViewById(R.id.rvCoupon);

        UserId = Utils.getUserId(context);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            car_type = bundle.getString("car_type");
            book_service = bundle.getString("book_service");
        }

        if (global.isNetworkAvailable()) {
            new GetCoupon().execute();
        } else {
            retryInternet();
        }

        mLayoutManager = new LinearLayoutManager(context);
        rvCoupon.setLayoutManager(mLayoutManager);
        rvCoupon.setHasFixedSize(true);
        packDataAdapter = new PackDataAdapter(context, packDataLists);
        rvCoupon.setAdapter(packDataAdapter);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetCoupon extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_COUPON + UserId
                    + "&car_type=" + car_type
                    + "&book_service=" + book_service;

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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("pack_data");
                            if (jsonArray != null && jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                    String Pack_id = jsonObjectList.getString("package_id");
                                    String Pack_Name = jsonObjectList.getString("package_name");
                                    List<PackService> packService = new ArrayList<>();
                                    JSONArray jsonArray1 = jsonObjectList.getJSONArray("pack_services");
                                    if (jsonArray1 != null && jsonArray1.length() != 0) {
                                        for (int j = 0; j < jsonArray1.length(); j++) {
                                            JSONObject jsonObjectList1 = jsonArray1.getJSONObject(j);
                                            String Pack_ser_id = jsonObjectList1.getString("pack_ser_id");
                                            String Short_desc = jsonObjectList1.getString("short_desc");
                                            packService.add(new PackService(Pack_ser_id, Short_desc));
                                        }
                                    }
                                    packDataLists.add(new PackDataList(Pack_id, Pack_Name, packService));
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

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (resCode.equalsIgnoreCase("0")) {
                packDataAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void init() {
        tvTitle = findViewById(R.id.toolbar_title);
        tvTitle.setText("Apply Coupons");
        Toolbar toolbar = findViewById(R.id.tool_bar);
        assert toolbar != null;
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            // Toast.makeText(MainCategoriesActivity.this, "BackWorking", Toast.LENGTH_SHORT).show();
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
                    new GetCoupon().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }
}
