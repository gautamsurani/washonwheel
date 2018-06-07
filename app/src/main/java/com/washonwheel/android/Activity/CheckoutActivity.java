package com.washonwheel.android.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.washonwheel.android.R;
import com.washonwheel.android.Util.AppConstant;
import com.washonwheel.android.Util.AppPersistance;
import com.washonwheel.android.Util.AppPreference;
import com.washonwheel.android.Util.Global;
import com.washonwheel.android.Util.RequestMethod;
import com.washonwheel.android.Util.RestClient;
import com.washonwheel.android.Util.Utils;
import com.washonwheel.android.location.Location;
import com.washonwheel.android.location.LocationInterface;

import org.json.JSONArray;
import org.json.JSONObject;

public class CheckoutActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    String Date, Name, MobileNo, Address, VehicleNo, CityId, TimeSlot, ModelType, BookService, Amount, UserId;

    private TextView btnPayNow, tvCouponText;

    private LinearLayout ll_wallet, ll_coupon, view_coupon, ll_mem_coupon, view_mem_coupon, llCouponText, llApplyView;

    private CheckBox chk_wallet;

    private ImageView imgOpen;

    RadioGroup rgPayType;

    Activity context;

    Global global;

    TextView tvTitle;

    ProgressDialog progressDialog;

    String resMessage, resCode;

    String wallet_bal, cod, online, wallet, packages, resMessage1, resCode1;

    TextView tvWallet, tvAmount, tvCouponMsg, tvDiscount, tvGrandTotal, tvWowAmount;

    RadioButton rbCOD, rbOnline;

    Button btnClear, btnApply, btnClear1;

    boolean isWowWallet = false, isServiceCoupon = false;

    EditText etCoupon;

    public static String final_discount = "", discount_service_id = "", Coupon_msg = "", Coupon_code = "";

    float GrandTotal = 0;

    String CouponCode, txtPayType = "cod", txtWalletAmount = "";

    PayuHashes payuHashes;

    String userID, product_info, amount, first_name, email, trans_id, udf1, udf2, payu_key, payment_hash,
            vas_for_mobile_sdk_hash, payment_related_details_for_mobile_sdk_hash;

    String order_fail, order_success;

    LocationInterface locationInterface;

    Location location;

    public static final int REQUEST_ID = 2;

    double latitude = 0;

    double longitude = 0;

    private static final int REQUEST_CHECK_SETTINGS = 151;

    String Latitude = "", Longitude = "";

    @Override
    protected void onResume() {
        super.onResume();
        if (!Coupon_msg.equals("")) {
            isServiceCoupon = true;
            view_mem_coupon.setVisibility(View.VISIBLE);
            view_coupon.setVisibility(View.GONE);
            llCouponText.setVisibility(View.GONE);
            tvCouponMsg.setText(Coupon_msg);
            if (Coupon_code.equals("0")) {
                tvCouponMsg.setTextColor(Color.GREEN);
            } else {
                tvCouponMsg.setTextColor(Color.RED);
            }
        } else {
            view_mem_coupon.setVisibility(View.GONE);
        }
        setGrandTotal();
    }

    public void setTvCouponText() {
        if (!Coupon_msg.equals("")) {
            isServiceCoupon = false;
            view_mem_coupon.setVisibility(View.GONE);
            llCouponText.setVisibility(View.VISIBLE);
            llApplyView.setVisibility(View.GONE);
            tvCouponText.setText(Coupon_msg);
            if (Coupon_code.equals("0")) {
                tvCouponText.setTextColor(Color.GREEN);
            } else {
                tvCouponText.setTextColor(Color.RED);
            }
        } else {
            view_coupon.setVisibility(View.GONE);
            llCouponText.setVisibility(View.GONE);
        }
        setGrandTotal();
    }

    @SuppressLint("SetTextI18n")
    public void setGrandTotal() {

        float Amount1, Amount2, Amount3 = 0;

        tvAmount.setText(getResources().getString(R.string.Rs) + " " + Amount);

        Amount1 = Float.parseFloat(Amount);

        tvDiscount.setText(getResources().getString(R.string.Rs) + " " + String.valueOf(final_discount));

        Amount2 = Float.parseFloat(final_discount);

        if (isWowWallet) {
            float AmountForWal = Float.parseFloat(Amount) - Float.parseFloat(String.valueOf(final_discount));
            if (AmountForWal > Float.parseFloat(wallet_bal)) {
                tvWowAmount.setText(getResources().getString(R.string.Rs) + " " + wallet_bal);
                Amount3 = Float.parseFloat(wallet_bal);
            } else {
                float WoWRemainingAmount = Float.parseFloat(wallet_bal) - AmountForWal;
                tvWowAmount.setText(getResources().getString(R.string.Rs) + " " + String.valueOf(AmountForWal));
                Amount3 = AmountForWal;
            }
        } else {
            tvWowAmount.setText(getResources().getString(R.string.Rs) + " 0");
        }

        txtWalletAmount = String.valueOf(Amount3);

        GrandTotal = Amount1 - Amount2 - Amount3;

        tvGrandTotal.setText(getResources().getString(R.string.Rs) + " " + String.valueOf(GrandTotal));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chackout);
        Payu.setInstance(this);
        context = this;
        global = new Global(context);
        init();

        final_discount = "0";
        discount_service_id = "";
        Coupon_msg = "";
        Coupon_code = "";

        btnPayNow = findViewById(R.id.btnPaynow);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvWallet = findViewById(R.id.tvWallet);
        tvAmount = findViewById(R.id.tvAmount);
        tvWowAmount = findViewById(R.id.tvWowAmount);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);
        tvCouponText = findViewById(R.id.tvCouponText);
        tvCouponMsg = findViewById(R.id.tvCouponMsg);
        ll_wallet = findViewById(R.id.ll_wallet);
        llCouponText = findViewById(R.id.llCouponText);
        llApplyView = findViewById(R.id.llApplyView);
        chk_wallet = findViewById(R.id.chk_wallet);
        ll_coupon = findViewById(R.id.ll_coupon);
        view_coupon = findViewById(R.id.view_coupon);
        imgOpen = findViewById(R.id.imgOpen);
        rbCOD = findViewById(R.id.rbCOD);
        rbOnline = findViewById(R.id.rbOnline);
        btnClear = findViewById(R.id.btnClear);
        rgPayType = findViewById(R.id.rgPayType);
        btnClear1 = findViewById(R.id.btnClear1);
        btnApply = findViewById(R.id.btnApply);
        etCoupon = findViewById(R.id.etCoupon);

        ll_mem_coupon = findViewById(R.id.ll_mem_coupon);
        view_mem_coupon = findViewById(R.id.view_mem_coupon);

        UserId = Utils.getUserId(context);

        locationInterface = new LocationInterface() {
            @Override
            public void onSuccess(double mLatitude, double mLongitude) {
                latitude = mLatitude;
                longitude = mLongitude;
                new BookServices().execute();
            }

            @Override
            public void onFailure() {

            }

            @Override
            public void onResolve(Status status) {
                try {
                    status.startResolutionForResult(CheckoutActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        };

        location = new Location(context, locationInterface);

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
            Amount = bundle.getString("Amount");
            BookService = bundle.getString("BookService");
            Latitude = bundle.getString("Latitude");
            Longitude = bundle.getString("Longitude");
        }

        AppPreference.setPreference(context, AppPersistance.keys.ModelType, ModelType);
        AppPreference.setPreference(context, AppPersistance.keys.BookService, BookService);

        btnPayNow.setOnClickListener(this);
        ll_wallet.setOnClickListener(this);
        ll_coupon.setOnClickListener(this);
        ll_mem_coupon.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnClear1.setOnClickListener(this);
        etCoupon.setOnClickListener(this);
        btnApply.setOnClickListener(this);

        if (global.isNetworkAvailable()) {
            new GetChackoutData().execute();
        } else {
            retryInternet();
        }

        chk_wallet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isWowWallet = isChecked;
                setGrandTotal();
            }
        });

        rgPayType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbCOD:
                        txtPayType = "cod";
                        break;
                    case R.id.rbOnline:
                        txtPayType = "online";
                        break;
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v == btnPayNow) {
            if (ActivityCompat.checkSelfPermission(CheckoutActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CheckoutActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ID);
            } else {
                location.findLocation();
            }

        } else if (ll_wallet == v) {
            if (ll_wallet.getTag().equals("f")) {
                ll_wallet.setTag("t");
                chk_wallet.setChecked(true);
            } else {
                ll_wallet.setTag("f");
                chk_wallet.setChecked(false);
            }
        } else if (v == ll_coupon) {
            if (view_coupon.getVisibility() == View.VISIBLE) {
                view_coupon.animate()
                        .translationY(0)
                        .alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                view_coupon.setVisibility(View.GONE);
                                imgOpen.setImageResource(R.drawable.ic_more);
                            }
                        });
            } else {
                // Prepare the View for the animation
                view_coupon.setVisibility(View.VISIBLE);
                llApplyView.setVisibility(View.VISIBLE);
                view_coupon.setAlpha(0.0f);

                // Start the animation
                view_coupon.animate()
                        .translationY(20)
                        .alpha(1.0f)
                        .setListener(null);

                imgOpen.setImageResource(R.drawable.ic_collapse);
            }
        } else if (v == ll_mem_coupon) {

            Intent intent = new Intent(CheckoutActivity.this, CouponActivity.class);
            intent.putExtra("car_type", ModelType);
            intent.putExtra("book_service", BookService);
            startActivity(intent);

        } else if (v == btnClear) {
            final_discount = "0";
            discount_service_id = "";
            Coupon_msg = "";
            Coupon_code = "";
            view_mem_coupon.setVisibility(View.GONE);
            setGrandTotal();
        } else if (v == btnApply) {
            CouponCode = etCoupon.getText().toString();
            if (CouponCode.equals("")) {
                Toast.makeText(context, "Please Enter Coupon Code!!!", Toast.LENGTH_SHORT).show();
            } else {
                new GetCouponData().execute();
            }
        } else if (v == btnClear1) {
            final_discount = "0";
            discount_service_id = "";
            Coupon_msg = "";
            Coupon_code = "";
            llCouponText.setVisibility(View.GONE);
            setGrandTotal();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(CheckoutActivity.this, "Location Permission Granted!", Toast.LENGTH_SHORT).show();
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(CheckoutActivity.this, "Location Permission Denied!!!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
            break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @SuppressLint("StaticFieldLeak")
    private class BookServices extends AsyncTask<String, Void, String> {
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

            String service_id, coupon_id, wallet_used;

            if (isWowWallet) {
                wallet_used = "Yes";
            } else {
                wallet_used = "No";
            }

            if (isServiceCoupon) {
                service_id = discount_service_id;
                coupon_id = "";
            } else {
                coupon_id = discount_service_id;
                service_id = "";
            }

            String strAPI = AppConstant.API_BOOK_SERVICE + UserId
                    + "&car_type=" + ModelType
                    + "&book_services=" + BookService
                    + "&service_total=" + Amount
                    + "&book_date=" + Date
                    + "&book_time=" + TimeSlot
                    + "&payment_type=" + txtPayType
                    + "&wallet_used=" + wallet_used
                    + "&b_firstname=" + Name
                    + "&b_email=" + Utils.getEmail(context)
                    + "&b_address1=" + Address
                    + "&b_zipcode=" + ""
                    + "&city_id=" + CityId
                    + "&b_phone1=" + MobileNo
                    + "&discount_service_id=" + service_id
                    + "&discount_coupon_id=" + coupon_id
                    + "&discount_value=" + final_discount
                    + "&final_grand_total=" + GrandTotal
                    + "&wallet_payment=" + final_discount
                    + "&car_no=" + VehicleNo
                    + "&latitude=" + Latitude
                    + "&longitude=" + Longitude;

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
                        try {
                            order_fail = jsonObjectList.getString("order_fail");
                            order_success = jsonObjectList.getString("order_success");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (resCode.equalsIgnoreCase("0") || resCode.equalsIgnoreCase("9")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("online_payment_data");
                            {
                                JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                                if (jsonObjectList != null && jsonObjectList.length() != 0) {
                                    userID = jsonObjectList.getString("userID");
                                    product_info = jsonObjectList.getString("product_info");
                                    amount = jsonObjectList.getString("amount");
                                    first_name = jsonObjectList.getString("first_name");
                                    email = jsonObjectList.getString("email");
                                    trans_id = jsonObjectList.getString("trans_id");
                                    udf1 = jsonObjectList.getString("udf1");
                                    udf2 = jsonObjectList.getString("udf2");
                                    payu_key = jsonObjectList.getString("payu_key");
                                    payment_hash = jsonObjectList.getString("payment_hash");
                                    vas_for_mobile_sdk_hash = jsonObjectList.getString("vas_for_mobile_sdk_hash");
                                    payment_related_details_for_mobile_sdk_hash = jsonObjectList.getString("payment_related_details_for_mobile_sdk_hash");
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
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
                String[] itemsDate = resMessage.split("_");
                Intent intent = new Intent(CheckoutActivity.this, TransectionStatusActivity.class);
                intent.putExtra("message", itemsDate[0]);
                intent.putExtra("message1", itemsDate[1]);
                intent.putExtra("msgCode", resCode);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else if (resCode.equalsIgnoreCase("9")) {
                Intent intent = new Intent(CheckoutActivity.this, com.payu.payuui.Activity.PayUBaseActivity.class);
                PaymentParams mPaymentParams = new PaymentParams();
                PayuConfig payuConfig = new PayuConfig();
                payuHashes = new PayuHashes();
                mPaymentParams.setKey(payu_key);
                mPaymentParams.setAmount(amount);
                mPaymentParams.setProductInfo(product_info);
                mPaymentParams.setFirstName(first_name);
                mPaymentParams.setEmail(email);
                mPaymentParams.setTxnId(trans_id);
                mPaymentParams.setSurl("http://www.washonwheel.com/api/index.php?view=order_payment_success");
                mPaymentParams.setFurl("http://www.washonwheel.com/api/index.php?view=order_payment_success");
                mPaymentParams.setUdf1(udf1);
                mPaymentParams.setUdf2(udf2);
                mPaymentParams.setUdf3("");
                mPaymentParams.setUdf4("");
                mPaymentParams.setUdf5("");
                mPaymentParams.setCardBin("");
                mPaymentParams.setPhone(MobileNo);
                mPaymentParams.setUserCredentials(payu_key + ":" + email);
                payuConfig.setEnvironment(PayuConstants.PRODUCTION_ENV);
                payuHashes.setPaymentHash(payment_hash);
                payuHashes.setPaymentRelatedDetailsForMobileSdkHash(payment_related_details_for_mobile_sdk_hash);
                payuHashes.setVasForMobileSdkHash(vas_for_mobile_sdk_hash);
                mPaymentParams.setHash(payment_hash);
                intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
                intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
                intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
                startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetCouponData extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_APPLY_COUPON + UserId
                    + "&discount_code=" + CouponCode
                    + "&service_total=" + Amount;

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
                        resMessage1 = jsonObjectList.getString("message");
                        resCode1 = jsonObjectList.getString("msgcode");

                        if (resCode1.equalsIgnoreCase("0")) {
                            final_discount = jsonObjectList.getString("final_discount");
                            discount_service_id = jsonObjectList.getString("discount_coupon_id");
                        } else {
                            final_discount = "0";
                            discount_service_id = "";
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
            Coupon_msg = resMessage1;
            Coupon_code = resCode1;

            if (resCode1.equalsIgnoreCase("0")) {
                setTvCouponText();
            } else {
                Toast.makeText(context, resMessage1, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        tvTitle = findViewById(R.id.toolbar_title);
        tvTitle.setText("Check Out");
        Toolbar toolbar = findViewById(R.id.tool_bar);
        assert toolbar != null;
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetChackoutData extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_CHECKOUT_DATA + UserId;

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
                    resMessage = jsonObjectList.getString("message");
                    resCode = jsonObjectList.getString("msgcode");
                    if (resCode.equalsIgnoreCase("0")) {
                        JSONArray jsonArray = jsonObjectList.getJSONArray("payment_data");
                        {
                            JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                            if (jsonObjectList != null && jsonObjectList.length() != 0) {
                                wallet_bal = jsonObjectList.getString("wallet_bal");
                                cod = jsonObjectList.getString("cod");
                                online = jsonObjectList.getString("online");
                                wallet = jsonObjectList.getString("wallet");
                                packages = jsonObjectList.getString("package");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (resCode.equalsIgnoreCase("0")) {
                tvWallet.setText(getResources().getString(R.string.Rs) + " " + wallet_bal);
                if (cod.equals("Yes")) {
                    rbCOD.setVisibility(View.VISIBLE);
                } else {
                    rbCOD.setVisibility(View.GONE);
                }
                if (online.equals("Yes")) {
                    rbOnline.setVisibility(View.VISIBLE);
                } else {
                    rbOnline.setVisibility(View.GONE);
                }
                if (wallet.equals("Yes")) {
                    ll_wallet.setVisibility(View.VISIBLE);
                } else {
                    ll_wallet.setVisibility(View.GONE);
                }
                if (packages.equals("Yes")) {
                    ll_mem_coupon.setVisibility(View.VISIBLE);
                } else {
                    ll_mem_coupon.setVisibility(View.GONE);
                }
                if (wallet_bal.equals("0") || wallet_bal.equals("0.0") || wallet_bal.equals("0.00") || wallet_bal.equals("")) {
                    chk_wallet.setEnabled(false);
                    ll_wallet.setEnabled(false);
                }
                setGrandTotal();
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
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
                    new GetChackoutData().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK) {
                //  new GetWalletBal();
                if (data.getStringExtra("status").equals("userCancelled")) {
                    Utils.displayDialog(getString(R.string.app_name), "Transaction Cancelled!", context, false);
                } else if (data.getStringExtra("status").equals("captured")) {
                    Intent intent = new Intent(context, TransectionStatusActivity.class);
                    intent.putExtra("message", data.getStringExtra("msg1"));
                    intent.putExtra("msgCode", 1);
                    //  intent.putExtra("msg2", data.getStringExtra("msg2"));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else if (data.getStringExtra("status").equals("failed")) {
                    Intent intent = new Intent(context, TransectionStatusActivity.class);
                    intent.putExtra("message", data.getStringExtra("msg1"));
                    intent.putExtra("msgCode", 1);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }
            }
        }
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                if (data != null)
                    Log.e("Success", "Success");
                String[] itemsDate = order_success.split("_");
                Intent intent = new Intent(context, TransectionStatusActivity.class);
                intent.putExtra("message", itemsDate[0] + "");
                intent.putExtra("msgCode", 0);
                intent.putExtra("message1", itemsDate[1] + "");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
            if (resultCode == RESULT_CANCELED) {
                if (data != null)
                    Log.e("Fail", "Fail");
                String[] itemsDate = order_fail.split("_");
                Intent intent = new Intent(context, TransectionStatusActivity.class);
                intent.putExtra("message", itemsDate[0] + "");
                intent.putExtra("msgCode", 1);
                intent.putExtra("message1", itemsDate[1] + "");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        }
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    location.findLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(CheckoutActivity.this, "Location not enabled..", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }
}