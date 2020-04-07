package com.nids.views;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;

import com.nids.kind4u.testapp.R;


public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

//        Intent intent = new Intent(SettingActivity.this, CarActivity.this);
//        Preference test = findPreference("regist_car");
//        test.setIntent(intent);
    }
}
