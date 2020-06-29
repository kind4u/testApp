package com.nids.util;
import android.app.Activity;
import android.widget.Toast;
/**
 * Created by kind4 on 2018-05-26.
 */
public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Toast toast;
    private Activity activity;
    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
            //2초가 지난 뒤 누르거나, 처음 누르면 backKeyPressedTime을 기록함. 그 후 toast 메세지 출력
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            toast.cancel();
            activity.moveTaskToBack(true);
            activity.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            //backpressed를 누르고 2초 이내에 한번 더 누를 시 activity를 종료하고 killprocess 실행
        }
    }
    private void showGuide() {
        toast = Toast.makeText(activity, "'뒤로'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
