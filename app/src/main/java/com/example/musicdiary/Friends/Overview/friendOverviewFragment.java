package com.example.musicdiary.Friends.Overview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicdiary.Container.FriendInfo;
import com.example.musicdiary.MAIN.DatabaseConnectorFirebase;
import com.example.musicdiary.MAIN.SharedPreferencesHelper;
import com.example.musicdiary.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class friendOverviewFragment extends Fragment {

    TextView noFriends;
    FriendsOverviewRecyclerViewAdapter adapter;
    List<FriendInfo> friendsList = new ArrayList<>();
    private String UID;


    public friendOverviewFragment(){
        // Alle alle
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_overview, container, false);
        noFriends = view.findViewById(R.id.noFriendsTextView);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFriends);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FriendsOverviewRecyclerViewAdapter(friendsList);
        recyclerView.setAdapter(adapter);

        SharedPreferencesHelper helper = new SharedPreferencesHelper(getContext());
        UID = helper.getUserID();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (friendsList.isEmpty()){
            noFriends.setVisibility(View.VISIBLE);
        }
        refreshData();
    }

    private void refreshData(){
        DatabaseConnectorFirebase databaseConnectorFirebase = new DatabaseConnectorFirebase();

        databaseConnectorFirebase.getFriendList(UID, friends -> {
            friendsList.clear();
            if (friends != null) {
                friendsList.addAll(friends.values());
            }
            adapter.notifyDataSetChanged();

            if (friendsList.isEmpty()) {
                noFriends.setVisibility(View.VISIBLE);
            } else {
                noFriends.setVisibility(View.GONE);
            }
        });
    }
}
