package com.washonwheel.android.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.washonwheel.android.Adapter.CitySelectAdapter;
import com.washonwheel.android.Pojo.CityList;
import com.washonwheel.android.R;
import com.washonwheel.android.Util.AndroidMultiPartEntity;
import com.washonwheel.android.Util.AppConstant;
import com.washonwheel.android.Util.AppPersistance;
import com.washonwheel.android.Util.AppPreference;
import com.washonwheel.android.Util.Global;
import com.washonwheel.android.Util.RequestMethod;
import com.washonwheel.android.Util.RestClient;
import com.washonwheel.android.Util.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class MyProfile extends Fragment {

    TextView tvTitle;

    Activity context;

    String UserName, MobileNo, Address = "", Email = "", City = "", Landmark = "", UserImage = "";

    TextView tvCity;

    EditText etName, etEmail, etMobileNo, etAddress, etLandmark;

    public static final int REQUEST_ID = 2;

    Button btnSubmit;

    boolean checkFile;

    String txtUserName, txtMobileNo, txtEmail, txtAddress, txtLandmark;

    Global global;

    String UserId, resMessage = "", resCode = "";

    ProgressDialog progressDialog;

    boolean isProgressDialog = true;

    List<CityList> cityLists = new ArrayList<>();

    String selectedCityId = "";

    Dialog dialog;

    RecyclerView rvSelectCity;

    CitySelectAdapter adapterCity;

    ArrayList<CityList> cityListResults = new ArrayList<>();

    ImageView imgChangeImage;

    CircleImageView imgUpdateImage;

    File fileImage;

    public MyProfile() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile, container, false);

        context = getActivity();

        global = new Global(context);

        init(v);

        fileImage = new File(android.os.Environment.getExternalStorageDirectory(),
                "wow.png");

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setData();

        imgChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtUserName = etName.getText().toString();
                txtMobileNo = etMobileNo.getText().toString();
                txtEmail = etEmail.getText().toString();
                txtAddress = etAddress.getText().toString();
                txtLandmark = etLandmark.getText().toString();

                if (global.isNetworkAvailable()) {
                    new ChangeProfile().execute();
                } else {
                    retryInternet();
                }
            }
        });

        if (global.isNetworkAvailable()) {
            GetCity stateAsync = new GetCity();
            stateAsync.execute();
        } else {
            retryInternet1();
        }

        tvCity.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                dialog.setContentView(R.layout.row_selectcity);
                TextView tvHeaderName = dialog.findViewById(R.id.tvHeadername);
                final LinearLayout lyt_other_area = dialog.findViewById(R.id.lyt_other_area);
                ImageView imgClose = dialog.findViewById(R.id.imgClose);
                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        dialog.dismiss();
                    }
                });
                tvHeaderName.setText("Choose City");
                final EditText etSelectedArea = dialog.findViewById(R.id.etSelectedCity);
                rvSelectCity = dialog.findViewById(R.id.rvSelectcity);
                RecyclerView.LayoutManager mLayoutManagerMain = new LinearLayoutManager(context);
                rvSelectCity.setLayoutManager(mLayoutManagerMain);
                rvSelectCity.setHasFixedSize(true);
                adapterCity = new CitySelectAdapter(context, cityLists);
                rvSelectCity.setAdapter(adapterCity);
                etSelectedArea.addTextChangedListener(new TextWatcher() {
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
                            adapterCity = new CitySelectAdapter(context, cityListResults);
                            rvSelectCity.setAdapter(adapterCity);
                            adapterCity.setOnItemClickListener(new CitySelectAdapter.OnItemClickListener() {
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
                                    @SuppressLint("SetTextI18n")
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


                adapterCity.setOnItemClickListener(new CitySelectAdapter.OnItemClickListener() {
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
        });

        return v;
    }

    private void setData() {

        UserName = Utils.getUserName(context);
        MobileNo = Utils.getMobileNo(context);
        Address = Utils.getAddress(context);
        Email = Utils.getEmail(context);
        UserId = Utils.getUserId(context);
        City = Utils.getCity(context);
        Landmark = Utils.getLandmark(context);
        UserImage = Utils.getUserImage(context);

        etName.setText(UserName);
        etEmail.setText(Email);
        etMobileNo.setText(MobileNo);
        etAddress.setText(Address);
        tvCity.setText(City);
        etLandmark.setText(Landmark);
        try {
            Glide.with(context)
                    .load(UserImage.trim())
                    .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.default_icon)
                    .into(imgUpdateImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermission() {
        if (context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_ID);
        }
        if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_ID);
        }
        if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ID);
        }

    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImage));
                    startActivityForResult(intent, 1);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetCity extends AsyncTask<String, Void, String> {
        JSONObject jsonObjectList;
        String Message = "";
        String Code = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isProgressDialog) {
                isProgressDialog = false;
                progressDialog = new ProgressDialog(context);
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
            new GetData().execute();
        }
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
                    new GetCity().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class ChangeProfile extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_CHANGE_PROFILE;
            try {
                String restUrl = strAPI.replaceAll(" ", "%20");
                String responseString;

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(restUrl);
                try {
                    AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                            new AndroidMultiPartEntity.ProgressListener() {
                                @Override
                                public void transferred(long num) {
                                }
                            });
                    String pathImage = fileImage.getAbsolutePath();

                    File sourceFile;
                    try {
                        entity.addPart("userID", new StringBody(UserId));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    entity.addPart("userPhone", new StringBody(txtMobileNo));
                    entity.addPart("name", new StringBody(txtUserName));
                    entity.addPart("email", new StringBody(txtEmail));
                    entity.addPart("address", new StringBody(txtAddress));
                    entity.addPart("city", new StringBody(selectedCityId));
                    entity.addPart("landmark", new StringBody(txtLandmark));

                    if (checkFile) {
                        sourceFile = new File(pathImage);
                        entity.addPart("file", new FileBody(sourceFile));
                    }
                    httppost.setEntity(entity);

                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity r_entity = response.getEntity();

                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        responseString = EntityUtils.toString(r_entity);
                        Log.e("respo", "respo" + responseString);
                        JSONObject jsonObject = new JSONObject(responseString);
                        resMessage = jsonObject.getString("message");
                        resCode = jsonObject.getString("msgcode");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
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
                AppPreference.setPreference(context, AppPersistance.keys.USER_NUMBER, txtMobileNo);
                new GetData().execute();
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetData extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_USER_DATA + UserId
                    + "&userPhone=" + Utils.getMobileNo(context);

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
                            JSONArray jsonArray = jsonObjectList.getJSONArray("get_detail");
                            {
                                JSONObject jsonObjectList = jsonArray.getJSONObject(0);
                                if (jsonObjectList != null && jsonObjectList.length() != 0) {
                                    String userimage = jsonObjectList.getString("userimage");
                                    String name = jsonObjectList.getString("name");
                                    String phone = jsonObjectList.getString("phone");
                                    String email = jsonObjectList.getString("email");
                                    String address = jsonObjectList.getString("address");
                                    String landmark = jsonObjectList.getString("landmark");
                                    String city = jsonObjectList.getString("city");
                                    String pincode = jsonObjectList.getString("pincode");
                                    String date_of_birth = jsonObjectList.getString("date_of_birth");
                                    String mrg_anniversary = jsonObjectList.getString("mrg_anniversary");

                                    AppPreference.setPreference(context, AppPersistance.keys.USERIMAGE, userimage);
                                    AppPreference.setPreference(context, AppPersistance.keys.USER_NAME, name);
                                    AppPreference.setPreference(context, AppPersistance.keys.USER_NUMBER, phone);
                                    AppPreference.setPreference(context, AppPersistance.keys.USER_EMAIL, email);
                                    AppPreference.setPreference(context, AppPersistance.keys.USER_ADDRESS, address);
                                    AppPreference.setPreference(context, AppPersistance.keys.LANDMARK, landmark);
                                    AppPreference.setPreference(context, AppPersistance.keys.CITY, city);
                                    AppPreference.setPreference(context, AppPersistance.keys.PINCODE, pincode);
                                    AppPreference.setPreference(context, AppPersistance.keys.DOB, date_of_birth);
                                    AppPreference.setPreference(context, AppPersistance.keys.MRG_ANVSRY, mrg_anniversary);
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
                setData();
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void init(View v) {
        tvTitle = context.findViewById(R.id.toolbar_title);
        tvTitle.setText("My Profile");
        tvCity = v.findViewById(R.id.tvCity);
        etName = v.findViewById(R.id.etName);
        etEmail = v.findViewById(R.id.etEmail);
        etMobileNo = v.findViewById(R.id.etMobileNo);
        etAddress = v.findViewById(R.id.etAddress);
        etLandmark = v.findViewById(R.id.etLandmark);
        btnSubmit = v.findViewById(R.id.btnSubmit);
        imgChangeImage = v.findViewById(R.id.imgChangeImage);
        imgUpdateImage = v.findViewById(R.id.imgUpdateimage);
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
                    new ChangeProfile().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();

                }
            }
        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                try {
                    Bitmap bitmap;

                    bitmap = Global.getSampleBitmapFromFile(fileImage.getAbsolutePath(), 500, 500);

                    OutputStream outFile;
                    try {
                        outFile = new FileOutputStream(fileImage);
                        assert bitmap != null;
                        bitmap.compress(Bitmap.CompressFormat.PNG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    imgUpdateImage.setImageBitmap(bitmap);
                    checkFile = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == 2) {
                try {
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    assert selectedImage != null;
                    Cursor c = context.getContentResolver().query(selectedImage, filePath, null, null, null);
                    assert c != null;
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    String picturePath = c.getString(columnIndex);
                    c.close();
                    Bitmap thumbnail;
                    BitmapFactory.Options options;

                    try {
                        thumbnail = (BitmapFactory.decodeFile(picturePath));
                    } catch (Exception e) {
                        try {
                            options = new BitmapFactory.Options();
                            options.inSampleSize = 2;
                            thumbnail = BitmapFactory.decodeFile(picturePath, options);
                        } catch (Exception yy) {
                            yy.printStackTrace();
                        }
                    }

                    thumbnail = Global.getSampleBitmapFromFile(picturePath, 300, 300);

                    imgUpdateImage.setImageBitmap(thumbnail);
                    checkFile = true;
                    fileImage = new File(picturePath);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    assert thumbnail != null;
                    thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("Permissions", "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Log.d("Permissions", "Permission Denied: " + permissions[i]);
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
}
