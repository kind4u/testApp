package com.nids.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nids.data.VOOutdoor;
import com.nids.data.VOSensorData;
import com.nids.data.VOStation;
import com.nids.data.VOUser;
import com.nids.kind4u.testapp.R;
import com.nids.util.gps.GpsTracker;
import com.nids.util.interfaces.NetworkCallBackInterface;
import com.nids.util.network.CommunicationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    List<VOSensorData> indoorDataList;
    VOSensorData indoorData;
    NetworkCallBackInterface networkCallBackInterface = new NetworkCallBackInterface() {
        @Override
        public void signInResult(boolean result, String message, VOUser userinfo) { }

        @Override
        public void modifyResult(boolean result) { }

        @Override
        public void findStation(boolean result, VOStation station_info) { }

        @Override
        public void dataReqResult(String result, List<VOSensorData> dataList) {
            if(result.equals("200")) {
                indoorDataList = dataList;
                /* 기존 - DataList를 전부 가져옴
                   현재(임시) - DataList에서 하나만 가져와서 위경도 설정 예정임
                 */
                indoorData = indoorDataList.get(0);
                LatLng pos = new LatLng(indoorData.getLat(), indoorData.getLon());
                final MarkerOptions mkOption = new MarkerOptions();
                mkOption.position(pos);
                mkOption.title("위치:"+indoorData.getLat()+","+indoorData.getLon());
                mkOption.snippet("미세먼지 농도 : " + indoorData.getPm100() + "㎍/㎥");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMap.addMarker(mkOption);
                    }
                });
            }
        }

        @Override
        public void dataReqResultOutdoor(boolean result, VOOutdoor data) { }
    };

    private GoogleMap mMap;

    public MapFragment() { }

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
        mMap = googleMap;

        String [][] arr = new String[][] {{"명동","37.563576","126.983431", "11.0"},
                {"가로수길", "37.520300", "127.023008","12.3"},
                {"광화문", "37.575268", "126.976896","8.6"},
                {"남산", "37.550925", "126.990945","24.5"},
                {"이태원", "37.540223", "126.994005","30.0"}
        };
        for(String[] item : arr)   {
            LatLng pos = new LatLng(Float.parseFloat(item[1]),Float.parseFloat(item[2]));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(pos);
            markerOptions.title("위치:"+item[0]);
            markerOptions.snippet("미세먼지 농도  : "+ item[3] + "㎍/㎥");
            mMap.addMarker(markerOptions);
        }

        CommunicationUtil c_util = new CommunicationUtil(networkCallBackInterface);

        LatLng position = new LatLng(MainActivity.gpsTracker.getLatitude(),MainActivity.gpsTracker.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        c_util.findData(((MainActivity)getActivity()).getId());

    }
}
