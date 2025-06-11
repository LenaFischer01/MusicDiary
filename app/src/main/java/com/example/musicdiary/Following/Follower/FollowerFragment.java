package com.example.musicdiary.Following.Follower;

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

import com.example.musicdiary.Container.FollowingInfo;
import com.example.musicdiary.Container.UserInfo;
import com.example.musicdiary.MAIN.DatabaseConnectorFirebase;
import com.example.musicdiary.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fragment representing the overview of friend requests.
 */
public class FollowerFragment extends Fragment {

    FollowerRecyclerViewAdapter adapter;
    List<UserInfo> follower = new ArrayList<>();
    TextView noFollower;

    /**
     * Default constructor.
     */
    public FollowerFragment(){
        // Empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for friend request overview
        View view = inflater.inflate(R.layout.follower_overview, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFollower);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FollowerRecyclerViewAdapter(follower);

        noFollower = view.findViewById(R.id.noFollowerTextView);

        recyclerView.setAdapter(adapter);

        return view;
    }

    private void refreshData(){
        DatabaseConnectorFirebase databaseConnectorFirebase = new DatabaseConnectorFirebase();

        databaseConnectorFirebase.getFollowersWithUsername(FirebaseAuth.getInstance().getCurrentUser().getUid(), new DatabaseConnectorFirebase.FollowerNameListCallback() {
                    @Override
                    public void onCallback(Map<String, String> uidToName) {
                        if (!uidToName.isEmpty()){
                            for (Map.Entry<String, String> entry : uidToName.entrySet()) {
                                follower.add(new UserInfo(entry.getValue()));
                            }
                        }
                        adapter.notifyDataSetChanged();

                        if (follower.isEmpty()){
                            noFollower.setVisibility(View.VISIBLE);
                        }
                        else {
                            noFollower.setVisibility(View.GONE);
                        }
                    }
                }
        );



    }

    @Override
    public void onResume() {
        super.onResume();
        follower.clear();
        refreshData();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Additional setup can be done here after the view is created
    }
}