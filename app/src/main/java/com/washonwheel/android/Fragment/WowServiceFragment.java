package com.washonwheel.android.Fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.washonwheel.android.Adapter.WowServiceAdapter;
import com.washonwheel.android.Pojo.WowServiceModel;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class WowServiceFragment extends Fragment {

    View view;

    Activity context;

    Global global;

    RecyclerView rvWowService;

    String UserId = "", resMessage = "", resCode = "";

    ProgressDialog progressDialog;

    GridLayoutManager gridLayoutManager;

    List<WowServiceModel> wowServiceModels = new ArrayList<>();

    WowServiceAdapter wowServiceAdapter;

    public WowServiceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wow, container, false);

        context = getActivity();
        global = new Global(context);

        init(view);

        gridLayoutManager = new GridLayoutManager(context, 2);
        rvWowService.setLayoutManager(gridLayoutManager);
        rvWowService.setHasFixedSize(true);
        wowServiceAdapter = new WowServiceAdapter(context, wowServiceModels);
        rvWowService.setAdapter(wowServiceAdapter);

        UserId = AppPreference.getPreference(context, AppPersistance.keys.USER_ID);

        if (global.isNetworkAvailable()) {
            new GetWowService().execute();
        } else {
            retryInternet();
        }

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class GetWowService extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_WOW_SERVICE;

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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("wow_services");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        WowServiceModel wowServiceModel = new WowServiceModel();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        wowServiceModel.setID(jsonObjectList.getString("ID"));
                                        wowServiceModel.setName(jsonObjectList.getString("name"));
                                        wowServiceModel.setImage(jsonObjectList.getString("image"));
                                        wowServiceModels.add(wowServiceModel);
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
                wowServiceAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void init(View view) {
        TextView tvTitle = getActivity().findViewById(R.id.toolbar_title);
        rvWowService = view.findViewById(R.id.rvWowService);
        tvTitle.setText("WOW Service");
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
                    new GetWowService().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }
}
