package com.example.musicdiary.Following;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.musicdiary.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Fragment handling the friends section with tabs for friends and search functionality.
 */
public class FollowingFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialization code can be added here if needed
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for the friends fragment containing ViewPager2 and TabLayout
        View view = inflater.inflate(R.layout.following, container, false);

        ViewPager2 viewPager2 = view.findViewById(R.id.viewPagerFriends);
        TabLayout tabLayout = view.findViewById(R.id.tabLayoutFriends);

        ViewPageAdapterFriends adapter = new ViewPageAdapterFriends(getActivity());
        viewPager2.setAdapter(adapter);
        viewPager2.setCurrentItem(0, true);

        // Attach TabLayoutMediator to synchronize TabLayout and ViewPager2
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Friends");
                            break;
                        case 1:
                            tab.setText("Search friends");
                            break;
                        // case 2:
                        //    tab.setText("Friend requests");
                        //    break;
                    }
                }).attach();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}