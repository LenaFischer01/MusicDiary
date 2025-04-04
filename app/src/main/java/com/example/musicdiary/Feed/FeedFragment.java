package com.example.musicdiary.Feed;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicdiary.Container.FriendObject;
import com.example.musicdiary.R;
import com.example.musicdiary.ViewPageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    FeedRecyclerViewAdapter recyclerViewAdapter;
    List<FriendObject> friendlist = new ArrayList<>();
    TextView noPostText;
    Boolean uploaded = false;

    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed, container, false);


        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFriendsPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewAdapter = new FeedRecyclerViewAdapter(friendlist){};
        recyclerView.setAdapter(recyclerViewAdapter);

        noPostText = view.findViewById(R.id.noPostTextFeed);

        refreshData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData(){
        recyclerViewAdapter.notifyDataSetChanged();

        if (friendlist.isEmpty()){
            noPostText.setVisibility(View.VISIBLE);
        }
        else{
            noPostText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button uploadButton = (Button) view.findViewById(R.id.buttonUploadPost);
        Button chooseSong = (Button) view.findViewById(R.id.buttonChooseSong);
        EditText input = (EditText) view.findViewById(R.id.editTextPost);
        Button scrollButton = (Button) view.findViewById(R.id.scrollButtonFeed);
        NestedScrollView scrollView = (NestedScrollView) view.findViewById(R.id.scrollviewFeed);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Upload the Post to the DB
                // Lock button until next day (If entry in DB, not working)
                if (!uploaded){
                    if (input.getText().isEmpty()){ // It's working, stop beeing so dramatic
                        Toast.makeText(getContext(), "You cannot post something empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // First post mechanic - DB still missing
                    String postText = input.getText() != null ? input.getText().toString() : "";
                    uploaded = true;
                    friendlist.add(new FriendObject("You", postText, ""));
                    input.setText("");
                    refreshData();
                }
                else {
                    Toast.makeText(getContext(), "You already posted something today", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Scrolls back to the top of the Fragment
        scrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0,0);
            }
        });

    }
}