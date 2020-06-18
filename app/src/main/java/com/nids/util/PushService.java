package com.nids.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.nids.kind4u.testapp.R;
import com.nids.views.MainActivity;

public class PushService extends Service {
    NotificationManager notifi_m;
    ServiceThread thread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notifi_m = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        pushServiceHandler handler = new pushServiceHandler();
        thread = new ServiceThread(handler);
        thread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        thread.stopForever();
        thread = null;
    }

    class pushServiceHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String messageTitle = "Content Title";
            String messageBody = "Content Body";
            Intent intent = new Intent(PushService.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(PushService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            String channelId = "Channel ID";
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(PushService.this, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(messageTitle)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)  {
                String channelName = "Channel Name";
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
            }
            assert notificationManager != null;
            notificationManager.notify(0, notificationBuilder.build());
        }
    }
}
