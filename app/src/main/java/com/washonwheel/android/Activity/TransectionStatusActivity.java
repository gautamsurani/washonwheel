package com.washonwheel.android.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.washonwheel.android.R;

import static com.washonwheel.android.Util.Utils.convertDpToPixel;

public class TransectionStatusActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtBack, txtOhh, txtMsg;
    ImageView imgTopImage;
    String StrMsg = "", StrMsg1 = "";
    int intCode = 0;
    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transection_status);

        context = this;

        FetchXMLId();

        Intent in = getIntent();
        if (in != null) {
            StrMsg = in.getStringExtra("message");
            StrMsg1 = in.getStringExtra("message1");
            intCode = in.getIntExtra("msgCode", 0);
        }

        if (intCode == 0) {
            imgTopImage.setImageResource(R.drawable.success);
        } else {
            imgTopImage.setImageResource(R.drawable.unsucess);
        }

        txtOhh.setText(StrMsg1);
        txtMsg.setText(StrMsg);
        txtBack.setOnClickListener(this);
    }

    private void FetchXMLId() {
        txtBack = findViewById(R.id.txtBack);
        txtOhh = findViewById(R.id.txtohh);
        txtMsg = findViewById(R.id.txtMsg);
        imgTopImage = findViewById(R.id.imgTopImage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtBack:
                openPopup();
                break;
        }
    }

    public void openPopup() {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context, R.style.CustomAlertDialog);
            AlertDialog alertDialog = alertDialogBuilder.create();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") final View alertLayout = inflater.inflate(R.layout.rate_popup, null);
            Button btnRateUS = alertLayout.findViewById(R.id.btnRateUS);
            TextView txtSignUp = alertLayout.findViewById(R.id.txtSignUp);
            LinearLayout fiveStart = alertLayout.findViewById(R.id.fivestart);
            ImageView ivClose = alertLayout.findViewById(R.id.ivClose);

            alertDialogBuilder.setView(alertLayout);
            alertDialog = alertDialogBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            final AlertDialog finalAlertDialog = alertDialog;

            txtSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finalAlertDialog.dismiss();
                    onBackPressed();
                }
            });

            fiveStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finalAlertDialog.dismiss();
                    onBackPressed();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("market://details?id=" + getPackageName()));
                    startActivity(i);
                }
            });

            btnRateUS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finalAlertDialog.dismiss();
                    onBackPressed();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("market://details?id=" + getPackageName()));
                    startActivity(i);
                }
            });

            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalAlertDialog.dismiss();
                }
            });

            alertDialog.show();

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alertDialog.getWindow().getAttributes());
            lp.width = convertDpToPixel(280, context);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            alertDialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        TransectionStatusActivity.this.finish();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.putExtra("PageType", "MainActivity");
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}