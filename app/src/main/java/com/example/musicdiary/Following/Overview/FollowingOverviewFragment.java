package com.example.musicdiary.Following.Overview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicdiary.Container.FollowingInfo;
import com.example.musicdiary.MAIN.DatabaseConnectorFirebase;
import com.example.musicdiary.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that displays an overview of friends.
 * Allows removal of friends via a dialog.
 */
public class FollowingOverviewFragment extends Fragment implements StopFollowingDialog.RemoveFriendDialogListener {

    TextView noFriends;
    FollowingOverviewRecyclerViewAdapter adapter;
    List<FollowingInfo> friendsList = new ArrayList<>();
    private String UID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.following_overview, container, false);
        noFriends = view.findViewById(R.id.noFriendsTextView);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFriends);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FollowingOverviewRecyclerViewAdapter(friendsList, new FollowingOverviewRecyclerViewAdapter.OnRemoveFriendClickListener() {
            @Override
            public void onRemoveFriendClick(FollowingInfo info, int pos) {
                // Show dialog to confirm friend removal
                StopFollowingDialog dialog = StopFollowingDialog.newInstance(info.getUserID(), pos);
                dialog.setListener(FollowingOverviewFragment.this);
                dialog.show(getParentFragmentManager(), "RemoveFriendDialog");
            }
        });
        recyclerView.setAdapter(adapter);

        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Show "no friends" message if the friends list is empty
        if (friendsList.isEmpty()) {
            noFriends.setVisibility(View.VISIBLE);
        }
        refreshData();
    }

    /**
     * Fetches friend list from the database and updates the UI.
     */
    private void refreshData() {
        DatabaseConnectorFirebase databaseConnectorFirebase = new DatabaseConnectorFirebase();
        databaseConnectorFirebase.getFriendInfoList(UID, friends -> {
            friendsList.clear();

            if (friends != null) {
                friendsList.addAll(friends.values());
            }

            adapter.notifyDataSetChanged();

            // Show or hide "no friends" message based on the list content
            if (friendsList.isEmpty()) {
                noFriends.setVisibility(View.VISIBLE);
            } else {
                noFriends.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Callback when friend removal is confirmed in the dialog.
     * @param friendUserId ID of the friend to remove
     * @param position Position in the list
     */
    @Override
    public void onDialogSubmit(String friendUserId, int position) {
        DatabaseConnectorFirebase databaseConnectorFirebase = new DatabaseConnectorFirebase();
        databaseConnectorFirebase.removeFriend(UID, friendUserId);
        adapter.removeItem(position);
        Toast.makeText(getContext(), "Stop following", Toast.LENGTH_SHORT).show();
        if (friendsList.isEmpty()) {
            noFriends.setVisibility(View.VISIBLE);
        }
    }
}