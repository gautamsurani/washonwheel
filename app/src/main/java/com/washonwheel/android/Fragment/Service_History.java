package com.washonwheel.android.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.washonwheel.android.Adapter.ServiceHistoryAdapter;
import com.washonwheel.android.Pojo.ServiceHistoryModel;
import com.washonwheel.android.R;
import com.washonwheel.android.Util.AppConstant;
import com.washonwheel.android.Util.AppPersistance;
import com.washonwheel.android.Util.AppPreference;
import com.washonwheel.android.Util.Global;
import com.washonwheel.android.Util.RequestMethod;
import com.washonwheel.android.Util.RestClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Service_History extends Fragment {
    View view;

    Activity context;

    Global global;

    RecyclerView rvServiceHistory;

    LinearLayoutManager mLayoutManager;

    List<ServiceHistoryModel> serviceHistoryModels = new ArrayList<>();

    ServiceHistoryAdapter serviceHistoryAdapter;

    String UserId = "", resMessage = "", resCode = "";

    ProgressDialog progressDialog;

    public Service_History() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.service_history, container, false);

        context = getActivity();
        global = new Global(context);

        init(view);

        mLayoutManager = new LinearLayoutManager(context);
        rvServiceHistory.setLayoutManager(mLayoutManager);
        rvServiceHistory.setHasFixedSize(true);
        serviceHistoryAdapter = new ServiceHistoryAdapter(context, serviceHistoryModels);
        rvServiceHistory.setAdapter(serviceHistoryAdapter);

        UserId = AppPreference.getPreference(context, AppPersistance.keys.USER_ID);

        if (global.isNetworkAvailable()) {
            new GetServiceHistory().execute();
        } else {
            retryInternet();
        }

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class GetServiceHistory extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_GET_SERVICE_HISTORY + UserId;

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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("booking_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        ServiceHistoryModel serviceHistoryModel = new ServiceHistoryModel();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        serviceHistoryModel.setLeadID(jsonObjectList.getString("leadID"));
                                        serviceHistoryModel.setLeadno(jsonObjectList.getString("leadno"));
                                        serviceHistoryModel.setService(jsonObjectList.getString("service_date"));
                                        serviceHistoryModel.setCar(jsonObjectList.getString("car"));
                                        serviceHistoryModel.setPayment(jsonObjectList.getString("payment_type"));
                                        serviceHistoryModel.setTotal(jsonObjectList.getString("total"));
                                        serviceHistoryModel.setStatus(jsonObjectList.getString("status"));
                                        serviceHistoryModels.add(serviceHistoryModel);
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
                serviceHistoryAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void init(View view) {
        TextView tvTitle = getActivity().findViewById(R.id.toolbar_title);
        rvServiceHistory = view.findViewById(R.id.rvServiceHistory);
        tvTitle.setText("Service History");
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
                    new GetServiceHistory().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }

}
