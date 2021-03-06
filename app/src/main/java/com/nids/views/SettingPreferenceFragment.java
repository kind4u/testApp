package com.nids.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import com.nids.data.VOUser;
import com.nids.kind4u.testapp.R;
import com.nids.util.WorkManager;
import com.nids.util.interfaces.JoinCallBackInterface;
import com.nids.util.network.CommunicationUtil;

import java.util.concurrent.TimeUnit;

public class SettingPreferenceFragment extends PreferenceFragmentCompat {
    private CommunicationUtil c_util_car;

    VOUser user;

    private int check;
    private int button;

    private Context context;

    private String id;
    public String platform;

    private SharedPreferences prefs;

    private ListPreference soundPreference;

    private String notice_term;
    private Integer notice_time;
    private TimeUnit notice_timeunit;

    private PeriodicWorkRequest periodicWorkRequest;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        context =getActivity();

        id = ((MainActivity)getActivity()).getId();
        platform = ((MainActivity)getActivity()).getPlatform();

        addPreferencesFromResource(R.xml.settings_preference);
        soundPreference = findPreference("sound_list");
        PreferenceScreen editUserPreference = findPreference("edit_user");
        Preference registCarPreference = findPreference("regist_car");
        Preference editCarPreference = findPreference("edit_car");
        SwitchPreference noticePreference = findPreference("notice");


        //차량 정보 존재하면
        //차량등록 버튼 클릭시
        //차량 수정 버튼 클릭시
        //차량 정보 존재하지 않으면
        //차량등록 버튼 클릭시
        //차량 수정 버튼 클릭시
        JoinCallBackInterface joinCallBackInterface = new JoinCallBackInterface() {

            @Override
            public void getUserResult(boolean result, String message, VOUser userinfo) {
                if (result) {
                    Intent intent_naver = new Intent(getContext(), ModifyActivity.class);
                    intent_naver.putExtra("user", userinfo);
                    intent_naver.putExtra("platform", platform);
                    intent_naver.putExtra("id", id);
                    startActivity(intent_naver);
                }
            }

            @Override
            public void carResult(boolean insert, String result, String message) {
            }

            @Override
            public void deleteCarResult(boolean delete, String result, String message) {
            }

            @Override
            public void editCarResult(boolean edit, String result, String message) {
            }

            @Override
            public void checkCarResult(String result, boolean exist) {
                Handler handler = new Handler(Looper.getMainLooper());

                if (exist) { //차량 정보 존재하면
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            check = 1;
                            if (button == 0) { //차량등록 버튼 클릭시
                                Toast.makeText(context, "이미 차량등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            } else if (button == 1) { //차량 수정 버튼 클릭시
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
                            if (button == 0) { //차량등록 버튼 클릭시
                                Intent intent = new Intent(getContext(), CarActivity.class);
                                intent.putExtra("id", ((MainActivity) getActivity()).getId());
                                intent.putExtra("page", "car");
                                startActivity(intent);
                            } else if (button == 1) {//차량 수정 버튼 클릭시
                                Toast.makeText(context, "차량 등록을 먼저 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void signUpResult(boolean insert, String result, String message) {
            }

            @Override
            public void positionResult(boolean position_result, String data) {
            }

            @Override
            public void existResult(String result, boolean exist) {
            }
        };
      
        c_util_car = new CommunicationUtil(joinCallBackInterface);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(!prefs.getString("sound_list", "").equals("")){
            soundPreference.setSummary(prefs.getString("sound_list", "5분"));

        }
        prefs.registerOnSharedPreferenceChangeListener(prefListener);

        notice_term = prefs.getString("sound_list","5분");

        ConvertTermTime(notice_term);
      
        registCarPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            @Override
            public boolean onPreferenceClick(Preference preference) {
                button =0;
                c_util_car.checkCar(((MainActivity) getActivity()).getId());
                return false;
            }
        });

        editCarPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
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
                switch (platform){
                    case "DEFAULT" :
                        Intent intent = new Intent(getContext(),ValidationActivity.class);
                        intent.putExtra("id",((MainActivity)getActivity()).getId());
                        intent.putExtra("platform",platform);
                        id = ((MainActivity)getActivity()).getId();
                        startActivity(intent);
                        break;
                    case "GOOGLE":
                    case "NAVER":
                    case "KAKAO":
                        c_util_car.getUser(((MainActivity) getActivity()).getId());
                        break;
                }
                return false;
            }
        });

        noticePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isValue = (Boolean) newValue;
                try {
                if(isValue) {
                    // PeriodicWorkRequest 추가
                    if(notice_time == null || notice_timeunit == null)  {
                        notice_time = 5;
                        notice_timeunit = TimeUnit.MINUTES;
                    }
                            periodicWorkRequest = new PeriodicWorkRequest.Builder(WorkManager.class, notice_time, notice_timeunit).setInitialDelay(notice_time,notice_timeunit).build();
                            androidx.work.WorkManager.getInstance(context.getApplicationContext()).enqueueUniquePeriodicWork("myWork", ExistingPeriodicWorkPolicy.KEEP,periodicWorkRequest);
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
    }

    private SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("sound_list")){
                soundPreference.setSummary(prefs.getString("sound_list", "5분"));
                notice_term = prefs.getString("sound_list","5분");
                ConvertTermTime(notice_term);
                Log.d("@@@","changed to " + notice_term);
                periodicWorkRequest = new PeriodicWorkRequest.Builder(WorkManager.class, notice_time, notice_timeunit).setInitialDelay(notice_time,notice_timeunit).build();
                androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork("myWork", ExistingPeriodicWorkPolicy.REPLACE,periodicWorkRequest);
            }
        }
    };
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) { }

    private void ConvertTermTime(String notice_term)   {
        switch (notice_term)    {
            case "1분":
                this.notice_time = 1;
                this.notice_timeunit = TimeUnit.MINUTES;
                break;
            case "5분":
                this.notice_time = 5;
                this.notice_timeunit = TimeUnit.MINUTES;
                break;
            case "10분":
                this.notice_time = 10;
                this.notice_timeunit = TimeUnit.MINUTES;
                break;
            case "30분":
                this.notice_time = 30;
                this.notice_timeunit = TimeUnit.MINUTES;
                break;
            case "1시간":
                this.notice_time = 1;
                this.notice_timeunit = TimeUnit.HOURS;
                break;
        }
    }
}
