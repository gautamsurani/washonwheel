package com.washonwheel.android.Fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.washonwheel.android.R;
import com.washonwheel.android.Util.AppConstant;
import com.washonwheel.android.Util.AppPersistance;
import com.washonwheel.android.Util.AppPreference;
import com.washonwheel.android.Util.Global;
import com.washonwheel.android.Util.RequestMethod;
import com.washonwheel.android.Util.RestClient;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUsFragment extends Fragment {

    View view;

    TextView tvtitle, tvAbout, tvTitle;

    ImageView ivAboutIMG;

    Activity context;

    Global global;

    String UserId = "", resMessage = "", resCode = "", image = "", title = "", text = "", facebook_link = "",
            google_link = "", linkdin_link = "", twitter_link = "", insta_link = "";

    ProgressDialog progressDialog;

    ImageView viewfb, viewgplus, viewin, viewinstagram, viewtwitter;

    public AboutUsFragment() {
        // Required empty public constructor
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_about_us, container, false);

        context = getActivity();
        global = new Global(context);

        init();

        UserId = AppPreference.getPreference(context, AppPersistance.keys.USER_ID);

        if (global.isNetworkAvailable()) {
            new GetAboutData().execute();
        } else {
            retryInternet();
        }

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class GetAboutData extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_ABOUT;

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
                            JSONObject jsonObject = jsonObjectList.getJSONObject("about");
                            image = jsonObject.getString("image");
                            title = jsonObject.getString("title");
                            text = jsonObject.getString("text");

                            facebook_link = jsonObject.getString("facebook_link");
                            google_link = jsonObject.getString("google_link");
                            linkdin_link = jsonObject.getString("linkdin_link");
                            twitter_link = jsonObject.getString("twitter_link");
                            insta_link = jsonObject.getString("insta_link");
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
                tvTitle.setText(title);
                tvAbout.setText(text);
                Glide.with(context)
                        .load(image)
                        .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(R.drawable.default_icon)
                        .into(ivAboutIMG);


                if (facebook_link.equalsIgnoreCase("")) {
                    viewfb.setVisibility(View.GONE);
                } else {
                    viewfb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Uri uri = Uri.parse(facebook_link);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                if (google_link.equalsIgnoreCase("")) {
                    viewgplus.setVisibility(View.GONE);
                } else {
                    viewgplus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Uri uri = Uri.parse(google_link);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }


                if (linkdin_link.equalsIgnoreCase("")) {
                    viewin.setVisibility(View.GONE);
                } else {
                    viewin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Uri uri = Uri.parse(linkdin_link);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                if (twitter_link.equalsIgnoreCase("")) {
                    viewtwitter.setVisibility(View.GONE);
                } else {
                    viewtwitter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Uri uri = Uri.parse(twitter_link);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                if (insta_link.equalsIgnoreCase("")) {
                    viewinstagram.setVisibility(View.GONE);
                } else {
                    viewinstagram.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Uri uri = Uri.parse(insta_link);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        tvtitle = getActivity().findViewById(R.id.toolbar_title);
        tvtitle.setText("About Us");
        tvAbout = view.findViewById(R.id.tvAbout);
        tvTitle = view.findViewById(R.id.tvTitle);
        ivAboutIMG = view.findViewById(R.id.ivAboutIMG);
        viewfb = view.findViewById(R.id.viewfb);
        viewgplus = view.findViewById(R.id.viewgplus);
        viewin = view.findViewById(R.id.viewin);
        viewinstagram = view.findViewById(R.id.viewinstagram);
        viewtwitter = view.findViewById(R.id.viewtwitter);
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
                    new GetAboutData().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }
}
