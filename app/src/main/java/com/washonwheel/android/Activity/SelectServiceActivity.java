package com.washonwheel.android.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
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

import com.washonwheel.android.Adapter.ServiceListAdapter;
import com.washonwheel.android.Pojo.ServiceList;
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

public class SelectServiceActivity extends AppCompatActivity {

    RecyclerView rvServiceList;

    TextView tvTitle, btnContinue;

    Activity context;

    Global global;

    List<ServiceList> serviceLists = new ArrayList<>();

    ServiceListAdapter serviceListAdapter;

    String UserId = "", resMessage = "", resCode = "", ModelType = "";

    ProgressDialog progressDialog;

    LinearLayoutManager mLayoutManager;

    @SuppressLint("StaticFieldLeak")
    public static TextView tvTotalAmount;

    public static String BookService = "";

    public static int Amount = 0;

    String Date, Name, MobileNo, Address, VehicleNo, CityId, TimeSlot;

    String Latitude = "", Longitude = "";

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_service);

        context = this;

        global = new Global(context);

        Amount = 0;
        BookService = "";

        init();

        UserId = Utils.getUserId(context);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ModelType = bundle.getString("ModelType");
            Date = bundle.getString("Date");
            Name = bundle.getString("Name");
            MobileNo = bundle.getString("MobileNo");
            Address = bundle.getString("Address");
            VehicleNo = bundle.getString("VehicleNo");
            CityId = bundle.getString("CityId");
            TimeSlot = bundle.getString("TimeSlot");
            Latitude = bundle.getString("Latitude");
            Longitude = bundle.getString("Longitude");
        }

        mLayoutManager = new LinearLayoutManager(context);
        rvServiceList.setLayoutManager(mLayoutManager);
        rvServiceList.setHasFixedSize(true);
        serviceListAdapter = new ServiceListAdapter(context, serviceLists);
        rvServiceList.setAdapter(serviceListAdapter);

        tvTotalAmount.setText(getResources().getString(R.string.Rs) + " 0");

        if (global.isNetworkAvailable()) {
            new GetServiceList().execute();
        } else {
            retryInternet();
        }

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Amount == 0 || BookService.equals("")) {
                    Toast.makeText(context, "Please select any service", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(SelectServiceActivity.this, CheckoutActivity.class);
                    intent.putExtra("ModelType", ModelType);
                    intent.putExtra("Date", Date);
                    intent.putExtra("Name", Name);
                    intent.putExtra("MobileNo", MobileNo);
                    intent.putExtra("Address", Address);
                    intent.putExtra("VehicleNo", VehicleNo);
                    intent.putExtra("CityId", CityId);
                    intent.putExtra("TimeSlot", TimeSlot);
                    intent.putExtra("Latitude", Latitude);
                    intent.putExtra("Longitude", Longitude);
                    intent.putExtra("Amount", String.valueOf(SelectServiceActivity.Amount));
                    intent.putExtra("BookService", SelectServiceActivity.BookService);
                    startActivity(intent);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        rvServiceList = findViewById(R.id.rvServiceList);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnContinue = findViewById(R.id.btnContinue);
        tvTitle = findViewById(R.id.toolbar_title);
        tvTitle.setText("Select Service");
        Toolbar toolbar = findViewById(R.id.tool_bar);
        assert toolbar != null;
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetServiceList extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_SERVICE_LIST + ModelType + "&userID=" + UserId;

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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("service_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        ServiceList serviceList = new ServiceList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        serviceList.setServiceID(jsonObjectList.getString("serviceID"));
                                        serviceList.setServicepriceID(jsonObjectList.getString("servicepriceID"));
                                        serviceList.setService_name(jsonObjectList.getString("service_name"));
                                        serviceList.setService_price(jsonObjectList.getString("service_price"));
                                        serviceLists.add(serviceList);
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

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (resCode.equalsIgnoreCase("0")) {
                serviceListAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
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
        Amount = 0;
        BookService = "";
        finish();
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
                    new GetServiceList().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }
}
