package com.example.musicdiary.Friends;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musicdiary.Friends.Overview.friendOverviewFragment;
import com.example.musicdiary.Friends.Search.FriendSearchFragment;
import com.example.musicdiary.defaultFragment;

/**
 * Adapter managing fragments for the ViewPager in the Friends section.
 */
public class ViewPageAdapterFriends extends FragmentStateAdapter {

    /**
     * Constructor with fragment activity.
     * @param fragmentActivity The hosting FragmentActivity.
     */
    public ViewPageAdapterFriends(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * Creates the fragment for the requested page position.
     * @param position Position index.
     * @return Fragment instance for the given position.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new friendOverviewFragment();
            case 1:
                return new FriendSearchFragment();
            // case 2:
            //     return new friendrequestFragment();
            default:
                return new defaultFragment();
        }
    }

    /**
     * Returns the total number of pages.
     * @return number of fragments/pages.
     */
    @Override
    public int getItemCount() {
        return 2;
    }
}