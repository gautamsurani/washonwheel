package com.washonwheel.android.Activity;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.washonwheel.android.Adapter.CitySelectAdapter;
import com.washonwheel.android.Pojo.CityList;
import com.washonwheel.android.R;
import com.washonwheel.android.Util.AppConstant;
import com.washonwheel.android.Util.AppPersistance;
import com.washonwheel.android.Util.AppPreference;
import com.washonwheel.android.Util.Global;
import com.washonwheel.android.Util.RequestMethod;
import com.washonwheel.android.Util.RestClient;
import com.washonwheel.android.Util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends Activity implements View.OnClickListener {
    private Button btnRegister;
    EditText etYourName, etEmail, etMobileNo, etPassword, etReferralCode;
    TextView tvCity;
    Global global;
    Activity context;
    boolean isProgressDialog = true;
    ProgressDialog progressDialog;
    List<CityList> cityLists = new ArrayList<>();
    String selectedCityId = "";
    Dialog dialog;
    RecyclerView rvSelectCity;
    CitySelectAdapter adaptercity;
    ArrayList<CityList> cityListResults = new ArrayList<>();
    String YourName = "", Email = "", MobileNo = "", Password = "", ReferralCode = "", City = "";
    String resMessage = "", resCode = "";
    String userID = "", name = "", email = "", phone = "";
    TextView tvTAndC;
    TextView showhide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = this;
        global = new Global(this);

        init();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            email = bundle.getString("email");
            etEmail.setText(email);
        }

        if (global.isNetworkAvailable()) {
            GetCity stateAsync = new GetCity();
            stateAsync.execute();

        } else {
            retryInternet();
        }

        btnRegister.setOnClickListener(this);
        tvCity.setOnClickListener(this);
        tvTAndC.setOnClickListener(this);
        showhide.setOnClickListener(this);
    }

    private void init() {
        progressDialog = new ProgressDialog(context);
        btnRegister = findViewById(R.id.btnRegister);
        etYourName = findViewById(R.id.etYourName);
        etEmail = findViewById(R.id.etEmail);
        etMobileNo = findViewById(R.id.etMobileNo);
        etPassword = findViewById(R.id.etPassword);
        etReferralCode = findViewById(R.id.etReferralCode);
        tvCity = findViewById(R.id.tvCity);
        tvTAndC = findViewById(R.id.tvTAndC);
        showhide = findViewById(R.id.showhide);
    }

    @Override
    public void onClick(View v) {
        if (v == btnRegister) {
            YourName = etYourName.getText().toString().trim();
            Email = etEmail.getText().toString().trim();
            MobileNo = etMobileNo.getText().toString().trim();
            Password = etPassword.getText().toString().trim();
            ReferralCode = etReferralCode.getText().toString().trim();
            City = tvCity.getText().toString().trim();

            if (YourName.isEmpty()) {
                Toast.makeText(context, "Enter your name", Toast.LENGTH_SHORT).show();
            } else if (MobileNo.isEmpty()) {
                Toast.makeText(context, "Enter mobile no", Toast.LENGTH_SHORT).show();
            } else if (Password.isEmpty()) {
                Toast.makeText(context, "Enter password", Toast.LENGTH_SHORT).show();
            } else {
                if (global.isNetworkAvailable()) {
                    new Register().execute();
                } else {
                    retryInternet1();
                }
            }

            /*Intent i = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);*/
        }
        if (v == tvCity) {
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            dialog.setContentView(R.layout.row_selectcity);
            TextView tvHeadername = dialog.findViewById(R.id.tvHeadername);
            final LinearLayout lyt_other_area = dialog.findViewById(R.id.lyt_other_area);
            ImageView imgClose = dialog.findViewById(R.id.imgClose);
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    dialog.dismiss();
                }
            });
            tvHeadername.setText("Choose City");
            final EditText etSelectedarea = dialog.findViewById(R.id.etSelectedCity);
            rvSelectCity = dialog.findViewById(R.id.rvSelectcity);
            RecyclerView.LayoutManager mLayoutManagermain = new LinearLayoutManager(context);
            rvSelectCity.setLayoutManager(mLayoutManagermain);
            rvSelectCity.setHasFixedSize(true);
            adaptercity = new CitySelectAdapter(context, cityLists);
            rvSelectCity.setAdapter(adaptercity);
            etSelectedarea.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    cityListResults.clear();
                    cityListResults = new ArrayList<>();
                    try {
                        for (CityList c : cityLists) {
                            if (c.getCityName().toLowerCase().contains(s.toString().toLowerCase())) {
                                cityListResults.add(c);
                            }
                        }
                        adaptercity = new CitySelectAdapter(context, cityListResults);
                        rvSelectCity.setAdapter(adaptercity);
                        adaptercity.setOnItemClickListener(new CitySelectAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position, View view, int which) {
                                if (which == 1) {
                                    Utils.hideKeyboard(context);
                                    tvCity.setText(cityListResults.get(position).getCityName());
                                    selectedCityId = cityListResults.get(position).getCityId();
                                    dialog.dismiss();
                                }
                            }
                        });


                        if (cityListResults.size() == 0) {

                            lyt_other_area.setVisibility(View.VISIBLE);
                            lyt_other_area.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    tvCity.setText("Other");
                                    selectedCityId = "";
                                    dialog.dismiss();
                                }
                            });
                            rvSelectCity.setVisibility(View.GONE);
                        } else {
                            rvSelectCity.setVisibility(View.VISIBLE);
                            lyt_other_area.setVisibility(View.GONE);
                        }

                    } catch (NullPointerException ne) {
                        ne.getMessage();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            adaptercity.setOnItemClickListener(new CitySelectAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, View view, int which) {

                    if (which == 1) {
                        Utils.hideKeyboard(context);
                        tvCity.setText(cityLists.get(position).getCityName());
                        selectedCityId = cityLists.get(position).getCityId();
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        }
        if (v == tvTAndC) {
      /*      startActivity(new Intent(SignupActivity.this, TermsAndConditionsActivity.class));
            overridePendingTransition(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);*/
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.washonwheel.com/terms-amp-conditions.html"));
            startActivity(browserIntent);
        }

        if (v == showhide) {
            if (showhide.getText().equals("Hide")) {
                showhide.setText("Show");
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else if (showhide.getText().equals("Show")) {
                showhide.setText("Hide");
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
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
                    new GetCity().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }

    public void retryInternet1() {
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
                    new Register().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }

    private class GetCity extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;
        String Message = "";
        String Code = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isProgressDialog) {
                isProgressDialog = false;
                progressDialog.show();
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                cityLists.clear();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String strProductFilterList = AppConstant.API_GET_CITY;
            Log.d("CityList", strProductFilterList);
            try {
                RestClient restClient = new RestClient(strProductFilterList);
                try {
                    restClient.Execute(RequestMethod.GET);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String ProductList = restClient.getResponse();
                Log.e("CityList", ProductList);

                if (ProductList != null && ProductList.length() != 0) {
                    jsonObjectList = new JSONObject(ProductList);
                    if (jsonObjectList.length() != 0) {
                        Message = jsonObjectList.getString("message");
                        Code = jsonObjectList.getString("msgcode");
                        if (Code.equalsIgnoreCase("0")) {
                            try {
                                JSONArray jsonArray = jsonObjectList.getJSONArray("city_list");
                                {
                                    if (jsonArray != null && jsonArray.length() != 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            CityList cityList = new CityList();
                                            JSONObject jsonObjectList = jsonArray.getJSONObject(i);

                                            cityList.setCityId(jsonObjectList.getString("cityID"));
                                            cityList.setCityName(jsonObjectList.getString("name"));

                                            cityLists.add(cityList);
                                        }
                                        tvCity.setText(cityLists.get(0).getCityName());
                                        selectedCityId = cityLists.get(0).getCityId();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
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
            super.onPostExecute(s);
            progressDialog.dismiss();

        }
    }

    private class Register extends AsyncTask<String, Void, String> {
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
            String strRegister = AppConstant.API_REGISTRATION + YourName
                    + "&semail=" + Email
                    + "&sphone=" + MobileNo
                    + "&spass=" + Password
                    + "&cityID=" + selectedCityId
                    + "&referral_code=" + ReferralCode;

            String strRegistertrim = strRegister.replaceAll(" ", "%20");
            Log.d("strRegistertrim", strRegistertrim);
            Log.d("strRegister", strRegister);
            try {
                RestClient restClient = new RestClient(strRegistertrim);
                try {
                    restClient.Execute(RequestMethod.POST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String Register = restClient.getResponse();
                Log.e("Register", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    resMessage = jsonObjectList.getString("message");
                    resCode = jsonObjectList.getString("msgcode");
                    if (jsonObjectList.length() != 0) {
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("customer_detail");
                            {
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
                AppPreference.setPreference(context, AppPersistance.keys.USER_ID, userID);
                AppPreference.setPreference(context, AppPersistance.keys.USER_NAME, name);
                AppPreference.setPreference(context, AppPersistance.keys.USER_EMAIL, email);
                AppPreference.setPreference(context, AppPersistance.keys.USER_NUMBER, phone);
                Intent i = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
