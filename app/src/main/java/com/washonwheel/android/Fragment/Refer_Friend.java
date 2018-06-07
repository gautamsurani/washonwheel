package com.washonwheel.android.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Refer_Friend extends Fragment {

    TextView tvTitle;

    TextView tvMessage, tvYouGet, tvFrdGet, tvReferCode;

    ImageView ivReferIMG;

    ProgressDialog progressDialog;

    Activity context;

    Global global;

    String UserId = "", resMessage = "", resCode = "";

    String image = "", message = "", share_image = "", ref_key = "", you_get = "", you_friend_get = "";

    View view;

    LinearLayout sharewp, sharefb, sharemail, sharmore;

    Button btnCopy;

    String link = "", share_message = "";

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    public Refer_Friend() {
        // Required empty public constructor
        // 183
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.refer_code, container, false);

        context = getActivity();
        global = new Global(context);

        init();

        UserId = AppPreference.getPreference(context, AppPersistance.keys.USER_ID);

        if (global.isNetworkAvailable()) {
            new GetReferData().execute();
        } else {
            retryInternet();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission();
        }

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", link);
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Download link copied.!", Toast.LENGTH_SHORT).show();
            }
        });
        sharewp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    shareRefereeWhatsapp();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        sharefb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    shareMore();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        sharemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent2 = new Intent();
                    intent2.setAction(Intent.ACTION_SEND);
                    intent2.setType("message/rfc822");
                    intent2.putExtra(Intent.EXTRA_SUBJECT, "WOW");
                    intent2.putExtra(Intent.EXTRA_TEXT, share_message);
                    startActivity(intent2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        sharmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    shareMore();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    public void shareRefereeWhatsapp() {
        String ImgListPath = share_image;
        new DownloadSelectedIMP().execute(ImgListPath);

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void permission() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if (!addPermission(permissionsList, Manifest.permission.READ_SMS))
            permissionsNeeded.add("Read SMS");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write Contacts");
        if (permissionsList.size() > 0) {
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (getActivity().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            return shouldShowRequestPermissionRationale(permission);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                perms.put(Manifest.permission.RECEIVE_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class DownloadSelectedIMP extends AsyncTask<String, String, String> {

        String ImgPath = "";
        Boolean isFile = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("loading");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected String doInBackground(String... arg0) {
            String filename = "HomeDezin.jpg";
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().toString() + "/HomeDezin/Event/"
            );

            wallpaperDirectory.mkdirs();
            ImgPath = wallpaperDirectory.getPath() + filename;
            String mys_receivedImage = arg0[0];
            int count;
            try {
                URL myUrl = new URL(mys_receivedImage);
                URLConnection connection = myUrl.openConnection();

                int lengthOfFile = connection.getContentLength();
                connection.connect();
                InputStream input = new BufferedInputStream(myUrl.openStream());
                OutputStream output = new FileOutputStream(ImgPath);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                isFile = true;
            } catch (IOException e) {
                isFile = false;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            try {
                if (isFile) {
                    File file = new File(ImgPath);
                    Uri imageUri = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setPackage("com.whatsapp");
                    intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    intent.putExtra(Intent.EXTRA_TEXT, share_message);
                    intent.putExtra(Intent.EXTRA_TITLE, "Home Dezin");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setType("image/*");

                    try {
                        startActivity(intent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(context, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(context, "Try again..", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                Toast.makeText(context, "Try again..", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void shareMore() {

        String ImgListPath = share_image;
        DownloadSelctedIMG d = new DownloadSelctedIMG();
        d.execute(ImgListPath);

    }

    @SuppressLint("StaticFieldLeak")
    class DownloadSelctedIMG extends AsyncTask<String, String, Void> {

        String ImgPath = "";
        Boolean isFile2 = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("loading");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        protected Void doInBackground(String... arg0) {
            String filename = "HomeDezin.jpg";
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().toString() + "/HomeDezin/Event/"
            );

            wallpaperDirectory.mkdirs();
            ImgPath = wallpaperDirectory.getPath() + filename;
            String mys_receivedImage = arg0[0];
            int count;
            try {
                URL myUrl = new URL(mys_receivedImage);
                URLConnection connection = myUrl.openConnection();

                int lengthOfFile = connection.getContentLength();
                connection.connect();
                InputStream input = new BufferedInputStream(myUrl.openStream());
                OutputStream output = new FileOutputStream(ImgPath);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                isFile2 = true;
            } catch (IOException e) {
                isFile2 = false;
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
                if (isFile2) {
                    File file = new File(ImgPath);
                    Uri imageUri = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    Log.d("imageUri", "imageUriIs" + imageUri);
                    intent.putExtra(Intent.EXTRA_STREAM, imageUri);

                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_TEXT, share_message);
                    intent.putExtra(Intent.EXTRA_TITLE, "WOW");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setType("image/*");
                    startActivity(Intent.createChooser(intent, "WOW"));
                } else {
                    Toast.makeText(context, "Try again..", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(context, "Try again..", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        tvTitle = context.findViewById(R.id.toolbar_title);
        tvMessage = view.findViewById(R.id.tvMessage);
        tvYouGet = view.findViewById(R.id.tvYouGet);
        tvFrdGet = view.findViewById(R.id.tvFrdGet);
        tvReferCode = view.findViewById(R.id.tvReferCode);
        ivReferIMG = view.findViewById(R.id.ivReferIMG);
        sharewp = view.findViewById(R.id.sharewp);
        sharefb = view.findViewById(R.id.sharefb);
        sharemail = view.findViewById(R.id.sharemail);
        sharmore = view.findViewById(R.id.sharmore);
        btnCopy = view.findViewById(R.id.btnCopy);
        tvTitle.setText("Refer");
    }

    @SuppressLint("StaticFieldLeak")
    private class GetReferData extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_SHARE_MSG + UserId;
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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("share_data");
                            {
                                JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                                if (jsonObjectList != null && jsonObjectList.length() != 0) {
                                    image = jsonObjectList.getString("image");
                                    message = jsonObjectList.getString("message");
                                    share_image = jsonObjectList.getString("share_image");
                                    share_message = jsonObjectList.getString("share_message");
                                    ref_key = jsonObjectList.getString("ref_key");
                                    you_get = jsonObjectList.getString("you_get");
                                    you_friend_get = jsonObjectList.getString("you_friend_get");
                                    link = jsonObjectList.getString("link");
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
                tvMessage.setText(message);
                tvYouGet.setText(you_get);
                tvReferCode.setText(ref_key);
                tvFrdGet.setText(you_friend_get);
                Glide.with(context)
                        .load(image)
                        .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(R.drawable.default_icon)
                        .into(ivReferIMG);
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
                    new GetReferData().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }
}
