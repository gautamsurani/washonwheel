package com.washonwheel.android.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.washonwheel.android.Adapter.UpdatesAdapter;
import com.washonwheel.android.Pojo.UpdatestData;
import com.washonwheel.android.R;
import com.washonwheel.android.Util.AppConstant;
import com.washonwheel.android.Util.RequestMethod;
import com.washonwheel.android.Util.RestClient;
import com.washonwheel.android.Util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OffersActivity extends AppCompatActivity {

    TextView tvTitle;

    private List<UpdatestData> UpdateNotificationList = new ArrayList<>();

    public UpdatesAdapter mUpdatesAdapter;

    TextView noData;

    SwipeRefreshLayout mSwipeRefreshLayout;

    String StrUserId;

    public int pagecode = 0;

    RecyclerView recyclerView;

    SwipeRefreshLayout swipeRefresh;

    String StrPage = "Push", resMessage = "", resCode = "";

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        init();
        FetchXMLId();

        StrUserId = Utils.getUserId(this);

        Intent in = getIntent();
        if (in != null) {
            StrPage = in.getStringExtra("PageTypeForPush");
        }
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        mUpdatesAdapter = new UpdatesAdapter(this, UpdateNotificationList);
        recyclerView.setAdapter(mUpdatesAdapter);


        mUpdatesAdapter.setOnItemClickListener(new UpdatesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int which) {

                UpdatestData mUpdatestData = UpdateNotificationList.get(position);
                Intent intent = new Intent(OffersActivity.this, OfferDetailActivity.class);
                intent.putExtra("title", "");
                intent.putExtra("content", mUpdatestData.getMessage());
                intent.putExtra("date", mUpdatestData.getAdded_on());
                intent.putExtra("IMgMain", mUpdatestData.getImage());
                intent.putExtra("sharemsg", mUpdatestData.getShre_msg());
                intent.putExtra("PButton", "");
                intent.putExtra("CBuuton", "");
                intent.putExtra("SubCat", "");
                intent.putExtra("ButtonID", "");
                intent.putExtra("Name", "");
                intent.putExtra("PageThis", "");
                startActivity(intent);
                overridePendingTransition(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);

            }
        });

        if (Utils.isNetworkAvailable(getBaseContext())) {
            mSwipeRefreshLayout.setVisibility(View.GONE);
            GetNotificationDetalil task = new GetNotificationDetalil();
            task.execute();
        } else {
            Utils.showToastShort("Please Check Your Internet Connection", this);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Utils.hideKeyboard(OffersActivity.this);
                mSwipeRefreshLayout.setRefreshing(true);
                (new Handler()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (Utils.isNetworkAvailable(getBaseContext())) {
                            pagecode = 0;
                            GetNotificationDetalil task = new GetNotificationDetalil();
                            task.execute();
                        } else {
                            Utils.showToastShort("Please Check Your Internet Connection", OffersActivity.this);
                        }
                    }
                });
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class GetNotificationDetalil extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(OffersActivity.this);
            progressDialog.show();
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String strAPI = AppConstant.API_OFFERS + "0";

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
                Log.e("API", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("offer_list");
                            if (jsonArray != null && jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    UpdatestData updatestData = new UpdatestData();
                                    JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                    updatestData.setImage(jsonObjectList.getString("image"));
                                    updatestData.setMessage(jsonObjectList.getString("message"));
                                    updatestData.setAdded_on(jsonObjectList.getString("added_on"));
                                    updatestData.setShre_msg(jsonObjectList.getString("shre_msg"));
                                    UpdateNotificationList.add(updatestData);
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
        protected void onPostExecute(String curators) {
            progressDialog.dismiss();
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            if (resCode.equalsIgnoreCase("0")) {
                mUpdatesAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(OffersActivity.this, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        tvTitle = findViewById(R.id.toolbar_title);
        tvTitle.setText("Offers");
        Toolbar toolbar = findViewById(R.id.tool_bar);
        assert toolbar != null;
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
    }

    @SuppressLint("CutPasteId")
    private void FetchXMLId() {
        //   progressBar1=(ProgressBar)findViewById(R.id.progressBar1);
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swiperefresh);
        noData = findViewById(R.id.nodata);

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
        finish();
        overridePendingTransition(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);
    }
}
