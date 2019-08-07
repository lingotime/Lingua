package com.lingua.lingua.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.lingua.lingua.fragments.tabs.ReceivedFriendRequestsFragment;
import com.lingua.lingua.fragments.tabs.SentFriendRequestsFragment;

public class FriendRequestTabsAdapter extends FragmentStatePagerAdapter {
    int numOfTabs;

    public FriendRequestTabsAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.numOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ReceivedFriendRequestsFragment receivedTab = new ReceivedFriendRequestsFragment();
                return receivedTab;
            case 1:
                SentFriendRequestsFragment sentTab = new SentFriendRequestsFragment();
                return sentTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}