package com.washonwheel.android.location;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/*
 * Created by Gautam on 13-02-2018.
 */

public class Location {

    private LocationInterface locationInterface;

    private Context context;

    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog progressBar;

    public Location(Context context, LocationInterface locationInterface) {
        this.context = context;
        this.locationInterface = locationInterface;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) context)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) context)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void findLocation() {
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
                        progressBar = new ProgressDialog(context);
                        progressBar.setCancelable(true);
                        progressBar.setMessage("Getting Current Location...");
                        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressBar.setIndeterminate(true);
                        progressBar.show();
                        getUserLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        locationInterface.onResolve(status);
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        locationInterface.onFailure();
                        break;
                }
            }
        });
    }

    private void getUserLocation() {
        GPSService mGPSService = new GPSService(context);
        mGPSService.getLocation();
        if (!mGPSService.isLocationAvailable) {
            progressBar.cancel();
            Toast.makeText(context, "Your location is not available, please try again.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            double latitude = mGPSService.getLatitude();
            double longitude = mGPSService.getLongitude();
            progressBar.cancel();
            locationInterface.onSuccess(latitude, longitude);
        }
        mGPSService.closeGPS();
    }
}