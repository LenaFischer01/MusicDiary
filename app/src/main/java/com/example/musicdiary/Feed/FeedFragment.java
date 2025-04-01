package com.example.musicdiary.Feed;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicdiary.Container.FriendObject;
import com.example.musicdiary.R;
import com.example.musicdiary.ViewPageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    FeedRecyclerViewAdapter recyclerViewAdapter;
    List<FriendObject> friendlist = new ArrayList<>();

    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed, container, false);

        //Testdaten
        friendlist.add(new FriendObject("Lenaaa", "Das ist ein Testpost. Hier stehen post sachen drinne", "Starlight - Muse"));
        friendlist.add(new FriendObject("Tom", "Just finished reading a great book!", "Imagine - John Lennon"));
        friendlist.add(new FriendObject("Sarah", "Had an amazing day at the beach today.", "Walking on Sunshine - Katrina and the Waves"));
        friendlist.add(new FriendObject("Jack", "Exploring new music genres lately.", "Bohemian Rhapsody - Queen"));
        friendlist.add(new FriendObject("Emily", "Can’t wait for the weekend to start.", "Friday I'm in Love - The Cure"));

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFriendsPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewAdapter = new FeedRecyclerViewAdapter(friendlist){};
        recyclerView.setAdapter(recyclerViewAdapter);

        return view;
    }
}