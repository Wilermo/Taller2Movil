package com.example.taller2compumovil.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Module
@InstallIn(ActivityComponent.class)
public class LocationService {
    public static final String TAG = LocationService.class.getName();
    private final Context context;

    //Location variables
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    @Setter
    LocationCallback locationCallback;

    @Inject
    public LocationService(@ApplicationContext Context context) {
        this.context = context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        locationRequest = createLocationRequest();
    }

    protected LocationRequest createLocationRequest() {
        return new LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, TimeUnit.SECONDS.toMillis(5))
            .build();
    }

    @SuppressLint("MissingPermission")
    public void startLocation() {
        if (locationCallback != null) {
            Log.d(TAG, "startLocation: Start location updates.");
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            SettingsClient client = LocationServices.getSettingsClient(context);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                task.addOnSuccessListener(context.getMainExecutor(), locationSettingsResponse -> {
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                });
            }
        } else {
            Log.e(TAG, "startLocation() returned: locationCallback is null, please define it first.");
        }
    }

    public void stopLocation() {
        Log.d(TAG, "stopLocation: Stopping location updates.");
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
