package com.washonwheel.android.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.washonwheel.android.Adapter.VehicleListAdapter;
import com.washonwheel.android.Pojo.VehicleList;
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

public class MyVehiclesActivity extends AppCompatActivity {

    TextView tvTitle;

    RecyclerView rvVehicles;

    TextView tvAddVehicles;

    Activity context;

    Global global;

    String UserId = "", resMessage = "", resCode = "";

    ProgressDialog progressDialog;

    List<VehicleList> vehicleLists = new ArrayList<>();

    VehicleListAdapter vehicleListAdapter;

    LinearLayoutManager mLayoutManager;

    String VNo1, VNo2, VNo3, VNo4;

    int selectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vehicles);

        context = this;

        global = new Global(context);

        initToolbar();

        initComp();

        UserId = Utils.getUserId(context);

        String Delete = "Yes";

        mLayoutManager = new LinearLayoutManager(context);
        rvVehicles.setLayoutManager(mLayoutManager);
        rvVehicles.setHasFixedSize(true);
        vehicleListAdapter = new VehicleListAdapter(context, vehicleLists, Delete);
        rvVehicles.setAdapter(vehicleListAdapter);

        if (global.isNetworkAvailable()) {
            new GetVehicleList().execute();
        } else {
            retryInternet();
        }

        tvAddVehicles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        vehicleListAdapter.setOnItemClickListener(new VehicleListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int which) {
                if (which == 2) {
                    selectedPosition = position;
                    new DeleteCar().execute();
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class DeleteCar extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_DELETE_CAR + vehicleLists.get(selectedPosition).getVehID();

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
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
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (resCode.equalsIgnoreCase("0")) {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                vehicleLists.clear();
                new MyVehiclesActivity.GetVehicleList().execute();
            } else {
                progressDialog.dismiss();
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openDialog() {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context, R.style.CustomAlertDialog);
            AlertDialog alertDialog = alertDialogBuilder.create();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams") final View alertLayout = inflater.inflate(R.layout.add_vehicle, null);
            final EditText etVNo1 = alertLayout.findViewById(R.id.etVNo1);
            final EditText etVNo2 = alertLayout.findViewById(R.id.etVNo2);
            final EditText etVNo3 = alertLayout.findViewById(R.id.etVNo3);
            final EditText etVNo4 = alertLayout.findViewById(R.id.etVNo4);
            TextView btnAddCar = alertLayout.findViewById(R.id.btnAddCar);

            alertDialogBuilder.setView(alertLayout);

            alertDialog = alertDialogBuilder.create();

            alertDialog.show();

            final AlertDialog finalAlertDialog = alertDialog;
            btnAddCar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VNo1 = etVNo1.getText().toString();
                    VNo2 = etVNo2.getText().toString();
                    VNo3 = etVNo3.getText().toString();
                    VNo4 = etVNo4.getText().toString();

                    if (etVNo1.getText().toString().equals("") || etVNo2.getText().toString().equals("") ||
                            etVNo3.getText().toString().equals("") || etVNo4.getText().toString().equals("")) {
                        Toast.makeText(context, "Please enter car number", Toast.LENGTH_SHORT).show();
                    } else {
                        finalAlertDialog.dismiss();
                        new AddCar().execute();
                    }
                }
            });

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alertDialog.getWindow().getAttributes());
            lp.width = Utils.convertDpToPixel(300, context);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            alertDialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AddCar extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_ADD_CAR + UserId
                    + "&veh_state_code=" + VNo1
                    + "&veh_city_code=" + VNo2
                    + "&veh_series=" + VNo3
                    + "&veh_no=" + VNo4;

            String strAPITrim = strAPI.replaceAll(" ", "%20");
            Log.d("strAPI", strAPITrim);
            try {
                RestClient restClient = new RestClient(strAPITrim);
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
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (resCode.equalsIgnoreCase("0")) {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                vehicleLists.clear();
                new GetVehicleList().execute();
            } else {
                progressDialog.dismiss();
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
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
                    new GetVehicleList().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetVehicleList extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!progressDialog.isShowing()) {
                progressDialog.show();
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_VEHICLE_LIST + UserId;

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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("vehicle_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        VehicleList vehicleList = new VehicleList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        vehicleList.setVehID(jsonObjectList.getString("vehID"));
                                        vehicleList.setCar_no(jsonObjectList.getString("car_no"));
                                        vehicleList.setVeh_state_code(jsonObjectList.getString("veh_state_code"));
                                        vehicleList.setVeh_city_code(jsonObjectList.getString("veh_city_code"));
                                        vehicleList.setVeh_series(jsonObjectList.getString("veh_series"));
                                        vehicleList.setVeh_no(jsonObjectList.getString("veh_no"));
                                        vehicleLists.add(vehicleList);
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
                vehicleListAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initComp() {
        progressDialog = new ProgressDialog(context);
        rvVehicles = findViewById(R.id.rvVehicles);
        tvAddVehicles = findViewById(R.id.tvAddVehicles);
    }

    @SuppressLint("SetTextI18n")
    private void initToolbar() {
        tvTitle = findViewById(R.id.toolbar_title);
        tvTitle.setText("My Vehicles");
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
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
