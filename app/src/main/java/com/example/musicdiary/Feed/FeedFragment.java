package com.example.musicdiary.Feed;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
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
import com.example.musicdiary.Container.Post;
import com.example.musicdiary.R;
import com.example.musicdiary.ViewPageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FeedFragment extends Fragment implements ChooseSongDialogFragment.ChooseSongDialogListener{

    FeedRecyclerViewAdapter recyclerViewAdapter;
    List<FriendObject> friendlist = new ArrayList<>();
    TextView noPostText;
    private String lastUploadDate;
    private Post tempPostObject;
    private TextView displaySong;

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
        Button scrollButton = (Button) view.findViewById(R.id.scrollButtonFeed);

        EditText input = (EditText) view.findViewById(R.id.editTextPost);

        displaySong = (TextView) view.findViewById(R.id.textView_song_feed);

        NestedScrollView scrollView = (NestedScrollView) view.findViewById(R.id.scrollviewFeed);

        // Get todays date in the pattern yyyy-M-d
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        String formattedDate = currentDate.format(formatter);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Upload the Post to the DB
                // Lock button until next day (If entry in DB, not working)
                if (tempPostObject == null){
                    tempPostObject = new Post();
                }
                if (tempPostObject.getSong() == null || tempPostObject.getSong() == ""){
                    Toast.makeText(getContext(), "Your song is missing", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tempPostObject.getPostContent() == null || tempPostObject.getPostContent()==""){
                    Toast.makeText(getContext(), "Your post is missing", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!formattedDate.equals(lastUploadDate)){
                    fillPostObject(input);
                    // First post mechanic - DB still missing
                    if (getOwnPost() != null){
                        lastUploadDate = formattedDate;
                        friendlist.add(new FriendObject("You", getOwnPost()));
                        input.setText("");
                        displaySong.setText("Your song");

                    }
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

        chooseSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

    }

    void showDialog(){
        DialogFragment dialogFragment = new ChooseSongDialogFragment();
        dialogFragment.setTargetFragment(this,0);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "inputSong");
    }

    /**This method checks if the tempPostObject is null or empty and returns it.
     * @return tempPostObject*/
    public Post getOwnPost(){
        if (tempPostObject != null && !tempPostObject.getPostContent().isEmpty()){
            if (tempPostObject.getSong() != null && !tempPostObject.getSong().isEmpty()){
                return tempPostObject;
            }
            else {
                Toast.makeText(getContext(), "Your song is missing...", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        else {
            return null;
        }
    }
    /**This method fills the TempPostObject with the user input in the EditText.*/
    public void fillPostObject(EditText input){
        if (input.getText() != null && !input.getText().toString().isEmpty()){
            tempPostObject.setPostContent(input.getText().toString());
        }
        else {
            tempPostObject.setPostContent("");
        }
    }

    /**This method is triggered when the submit button in the dialog to choose the song is pressed.
     * It validates the input and adds it to the TempPostObject.*/
    @Override
    public void onDialogSubmit(String songName) {
        if (!songName.isEmpty()){
            songName.replace(" ", "");
            if (songName.matches("^\\s*.+\\s*-\\s*.+\\s*$")){
                String[] song = songName.split("-");
                if (tempPostObject == null){
                    tempPostObject = new Post();
                }
                tempPostObject.setSong(song[0]+" - "+song[1]);

                displaySong.setText(song[0]+" - "+song[1]);
            }
            else {
                Toast.makeText(getContext(), "Please use the correct syntax -> Song-Artist", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getContext(), "You need to enter something...", Toast.LENGTH_SHORT).show();
        }
    }
}