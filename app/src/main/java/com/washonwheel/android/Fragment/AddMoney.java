package com.washonwheel.android.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.washonwheel.android.R;
import com.washonwheel.android.Util.AppConstant;
import com.washonwheel.android.Util.RequestMethod;
import com.washonwheel.android.Util.RestClient;
import com.washonwheel.android.Util.Utils;

import org.json.JSONObject;

public class AddMoney extends Fragment {
    TextView tvTitle;

    EditText etAmount;

    Button btnAddMoney;

    Activity context;

    ProgressDialog progressDialog;

    String strUserId,strMobile,resMessage = "",resCode = "";

    public AddMoney() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_money, container, false);

        context = getActivity();

        strUserId = Utils.getUserId(context);
        strMobile = Utils.getMobileNo(context);

        init(v);

        btnAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddWalletAsync mAddWalletMoney = new AddWalletAsync();
                mAddWalletMoney.execute();
            }
        });

        return v;
    }

    @SuppressLint("StaticFieldLeak")
    private class AddWalletAsync extends AsyncTask<String, Void, String> {
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

            String strAPI =  AppConstant.API_ABOUT;

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
                            JSONObject jsonObject = jsonObjectList.getJSONObject("about");

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

            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void init(View view) {
        tvTitle = getActivity().findViewById(R.id.toolbar_title);
        etAmount = view.findViewById(R.id.etAmount);
        btnAddMoney = view.findViewById(R.id.btnAddMoney);
        tvTitle.setText("Wallet");
    }
}
