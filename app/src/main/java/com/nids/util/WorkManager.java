package com.nids.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.nids.data.VOOutdoor;
import com.nids.data.VOSensorData;
import com.nids.data.VOStation;
import com.nids.data.VOUser;
import com.nids.kind4u.testapp.R;
import com.nids.util.gps.GpsTracker;
import com.nids.util.interfaces.NetworkCallBackInterface;
import com.nids.util.network.CommunicationUtil;
import com.nids.views.MainActivity;

import java.util.List;


public class WorkManager extends Worker {
    private String station_name = "";
    private VOOutdoor nullOutDoor;

    private static boolean notifyResult = false;
    private NetworkCallBackInterface networkCallBackInterface = new NetworkCallBackInterface() {
        @Override
        public void signInResult(boolean result, String message, VOUser userinfo) { }

        @Override
        public void modifyResult(boolean result) { }

        @Override
        public void modifyUserResult(boolean result) { }

        @Override
        public void findStation(boolean result, VOStation station_info) {
            if (result) {
                station_name = station_info.getStationName();
                c_util.getOutDoorData(station_name);        // 받은 축정소 정보를 파라미터로 한 후 해당 측정소 데이터 불러오기}
            } else {
                sendNotification(nullOutDoor = new VOOutdoor());
            }
        }

        @Override
        public void dataReqResult(String result, List<VOSensorData> dataList) { }

        @Override
        public void dataReqResultOutdoor(boolean result, VOOutdoor data) {
            if (result) {
                sendNotification(data);
            }   else    {
                sendNotification(nullOutDoor = new VOOutdoor());
            }
        }
    };
    private CommunicationUtil c_util = new CommunicationUtil(networkCallBackInterface);

    public WorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork() {
        Handler mHandler = new Handler(Looper.getMainLooper());
        Runnable loadGPS = new Runnable() {
            @Override
            public void run() {
                GpsTracker gpsTracker = new GpsTracker(getApplicationContext());
                String latitude = String.valueOf(gpsTracker.getLatitude());         // 위도 측정
                String longitude = String.valueOf(gpsTracker.getLongitude());       // 경도 측정
                c_util.findStationWithGPS(latitude, longitude);                         // 받은 위경도 값으로 근처 측정소 검색
            }
        };
        mHandler.postDelayed(loadGPS,1000);
        return Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();
    }


    private void sendNotification(VOOutdoor data) {
        String info = "";
        float pm10 = 0;
        if (!data.isNull()) {
            pm10 = data.getPM100();                            // 미세먼지 농도 추출
            if (pm10 > 150.0) {
                info = "매우나쁨";
            } else if (pm10 > 80.0) {
                info = "나쁨";
            } else if (pm10 > 30.0) {
                info = "보통";
            } else {
                info = "좋음";
            }
        }
        String messageTitle = "TestAPP 제목";
        String messageBody = "현재 미세먼지 농도는 " + pm10 + "㎍/㎥ 입니다.(상태 : " + info + ")";
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //이미 존재하는 액티비티를 포그라운드로 가져옴
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String channelId = "Channel ID";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true) //알림터치시 반응 후 삭제
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "Channel Name";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
        assert notificationManager != null;
        notificationManager.notify(0, notificationBuilder.build());
        notifyResult = true;
    }
}
