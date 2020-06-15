package com.nids.util;

import android.os.Handler;

public class ServiceThread extends Thread {
    private Handler handler;
    private boolean isRun = true;

    ServiceThread(Handler handler)   {
        this.handler = handler;
    }

    void stopForever()   {
        synchronized (this) {
            this.isRun = false;
        }
    }
    @Override
    public void run() {
        while(isRun)    {
            handler.sendEmptyMessage(0);
            try {
                Thread.sleep(10000);
            }   catch (Exception e) {e.printStackTrace();}
        }
    }
}
