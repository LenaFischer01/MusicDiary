package com.example.musicdiary.Feed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicdiary.Container.FriendObject;
import com.example.musicdiary.R;

import java.util.List;

public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.EntryViewHolder>{

    private List<FriendObject> items;
    public FeedRecyclerViewAdapter(List<FriendObject> items){
        this.items = items;

    }
    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendpost, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        FriendObject friend = items.get(position);

        holder.viewFriendsName.setText(friend.getUsername());
        holder.viewFriendspost.setText(friend.getTodaysPost().getPostContent());
        holder.viewFriendssong.setText(friend.getTodaysPost().getSong());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView viewFriendsName, viewFriendspost, viewFriendssong;
        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            viewFriendsName = itemView.findViewById(R.id.textViewFriendName);
            viewFriendspost = itemView.findViewById(R.id.textViewPostContent);
            viewFriendssong = itemView.findViewById(R.id.textViewSong);
        }
    }
}
