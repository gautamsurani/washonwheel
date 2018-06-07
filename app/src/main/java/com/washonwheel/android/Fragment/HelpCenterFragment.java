package com.washonwheel.android.Fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.washonwheel.android.R;
import com.washonwheel.android.Util.AppConstant;
import com.washonwheel.android.Util.AppPersistance;
import com.washonwheel.android.Util.AppPreference;
import com.washonwheel.android.Util.Global;
import com.washonwheel.android.Util.RequestMethod;
import com.washonwheel.android.Util.RestClient;
import com.washonwheel.android.Util.Utils;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpCenterFragment extends Fragment {

    View view;
    TextView tvtitle;
    EditText etYourName, etEmail, etPhone, etMsg;
    Button btnSubmit;
    Activity context;
    Global global;
    String YourName, Email, Phone, Msg, UserId, resMessage, resCode;
    ProgressDialog progressDialog;

    public HelpCenterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_help_center, container, false);

        context = getActivity();
        global = new Global(context);

        init();

        YourName = Utils.getUserName(context);
        Email = Utils.getEmail(context);
        Phone = Utils.getMobileNo(context);

        etYourName.setText(YourName);
        etEmail.setText(Email);
        etPhone.setText(Phone);

        UserId = AppPreference.getPreference(context, AppPersistance.keys.USER_ID);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YourName = etYourName.getText().toString().trim();
                Email = etEmail.getText().toString().trim();
                Phone = etPhone.getText().toString().trim();
                Msg = etMsg.getText().toString().trim();
                if (YourName.isEmpty()) {
                    Toast.makeText(context, "Enter Your Name", Toast.LENGTH_SHORT).show();
                } else if (Phone.isEmpty()) {
                    Toast.makeText(context, "Enter Mobile No", Toast.LENGTH_SHORT).show();
                } else if (Msg.isEmpty()) {
                    Toast.makeText(context, "Enter Message", Toast.LENGTH_SHORT).show();
                } else {
                    if (global.isNetworkAvailable()) {
                        new InsertHelp().execute();
                    } else {
                        retryInternet();
                    }
                }
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class InsertHelp extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_INSERT_HELP + YourName
                    + "&email=" + Email
                    + "&phone=" + Phone
                    + "&subject=" + "Test"
                    + "&message=" + Msg;

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
            progressDialog.dismiss();
            if (resCode.equalsIgnoreCase("0")) {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            } else {
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
                    new InsertHelp().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        tvtitle = getActivity().findViewById(R.id.toolbar_title);
        etYourName = view.findViewById(R.id.etYourName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etMsg = view.findViewById(R.id.etMsg);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        tvtitle.setText("Help Center");
    }
}
