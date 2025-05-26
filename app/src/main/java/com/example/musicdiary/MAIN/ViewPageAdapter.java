package com.example.musicdiary.MAIN;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musicdiary.Feed.FeedFragment;
import com.example.musicdiary.Friends.FriendsFragment;
import com.example.musicdiary.Settings.SettingsFragment;

/**
 * Adapter for the ViewPager2 in MainActivity, manages the fragments for Feed, Friends, and Settings tabs.
 */
public class ViewPageAdapter extends FragmentStateAdapter {

    /**
     * Constructor for the adapter.
     * @param fragmentActivity The hosting FragmentActivity.
     */
    public ViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * Creates a fragment for the given position.
     * @param position The index of the fragment to create.
     * @return The Fragment corresponding to the position.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new FeedFragment();
            case 1:
                return new FriendsFragment();
            case 2:
                return new SettingsFragment();
            default:
                return new FeedFragment();
        }
    }

    /**
     * Returns the total number of pages.
     * @return Number of total pages/fragments.
     */
    @Override
    public int getItemCount() {
        return 3;
    }
}