package com.washonwheel.android.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.washonwheel.android.R;
import com.washonwheel.android.Util.AppConstant;
import com.washonwheel.android.Util.AppPersistance;
import com.washonwheel.android.Util.AppPreference;
import com.washonwheel.android.Util.Global;
import com.washonwheel.android.Util.RequestMethod;
import com.washonwheel.android.Util.RestClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends Activity implements View.OnClickListener {

    private Button btnSignIn, btnSignInWithFB;

    TextView btnSignUp, tvForget;

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    EditText etMobileNo, etPassword;

    String MobileNo = "", Password = "";

    Global global;

    Context context;

    ProgressDialog progressDialog;

    String resMessage = "", resCode = "";

    String userID = "", name = "", email = "", phone = "", UserId = "";

    String full_name = "";

    String strEmail = "";

    String strFbId = "";

    CallbackManager callbackManager;

    TextView showHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);
        context = this;
        if (isLogin()) {
            startActivity(new Intent(context, MainActivity.class));
            finish();
        }
        global = new Global(context);
        init();
        askPermission();

        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        tvForget.setOnClickListener(this);
        btnSignInWithFB.setOnClickListener(this);
        showHide.setOnClickListener(this);
    }

    private boolean isLogin() {
        UserId = AppPreference.getPreference(context, AppPersistance.keys.USER_ID);
        return UserId != null && !UserId.isEmpty();
    }

    private void init() {
        showHide = findViewById(R.id.showhide);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignInWithFB = findViewById(R.id.btnSignInWithFB);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvForget = findViewById(R.id.tvForget);
        etMobileNo = findViewById(R.id.etMobileNo);
        etPassword = findViewById(R.id.etPassword);
    }

    private void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission();
        }
    }

    public void GetFbData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                if (jsonObject != null) {
                    new LongOperation(jsonObject).execute("");
                } else if (graphResponse.getError() != null) {
                    switch (graphResponse.getError().getCategory()) {
                        case LOGIN_RECOVERABLE:
                            break;
                        case TRANSIENT:
                            break;
                        case OTHER:
                            break;
                    }
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @SuppressLint("StaticFieldLeak")
    private class LongOperation extends AsyncTask<String, Void, String> {

        JSONObject objUser;
        JSONObject jsonObjectList;
        ProgressDialog loading1;

        LongOperation(JSONObject jsonObject) {
            objUser = jsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                loading1 = new ProgressDialog(LoginActivity.this);
                loading1.show();
                loading1.setMessage("Please wait..");
                loading1.setCancelable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                full_name = objUser.getString("name");
                strFbId = objUser.getString("id");
                if (objUser.has("email")) {
                    if (objUser.getString("email") != null) {
                        strEmail = objUser.getString("email");
                        Log.e("Email", "StrEmail:-" + strEmail);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String strLoginURL = AppConstant.API_LOGIN_WITH_FB + strEmail
                    + "&suid=" + strFbId;

            Log.e("strLoginURL ", strLoginURL);
            try {
                RestClient restClient = new RestClient(strLoginURL);
                try {
                    restClient.Execute(RequestMethod.GET);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String VerifyUser = restClient.getResponse();
                if (VerifyUser != null && VerifyUser.length() != 0) {
                    jsonObjectList = new JSONObject(VerifyUser);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        Log.e("resMessage ", resMessage);
                        Log.e("resCode ", resCode);
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("customer_detail");
                            JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                            userID = jsonObjectList.getString("userID");
                            name = jsonObjectList.getString("name");
                            email = jsonObjectList.getString("email");
                            phone = jsonObjectList.getString("phone");
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
            super.onPostExecute(s);
            if (loading1.isShowing() && loading1 != null) {
                loading1.dismiss();
                if (resCode.equalsIgnoreCase("0")) {
                    Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                    AppPreference.setPreference(context, AppPersistance.keys.USER_ID, userID);
                    AppPreference.setPreference(context, AppPersistance.keys.USER_NAME, name);
                    AppPreference.setPreference(context, AppPersistance.keys.USER_EMAIL, email);
                    AppPreference.setPreference(context, AppPersistance.keys.USER_NUMBER, phone);
                    Intent i = new Intent(context, MainActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    finish();
                } else {
                    Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                    Intent it = new Intent(LoginActivity.this, SignupActivity.class);
                    it.putExtra("email", strEmail);
                    startActivity(it);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (v == btnSignUp) {
            Intent i = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        } else if (v == btnSignIn) {
            MobileNo = etMobileNo.getText().toString().trim();
            Password = etPassword.getText().toString().trim();
            if (MobileNo.isEmpty()) {
                Toast.makeText(context, "Enter Mobile No", Toast.LENGTH_SHORT).show();
            } else if (Password.isEmpty()) {
                Toast.makeText(context, "Enter Password", Toast.LENGTH_SHORT).show();
            } else {
                if (global.isNetworkAvailable()) {
                    new Login().execute();
                } else {
                    retryInternet();
                }
            }
        } else if (v == tvForget) {
            Intent i = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        } else if (v == btnSignInWithFB) {
            try {
                LoginManager.getInstance().logOut();
            } catch (Exception e) {
                e.printStackTrace();
            }
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_birthday"));
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    if (global.isNetworkAvailable()) {
                        if (loginResult.getAccessToken() != null) {
                            GetFbData();
                        }
                    } else {
                        retryInternet1();
                    }
                }

                @Override
                public void onCancel() {
                    // App code
                    Toast.makeText(LoginActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException exception) {
                    exception.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Something wrong happen..!!", Toast.LENGTH_SHORT).show();
                    Log.e("facebook", "fbEx__" + exception.getMessage());
                }
            });
        } else if (v == showHide) {
            if (showHide.getText().equals("Hide")) {
                showHide.setText("Show");
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else if (showHide.getText().equals("Show")) {
                showHide.setText("Hide");
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Login extends AsyncTask<String, Void, String> {
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
            String strLogin = AppConstant.API_LOGIN + MobileNo
                    + "&password=" + Password;

            String strLoginTrim = strLogin.replaceAll(" ", "%20");
            Log.d("strLoginTrim", strLoginTrim);
            try {
                RestClient restClient = new RestClient(strLogin);
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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("customer_detail");
                            JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                            if (jsonObjectList != null && jsonObjectList.length() != 0) {
                                userID = jsonObjectList.getString("userID");
                                name = jsonObjectList.getString("name");
                                email = jsonObjectList.getString("email");
                                phone = jsonObjectList.getString("phone");

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
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                AppPreference.setPreference(context, AppPersistance.keys.USER_ID, userID);
                AppPreference.setPreference(context, AppPersistance.keys.USER_NAME, name);
                AppPreference.setPreference(context, AppPersistance.keys.USER_EMAIL, email);
                AppPreference.setPreference(context, AppPersistance.keys.USER_NUMBER, phone);
                Intent i = new Intent(context, MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void permission() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if (!addPermission(permissionsList, Manifest.permission.READ_SMS))
            permissionsNeeded.add("Read SMS");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write Contacts");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("Location");
        if (permissionsList.size() > 0) {
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            return shouldShowRequestPermissionRationale(permission);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.RECEIVE_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }
                if (perms.get(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                    new Login().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    public void retryInternet1() {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.isNetworkAvailable()) {
                    dialog.dismiss();
                    GetFbData();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.nonetwork, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
