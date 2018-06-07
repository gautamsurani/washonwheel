package com.washonwheel.android.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.washonwheel.android.Activity.SelectServiceActivity;
import com.washonwheel.android.Adapter.VehicleListAdapter;
import com.washonwheel.android.Pojo.CityList;
import com.washonwheel.android.Pojo.ModelList;
import com.washonwheel.android.Pojo.TimeSlotList;
import com.washonwheel.android.Pojo.VehicleList;
import com.washonwheel.android.R;
import com.washonwheel.android.Util.AppConstant;
import com.washonwheel.android.Util.Global;
import com.washonwheel.android.Util.RequestMethod;
import com.washonwheel.android.Util.RestClient;
import com.washonwheel.android.Util.Utils;
import com.washonwheel.android.location.GPSService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Book_Services extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView btnBook;

    private LinearLayout llHatchback, llSedan, llPremium;

    private TextView textType1, textType2, textType3;

    TextView tvTitle;

    TextView tvDate;

    Activity context;

    LinearLayoutManager mLayoutManager;

    Global global;

    String UserId, resMessage = "", resCode = "", vehicle = "", days = "", modelTypeId = "", selectedCityId = "", selectedTimeSlot = "";

    ProgressDialog progressDialog;

    public static final int REQUEST_ID = 2;

    List<ModelList> modelLists = new ArrayList<>();

    List<CityList> cityLists = new ArrayList<>();

    List<TimeSlotList> timeSlotLists = new ArrayList<>();

    ArrayList<String> city = new ArrayList<>();

    ArrayList<String> timeSlot = new ArrayList<>();

    Spinner spiCity, spiTimeSlot;

    String Name = "", MobileNo = "", Address = "", Date = "", VehicleNo = "";

    EditText etAddress, etName, etMobileNo, etVNo1, etVNo2, etVNo3, etVNo4;

    Button btnSelectCar;

    List<VehicleList> vehicleLists = new ArrayList<>();

    VehicleListAdapter vehicleListAdapter;

    Dialog dialog;

    ImageView ivLocation;

    GoogleApiClient mGoogleApiClient;

    private static final int REQUEST_CHECK_SETTINGS = 151;

    ProgressDialog progressBar;

    boolean editAddress = true;

    double latitude, longitude;

    String strLatitude = "", strLongitude = "";

    public Book_Services() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (mGoogleApiClient.isConnected()) {
                            Toast.makeText(getActivity(), "Location enabled!", Toast.LENGTH_LONG).show();
                            progressBar = new ProgressDialog(getActivity());
                            progressBar.setCancelable(true);
                            progressBar.setMessage("Getting Location...");
                            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressBar.setIndeterminate(true);
                            progressBar.show();
                            getUserLocation();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getActivity(), "Location not enabled..", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.book_service, container, false);

        tvTitle = getActivity().findViewById(R.id.toolbar_title);
        tvTitle.setText("Select Your Car");

        context = getActivity();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        global = new Global(context);

        init(v);

        btnBook.setOnClickListener(this);
        llHatchback.setOnClickListener(this);
        llSedan.setOnClickListener(this);
        llPremium.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        btnSelectCar.setOnClickListener(this);
        ivLocation.setOnClickListener(this);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        tvDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(day).append("-").append(month + 1).append("-")
                .append(year).append(" "));

        UserId = Utils.getUserId(context);
        Name = Utils.getUserName(context);
        MobileNo = Utils.getMobileNo(context);
        Address = Utils.getAddress(context);

        etAddress.setText(Address);
        etName.setText(Name);
        etMobileNo.setText(MobileNo);

        if (global.isNetworkAvailable()) {
            new GetBookServiceData().execute();
        } else {
            retryInternet();
        }

        spiCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    selectedCityId = cityLists.get(position).getCityId();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spiTimeSlot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    selectedTimeSlot = timeSlotLists.get(position - 1).getName();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return v;
    }

    private void init(View v) {
        btnBook = v.findViewById(R.id.btnBook);
        llHatchback = v.findViewById(R.id.llHatchback);
        llSedan = v.findViewById(R.id.llSedan);
        llPremium = v.findViewById(R.id.llPremium);
        textType1 = v.findViewById(R.id.textHatchback);
        textType2 = v.findViewById(R.id.textSedan);
        textType3 = v.findViewById(R.id.textPremium);
        tvDate = v.findViewById(R.id.tvDate);
        spiCity = v.findViewById(R.id.spiCity);
        spiTimeSlot = v.findViewById(R.id.spiTimeSlot);
        etAddress = v.findViewById(R.id.etAddress);
        etName = v.findViewById(R.id.etName);
        etMobileNo = v.findViewById(R.id.etMobileNo);
        etVNo1 = v.findViewById(R.id.etVNo1);
        etVNo2 = v.findViewById(R.id.etVNo2);
        etVNo3 = v.findViewById(R.id.etVNo3);
        etVNo4 = v.findViewById(R.id.etVNo4);
        btnSelectCar = v.findViewById(R.id.btnSelectCar);
        ivLocation = v.findViewById(R.id.ivLocation);
    }

    @Override
    public void onClick(View v) {
        if (v == btnBook) {
            editAddress = false;
            Date = tvDate.getText().toString();
            Name = etName.getText().toString();
            MobileNo = etMobileNo.getText().toString();
            Address = etAddress.getText().toString();
            VehicleNo = etVNo1.getText().toString()
                    + "-" + etVNo2.getText().toString()
                    + "-" + etVNo3.getText().toString()
                    + "-" + etVNo4.getText().toString();

            if (etVNo1.getText().toString().equals("") || etVNo2.getText().toString().equals("") ||
                    etVNo3.getText().toString().equals("") || etVNo4.getText().toString().equals("")) {
                Toast.makeText(context, "Please enter car number", Toast.LENGTH_SHORT).show();
            } else if (strLatitude.equals("") || strLongitude.equals("")) {
                findLocation();
            } else {
                Intent intent = new Intent(context, SelectServiceActivity.class);
                intent.putExtra("ModelType", modelTypeId);
                intent.putExtra("Date", Date);
                intent.putExtra("Name", Name);
                intent.putExtra("MobileNo", MobileNo);
                intent.putExtra("Address", Address);
                intent.putExtra("VehicleNo", VehicleNo);
                intent.putExtra("CityId", selectedCityId);
                intent.putExtra("TimeSlot", selectedTimeSlot);
                intent.putExtra("Latitude", strLatitude);
                intent.putExtra("Longitude", strLongitude);
                startActivity(intent);
                context.overridePendingTransition(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);
            }

        } else if (v == llHatchback) {
            modelTypeId = modelLists.get(0).getTypeID();
            llHatchback.setBackgroundResource(R.drawable.line_below);
            llSedan.setBackgroundResource(R.drawable.line_remove);
            llPremium.setBackgroundResource(R.drawable.line_remove);
            textType1.setTextColor(getResources().getColor(R.color.colorPrimary));
            textType2.setTextColor(getResources().getColor(R.color.colorBlack));
            textType3.setTextColor(getResources().getColor(R.color.colorBlack));
        } else if (v == llSedan) {
            modelTypeId = modelLists.get(1).getTypeID();
            llHatchback.setBackgroundResource(R.drawable.line_remove);
            llSedan.setBackgroundResource(R.drawable.line_below);
            llPremium.setBackgroundResource(R.drawable.line_remove);
            textType1.setTextColor(getResources().getColor(R.color.colorBlack));
            textType2.setTextColor(getResources().getColor(R.color.colorPrimary));
            textType3.setTextColor(getResources().getColor(R.color.colorBlack));

        } else if (v == llPremium) {
            modelTypeId = modelLists.get(2).getTypeID();
            llHatchback.setBackgroundResource(R.drawable.line_remove);
            llSedan.setBackgroundResource(R.drawable.line_remove);
            llPremium.setBackgroundResource(R.drawable.line_below);
            textType1.setTextColor(getResources().getColor(R.color.colorBlack));
            textType2.setTextColor(getResources().getColor(R.color.colorBlack));
            textType3.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else if (v == tvDate) {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR); // current year
            int mMonth = c.get(Calendar.MONTH); // current month
            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
            // date picker dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            // set day of month , month and year value in the edit text
                            tvDate.setText(dayOfMonth + "-"
                                    + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 60 * 1000 * 60 * 24 * Integer.parseInt(days));
            datePickerDialog.show();
        } else if (v == btnSelectCar) {
            openDialog();
        } else if (v == ivLocation) {
            editAddress = true;
            findLocation();
        }
    }

    private void findLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ID);
            return;
        }

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        progressBar = new ProgressDialog(getActivity());
                        progressBar.setCancelable(true);
                        progressBar.setMessage("Getting Current Location...");
                        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressBar.setIndeterminate(true);
                        progressBar.show();
                        getUserLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    private void openDialog() {
        dialog = new BottomSheetDialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        dialog.setContentView(R.layout.select_car_layout);

        RecyclerView rvSelectCar = dialog.findViewById(R.id.rvSelectCar);

        String Delete = "No";

        mLayoutManager = new LinearLayoutManager(context);
        rvSelectCar.setLayoutManager(mLayoutManager);
        rvSelectCar.setHasFixedSize(true);
        vehicleListAdapter = new VehicleListAdapter(context, vehicleLists, Delete);
        rvSelectCar.setAdapter(vehicleListAdapter);

        vehicleListAdapter.setOnItemClickListener(new VehicleListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int which) {
                if (which == 3) {
                    etVNo1.setText(vehicleLists.get(position).getVeh_state_code());
                    etVNo2.setText(vehicleLists.get(position).getVeh_city_code());
                    etVNo3.setText(vehicleLists.get(position).getVeh_series());
                    etVNo4.setText(vehicleLists.get(position).getVeh_no());
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void getUserLocation() {
        GPSService mGPSService = new GPSService(context);
        mGPSService.getLocation();
        String addressaaa = "", city = "", state = "", country = "";
        if (!mGPSService.isLocationAvailable) {
            progressBar.cancel();
            Toast.makeText(context, "Your location is not available, please try again.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            latitude = mGPSService.getLatitude();
            longitude = mGPSService.getLongitude();

            strLatitude = String.valueOf(latitude);
            strLongitude = String.valueOf(longitude);
            mGPSService.getLocationAddress();
            Geocoder geocoder;
            List<android.location.Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                addressaaa = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        addressaaa = addressaaa.replace(", " + city, "");
        addressaaa = addressaaa.replace(", " + state, "");
        addressaaa = addressaaa.replace(", " + country, "");

        progressBar.cancel();
        if (editAddress) {
            etAddress.append(addressaaa + "");
        } else {
            Intent intent = new Intent(context, SelectServiceActivity.class);
            intent.putExtra("ModelType", modelTypeId);
            intent.putExtra("Date", Date);
            intent.putExtra("Name", Name);
            intent.putExtra("MobileNo", MobileNo);
            intent.putExtra("Address", Address);
            intent.putExtra("VehicleNo", VehicleNo);
            intent.putExtra("CityId", selectedCityId);
            intent.putExtra("TimeSlot", selectedTimeSlot);
            intent.putExtra("Latitude", strLatitude);
            intent.putExtra("Longitude", strLongitude);
            startActivity(intent);
            context.overridePendingTransition(R.anim.fragment_animation_fade_in, R.anim.fragment_animation_fade_out);
        }
        mGPSService.closeGPS();
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
                    new GetBookServiceData().execute();
                } else {
                    Toast.makeText(context, R.string.nonetwork, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
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
    private class GetBookServiceData extends AsyncTask<String, Void, String> {
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

            String strAPI = AppConstant.API_BOOK_SERVICE_DATA + UserId;

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
                Log.e("API", Register);

                if (Register != null && Register.length() != 0) {
                    jsonObjectList = new JSONObject(Register);
                    if (jsonObjectList.length() != 0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        days = jsonObjectList.getString("days");
                        vehicle = jsonObjectList.getString("vehicle");
                        if (resCode.equalsIgnoreCase("0")) {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("model_list");
                            {
                                if (jsonArray != null && jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        ModelList modelList = new ModelList();
                                        JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                        modelList.setTypeID(jsonObjectList.getString("typeID"));
                                        modelList.setName(jsonObjectList.getString("name"));
                                        modelLists.add(modelList);
                                    }
                                }
                            }
                            JSONArray jsonArray1 = jsonObjectList.getJSONArray("city_list");
                            {
                                if (jsonArray1 != null && jsonArray1.length() != 0) {
                                    for (int i = 0; i < jsonArray1.length(); i++) {
                                        CityList cityList = new CityList();
                                        JSONObject jsonObjectList = jsonArray1.getJSONObject(i);
                                        cityList.setCityId(jsonObjectList.getString("cityID"));
                                        cityList.setCityName(jsonObjectList.getString("name"));
                                        cityLists.add(cityList);
                                        city.add(jsonObjectList.getString("name"));
                                    }
                                }
                            }
                            JSONArray jsonArray2 = jsonObjectList.getJSONArray("timeslot");
                            {
                                timeSlot.add("Time Slot");
                                if (jsonArray2 != null && jsonArray2.length() != 0) {
                                    for (int i = 0; i < jsonArray2.length(); i++) {
                                        TimeSlotList timeSlotList = new TimeSlotList();
                                        JSONObject jsonObjectList = jsonArray2.getJSONObject(i);
                                        timeSlotList.setName(jsonObjectList.getString("name"));
                                        timeSlot.add(jsonObjectList.getString("name"));
                                        timeSlotList.setSlotID(jsonObjectList.getString("slotID"));
                                        timeSlotList.setStatus(jsonObjectList.getString("status"));
                                        timeSlotLists.add(timeSlotList);
                                    }
                                }
                            }
                            if (vehicle.equals("Yes")) {
                                JSONArray jsonArray3 = jsonObjectList.getJSONArray("vehicle_list");
                                {
                                    if (jsonArray3 != null && jsonArray3.length() != 0) {
                                        for (int i = 0; i < jsonArray3.length(); i++) {
                                            VehicleList vehicleList = new VehicleList();
                                            JSONObject jsonObjectList = jsonArray3.getJSONObject(i);
                                            vehicleList.setVehID(jsonObjectList.getString("vehID"));
                                            vehicleList.setCar_no(jsonObjectList.getString("car_no"));
                                            vehicleList.setVeh_state_code(jsonObjectList.getString("veh_state_code"));
                                            vehicleList.setVeh_city_code(jsonObjectList.getString("veh_city_code"));
                                            vehicleList.setVeh_series(jsonObjectList.getString("veh_series"));
                                            vehicleList.setVeh_no(jsonObjectList.getString("veh_no"));
                                            vehicleLists.add(vehicleList);
                                        }
                                    }
                                }
                            } else {
                                btnSelectCar.setVisibility(View.GONE);
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
                textType1.setText(modelLists.get(0).getName());
                textType2.setText(modelLists.get(1).getName());
                textType3.setText(modelLists.get(2).getName());
                modelTypeId = modelLists.get(0).getTypeID();

                ArrayAdapter aa = new ArrayAdapter(context, R.layout.spinner_item, city);
                aa.setDropDownViewResource(R.layout.spinner_item);
                spiCity.setAdapter(aa);

                selectedCityId = cityLists.get(0).getCityId();

                ArrayAdapter aa1 = new ArrayAdapter(context, R.layout.spinner_item, timeSlot);
                aa1.setDropDownViewResource(R.layout.spinner_item);
                spiTimeSlot.setAdapter(aa1);
            } else {
                Toast.makeText(context, resMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }
}