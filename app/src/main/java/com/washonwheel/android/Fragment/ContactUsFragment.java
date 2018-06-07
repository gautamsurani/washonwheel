package com.washonwheel.android.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.washonwheel.android.R;
import com.washonwheel.android.Util.AppConstant;
import com.washonwheel.android.Util.AppPersistance;
import com.washonwheel.android.Util.AppPreference;
import com.washonwheel.android.Util.Global;
import com.washonwheel.android.Util.RequestMethod;
import com.washonwheel.android.Util.RestClient;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUsFragment extends Fragment {

    View view;
    TextView tvTitle, tvAddress, tvWebSite, tvPhone, tvCall, tvEmail;
    Activity context;
    Global global;
    String UserId = "", resMessage = "", resCode = "", address = "", email = "", phone = "", website = "", call = "", message = "";
    ProgressDialog progressDialog;
    public static final int REQUEST_ID = 55;
    Button btnCallNow;

    public ContactUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contact_us, container, false);

        context = getActivity();

        global = new Global(context);

        UserId = AppPreference.getPreference(context, AppPersistance.keys.USER_ID);

        init();

        if (global.isNetworkAvailable()) {
            new GetContactData().execute();
        } else {
            retryInternet();
        }

        btnCallNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CALL_PHONE},
                            REQUEST_ID);
                } else {

                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + call));
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class GetContactData extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_GET_CONTACT_US + UserId;

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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("contact");
                            {
                                JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                                if (jsonObjectList != null && jsonObjectList.length() != 0) {
                                    address = jsonObjectList.getString("address_1");
                                    email = jsonObjectList.getString("email");
                                    phone = jsonObjectList.getString("phone");
                                    website = jsonObjectList.getString("website");
                                    call = jsonObjectList.getString("call");
                                    message = jsonObjectList.getString("message");
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
                tvAddress.setText(address);
                tvCall.setText(call);
                tvEmail.setText(email);
                tvPhone.setText(phone);
                tvWebSite.setText(website);
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        tvTitle = context.findViewById(R.id.toolbar_title);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvWebSite = view.findViewById(R.id.tvWebSite);
        btnCallNow = view.findViewById(R.id.btnCallnow);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvCall = view.findViewById(R.id.tvCall);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvTitle.setText("Contact Us");
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
                    new GetContactData().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }
}
