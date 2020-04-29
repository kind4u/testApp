package com.nids.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.location.LocationManager;

import com.google.android.material.tabs.TabLayout;
import com.nids.data.VOOutdoor;
import com.nids.data.VOSensorData;
import com.nids.data.VOStation;
import com.nids.data.VOUser;
import com.nids.kind4u.testapp.R;
import com.nids.util.BackPressCloseHandler;
import com.nids.util.PushService;
import com.nids.util.TabPagerAdapter;
import com.nids.util.WorkManager;
import com.nids.util.gps.GpsTracker;
import com.nids.util.interfaces.NetworkCallBackInterface;
import com.nids.util.network.CommunicationUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    InsideFragment insideFragment;
    OutsideFragment outsideFragment;
    Button btn_analysis;
    String station_name = "";
    List<VOSensorData> inDoorDataList;
    VOSensorData inDoorData;
    VOOutdoor data = new VOOutdoor();

    // Bluetooth variable 모음들
    private static final int REQUEST_ENABLE_BT = 10;    // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter;          // 블루투스 어댑터
    private int pariedDeviceCount;                      // 페어링 된 디바이스 크기
    private Set<BluetoothDevice> devices;               // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice;            // 블루투스 디바이스
    private BluetoothSocket bluetoothSocket = null;     // 블루투스 소켓
    private OutputStream outputStream = null;           // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null;             // 블루투스에 데이터를 입력하기 위한 입력 스트림
    private Thread workerThread = null;                 // 문자열 수신에 사용되는 쓰레드
    private byte[] readBuffer;                          // 수신 된 문자열을 저장하기 위한 버퍼
    private int readBufferPosition;                     // 버퍼 내 문자 저장 위치
    // end of Bluetooth variable

    private String id;

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id =id;
    }

    public BackPressCloseHandler backPressCloseHandler;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    NetworkCallBackInterface callBackInterface = new NetworkCallBackInterface() {        // callback 값을 받기 위한 callback Interface 호출
        @Override
        public void signInResult(boolean result, String message, VOUser userinfo) { }
        @Override
        public void modifyResult(boolean result) { }

        @Override
        public void findStation(boolean result, VOStation station_info) {       // 현재 위치에서 가까운 측정소의 정보
            station_name = station_info.getStationName();
            c_util.getOutDoorData(station_name);        // 받은 축정소 정보를 파라미터로 한 후 해당 측정소 데이터 불러오기
        }

        @Override
        public void dataReqResult(String result, List<VOSensorData> dataList) {
            if(result.equals("200"))  {
                    inDoorDataList = dataList;
                    inDoorData = inDoorDataList.get(0);
                    setInDoorData(inDoorData);
            }
        }

        @Override
        public void dataReqResultOutdoor(boolean result, VOOutdoor data) {      // 측정소에서 측정한 미세먼지 데이터
            if(result){ setData(data);}         // 정상적으로 데이터 수신 시 data 객체 내에 set
        }
    };

    CommunicationUtil c_util = new CommunicationUtil(callBackInterface);      // 해당 Interface를 가지고 있는 Communication 객체 생성

    static GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE=100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION};                    // GPS 측정을 위한 퍼미션 요구사항들


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        id = intent.getExtras().get("id").toString();

        setContentView(R.layout.activity_main);

        if(!checkLocationServiceStatus())   {                   // 177줄 - 위치 서비스 활성화 여부 확인
            showDialogForLocationServiceSetting();              // 139줄 - 위치 설정 허용 확인 메세지 출력
        }   else    {
            checkRunTimePermission();                           // 160줄 - 런타임 퍼미션 실행
        }

        // 블루투스 활성화
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();        // bluetoothAdapter를 기본 어댑터로 설정
        if(bluetoothAdapter == null)    {
            //bluetooth 기능을 지원하지 않음
        }   else    {
            if(bluetoothAdapter.isEnabled())    {       // 블루투스 활성화 상태
                selectBluetoothDevice();    // 블루투스 디바이스 선택 함수
            }
            else    {       // 블루투스 비활성화 상태
                // 블루투스 활성화를 위한 Dialog 출력
                Intent BTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // 선택한 값은 onActivityResult로 출력
                startActivityForResult(BTIntent, REQUEST_ENABLE_BT);
            }
        }
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("먼지"));
        tabLayout.addTab(tabLayout.newTab().setText("지도"));
        tabLayout.addTab(tabLayout.newTab().setText("설정"));

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        backPressCloseHandler = new BackPressCloseHandler(this);

        Intent pushIntent = new Intent(MainActivity.this, PushService.class);
        startService(pushIntent);

        // PeriodicWorkRequest 추가
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(WorkManager.class, 10, TimeUnit.SECONDS).setInitialDelay(10, TimeUnit.SECONDS).build();
        androidx.work.WorkManager.getInstance(getApplicationContext()).enqueue(periodicWorkRequest);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
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
            case REQUEST_ENABLE_BT:
                if(requestCode == RESULT_OK)    {
                    selectBluetoothDevice();
                    return;
                }   else    {
                    // reject to select BT devices
                    Toast.makeText(this, "블루투스를 활성화 해야 합니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    public Map<String, Object> getInDoorData()  {

        Map<String, Object> inDoorMap = new HashMap<String, Object>();


        c_util.getInDoorData(id);

        while(true)   {
            if(inDoorData != null)  {
                inDoorMap.put("data",inDoorData);
                return inDoorMap;
            }   else    {}
        }
    }

    public Map<String, Object> getData() {
        System.out.println("getData called");
        gpsTracker = new GpsTracker(MainActivity.this);     // com.nids.util.gps.GpsTracker.java
        Map<String, Object> map = new HashMap<String, Object>();
        String latitude = String.valueOf(gpsTracker.getLatitude());         // 위도 측정
        String longitude = String.valueOf(gpsTracker.getLongitude());       // 경도 측정
        System.out.println("latitude = " + latitude);
        System.out.println("longitude = " + longitude);
        c_util.findStationWithGPS(latitude, longitude);                         // 받은 위경도 값으로 근처 측정소 검색
        //c_util.findStationWithGPS("37.441722","127.171786");              // ※ Virtual Device는 위경도를 측정할 수 없음
                map.put("data", data);                      // 측정 미세먼지 데이터 객체
                map.put("lat",latitude);
                map.put("lon",longitude);
                return map;                                 // outsideFragment로 전송
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

    public void selectBluetoothDevice() {
        // 페어링 되어있는 BT device 검색
        devices = bluetoothAdapter.getBondedDevices();
        // 페어링 된 디바이스 크기 저장
        pariedDeviceCount = devices.size();
        if (pariedDeviceCount == 0) {
            // 페어링 함수 검색해서 찾아보자
        }
        // 페어링 장치 보유한 경우
        else    {
            // 디바이스 선택을 위한 Dialog 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("페어링 되어있는 블루투스 디바이스 목록");
            // 페어링 된 각각의 디바이스 이름과 주소 저장
            List<String> list = new ArrayList<>();
            // 모든 디바이스 이름을 리스트에 추가
            for(BluetoothDevice bluetoothDevice : devices)  {
                list.add(bluetoothDevice.getName());
            }
            list.add("취소");

            // List를 CharSequence 배열로 변경
            final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);
            list.toArray(new CharSequence[list.size()]);

            // 해당 아이템을 눌렀을 때 호출되는 이벤트 리스너
            builder.setItems(charSequences, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 해당 디바이스와 연결하는 함수
                    connectDevice(charSequences[which].toString());
                }
            });

            // 뒤로가기 버튼 누를 때 창이 안 닫히도록 설정
            builder.setCancelable(false);
            // Dialog 생성
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void connectDevice(String deviceName)    {
        // 페어링 된 디바이스들 모두 탐색
        for(BluetoothDevice tempDevice : devices)   {
            // 사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료
            if(deviceName.equals(tempDevice.getName())) {
                bluetoothDevice = tempDevice;
                break;
            }
        }

        // UUID 생성
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        // Rfcomm 채널을 통해 블루투스 디바이스와 통신하는 소켓 생성
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            // 데이터 송,수신 스트림 취득
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();
            // 데이터 수신 함수 호출
            receiveData();
        }   catch(IOException e)    {
            e.printStackTrace();
        }
    }

    public void receiveData()   {
        final Handler handler = new Handler();
        // 데이터 수신을 위한 버퍼 생성
        readBufferPosition = 0;
        readBuffer = new byte[1024];

        // 데이터 수신을 위한 thread 생성
        workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(Thread.currentThread().isInterrupted())   {
                    try {
                        // 데이터 수신 확인
                        int byteAvailable = inputStream.available();
                        if(byteAvailable > 0)   {
                            // 입력 스트림에서 바이트 단위로 수집
                            byte[] bytes = new byte[byteAvailable];
                            inputStream.read(bytes);
                            // 입력 스트림 바이트를 한 바이트씩 읽어 옴
                            for(int i=0;i<byteAvailable;i++)    {
                                byte tempByte = bytes[i];
                                // 개행문자를 기준으로 수집
                                if(tempByte == '\n')    {
                                    // readBuffer 배열
                                    // 을 encodedBytesd로 복사
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    // 인코딩 된 바이트 배열을 문자열로 변환
                                    final String text = new String(encodedBytes, "UTF-8");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                                // 텍스트 뷰에 출력(예정)
                                            System.out.println("text : " + text);
                                            }
                                        });
                                }   // 개행 문자가 아닐 경우
                                else    {
                                    readBuffer[readBufferPosition++] = tempByte;
                                }
                            }
                        }
                    }   catch (IOException e)   {
                        e.printStackTrace();
                    }
                    try {
                        // 1초마다 받아옴
                        Thread.sleep(1000);
                    }   catch (InterruptedException e)  {
                        e.printStackTrace();
                    }
                }
            }
        });
        workerThread.start();
    }
    public void setData(VOOutdoor data) {this.data = data;}
    public void setInDoorData(VOSensorData inDoorData) { this.inDoorData = inDoorData;}
}
