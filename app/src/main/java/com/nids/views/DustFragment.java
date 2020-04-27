package com.nids.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.nids.data.VOOutdoor;
import com.nids.data.VOSensorData;
import com.nids.data.VOStation;
import com.nids.data.VOUser;
import com.nids.kind4u.testapp.R;
import com.nids.util.gps.GpsTracker;
import com.nids.util.interfaces.NetworkCallBackInterface;
import com.nids.util.network.CommunicationUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class DustFragment extends Fragment {
    InsideFragment insideFragment;
    OutsideFragment outsideFragment;
    Button btn_analysis;
    String station_name = "";
    List<VOSensorData> inDoorDataList;
    VOSensorData inDoorData;
    VOOutdoor data = new VOOutdoor();
    String id;

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
    public DustFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dust, container, false);
        insideFragment = new InsideFragment();
        outsideFragment = new OutsideFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.container1,insideFragment).commit();
        getChildFragmentManager().beginTransaction().replace(R.id.container2,outsideFragment).commit();

        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Log.d("timerTask","inside fragment refresh started");
                if(!isAdded()){}    else {
                    FragmentTransaction inft = getChildFragmentManager().beginTransaction();
                    inft.detach(insideFragment);
                    inft.attach(insideFragment);
                    inft.commit();
                }
            }
        };
        TimerTask tt2 = new TimerTask() {
            @Override
            public void run() {
                Log.d("timerTask2","outside fragment refresh started");
                if(!isAdded()){}    else {
                    FragmentTransaction outft = getChildFragmentManager().beginTransaction();
                    outft.detach(outsideFragment);
                    outft.attach(outsideFragment);
                    outft.commit();
                }
            }
        };

        Timer timer = new Timer();
        timer.schedule(tt,0,10000);
        timer.schedule(tt2,0,10000);
        return v;
    }

//    public Map<String, Object> getInDoorData()  {
//
//        Map<String, Object> inDoorMap = new HashMap<String, Object>();
//
//
//        c_util.getInDoorData(id);
//
//        while(true)   {
//            if(inDoorData != null)  {
//                inDoorMap.put("data",inDoorData);
//                return inDoorMap;
//            }   else    {}
//        }
//    }
//
//    public Map<String, Object> getData() {
//        System.out.println("getData called");
//        gpsTracker = new GpsTracker(MainActivity.this);     // com.nids.util.gps.GpsTracker.java
//        Map<String, Object> map = new HashMap<String, Object>();
//        String latitude = String.valueOf(gpsTracker.getLatitude());         // 위도 측정
//        String longitude = String.valueOf(gpsTracker.getLongitude());       // 경도 측정
//        System.out.println("latitude = " + latitude);
//        System.out.println("longitude = " + longitude);
//        //Toast.makeText(MainActivity.this, "lat = "+ latitude + "lon = " + longitude, Toast.LENGTH_SHORT).show();
//        c_util.findStationWithGPS(latitude, longitude);                         // 받은 위경도 값으로 근처 측정소 검색
//        //c_util.findStationWithGPS("37.441722","127.171786");              // ※ Virtual Device는 위경도를 측정할 수 없음
////        while(true) {
////            if(data != null) {                  // 측정소 및 미세먼지 데이터가 들어올 때까지 강제로 Holding (좋은 방법은 아닌 듯)
//        map.put("data", data);                      // 측정 미세먼지 데이터 객체
//        map.put("lat",latitude);
//        map.put("lon",longitude);
//        return map;                                 // outsideFragment로 전송
////            }   else    {}
////        }
//    }

    public void setData(VOOutdoor data) {this.data = data;}
    public void setInDoorData(VOSensorData inDoorData) { this.inDoorData = inDoorData;}
}
