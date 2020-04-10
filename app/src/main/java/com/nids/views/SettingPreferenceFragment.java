package com.nids.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.nids.kind4u.testapp.R;

public class SettingPreferenceFragment extends PreferenceFragmentCompat {

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

        editUserPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(),ValidationActivity.class);
                intent.putExtra("id",((MainActivity)getActivity()).getId());
                startActivity(intent);
                return false;
            }
        });

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

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


}
