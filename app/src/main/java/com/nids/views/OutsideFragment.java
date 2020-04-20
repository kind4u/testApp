package com.nids.views;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
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
//        if(getActivity() != null && getActivity() instanceof MainActivity)  {
//            map = ((MainActivity)getActivity()).getData();                  // MainActivity의 getData 메소드 호출
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_outside, container,false);
        stationText = v.findViewById(R.id.stationTextOutDoor);
        dateText = v.findViewById(R.id.dateTextOutDoor);
        dustText = v.findViewById(R.id.dustTextOutDoor);
        infoText = v.findViewById(R.id.infoTextOutDoor);
        lat = v.findViewById(R.id.latitude);
        lon = v.findViewById(R.id.longitude);

        bindComponent(v);
        // Inflate the layout for this fragment
        return v;
    }

    private void bindComponent(View v){                // 데이터 수신 메소드

        map = ((MainActivity)getActivity()).getData();                  // MainActivity의 getData 메소드 호출

        VOOutdoor outDoorData = (VOOutdoor) map.get("data");            // onAttach에서 getData 메소드로 얻어낸 데이터 Input

        if(outDoorData.isNull() != true) {
            float pm10 = outDoorData.getPM100();                            // 미세먼지 농도 추출
            System.out.println("called bindComponent");

            stationText.setText("실외 미세먼지 현황 (" + outDoorData.getStationName() + "측정소)");
            dateText.setText("측정시간 : " + outDoorData.getMeasureDate());
            dustText.setText(outDoorData.getPM100() + "㎍/㎥");
            if (pm10 > 75.0) {
                infoText.setText("매우나쁨");
                createNotification4();
            } else if (pm10 > 35.0) {
                infoText.setText("나쁨");
                createNotification3();
            } else if (pm10 > 15.0) {
                infoText.setText("보통");
                createNotification2();
            } else {
                infoText.setText("좋음");
                createNotification1();
            }
            lat.setText(String.valueOf(map.get("lat")));
            lon.setText(String.valueOf(map.get("lon")));
        }
    }

    public void createNotification1(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "default");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("미세먼지 농도 알림");
        builder.setContentText("미세먼지 농도 : 좋음");
        NotificationManager notificationManager= (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        notificationManager.notify(1, builder.build());
    }
    public void createNotification2(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "default");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("미세먼지 농도 알림");
        builder.setContentText("미세먼지 농도 : 보통");
        NotificationManager notificationManager= (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        notificationManager.notify(1, builder.build());
    }
    public void createNotification3(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "default");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("미세먼지 농도 알림");
        builder.setContentText("미세먼지 농도 : 나쁨");
        NotificationManager notificationManager= (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        notificationManager.notify(1, builder.build());
    }
    public void createNotification4(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "default");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("미세먼지 농도 알림");
        builder.setContentText("미세먼지 농도 : 매우나쁨");
        NotificationManager notificationManager= (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        notificationManager.notify(1, builder.build());
    }

//    private  void setData(VOOutdoor data){
//        stationText.setText("실외 미세먼지 현황 ("+ map.get("lan") +"측정소)");
//        dateText.setText("측정시간 : " + data.getMeasureDate());
//    }
}
