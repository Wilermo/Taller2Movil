package com.example.taller2compumovil.services;

import android.content.Context;
import android.util.Log;

import com.example.taller2compumovil.model.GeoInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import lombok.Getter;

@Getter
@Module
@InstallIn(ActivityComponent.class)
public class GeoInfoFromJsonService {
    public static final String TAG = GeoInfoFromJsonService.class.getName();
    private final Context context;
    private ArrayList<GeoInfo> geoInfoList = new ArrayList<>();

    @Inject
    public GeoInfoFromJsonService(@ApplicationContext Context context) {
        this.context = context;
        //loadGeoInfoFromJson();
    }


}
