package com.example.musicdiary.Friends.Search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicdiary.Container.FriendInfo;
import com.example.musicdiary.MAIN.DatabaseConnectorFirebase;
import com.example.musicdiary.MAIN.SharedPreferencesHelper;
import com.example.musicdiary.R;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class FriendSearchRecyclerViewAdapter extends RecyclerView.Adapter<FriendSearchRecyclerViewAdapter.EntryViewHolder> {

    private List<FriendInfo> items;
    private String UID;

    public FriendSearchRecyclerViewAdapter(List<FriendInfo> items){
        this.items = items;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendcard_search, parent, false);
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return new FriendSearchRecyclerViewAdapter.EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        DatabaseConnectorFirebase databaseConnectorFirebase = new DatabaseConnectorFirebase();

        FriendInfo info = items.get(position);
        holder.viewFriendName.setText(info.getUsername());

        databaseConnectorFirebase.getFriendList(UID, new DatabaseConnectorFirebase.FriendListCallback() {
            @Override
            public void onCallback(Map<String, FriendInfo> friends) {
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
                    public void onCallback(Map<String, FriendInfo> friends) {
                        if (friends.containsKey(info.getUserID())){
                            Toast.makeText(v.getContext(), "You are already friends!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(v.getContext(), "Added!", Toast.LENGTH_SHORT).show();
                            holder.addFriend.setText("Added");

                            holder.addFriend.setEnabled(false);

                            // Mechanic to really add friend
                            info.setSinceTimestamp(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

                            databaseConnectorFirebase.addFriend(FirebaseAuth.getInstance().getCurrentUser().getUid(), info);
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
