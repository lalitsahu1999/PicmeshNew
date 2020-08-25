package com.ihsuraa.picmesh;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class CustomPickerAdapter extends FragmentStatePagerAdapter {

    CustomPickerAdapter(FragmentManager fm) {
        super(fm,FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new FragmentImageChooser();
            case 1: return new FragmentVideoChooser();
        }
        return null;
    }



    @Override
    public int getCount() {
        return 2;
    }
}