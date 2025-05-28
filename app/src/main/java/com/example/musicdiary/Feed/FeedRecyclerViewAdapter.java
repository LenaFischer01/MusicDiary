package com.example.musicdiary.Feed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicdiary.Container.FollowingPostObject;
import com.example.musicdiary.R;

import java.util.List;

/**
 * RecyclerView adapter to display posts from friends in a feed.
 */
public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.EntryViewHolder>{

    private List<FollowingPostObject> items;

    /**
     * Constructor to initialize adapter with a list of FriendPostObject items.
     * @param items List of FriendPostObject to display
     */
    public FeedRecyclerViewAdapter(List<FollowingPostObject> items){
        this.items = items;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single friend post item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.followingpost, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        // Bind data from FriendPostObject to the views
        FollowingPostObject friend = items.get(position);

        holder.viewFriendsName.setText(friend.getTodaysPost().getAuthor());
        holder.viewFriendspost.setText(friend.getTodaysPost().getPostContent());
        holder.viewFriendssong.setText(friend.getTodaysPost().getSong());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * ViewHolder class to hold references to the views of a single post entry.
     */
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