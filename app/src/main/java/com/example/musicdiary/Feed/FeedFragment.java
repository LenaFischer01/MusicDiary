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
import com.example.musicdiary.DatabaseConnectorFirebase;
import com.example.musicdiary.R;
import com.example.musicdiary.SharedPreferencesHelper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FeedFragment extends Fragment implements ChooseSongDialogFragment.ChooseSongDialogListener{

    FeedRecyclerViewAdapter recyclerViewAdapter;
    List<FriendObject> friendlist = new ArrayList<>();
    TextView noPostText;
    private Post tempPostObject;
    private TextView displaySong;
    private LocalDate currentDate;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");

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

        currentDate = LocalDate.now();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    //THis will need to collect the list of friends in sharedpreferences and fetch the posts of the friends and add them to the friendlist
    //And only add those, that have the correct date
    private void refreshData(){
        friendlist.clear();

        SharedPreferencesHelper helper = new SharedPreferencesHelper(getContext());
        DatabaseConnectorFirebase connectorFirebase = new DatabaseConnectorFirebase();

        Set<String> friends = helper.getFriends();
        Map<String, Post> postMap = new HashMap<>();

        connectorFirebase.getPostForUser(helper.getUsername(), new DatabaseConnectorFirebase.PostCallback() {
            @Override
            public void onCallback(Post post) {
                friendlist.add(new FriendObject(helper.getUsername(), post));
            }
        });

        for (String friend : friends){
            connectorFirebase.getPostForUser(friend, new DatabaseConnectorFirebase.PostCallback() {
                @Override
                public void onCallback(Post post) {
                    if (post != null){
                        postMap.put(friend, post);
                    }
                }
            });

            if (currentDate != null) {
                String formattedDate = currentDate.format(formatter);

                if (postMap.get(friend) != null){
                    if (postMap.get(friend).getPostContent().startsWith(formattedDate)){
                        friendlist.add(new FriendObject(friend, postMap.get(friend)));
                    }
                    // Logic, to manage namechange Posts - Needs to be tested
                    else if (postMap.get(friend).getPostContent().startsWith("#changedName")){
                        String oldname = friend;
                        String newName = postMap.get(friend).getPostContent().replace(" ", "").replace("#changedName", "");
                        helper.deleteFriend(oldname);
                        helper.addFriend(newName);
                    }
                }
            }
        }

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

        SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(getContext());

        // Get todays date in the pattern yyyy-M-d
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
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
                    // First post mechanic - DB still missing
                    if (getOwnPost() != null){
                        preferencesHelper.saveUploadDate(formattedDate);
                        //this will has to be removed and replaced with a push to the db
                        uploadPost(preferencesHelper.getUsername(), getOwnPost());
                        friendlist.add(new FriendObject(preferencesHelper.getUsername(), getOwnPost()));
                    }
                    refreshData();
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