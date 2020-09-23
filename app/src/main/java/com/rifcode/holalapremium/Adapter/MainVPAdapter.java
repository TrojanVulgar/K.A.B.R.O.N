package com.rifcode.holalapremium.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainVPAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mList = new ArrayList<>();


    public MainVPAdapter(@NonNull FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }


    @Override
    public Fragment getItem(int i) {
        return mList.get(i);
    }
    @Override
    public int getCount() {
        return mList.size();
    }
    public void addFragment(Fragment fragment) {
        mList.add(fragment);

    }


}