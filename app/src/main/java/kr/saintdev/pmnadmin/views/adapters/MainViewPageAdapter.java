package kr.saintdev.pmnadmin.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import kr.saintdev.pmnadmin.views.fragments.SuperFragment;
import kr.saintdev.pmnadmin.views.fragments.main.AlarmFragment;
import kr.saintdev.pmnadmin.views.fragments.main.SettingsFragment;
import kr.saintdev.pmnadmin.views.fragments.main.StaffFragment;
import kr.saintdev.pmnadmin.views.fragments.main.StoreFragment;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-17
 */

public class MainViewPageAdapter extends FragmentStatePagerAdapter {
    public MainViewPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        SuperFragment fragmn = null;

        switch(position) {
            case 0: fragmn = new StaffFragment(); break;
            case 1: fragmn = new StoreFragment(); break;
            case 2: fragmn = new AlarmFragment(); break;
            case 3: fragmn = new SettingsFragment();  break;
            default: fragmn = new StaffFragment();
        }

        return fragmn;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
