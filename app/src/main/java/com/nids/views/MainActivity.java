package com.nids.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.location.LocationManager;

import com.nids.data.VOOutdoor;
import com.nids.data.VOSensorData;
import com.nids.data.VOStation;
import com.nids.data.VOUser;
import com.nids.kind4u.testapp.R;
import com.nids.util.gps.GpsTracker;
import com.nids.util.interfaces.NetworkCallBackInterface;
import com.nids.util.network.CommunicationUtil;

import org.apache.http.impl.execchain.MainClientExec;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    CommunicationUtil c_util;
    InsideFragment insideFragment;
    OutsideFragment outsideFragment;
    Button btn_analysis;
    String station_name;
    VOOutdoor data;

    NetworkCallBackInterface callBackInterface;

    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE=100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION};                    // GPS 측정을 위한 퍼미션 요구사항들

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkLocationServiceStatus())   {                   // 177줄 - 위치 서비스 활성화 여부 확인
            showDialogForLocationServiceSetting();              // 139줄 - 위치 설정 허용 확인 메세지 출력
        }   else    {
            checkRunTimePermission();                           // 160줄 - 런타임 퍼미션 실행
        }
        insideFragment = new InsideFragment();
        outsideFragment = new OutsideFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container1,insideFragment);
        getSupportFragmentManager().beginTransaction().replace(R.id.container2,outsideFragment);
        btn_analysis = findViewById(R.id.AnalysisButton);
        btn_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"아직 안 만듬",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {       //모름... startActivityForResult랑 연관있다고 함
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)    {
            case GPS_ENABLE_REQUEST_CODE:

                // 사용자가 GPS를 활성화 시켰는지 검사
                if(checkLocationServiceStatus())    {
                    Log.d("@@@","onActivityResult : GPS 활성화 되었음");
                    checkRunTimePermission();       // 160줄 - 런타임 퍼미션 실행
                   return;
                }
                break;
        }
    }

    public Map<String, Object> getData() {
        callBackInterface = new NetworkCallBackInterface() {        // callback 값을 받기 위한 callback Interface 호출
            @Override
            public void signInResult(boolean result, String message, VOUser userinfo) {
            }

            @Override
            public void findStation(boolean result, VOStation station_info) {       // 현재 위치에서 가까운 측정소의 정보
                station_name = station_info.getStationName();
                c_util.getOutDoorData(station_name);        // 받은 축정소 정보를 파라미터로 한 후 해당 측정소 데이터 불러오기
            }

            @Override
            public void dataReqResult(boolean result, List<VOSensorData> data) {
            }

            @Override
            public void dataReqResultOutdoor(boolean result, VOOutdoor data) {      // 측정소에서 측정한 미세먼지 데이터
                if(result){ setData(data);}         // 정상적으로 데이터 수신 시 data 객체 내에 set
            }
        };

        c_util = new CommunicationUtil(callBackInterface);      // 해당 Interface를 가지고 있는 Communication 객체 생성

        gpsTracker = new GpsTracker(MainActivity.this);     // com.nids.util.gps.GpsTracker.java
        Map<String, Object> map = new HashMap<String, Object>();
        String latitude = String.valueOf(gpsTracker.getLatitude());         // 위도 측정
        String longitude = String.valueOf(gpsTracker.getLongitude());       // 경도 측정
        System.out.println("latitude = " + latitude);
        System.out.println("longitude = " + longitude);
        //c_util.findStationWithGPS(latitude, longitude);                         // 받은 위경도 값으로 근처 측정소 검색
        c_util.findStationWithGPS("37.441722","127.171786");              // ※ Virtual Device는 위경도를 측정할 수 없음
        while(true) {
            if(data != null) {                  // 측정소 및 미세먼지 데이터가 들어올 때까지 강제로 Holding (좋은 방법은 아닌 듯)
                map.put("station_name", station_name);      // 측정소 이름
                map.put("data", data);                      // 측정 미세먼지 데이터 객체
                return map;                                 // outsideFragment로 전송
            }   else    {}
        }
    }

    private void showDialogForLocationServiceSetting()  {           // 위치 설정 허용 확인 메세지 출력
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("측정소 위치를 사용하기 위해 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);      // 모름... onActivityResult랑 연관있다고 함
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    void checkRunTimePermission()   {
        // 런타임 퍼미션 처리
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED)  {
                        // 둘 다 퍼미션이 허용되어있는 경우 그냥 넘어감, 퍼미션이 하나라도 거부되어있으면 오류
        }   else    {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }   else    {
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    public boolean checkLocationServiceStatus() {           // 위치 서비스 활성화 여부 확인
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setData(VOOutdoor data) {
        this.data = data;
    }

}
