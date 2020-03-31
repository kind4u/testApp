package com.nids.util.gps;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.nids.views.MainActivity;

public class GpsTracker extends Service implements LocationListener {

    MainActivity mainActivity;
    private final Context mContext;
    Location location;
    double latitude;
    double longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    protected LocationManager locationManager;


    public  GpsTracker (Context context)    {           // 생성자 생성 시 위치정보 탐색
        this.mContext = context;
        mainActivity = new MainActivity();
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        getLocation();
    }

    public Location getLocation()   {
        try {
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // GPS와 Network 퍼미션이 둘 중 하나라도 허용되어있을 때만 실행(둘 다 허용되어 있지 않으면 실행이 안됨)
            } else {
                int hasFineLocationPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);      // 위치정보 퍼미션 확인 1
                int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);  // 위치정보 퍼미션 확인 2

                if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
                    //왜인지 비어있음
                } else return null;

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
                            // 위치정보 수신 후 확인
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);      // 가장 최근 검색된 위치정보 수신
                        if (location != null) {     // 성공적으로 수신 시 위/경도 세팅
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {         //51~61 줄이랑 코드 같음
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            Log.d("@@@",""+e.toString());
        }
        return location;
    }

    public double getLatitude() {
        if(location != null)    {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude()    {
        if(location != null)    {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    // 이 아래는 입력하지 않은 Override 세팅들 (추후 개발 가능성 있음)

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        // 위치값이 갱신되면 이벤트 발생
        Log.d("test","onLocationChanged, location : " + location);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
