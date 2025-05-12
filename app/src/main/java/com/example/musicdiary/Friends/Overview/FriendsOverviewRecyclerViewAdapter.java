package com.example.musicdiary.Friends.Overview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicdiary.Container.FriendInfo;
import com.example.musicdiary.R;

import java.util.List;

public class FriendsOverviewRecyclerViewAdapter extends RecyclerView.Adapter<FriendsOverviewRecyclerViewAdapter.EntryViewHolder> {

    public interface OnRemoveFriendClickListener {
        void onRemoveFriendClick(FriendInfo info, int pos);
    }

    List<FriendInfo> items;
    OnRemoveFriendClickListener onRemoveFriendClickListener;

    public FriendsOverviewRecyclerViewAdapter(List<FriendInfo> items, OnRemoveFriendClickListener listener) {
        this.items = items;
        this.onRemoveFriendClickListener = listener;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendcard, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        FriendInfo info = items.get(position);

        holder.friendname.setText(info.getUsername());
        holder.friendsSince.setText(info.getSinceTimestamp());

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

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

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