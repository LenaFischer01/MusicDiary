package com.example.musicdiary.Following;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musicdiary.Following.Follower.FollowerFragment;
import com.example.musicdiary.Following.Overview.FollowingOverviewFragment;
import com.example.musicdiary.Following.Search.UserSearchFragment;
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
                return new FollowingOverviewFragment();
            case 1:
                return new UserSearchFragment();
             case 2:
                 return new FollowerFragment();
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
        return 3;
    }
}