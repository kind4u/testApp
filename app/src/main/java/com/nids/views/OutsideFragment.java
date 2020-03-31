package com.nids.views;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nids.data.VOOutdoor;
import com.nids.kind4u.testapp.R;

import java.util.Map;


public class OutsideFragment extends Fragment {
    MainActivity activity;
    Map<String, Object> map;

    String station;
    TextView dateText;
    TextView stationText;
    TextView dustText;
    TextView infoText;

    TextView lat;
    TextView lon;

    public OutsideFragment() {
        // Required empty public constructor
        super();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(getActivity() != null && getActivity() instanceof MainActivity)  {
            map = ((MainActivity)getActivity()).getData();                  // MainActivity의 getData 메소드 호출
        }
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_outside, container,false);
        stationText = v.findViewById(R.id.stationText);
        dateText = v.findViewById(R.id.dateTextInDoor);
        dustText = v.findViewById(R.id.dustTextInDoor);
        infoText = v.findViewById(R.id.infoTextInDoor);
        lat = v.findViewById(R.id.latitude);
        lon = v.findViewById(R.id.longitude);

        bindComponent(v);
        // Inflate the layout for this fragment
        return v;
    }

    private void bindComponent(View v){                // 데이터 수신 메소드

        map = ((MainActivity)getActivity()).getData();                  // MainActivity의 getData 메소드 호출

        VOOutdoor outDoorData = (VOOutdoor) map.get("data");            // onAttach에서 getData 메소드로 얻어낸 데이터 Input
        float pm10 = outDoorData.getPM100();                            // 미세먼지 농도 추출
        System.out.println("called bindComponent");

        stationText.setText("실외 미세먼지 현황 ("+ map.get("station_name") +"측정소)");
        dateText.setText("측정시간 : "+outDoorData.getMeasureDate());
        dustText.setText(outDoorData.getPM100()+"㎍/㎥");
        if(pm10 > 75.0){
            infoText.setText("매우나쁨");
        }
        else if(pm10 > 35.0){
            infoText.setText("나쁨");
        }
        else if(pm10 > 15.0){
            infoText.setText("보통");
        }
        else{
            infoText.setText("좋음");
        }
        lat.setText(String.valueOf(map.get("lat")));
        lon.setText(String.valueOf(map.get("lon")));
    }



//    private  void setData(VOOutdoor data){
//        stationText.setText("실외 미세먼지 현황 ("+ map.get("lan") +"측정소)");
//        dateText.setText("측정시간 : " + data.getMeasureDate());
//    }
}
