package com.ruslanlyalko.snoopyapp.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 20.01.2018.
 */
public class LocationHandler {

    public static final int REQUEST_LOCATION = 9999;
    private static final String TAG = "LocationHandler";
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 9998;
    private static final int INTERVAL = 10 * 1000;
    private static final int FASTEST_INTERVAL = 5 * 1000;
    private static final float SMALLEST_DISPLACEMENT = 10;

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private List<LocationListener> mCustomLocationListener;
    private OnLocationRequest mOnLocationRequest;
    private boolean mOneTimeAsked;
    private boolean mAlreadyAsked;
    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener = connectionResult -> {
        if (mOnLocationRequest != null)
            mOnLocationRequest.onError(new Exception("Connection Failed"));
    };
    private com.google.android.gms.location.LocationListener mGmsLocationListener = new com.google.android.gms.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "Location updated");
            if (mCustomLocationListener != null) {
                for (int i = mCustomLocationListener.size() - 1; i >= 0; i--) {
                    mCustomLocationListener.get(i).handleUpdateLocation(location);
                }
            }
        }
    };
    private com.google.android.gms.location.LocationListener mGmsLocationOneTimeListener = new com.google.android.gms.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "Location finded");
            if (mCustomLocationListener != null) {
                for (int i = mCustomLocationListener.size() - 1; i >= 0; i--) {
                    mCustomLocationListener.get(i).handleFindLocation(location);
                }
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mGmsLocationOneTimeListener);
            }
        }
    };
    private ResultCallback<LocationSettingsResult> mLocationSettingsResultResultCallback = result -> {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                getLocation();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.d(TAG, "Try to enable GPS");
                try {
                    if (!mAlreadyAsked) {
                        status.startResolutionForResult(getContext(), REQUEST_LOCATION);
                        mAlreadyAsked = true;
                        return;
                    }
                    if (!mOneTimeAsked)
                        status.startResolutionForResult(getContext(), REQUEST_LOCATION);
                } catch (IntentSender.SendIntentException e) {
                    // Ignore the error.
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.d(TAG, "Cannot change gps settings");
                if (mCustomLocationListener != null) {
                    for (int i = mCustomLocationListener.size() - 1; i >= 0; i--) {
                        mCustomLocationListener.get(i).handleDisabledGPSError();
                    }
                }
                break;
        }
    };
    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            initLocationSettings();
        }

        @Override
        public void onConnectionSuspended(int i) {
            if (mOnLocationRequest != null)
                mOnLocationRequest.onError(new Exception("Connection Suspended"));
        }
    };

    public LocationHandler(Context context) {
        mContext = context;
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mOnConnectionFailedListener).build();
    }

    public void getLocation(OnLocationRequest onLocationRequest) {
        mOnLocationRequest = onLocationRequest;
        initLocationSettings();
    }

    @SuppressLint("MissingPermission")
    private void initLocationSettings() {
        if (!checkPermissions()) return;
        if (!mGoogleApiClient.isConnected()) return;
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(mLocationSettingsResultResultCallback);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mGmsLocationListener);
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (mContext instanceof BaseFragment) {
//                    Log.d(TAG, "Ask permissions from fragment");
//                    ((Activity) mContext).requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
//                            REQUEST_CODE_ASK_PERMISSIONS);
//                } else
                if (mContext instanceof Activity) {
                    Log.d(TAG, "Ask permissions from activity");
                    ((Activity) mContext).requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_CODE_ASK_PERMISSIONS);
                }
            }
            return false;
        }
        return true;
    }

    public Activity getContext() {
        if (mContext instanceof Activity)
            return ((Activity) mContext);
        else
            return null;
    }

    public void setForOneTimeAsked() {
        mOneTimeAsked = true;
    }

    public void addLocationListener(LocationListener listener) {
        if (mCustomLocationListener == null) {
            mCustomLocationListener = new ArrayList<>();
        }
        mCustomLocationListener.add(listener);
    }

    public void removeLocationListener(LocationListener listener) {
        if (mCustomLocationListener != null) {
            mCustomLocationListener.remove(listener);
        }
    }

    public void clearLocationListeners() {
        if (mCustomLocationListener != null) {
            mCustomLocationListener.clear();
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Log.d(TAG, "GPS Enabled");
                        getLocation();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        Log.d(TAG, "GPS Enable Canceled");
                        if (mCustomLocationListener != null) {
                            for (int i = mCustomLocationListener.size() - 1; i >= 0; i--) {
                                mCustomLocationListener.get(i).handleDisabledGPSError();
                            }
                        }
                        if (mOnLocationRequest != null)
                            mOnLocationRequest.onError(new Exception("GPS Canceled"));
                        break;
                    }
                }
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        if (!checkPermissions()) return;
        if (!mGoogleApiClient.isConnected()) return;
        LocationServices.getFusedLocationProviderClient(getContext()).getLastLocation()
                .addOnCompleteListener(task -> task.addOnSuccessListener(location -> {
                    Log.d(TAG, "Is connected: " + mGoogleApiClient.isConnected());
                    Log.d(TAG, "Location: " + (location != null));
                    if (location == null) {
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mGmsLocationOneTimeListener);
                        return;
                    }
                    if (mCustomLocationListener != null) {
                        for (int i = mCustomLocationListener.size() - 1; i >= 0; i--) {
                            mCustomLocationListener.get(i).handleFindLocation(location);
                        }
                    }
                    if (mOnLocationRequest != null) {
                        mOnLocationRequest.onFindLocation(location);
                    }
                }).addOnFailureListener(e -> {
                    if (mCustomLocationListener != null) {
                        for (int i = mCustomLocationListener.size() - 1; i >= 0; i--) {
                            mCustomLocationListener.get(i).handleNotFindLocation(e);
                        }
                    }
                    if (mOnLocationRequest != null) {
                        mOnLocationRequest.onError(e);
                    }
                }));
    }

    public void handleRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (isPermissionsGranted(grantResults)) {
                    Log.d(TAG, "All perms are granted");
                    initLocationSettings();
                } else {
                    Log.d(TAG, "Not all perms are granted");
                    if (mCustomLocationListener != null) {
                        for (int i = mCustomLocationListener.size() - 1; i >= 0; i--) {
                            mCustomLocationListener.get(i).handleNoPermissionError();
                        }
                    }
                    if (mOnLocationRequest != null)
                        mOnLocationRequest.onError(new Exception("No permissions"));
                }
                break;
        }
    }

    private boolean isPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults)
            if (grantResult != PackageManager.PERMISSION_GRANTED)
                return false;
        return true;
    }

    public void onResume() {
        mGoogleApiClient.connect();
    }

    public void onPause() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public interface LocationListener {

        void handleFindLocation(Location location);

        void handleUpdateLocation(Location location);

        void handleDisabledGPSError();

        void handleNoPermissionError();

        void handleNotFindLocation(Exception e);
    }

    public interface OnLocationRequest {

        void onFindLocation(Location location);

        void onError(Exception e);
    }
}
