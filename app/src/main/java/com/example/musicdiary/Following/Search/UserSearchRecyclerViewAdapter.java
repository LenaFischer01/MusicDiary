package com.example.musicdiary.Following.Search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicdiary.Container.FollowingInfo;
import com.example.musicdiary.MAIN.DatabaseConnectorFirebase;
import com.example.musicdiary.R;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * RecyclerView adapter for displaying search results for friends.
 * Allows adding friends from the search results.
 */
public class UserSearchRecyclerViewAdapter extends RecyclerView.Adapter<UserSearchRecyclerViewAdapter.EntryViewHolder> {

    private List<FollowingInfo> items;
    private String UID;

    /**
     * Constructor initializing with a list of FriendInfo items.
     * @param items List of friend search result items to display.
     */
    public UserSearchRecyclerViewAdapter(List<FollowingInfo> items){
        this.items = items;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single search result friend card
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usercard_search, parent, false);
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return new UserSearchRecyclerViewAdapter.EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        DatabaseConnectorFirebase databaseConnectorFirebase = new DatabaseConnectorFirebase();

        FollowingInfo info = items.get(position);
        holder.viewFriendName.setText(info.getUsername());

        // Check if the user is already a friend to update the button state
        databaseConnectorFirebase.getFriendList(UID, new DatabaseConnectorFirebase.FriendListCallback() {
            @Override
            public void onCallback(Map<String, FollowingInfo> friends) {
                if (friends.containsKey(info.getUserID())) {
                    holder.addFriend.setText("Added");
                    holder.addFriend.setEnabled(false);
                } else {
                    holder.addFriend.setText("Add");
                    holder.addFriend.setEnabled(true);
                }
            }
        });

        holder.addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseConnectorFirebase.getFriendList(FirebaseAuth.getInstance().getCurrentUser().getUid(), new DatabaseConnectorFirebase.FriendListCallback() {
                    @Override
                    public void onCallback(Map<String, FollowingInfo> friends) {
                        if (friends.containsKey(info.getUserID())){
                            Toast.makeText(v.getContext(), "You are already friends!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(v.getContext(), "Added!", Toast.LENGTH_SHORT).show();
                            holder.addFriend.setText("Added");
                            holder.addFriend.setEnabled(false);

                            // Record the current date as the friendship start date
                            info.setSinceTimestamp(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

                            // Add the friend using the database connector
                            databaseConnectorFirebase.addFriend(FirebaseAuth.getInstance().getCurrentUser().getUid(), info);
                            databaseConnectorFirebase.addFollower(FirebaseAuth.getInstance().getCurrentUser().getUid(), info.getUserID());
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * ViewHolder class to hold references to views of a single friend search result.
     */
    static class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView viewFriendName;
        Button addFriend;
        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);

            viewFriendName = itemView.findViewById(R.id.textViewSearchFriendName);
            addFriend = itemView.findViewById(R.id.buttonSendRequest);
        }
    }
}