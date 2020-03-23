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
        bindComponent(v);
        // Inflate the layout for this fragment
        return v;
    }

    private  void bindComponent(View v){                // 데이터 수신 메소드
        stationText = v.findViewById(R.id.stationText);
        dateText = v.findViewById(R.id.dateText);
        dustText = v.findViewById(R.id.dustText);
        infoText = v.findViewById(R.id.infoText);
        VOOutdoor outDoorData = (VOOutdoor) map.get("data");            // onAttach에서 getData 메소드로 얻어낸 데이터 Input
        float pm10 = outDoorData.getPM100();                            // 미세먼지 농도 추출

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
    }

//    private  void setData(VOOutdoor data){
//        stationText.setText("실외 미세먼지 현황 ("+ map.get("lan") +"측정소)");
//        dateText.setText("측정시간 : " + data.getMeasureDate());
//    }
}
