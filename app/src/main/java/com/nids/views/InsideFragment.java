package com.nids.views;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nids.data.VOSensorData;
import com.nids.kind4u.testapp.R;

import java.util.Map;


public class InsideFragment extends Fragment {

    Map<String, Object> map;
    MainActivity activity;
    TextView dateTextInDoor;
    TextView dustTextInDoor;
    TextView infoTextInDoor;

    public InsideFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//        if(getActivity() != null && getActivity() instanceof MainActivity)  {
//            map = ((MainActivity)getActivity()).getInDoorData();                  // MainActivity의 getData 메소드 호출
//        }
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v =  inflater.inflate(R.layout.fragment_inside, container, false);
        dateTextInDoor = v.findViewById(R.id.dateTextInDoor);
        dustTextInDoor = v.findViewById(R.id.dustTextInDoor);
        infoTextInDoor = v.findViewById(R.id.infoTextInDoor);
        bindInDoorView(v);
        return v;
    }

    private void bindInDoorView(View v) {
        map = ((MainActivity)getActivity()).getInDoorData();                  // MainActivity의 getData 메소드 호출
        VOSensorData sensorData = (VOSensorData) map.get("data");
        float pm10 = sensorData.getPm100();                            // 미세먼지 농도 추출


        dateTextInDoor.setText(sensorData.getDate());
        dustTextInDoor.setText(pm10+"㎍/㎥");
        if(pm10 > 75.0){
            infoTextInDoor.setText("매우나쁨");
        }
        else if(pm10 > 35.0){
            infoTextInDoor.setText("나쁨");
        }
        else if(pm10 > 15.0){
            infoTextInDoor.setText("보통");
        }
        else{
            infoTextInDoor.setText("좋음");
        }

    }
}
