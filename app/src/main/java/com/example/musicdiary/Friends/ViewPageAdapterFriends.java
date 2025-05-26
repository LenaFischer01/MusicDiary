package com.example.musicdiary.Friends;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musicdiary.Friends.Overview.friendOverviewFragment;
import com.example.musicdiary.Friends.Search.FriendSearchFragment;
import com.example.musicdiary.defaultFragment;

public class ViewPageAdapterFriends extends FragmentStateAdapter {
    public ViewPageAdapterFriends(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new friendOverviewFragment();
            case 1:
                return new FriendSearchFragment();
//            case 2:
//                return new friendrequestFragment();
            default:
                return new defaultFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
