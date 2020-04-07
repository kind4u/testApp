package com.nids.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
//import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.BaseAdapter;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceClickListener;

import com.nids.kind4u.testapp.R;

public class SettingPreferenceFragment extends PreferenceFragment { //implements OnPreferenceClickListener

    SharedPreferences prefs;

    PreferenceScreen editUserPreference;
    Preference registCarPreference;
    PreferenceScreen editCarPreference;
    ListPreference soundPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings_preference);
        soundPreference = (ListPreference)findPreference("sound_list");
        editUserPreference = (PreferenceScreen) findPreference("edit_user");
        registCarPreference = (Preference) findPreference("regist_car");
        editCarPreference = (PreferenceScreen) findPreference("edit_car");

        //registCarPreference.setOnPreferenceClickListener(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

//        Intent intent = new Intent(getContext(), CarActivity.class);
//        activityPreference.setIntent(intent);

        if(!prefs.getString("sound_list", "").equals("")){
            soundPreference.setSummary(prefs.getString("sound_list", "5분"));
        }

        prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }
    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {



//        @Override
//        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference){
//            String key = preference.getKey();
//            if(preference.getKey().equals("regist_car")){
//                //Intent intent = new Intent(Intent.ACTION_SENDTO);
//                startActivity(new Intent(SettingPreferenceFragment.this, CarActivity.class));
//                Log.d("tag", "클릭된 Preference의 key는 "+key);
//                return true;
//            }
//            return false;
//        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("sound_list")){
                soundPreference.setSummary(prefs.getString("sound_list", "5분"));
            }
        }
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

//    @Override
//    public boolean onPreferenceClick(Preference preference) {
//        if(preference.getKey().equals("regist_car")) {
//            Intent intent = new Intent(CarActivity.class);
//            registCarPreference.setIntent(intent);
//        }
//        return false;
//    }

}
