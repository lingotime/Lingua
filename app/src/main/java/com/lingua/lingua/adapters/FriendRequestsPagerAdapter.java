package com.lingua.lingua.adapters;

import android.os.Parcelable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.lingua.lingua.fragments.tabs.ReceivedFriendRequestsFragment;
import com.lingua.lingua.fragments.tabs.SentFriendRequestsFragment;
import com.lingua.lingua.models.User;

public class FriendRequestsPagerAdapter extends FragmentStatePagerAdapter {
    int numOfTabs;
    User user;

    public FriendRequestsPagerAdapter(FragmentManager fm, int NumOfTabs, User user) {
        super(fm);
        this.user = user;
        this.numOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ReceivedFriendRequestsFragment receivedTab = ReceivedFriendRequestsFragment.newInstance(user);
                return receivedTab;
            case 1:
                SentFriendRequestsFragment sentTab = SentFriendRequestsFragment.newInstance(user);
                return sentTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}