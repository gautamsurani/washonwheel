package com.washonwheel.android.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.washonwheel.android.Activity.AddMoneyActivity;
import com.washonwheel.android.Adapter.WalletHistoryAdapter;
import com.washonwheel.android.Pojo.TransactionData;
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

public class Wallet extends Fragment implements View.OnClickListener {

    TextView tvBalance;

    @SuppressLint("StaticFieldLeak")
    private static Button btnAddMoney;

    RecyclerView rvWalletList;

    Activity context;

    Global global;

    String UserId = "", resMessage = "", resCode = "", walletBal = "0";

    ProgressDialog progressDialog;

    List<TransactionData> transactionData = new ArrayList<>();

    LinearLayoutManager mLayoutManager;

    WalletHistoryAdapter walletHistoryAdapter;

    ProgressBar pbLoading;

    int pagecode = 0;

    boolean IsLAstLoading = true;

    public Wallet() {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wallet, container, false);

        TextView tvTitle = getActivity().findViewById(R.id.toolbar_title);
        tvTitle.setText("Wallet");

        init(v);

        context = getActivity();
        global = new Global(context);

        btnAddMoney.setOnClickListener(this);

        UserId = AppPreference.getPreference(context, AppPersistance.keys.USER_ID);

        mLayoutManager = new LinearLayoutManager(context);
        rvWalletList.setLayoutManager(mLayoutManager);
        rvWalletList.setHasFixedSize(true);
        walletHistoryAdapter = new WalletHistoryAdapter(getActivity(), getActivity(), transactionData);
        rvWalletList.setAdapter(walletHistoryAdapter);

        if (global.isNetworkAvailable()) {
            new GetWalletHistory().execute();
        } else {
            retryInternet();
        }

        rvWalletList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down

                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (!IsLAstLoading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount &&
                                recyclerView.getChildAt(recyclerView.getChildCount() - 1).getBottom() <= recyclerView.getHeight()) {
                            pagecode++;
                            GetWalletHistory getMyWalletHistory = new GetWalletHistory();
                            getMyWalletHistory.execute();
                        }
                    }
                }
            }
        });

        return v;
    }

    @SuppressLint("StaticFieldLeak")
    private class GetWalletHistory extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (pagecode == 0) {
                progressDialog = new ProgressDialog(context);
                progressDialog.show();
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
            } else {
                pbLoading.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String strAPI = AppConstant.ADI_TRANSACTION_DATA + UserId +
                    "&pagecode=" + pagecode;

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
                        walletBal = jsonObjectList.getString("walletBal");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("transction_data");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        TransactionData data = new TransactionData();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        data.setRemark(jsonObjectList.getString("Remark"));
                                        data.setSymbol(jsonObjectList.getString("symbol"));
                                        data.setAmount(jsonObjectList.getString("Amount"));
                                        data.setWallet_type(jsonObjectList.getString("wallet_type"));
                                        data.setType(jsonObjectList.getString("type"));
                                        data.setTransactionDate(jsonObjectList.getString("TransactionDate"));
                                        transactionData.add(data);
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

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            if (pagecode == 0) {
                progressDialog.dismiss();
            } else {
                pbLoading.setVisibility(View.GONE);
            }
            if (resCode.equalsIgnoreCase("0")) {
                IsLAstLoading = false;
                walletHistoryAdapter.notifyDataSetChanged();
            } else {
                IsLAstLoading = true;
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
            tvBalance.setText(getResources().getString(R.string.Rs) + " " + walletBal);
        }
    }

    private void init(View v) {
        btnAddMoney = v.findViewById(R.id.btnAddMoney);
        rvWalletList = v.findViewById(R.id.rvWalletHistory);
        tvBalance = v.findViewById(R.id.tvBalance);
        pbLoading = v.findViewById(R.id.pbLoading);
    }

    @Override
    public void onClick(View v) {
        if (v == btnAddMoney) {
            Intent intent = new Intent(context, AddMoneyActivity.class);
            intent.putExtra("walletBal", walletBal);
            startActivity(intent);
            context.overridePendingTransition(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);
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
                    new GetWalletHistory().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }

}
