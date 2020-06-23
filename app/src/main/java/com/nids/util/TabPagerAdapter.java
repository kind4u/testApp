package com.nids.util;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.nids.kind4u.testapp.R;
import com.nids.views.DustFragment;
import com.nids.views.MapFragment;
import com.nids.views.SettingFragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

//    public Context context;
//
//    private ImageView image1 = new ImageView(context);
//    private ImageView image2 = new ImageView(context);
//    private ImageView image3 = new ImageView(context);
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
                //image1.setImageResource(R.drawable.home);
                return new DustFragment();
            case 1:
//                image2.setImageResource(R.drawable.map);
                return new MapFragment();
            case 2:
//                image3.setImageResource(R.drawable.setting);
                return new SettingFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
