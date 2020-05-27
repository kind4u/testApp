package com.nids.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nids.data.VOSensorData;
import com.nids.kind4u.testapp.R;
import com.nids.util.gps.GpsTracker;

import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    Map<String, Object> m;

   GoogleMap mMap;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        m = ((MainActivity)getActivity()).getInDoorData();                  // MainActivity의 getData 메소드 호출
//        VOSensorData sensorData = (VOSensorData) m.get("data");
//        String latString = sensorData.getLat();                            // 센서 데이터 내 위도 추출
//        double lat = Double.parseDouble(latString);
//        String lonString = sensorData.getLon();                            // 센서 데이터 내 경도 추출
//        double lon = Double.parseDouble(lonString);
//
//        mMap = googleMap;
//        for(int idx =0; idx < 10; idx++){
//            // 1. 마커 옵션 설정 (만드는 과정)
//
//            MarkerOptions makerOptions = new MarkerOptions();
//            makerOptions // LatLng에 대한 어레이를 만들어서 이용할 수도 있다.
//                    .position(new LatLng(lat + idx, lon))
//                    //.position(new LatLng(37.52487 + idx, 126.92723))
//                    .title("마커" + idx); // 타이틀.
//
//            // 2. 마커 생성 (마커를 나타냄)
//            mMap.addMarker(makerOptions);
//
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
//        }
        //LatLng position = new LatLng(MainActivity.gpsTracker.getLatitude(),MainActivity.gpsTracker.getLongitude());

//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(position);
//        markerOptions.title("현재 위치");
//        markerOptions.snippet("위치정보 : "+position.latitude+", "+position.longitude);
//        mMap.addMarker(markerOptions);

//        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }
}
