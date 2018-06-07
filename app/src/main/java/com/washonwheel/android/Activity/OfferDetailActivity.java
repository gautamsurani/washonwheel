package com.washonwheel.android.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.washonwheel.android.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class OfferDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int progress_bar_type = 0;
    ProgressDialog progressDialog;
    Button btnViewmore;
    String STrThisPage = "Push";
    final String LOCAL_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BooksAlways/OfferList";
    String Strtitle = "", Strcontent = "", Strdate = "", StrIMgMain = "", StrShareMsg = "";
    String PButton = "", CBuuton = "", SubCat = "", ButtonID = "", Name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_detail);

        Intent in = getIntent();
        if (in != null) {

            STrThisPage = in.getStringExtra("PageThis");
            Strtitle = in.getStringExtra("title");
            Strcontent = in.getStringExtra("content");
            Strdate = in.getStringExtra("date");
            StrIMgMain = in.getStringExtra("IMgMain");
            StrShareMsg = in.getStringExtra("sharemsg");
            PButton = in.getStringExtra("PButton");
            CBuuton = in.getStringExtra("CBuuton");
            SubCat = in.getStringExtra("SubCat");
            ButtonID = in.getStringExtra("ButtonID");
            Name = in.getStringExtra("Name");

        }
        Log.e("STrThisPage", STrThisPage);
        FloatingActionButton fab = findViewById(R.id.fab);


        ImageView IMgMain = findViewById(R.id.image);
        TextView title = findViewById(R.id.title);
        TextView content = findViewById(R.id.content);
        TextView date = findViewById(R.id.date);


        title.setText(Strtitle);
        content.setText(Strcontent);
        date.setText(Strdate);

        Glide.with(this)
                .load(StrIMgMain)
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.default_icon)
                .into(IMgMain);

        btnViewmore = findViewById(R.id.btnViewmore);


        File createfolader2 = new File(LOCAL_PATH);
        if (!createfolader2.exists()) {
            createfolader2.mkdirs();
        }
        initToolbar();
        fab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {


                /*Intent intent = new Intent(OfferDetailActivity.this, SingleOfferImage.class);
                intent.putExtra("singleImage", StrIMgMain);
                intent.putExtra("singleImageTitle", Strtitle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
            }

        });
        IMgMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intent = new Intent(OfferDetailActivity.this, SingleOfferImage.class);
                intent.putExtra("singleImage", StrIMgMain);
                intent.putExtra("singleImageTitle", Strtitle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
            }
        });


        btnViewmore.setOnClickListener(this);
        if (PButton.equalsIgnoreCase("No") & SubCat.equalsIgnoreCase("No")) {

            btnViewmore.setVisibility(View.GONE);

        }
    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_update_share:

                DownloadSelctedIMG d = new DownloadSelctedIMG();
                d.execute(StrIMgMain);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                ProgressDialog pDialog = new ProgressDialog(this);
                pDialog.setMessage("Loading, please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_update_share, menu);
        return true;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        /*if (PButton.equalsIgnoreCase("Yes") & CBuuton.equalsIgnoreCase("No") &
                SubCat.equalsIgnoreCase("No")) {
            Intent it = new Intent(OfferDetailActivity.this, ProductDetailActivity.class);
            it.putExtra("subCatId", ButtonID);
            startActivity(it);

            overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
        } else if (PButton.equalsIgnoreCase("No") & CBuuton.equalsIgnoreCase("Yes") &
                SubCat.equalsIgnoreCase("Yes")) {
            Intent it = new Intent(OfferDetailActivity.this, CategoryExpandableListView.class);
            it.putExtra("catID", ButtonID);
            it.putExtra("catName", Name);
            startActivity(it);
            //    Toast.makeText(OfferDetailActivity.this,strButtonId, Toast.LENGTH_LONG).show();
            overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
        } else if (PButton.equalsIgnoreCase("No") & CBuuton.equalsIgnoreCase("Yes")
                & SubCat.equalsIgnoreCase("No")) {
            Intent it = new Intent(OfferDetailActivity.this, ProductListActivity.class);
            it.putExtra("catID", ButtonID);
            it.putExtra("catName", Name);
            startActivity(it);
            //  Toast.makeText(OfferDetailActivity.this,strButtonId, Toast.LENGTH_LONG).show();
            overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_left);
        }*/

    }


    @SuppressLint("StaticFieldLeak")
    private class DownloadSelctedIMG extends AsyncTask<String, String, Void> {

        String ImgPath = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(OfferDetailActivity.this);
            progressDialog.setMessage("loading");
            progressDialog.setCancelable(false);
            progressDialog.show();


        }

        protected Void doInBackground(String... arg0) {
            String filename = "BooksAlways.jpg";
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().toString() + "/BooksAlways/Event/");

            wallpaperDirectory.mkdirs();
            ImgPath = wallpaperDirectory.getPath() + filename;
            String myu_recivedimage = arg0[0];
            int count;
            try {
                URL myurl = new URL(myu_recivedimage);
                URLConnection conection = myurl.openConnection();

                int lenghtOfFile = conection.getContentLength();
                conection.connect();
                conection.getContentLength();
                InputStream inpit = new BufferedInputStream(myurl.openStream());
                OutputStream output = new FileOutputStream(ImgPath);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = inpit.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                inpit.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            try {

                String shareBody = StrShareMsg;

                //Uri imageUri = Uri.parse(imgPath);
                File file = new File(ImgPath);
                Uri imageUri = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "BooksAlways");
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);

                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "Share via"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);
    }
}
