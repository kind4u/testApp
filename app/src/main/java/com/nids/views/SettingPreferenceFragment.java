package com.nids.views;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.nids.kind4u.testapp.R;
import com.nids.util.WorkManager;
import com.nids.util.interfaces.JoinCallBackInterface;
import com.nids.util.network.CommunicationUtil;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SettingPreferenceFragment extends PreferenceFragmentCompat {
    JoinCallBackInterface joinCallBackInterface;
    CommunicationUtil c_util_car;

    int check;
    int button;

    private Context context;

    SharedPreferences prefs;

    PreferenceScreen editUserPreference;
    Preference registCarPreference;
    Preference editCarPreference;
    ListPreference soundPreference;
    SwitchPreference noticePrefernece;

    PeriodicWorkRequest periodicWorkRequest;
    WorkInfo.State workState;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        context =getActivity();


        addPreferencesFromResource(R.xml.settings_preference);
        soundPreference = (ListPreference)findPreference("sound_list");
        editUserPreference = (PreferenceScreen) findPreference("edit_user");
        registCarPreference = (Preference) findPreference("regist_car");
        editCarPreference = (Preference) findPreference("edit_car");
        noticePrefernece = (SwitchPreference) findPreference("notice");

        joinCallBackInterface = new JoinCallBackInterface() {
            @Override
            public void carResult(boolean insert, String result, String message) { }

            @Override
            public void deleteCarResult(boolean delete, String result, String message){ }

            @Override
            public void editCarResult(boolean edit, String result, String message){ }

            @Override
            public void checkCarResult(String result, boolean exist) {
                Handler handler = new Handler(Looper.getMainLooper());

                if (exist) { //차량 정보 존재하면
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            check = 1;
                            if(button == 0){ //차량등록 버튼 클릭시
                                Toast.makeText(context, "이미 차량등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            }else if(button == 1){ //차량 수정 버튼 클릭시
                                Intent intent = new Intent(getContext(), EditCarActivity.class);
                                intent.putExtra("id", ((MainActivity) getActivity()).getId());
                                startActivity(intent);
                            }
                        }
                    });
                } else { //차량 정보 존재하지 않으면
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            check = 0;
                            if(button == 0){ //차량등록 버튼 클릭시
                                Intent intent = new Intent(getContext(),CarActivity.class);
                                intent.putExtra("id",((MainActivity)getActivity()).getId());
                                intent.putExtra("page", "main");
                                startActivity(intent);
                            }else if(button == 1){//차량 수정 버튼 클릭시
                                Toast.makeText(context, "차량 등록을 먼저 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void signUpResult(boolean insert, String result, String message) { }

            @Override
            public void positionResult(boolean position_result, String data) { }

            @Override
            public void existResult(String result, boolean exist) { }
        };
      
        c_util_car = new CommunicationUtil(joinCallBackInterface);


      
        registCarPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            @Override
            public boolean onPreferenceClick(Preference preference) {
                button =0;
                c_util_car.checkCar(((MainActivity) getActivity()).getId());
                return false;
            }
        });

        editCarPreference.setOnPreferenceClickListener( new Preference.OnPreferenceClickListener(){
            @Override
            public boolean onPreferenceClick(Preference preference) {
                button =1;
                c_util_car.checkCar(((MainActivity) getActivity()).getId());
                return false;
                }
        });
        editUserPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(),ValidationActivity.class);
                intent.putExtra("id",((MainActivity)getActivity()).getId());
                startActivity(intent);
                return false;
            }
        });

        noticePrefernece.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isValue = (Boolean) newValue;
                try {
                if(isValue) {
                    // PeriodicWorkRequest 추가
                            periodicWorkRequest = new PeriodicWorkRequest.Builder(WorkManager.class, 10, TimeUnit.SECONDS).setInitialDelay(10, TimeUnit.SECONDS).build();
                            androidx.work.WorkManager.getInstance(context.getApplicationContext()).enqueueUniquePeriodicWork("mywork", ExistingPeriodicWorkPolicy.KEEP,periodicWorkRequest);
                            //System.out.println("work state : " + androidx.work.WorkManager.getInstance(context).getWorkInfoById(periodicWorkRequest.getId()).get().getState().toString());
                }   else    {
                        androidx.work.WorkManager.getInstance(context.getApplicationContext()).cancelAllWork();
                        //ystem.out.println("work state : " + androidx.work.WorkManager.getInstance(context).getWorkInfoById(periodicWorkRequest.getId()).get().getState().toString());
                }

                //ListenableFuture<WorkInfo> workInfo = androidx.work.WorkManager.getInstance(context.getApplicationContext()).getWorkInfoById(periodicWorkRequest.getId());
                //System.out.println("work info : " + workInfo);
                return true;
                } catch (Exception e)   {
                    e.printStackTrace();
                    return false;
                }
            }
        });




        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(!prefs.getString("sound_list", "").equals("")){
            soundPreference.setSummary(prefs.getString("sound_list", "5분"));
        }
        prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }

    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

      @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("sound_list")){
                soundPreference.setSummary(prefs.getString("sound_list", "5분"));
            }
        }
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) { }


}
