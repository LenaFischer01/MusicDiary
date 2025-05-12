package com.example.musicdiary.Friends.Overview;

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

import java.util.List;

public class FriendsOverviewRecyclerViewAdapter extends RecyclerView.Adapter<FriendsOverviewRecyclerViewAdapter.EntryViewHolder> {

    List<FriendInfo> items;

    public FriendsOverviewRecyclerViewAdapter(List<FriendInfo> items){
        this.items = items;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendcard, parent, false);
        return new FriendsOverviewRecyclerViewAdapter.EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        DatabaseConnectorFirebase databaseConnectorFirebase = new DatabaseConnectorFirebase();

        FriendInfo info = items.get(position);

        holder.friendname.setText(info.getUsername());
        holder.friendsSince.setText(info.getSinceTimestamp());

        holder.removeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesHelper helper = new SharedPreferencesHelper(v.getContext());
                databaseConnectorFirebase.removeFriend(helper.getUserID(), info.getUserID());
                Toast.makeText(v.getContext(), "Removed friend", Toast.LENGTH_SHORT).show();
                int pos = holder.getAdapterPosition();
                items.remove(pos);
                notifyItemRemoved(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class EntryViewHolder extends RecyclerView.ViewHolder{
        TextView friendname, friendsSince;
        Button removeFriend;
        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            friendname = itemView.findViewById(R.id.textViewFriendName);
            friendsSince = itemView.findViewById(R.id.textViewFriendshipDate);
            removeFriend = itemView.findViewById(R.id.removeFriendButton);
        }
    }
}
