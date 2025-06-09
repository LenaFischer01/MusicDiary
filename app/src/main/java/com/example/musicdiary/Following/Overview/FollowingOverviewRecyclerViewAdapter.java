package com.example.musicdiary.Following.Overview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicdiary.Container.FollowingInfo;
import com.example.musicdiary.MAIN.DatabaseConnectorFirebase;
import com.example.musicdiary.R;

import java.util.List;

/**
 * RecyclerView adapter for displaying a list of friends with option to remove each friend.
 */
public class FollowingOverviewRecyclerViewAdapter extends RecyclerView.Adapter<FollowingOverviewRecyclerViewAdapter.EntryViewHolder> {

    /**
     * Listener interface for handling remove friend button clicks.
     */
    public interface OnRemoveFriendClickListener {
        void onRemoveFriendClick(FollowingInfo info, int pos);
    }

    List<FollowingInfo> items;
    OnRemoveFriendClickListener onRemoveFriendClickListener;

    /**
     * Constructor to initialize adapter with items and listener.
     * @param items List of FriendInfo objects to display
     * @param listener Listener for remove friend button clicks
     */
    public FollowingOverviewRecyclerViewAdapter(List<FollowingInfo> items, OnRemoveFriendClickListener listener) {
        this.items = items;
        this.onRemoveFriendClickListener = listener;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single friend card
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.followingcard, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        // Bind friend data to the viewholder
        FollowingInfo info = items.get(position);
        holder.friendsSince.setText(info.getSinceTimestamp());

        DatabaseConnectorFirebase db = new DatabaseConnectorFirebase();

        db.getUsernameByID(info.getUserID(), new DatabaseConnectorFirebase.GetUserCallback() {
            @Override
            public void onCallback(String userString, String userId) {
                holder.friendname.setText(userString);
            }
        });

        holder.removeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRemoveFriendClickListener != null) {
                    onRemoveFriendClickListener.onRemoveFriendClick(info, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Removes the friend item at the specified position and notifies the adapter.
     * @param position Position of the item to remove
     */
    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * ViewHolder class holding references to friend card views.
     */
    static class EntryViewHolder extends RecyclerView.ViewHolder {
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