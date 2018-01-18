package com.example.enlogty.communicationassistant.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.enlogty.communicationassistant.fragment.RestorePictureFragment;
import com.example.enlogty.communicationassistant.fragment.SynchroRecordFragment;
import com.example.enlogty.communicationassistant.fragment.TestFragment;

/**
 * Created by enlogty on 2017/12/23.
 */

public class SyncNoteFragmentPagerAdapter extends FragmentPagerAdapter{
    private String[] mTitles = new String[]{"同步记录","恢复图片"};

    public SyncNoteFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0){
            return new SynchroRecordFragment();
        }else if (position == 1){
            return new RestorePictureFragment();
        }
        return new TestFragment();
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
