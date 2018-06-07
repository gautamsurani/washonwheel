package com.washonwheel.android.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.washonwheel.android.R;
import com.washonwheel.android.Util.AppConstant;
import com.washonwheel.android.Util.RequestMethod;
import com.washonwheel.android.Util.RestClient;
import com.washonwheel.android.Util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class AddMoneyActivity extends AppCompatActivity implements View.OnClickListener {

    Activity context;

    ProgressDialog progressDialog;

    String strUserId, strMobile, resMessage = "", resCode = "";

    TextView tvTitle;

    EditText etAmount;
    Button btnContinueWallet;
    TextView tv100, tv200, tv500, tv1000, tvWalletMoney;
    private PaymentParams mPaymentParams;
    ImageView imgAddPayUbiz;
    PayuHashes payuHashes;
    private PayuConfig payuConfig;
    String payu_wallet_fail = "", payu_wallet_success = "";
    String selectedAmount = "";
    String walletBal = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);
        Payu.setInstance(this);

        context = this;

        strUserId = Utils.getUserId(context);
        strMobile = Utils.getMobileNo(context);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            walletBal = bundle.getString("walletBal");
        }

        init();

        initComponent();

        tvWalletMoney.setText("WoW Wallet : " + getResources().getString(R.string.Rs) + " " + walletBal);

    }

    public void initComponent() {
        tv100 = findViewById(R.id.tv100);
        tv200 = findViewById(R.id.tv200);
        tv500 = findViewById(R.id.tv500);
        tv1000 = findViewById(R.id.tv1000);
        tvWalletMoney = findViewById(R.id.tvWalletMoney);
        etAmount = findViewById(R.id.etAmount);
        imgAddPayUbiz = findViewById(R.id.imgAddPayUbiz);
        //  imgAddPaytm = (ImageView) findViewById(R.id.imgAddPaytm);
        btnContinueWallet = findViewById(R.id.btnContinueWallet);
        tv100.setOnClickListener(this);
        tv200.setOnClickListener(this);
        tv500.setOnClickListener(this);
        tv1000.setOnClickListener(this);
        imgAddPayUbiz.setOnClickListener(this);
        // imgAddPaytm.setOnClickListener(this);
        btnContinueWallet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == tv100) {
            etAmount.setText("100");
        } else if (v == tv200) {
            etAmount.setText("200");
        } else if (v == tv500) {
            etAmount.setText("500");
        } else if (v == tv1000) {
            etAmount.setText("1000");
        } else if (v == imgAddPayUbiz) {

            if (etAmount.getText().toString().equals("")) {
                Toast.makeText(context, "Please select the amount", Toast.LENGTH_SHORT).show();
            } else {
                if (Utils.isNetworkAvailable(context)) {
                    AddWalletAsync mAddWalletMoney = new AddWalletAsync();
                    mAddWalletMoney.execute();
                } else {
                    retryInternet();
                }
            }
        } else if (v == btnContinueWallet) {

            if (etAmount.getText().toString().equals("")) {
                Toast.makeText(context, "Please select the amount", Toast.LENGTH_SHORT).show();
            } else {
                if (Utils.isNetworkAvailable(context)) {
                    AddWalletAsync mAddWalletMoney = new AddWalletAsync();
                    mAddWalletMoney.execute();
                } else {
                    retryInternet();
                }
            }
            /*
            Intent intentWallet = new Intent(WalletActivity.this, MainActivity.class);
            startActivity(intentWallet);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();*/
        }
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
                String[] itemsDate = payu_wallet_success.split("_");
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
                String[] itemsDate = payu_wallet_fail.split("_");
                Intent intent = new Intent(context, TransectionStatusActivity.class);
                intent.putExtra("message", itemsDate[0] + "");
                intent.putExtra("msgCode", 1);
                intent.putExtra("message1", itemsDate[1] + "");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
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
                if (Utils.isNetworkAvailable(context)) {
                    dialog.dismiss();
                    new AddWalletAsync().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }

    private class AddWalletAsync extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;
        String product_info, amount, first_name, email, trans_id, udf1, payu_key, payment_hash,
                vas_for_mobile_sdk_hash, payment_related_details_for_mobile_sdk_hash;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            selectedAmount = etAmount.getText().toString().trim();
            progressDialog = new ProgressDialog(context);
            progressDialog.show();
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.API_ADD_MONEY + strUserId
                    + "&amount=" + selectedAmount
                    + "&userPhone=" + strMobile
                    + "&page=add_money&type=payu";

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
                        payu_wallet_fail = jsonObjectList.getString("payu_wallet_fail");
                        payu_wallet_success = jsonObjectList.getString("payu_wallet_success");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("add_money_data");
                            {
                                JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                                if (jsonObjectList != null && jsonObjectList.length() != 0) {

                                    product_info = jsonObjectList.getString("product_info");
                                    amount = jsonObjectList.getString("amount");
                                    first_name = jsonObjectList.getString("first_name");
                                    email = jsonObjectList.getString("email");
                                    trans_id = jsonObjectList.getString("trans_id");
                                    udf1 = jsonObjectList.getString("udf1");
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
                Intent intent = new Intent(context, com.payu.payuui.Activity.PayUBaseActivity.class);
                mPaymentParams = new PaymentParams();
                payuConfig = new PayuConfig();
                payuHashes = new PayuHashes();
                mPaymentParams.setKey(payu_key);
                mPaymentParams.setAmount(amount);
                mPaymentParams.setProductInfo(product_info);
                mPaymentParams.setFirstName(first_name);
                mPaymentParams.setEmail(email);
                mPaymentParams.setTxnId(trans_id);
                mPaymentParams.setSurl("http://www.washonwheel.com/api/index.php?view=wallet_payment_success");
                mPaymentParams.setFurl("http://www.washonwheel.com/api/index.php?view=wallet_payment_success");
                mPaymentParams.setUdf2("");
                mPaymentParams.setUdf3("");
                mPaymentParams.setUdf4("");
                mPaymentParams.setUdf5("");
                mPaymentParams.setUdf1(udf1);
                mPaymentParams.setCardBin("");
                mPaymentParams.setPhone(strMobile);
                mPaymentParams.setUserCredentials(payu_key + ":" + email);
                // payuConfig.setEnvironment(PayuConstants.PRODUCTION_ENV);
                payuConfig.setEnvironment(Integer.parseInt("0"));
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

    public void init() {
        tvTitle = findViewById(R.id.toolbar_title);
        tvTitle.setText("Add Money");
        Toolbar toolbar = findViewById(R.id.tool_bar);
        assert toolbar != null;
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
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
        overridePendingTransition(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);
        finish();
    }
}
