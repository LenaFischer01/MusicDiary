package com.example.musicdiary.Following.Follower;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicdiary.Container.FollowingInfo;
import com.example.musicdiary.Container.UserInfo;
import com.example.musicdiary.Following.Overview.FollowingOverviewRecyclerViewAdapter;
import com.example.musicdiary.R;
import com.firebase.ui.auth.data.model.User;

import java.util.List;

public class FollowerRecyclerViewAdapter extends RecyclerView.Adapter<FollowerRecyclerViewAdapter.EntryViewHolder> {

    List<UserInfo> follower;

    public FollowerRecyclerViewAdapter(List<UserInfo> follower){
        this.follower = follower;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.followercard, parent, false);
        return new FollowerRecyclerViewAdapter.EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        UserInfo item = follower.get(position);

        holder.followername.setText("> "+item.getUsername());
    }

    @Override
    public int getItemCount() {
        return follower.size();
    }

    static class EntryViewHolder extends RecyclerView.ViewHolder{

        TextView followername;
        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            followername = itemView.findViewById(R.id.textViewFollowerName);
        }
    }
}
