package com.example.taller2compumovil;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.taller2compumovil.databinding.MapaActivityBinding;
import com.example.taller2compumovil.fragments.MapsFragment;
import com.example.taller2compumovil.services.LocationService;
import com.example.taller2compumovil.services.PermissionService;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapaActivity extends MainActivity
{
    public static final String TAG = MainActivity.class.getName();

    MapaActivityBinding binding;

    @Inject
    PermissionService permissionService;

    @Inject
    LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MapaActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        locationService.setLocationCallback(new LocationCallback() {
            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i(TAG, "onLocationResult: " + locationResult.getLastLocation().toString());
                MapsFragment fragment = binding.map.getFragment();
                fragment.updateUserPositionOnMap(locationResult);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        permissionService.getLocationPermission(this);
        if (permissionService.isMLocationPermissionGranted()) {
            locationService.startLocation();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationService.stopLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionService.PERMISSIONS_REQUEST_LOCATION) {
            permissionService.getLocationPermission(this);
            if (permissionService.isMLocationPermissionGranted()) {
                locationService.startLocation();
            }
        }
    }

}
