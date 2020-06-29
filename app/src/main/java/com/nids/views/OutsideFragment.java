package com.nids.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.style.IconMarginSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import com.nids.data.VOOutdoor;
import com.nids.kind4u.testapp.R;

import java.util.Map;


public class OutsideFragment extends Fragment {
    private MainActivity activity;
    private Map<String, Object> map;

    private TextView dateText;
    private TextView stationText;
    private TextView dustText;
    private TextView infoText;
    private ImageView imageView;

    private ConstraintLayout backGround;

    public OutsideFragment() {
        // Required empty public constructor
        super();
    }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_outside, container,false);
        stationText = v.findViewById(R.id.stationTextOutDoor);
        dateText = v.findViewById(R.id.dateTextOutDoor);
        dustText = v.findViewById(R.id.dustTextOutDoor);
        infoText = v.findViewById(R.id.infoTextOutDoor);
//        lat = v.findViewById(R.id.latitude);
//        lon = v.findViewById(R.id.longitude);
        imageView = v.findViewById(R.id.imageOutside);

        backGround =v.findViewById(R.id.out);

        bindComponent(v);
        // Inflate the layout for this fragment
        return v;
    }

    @SuppressLint("SetTextI18n")
    private void bindComponent(View v){                // 데이터 수신 메소드

        map = activity.getData();                  // MainActivity의 getData 메소드 호출

        VOOutdoor outDoorData = (VOOutdoor) map.get("data");            // onAttach에서 getData 메소드로 얻어낸 데이터 Input

        if(!outDoorData.isNull()) {
            float pm10 = outDoorData.getPM100();                            // 미세먼지 농도 추출
            System.out.println("called bindComponent");

            stationText.setText("실외 미세먼지 현황 (" + outDoorData.getStationName() + "측정소)");
            dateText.setText("측정시간 : " + outDoorData.getMeasureDate());
            dustText.setText(pm10 + "㎍/㎥");
            if (pm10 > 150.0) {
                infoText.setText("매우나쁨");
                backGround.setBackgroundColor(Color.parseColor("#F09494"));
                imageView.setImageResource(R.drawable.verybad);
            } else if (pm10 > 80.0) {
                infoText.setText("나쁨");
                backGround.setBackgroundColor(Color.parseColor("#FFD9E4"));
                imageView.setImageResource(R.drawable.bad);
            } else if (pm10 > 30.0) {
                infoText.setText("보통");
                backGround.setBackgroundColor(Color.parseColor("#BEF5BE"));
                imageView.setImageResource(R.drawable.good);
            } else {
                infoText.setText("좋음");
                backGround.setBackgroundColor(Color.parseColor("#A5D8FA"));
                imageView.setImageResource(R.drawable.verygood);
            }
        }
    }
}
