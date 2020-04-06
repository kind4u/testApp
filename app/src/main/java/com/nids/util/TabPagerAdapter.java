package com.nids.util;

import android.app.StatusBarManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.nids.views.DustFragment;
import com.nids.views.MapFragment;
import com.nids.views.SettingFragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;

    public TabPagerAdapter(@NonNull FragmentManager fm, int tabCount) {
        super(fm, tabCount);
        this.tabCount = tabCount;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position)    {
            case 0:
                DustFragment dustFragment = new DustFragment();
                return dustFragment;
            case 1:
                MapFragment mapFragment = new MapFragment();
                return mapFragment;
            case 2:
                SettingFragment settingFragment = new SettingFragment();
                return settingFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
