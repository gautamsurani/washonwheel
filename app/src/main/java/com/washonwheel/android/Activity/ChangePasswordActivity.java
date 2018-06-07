package com.washonwheel.android.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.washonwheel.android.R;
import com.washonwheel.android.Util.AppConstant;
import com.washonwheel.android.Util.Global;
import com.washonwheel.android.Util.RequestMethod;
import com.washonwheel.android.Util.RestClient;
import com.washonwheel.android.Util.Utils;

import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity {

    TextView tvSubmit;

    EditText etNewPass, etOldPass, etCPassword;

    String txtNewPass = "", txtOldPass = "", txtMobileNo = "", UserId = "", resMessage = "", resCode = "", txtCPassword = "";

    TextView tvTitle;

    Activity context;

    Global global;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        context = this;
        global = new Global(context);
        init();
        initCmp();

        UserId = Utils.getUserId(context);

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtMobileNo = Utils.getMobileNo(context);
                txtOldPass = etOldPass.getText().toString();
                txtNewPass = etNewPass.getText().toString();
                txtCPassword = etCPassword.getText().toString();

                if (txtOldPass.equals("")) {
                    Utils.showToast(context, "Enter Old Password");
                } else if (txtNewPass.equals("")) {
                    Utils.showToast(context, "Enter New Password");
                } else if (!txtNewPass.equals(txtCPassword)) {
                    Utils.showToast(context, "Your password and confirmation password do not match.");
                } else {
                    if (global.isNetworkAvailable()) {
                        new ChangePassword().execute();
                    } else {
                        retryInternet();
                    }
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class ChangePassword extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_CHANGE_PASSWORD + UserId
                    + "&userPhone=" + txtMobileNo
                    + "&old_pass=" + txtOldPass
                    + "&new_pass=" + txtNewPass;

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
                onBackPressed();
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
                    new ChangePassword().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        tvTitle = findViewById(R.id.toolbar_title);
        tvTitle.setText("Change Password");
        Toolbar toolbar = findViewById(R.id.tool_bar);
        assert toolbar != null;
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
    }

    private void initCmp() {
        tvSubmit = findViewById(R.id.tvSubmit);
        etNewPass = findViewById(R.id.etNewPass);
        etOldPass = findViewById(R.id.etOldPass);
        etCPassword = findViewById(R.id.etCPassword);
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
}
