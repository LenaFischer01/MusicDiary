package com.example.musicdiary.Friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.musicdiary.R;

public class friendOverviewFragment extends Fragment {

    TextView noFriends;

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
        // Space for some code ...
        noFriends = view.findViewById(R.id.noFriendsTextView);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData(){
        // The same stuff as in in the Feed code
    }
}
