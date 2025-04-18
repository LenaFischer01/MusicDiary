package com.example.musicdiary.Feed;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicdiary.Container.FriendObject;
import com.example.musicdiary.Container.Post;
import com.example.musicdiary.DatabaseConnectorFirebase;
import com.example.musicdiary.R;
import com.example.musicdiary.SharedPreferencesHelper;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class FeedFragment extends Fragment implements ChooseSongDialogFragment.ChooseSongDialogListener{

    FeedRecyclerViewAdapter recyclerViewAdapter;
    List<FriendObject> friendlist = new ArrayList<>();
    TextView noPostText;
    private Post tempPostObject;
    private TextView displaySong;
    private LocalDate currentDate;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

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

        currentDate = LocalDate.now();

//        DatabaseConnectorFirebase db = new DatabaseConnectorFirebase();
//        db.addPost("friend1", new Post("16.04.2025\n\ndas ist von User 1", "test-test"));
//        db.addPost("friend2", new Post("16.04.2024\n\ndas ist von User 2", "test-test"));
//        db.addUser("friend1");
//        db.addUser("friend2");
//
        SharedPreferencesHelper helper = new SharedPreferencesHelper(getContext());
//        helper.addFriend("friend1");
//        helper.addFriend("friend2");
        helper.saveUploadDate("");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    //THis will need to collect the list of friends in sharedpreferences and fetch the posts of the friends and add them to the friendlist
    //And only add those, that have the correct date
    private void refreshData() {
        friendlist.clear();

        SharedPreferencesHelper helper = new SharedPreferencesHelper(getContext());
        DatabaseConnectorFirebase connectorFirebase = new DatabaseConnectorFirebase();
        Set<String> friends = helper.getFriends();

        String myUsername = helper.getUsername();
        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        AtomicInteger counter = new AtomicInteger(0);
        int total = friends.size() + 1; // friends + user

        // Get user post
        connectorFirebase.getPostForUser(myUsername, post -> {
            if (post != null && post.getPostContent() != null) {
                if (post.getPostContent().startsWith(formattedDate)) {
                    friendlist.add(new FriendObject(myUsername, post));
                }
            }
            if (counter.incrementAndGet() == total) {
                recyclerViewAdapter.notifyDataSetChanged();
                noPostText.setVisibility(friendlist.isEmpty() ? View.VISIBLE : View.INVISIBLE);
            }
        });

        for (String friend : friends) {
            if (friend.equals(myUsername)) continue;
            connectorFirebase.getPostForUser(friend, post -> {
                if (post != null && post.getPostContent() != null) {
                    if (post.getPostContent().startsWith(formattedDate)) {
                        friendlist.add(new FriendObject(friend, post));
                    }
                }
                if (counter.incrementAndGet() == total) {
                    recyclerViewAdapter.notifyDataSetChanged();
                    noPostText.setVisibility(friendlist.isEmpty() ? View.VISIBLE : View.INVISIBLE);
                }
            });
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

        SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(getContext());

        // Get todays date in the pattern yyyy-M-d
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Upload the Post to the DB
                // Lock button until next day (If entry in DB, not working)

                // If no valid username, do not post anything
                if (preferencesHelper.getUsername() == null){
                    Toast.makeText(getContext(), "First choose a valid username :)", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check if post exists, else make new
                if (tempPostObject == null){
                    tempPostObject = new Post();
                }
                //Check if the song and post content are there
                if (tempPostObject.getSong() == null || tempPostObject.getSong() == ""){
                    Toast.makeText(getContext(), "Your song is missing", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check if there has been a post today
                if (!formattedDate.equals(preferencesHelper.getUploadDAte())){
                    fillPostObject(input);

                    if (tempPostObject.getPostContent() == null || tempPostObject.getPostContent()==""){
                        Toast.makeText(getContext(), "Your post is missing", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (getOwnPost() != null){
                        preferencesHelper.saveUploadDate(formattedDate);
                        //this will has to be removed and replaced with a push to the db
                        uploadPost(preferencesHelper.getUsername(), getOwnPost());
                    }
                }
                else {
                    Toast.makeText(getContext(), "You already posted something today", Toast.LENGTH_SHORT).show();
                }
                clearInputStuff(displaySong, input);
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

    private void clearInputStuff(TextView text, EditText eText){
        text.setText("Your song");
        eText.setText("");
        tempPostObject = null;
    }

    private void uploadPost(String username, Post post){
        DatabaseConnectorFirebase databaseConnectorFirebase = new DatabaseConnectorFirebase();
        databaseConnectorFirebase.addPost(username, post);
        refreshData();
    }

    private void showDialog(){
        DialogFragment dialogFragment = new ChooseSongDialogFragment();
        dialogFragment.setTargetFragment(this,0);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "inputSong");
    }

    /**This method checks if the tempPostObject is null or empty and returns it.
     * @return tempPostObject*/
    private Post getOwnPost(){
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
    private void fillPostObject(EditText input){
        if (input.getText() != null && !input.getText().toString().isEmpty()){
            String formattedDate = currentDate.format(formatter);
            tempPostObject.setPostContent(formattedDate+"\n\n" + input.getText().toString());
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