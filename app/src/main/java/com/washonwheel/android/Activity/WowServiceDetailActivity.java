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
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.washonwheel.android.R;
import com.washonwheel.android.Util.AppConstant;
import com.washonwheel.android.Util.Global;
import com.washonwheel.android.Util.RequestMethod;
import com.washonwheel.android.Util.RestClient;

import org.json.JSONArray;
import org.json.JSONObject;

public class WowServiceDetailActivity extends AppCompatActivity {

    TextView tvTitle, tvName;
    WebView tvDesc;
    Activity context;
    Global global;
    ImageView ivIMG;
    String ID = "";
    String resMessage = "", resCode = "", Name = "", Image = "", Text = "";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wow_service_detail);

        context = this;
        global = new Global(context);
        init();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ID = bundle.getString("ID");
        }

        if (global.isNetworkAvailable()) {
            new GetWowDetail().execute();
        } else {
            retryInternet();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetWowDetail extends AsyncTask<String, Void, String> {
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

            String strAPI =  AppConstant.API_WOW_SERVICE_DETAIL + ID;

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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("wow_services_detail");
                            {
                                JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                                if (jsonObjectList != null && jsonObjectList.length() != 0) {
                                    Name = jsonObjectList.getString("name");
                                    Image = jsonObjectList.getString("image");
                                    Text = jsonObjectList.getString("text");
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
                tvName.setText(Name);
                tvDesc.loadData("<HTML><BODY>" + Text + "</BODY></HTML>", "text/html; charset=UTF-8", null);
                Glide.with(context)
                        .load(Image)
                        .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(R.drawable.default_icon)
                        .into(ivIMG);
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
                    new GetWowDetail().execute();
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
        tvName = findViewById(R.id.tvName);
        tvDesc = findViewById(R.id.tvDesc);
        ivIMG = findViewById(R.id.ivIMG);
        tvTitle.setText("Service Detail");
        Toolbar toolbar = findViewById(R.id.tool_bar);
        assert toolbar != null;
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
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
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);

    }
}
