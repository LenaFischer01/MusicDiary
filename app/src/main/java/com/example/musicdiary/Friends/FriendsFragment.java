package com.example.musicdiary.Friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.musicdiary.R;
import com.example.musicdiary.ViewPageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FriendsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends, container, false);

        ViewPager2 viewPager2;
        TabLayout tabLayout;


        viewPager2 = (ViewPager2) view.findViewById(R.id.viewPagerFriends);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayoutFriends);

        ViewPageAdapterFriends adapter = new ViewPageAdapterFriends(getActivity());
        viewPager2.setAdapter(adapter);
        viewPager2.setCurrentItem(0,true);

        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Friends");
                            break;
                        case 1:
                            tab.setText("Friend requests");
                            break;
                        case 2:
                            tab.setText("Search friends");
                            break;
                    }
                }).attach();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }
}
