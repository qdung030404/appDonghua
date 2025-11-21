package com.example.appdonghua.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.appdonghua.Fragment.CaseFragment;
import com.example.appdonghua.Fragment.ClassifyFragment;
import com.example.appdonghua.Fragment.HomeFragment;
import com.example.appdonghua.Fragment.UserFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {


    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new ClassifyFragment();
                break;
            case 2:
                fragment = new CaseFragment();
                break;
            case 3:
                fragment = new UserFragment();
                break;
            default:
                fragment = new HomeFragment();
                break;

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }
}
