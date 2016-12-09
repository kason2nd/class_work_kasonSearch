package com.example.kasonSearch;


import com.baidu.mapapi.SDKInitializer;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;


public class LocationApplication extends Application {
	public LocationService locationService;
    public Vibrator mVibrator;
    public MainActivity mapActivity;
    @Override
    public void onCreate() {
        super.onCreate();
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());  
       
    }
}
