package com.nids.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nids.data.VOSensorData;
import com.nids.kind4u.testapp.R;

import java.util.Map;


public class InsideFragment extends Fragment {

    private MainActivity activity;
    private TextView dateTextInDoor;
    private TextView dustTextInDoor;
    private TextView infoTextInDoor;
    private TextView tempTextInDoor;
    private TextView humiTextInDoor;
    private ImageView imageView;

    private ConstraintLayout backGround;

    public InsideFragment() { }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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
        tempTextInDoor = v.findViewById(R.id.tempTextInDoor);
        humiTextInDoor = v.findViewById(R.id.humiTextInDoor);
        imageView = v.findViewById(R.id.imageInside);

        backGround =v.findViewById(R.id.in);

        Button bluetoothButton = v.findViewById(R.id.button_bt);
        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.selectBluetoothDevice();
            }
        });
        bluetoothButton.setVisibility(View.GONE);
        bluetoothButton.setEnabled(false);

        bindInDoorView(v);
        return v;
    }

    @SuppressLint("SetTextI18n")
    private void bindInDoorView(View v) {
        Map<String, Object> map = ((MainActivity) getActivity()).getInDoorData();                  // MainActivity의 getData 메소드 호출
        VOSensorData sensorData = (VOSensorData) map.get("data");

        if(sensorData!=null) {
        float pm10 = sensorData.getPm100();                            // 미세먼지 농도 추출
        float temp = sensorData.getTemp();
        float humi = sensorData.getHumi();

        dateTextInDoor.setText(sensorData.getDate());
        dustTextInDoor.setText(pm10+"㎍/㎥");
        tempTextInDoor.setText("온도: "+temp+"°C");
        humiTextInDoor.setText("습도: "+humi+"%");
        if(pm10 > 75.0){
            infoTextInDoor.setText("매우나쁨");
            backGround.setBackgroundColor(Color.parseColor("#B9062F"));
        }
        else if(pm10 > 35.0){
            infoTextInDoor.setText("나쁨");
            backGround.setBackgroundColor(Color.parseColor("#FF9E9B"));
        }
        else if(pm10 > 15.0){
            infoTextInDoor.setText("보통");
            backGround.setBackgroundColor(Color.parseColor("#5AD18F"));
            } else {
                infoTextInDoor.setText("좋음");
                backGround.setBackgroundColor(Color.parseColor("#5ABEFF"));
            }
        }
    }
}
