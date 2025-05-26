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

import com.example.musicdiary.Container.FriendInfo;
import com.example.musicdiary.Container.FriendPostObject;
import com.example.musicdiary.Container.Post;
import com.example.musicdiary.MAIN.DatabaseConnectorFirebase;
import com.example.musicdiary.R;
import com.example.musicdiary.MAIN.SharedPreferencesHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import java.util.List;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fragment displaying the feed with posts from friends and self.
 */
public class FeedFragment extends Fragment implements ChooseSongDialogFragment.ChooseSongDialogListener{

    FeedRecyclerViewAdapter recyclerViewAdapter;
    List<FriendPostObject> friendlist = new ArrayList<>();
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

        DatabaseConnectorFirebase db = new DatabaseConnectorFirebase();
        SharedPreferencesHelper helper = new SharedPreferencesHelper(getContext());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    /**
     * This method collects the list of friends from the database,
     * fetches their posts and adds them to the friend list.
     */
    private void refreshData() {
        friendlist.clear();

        SharedPreferencesHelper helper = new SharedPreferencesHelper(getContext());
        DatabaseConnectorFirebase connectorFirebase = new DatabaseConnectorFirebase();
        List<FriendInfo> friendInfoSet = new ArrayList<>();

        connectorFirebase.getFriendList(FirebaseAuth.getInstance().getCurrentUser().getUid(), new DatabaseConnectorFirebase.FriendListCallback() {
            @Override
            public void onCallback(Map<String, FriendInfo> friends) {

                friendInfoSet.addAll(friends.values());

                String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                AtomicInteger counter = new AtomicInteger(0);
                int total = friendInfoSet.size() + 1; // friends + user

                // Get user post
                connectorFirebase.getPostForUser(myUserID, post -> {
                    if (post != null && post.getPostContent() != null) {
                        friendlist.add(new FriendPostObject(helper.getName(), post));
                    }
                    if (counter.incrementAndGet() == total) {
                        recyclerViewAdapter.notifyDataSetChanged();
                        noPostText.setVisibility(friendlist.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                });

                // Adds FriendPostObjects to the friend list for all posts in the database
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate limitDate = LocalDate.now().minusDays(2);

                for (FriendInfo friend : friendInfoSet){
                    connectorFirebase.getPostForUser(friend.getUserID(), post -> {
                        if (post != null && post.getPostContent() != null){

                            // Extract the Date from the Post
                            String postContent = post.getPostContent();
                            if (postContent.length() < 10) {
                                // Ignore post, syntax incorrect
                                if (counter.incrementAndGet() == total){
                                    recyclerViewAdapter.notifyDataSetChanged();
                                    noPostText.setVisibility(friendlist.isEmpty() ? View.VISIBLE : View.GONE);
                                }
                                return;
                            }

                            String dateString = postContent.substring(0, 10);
                            LocalDate postDate;
                            try {
                                postDate = LocalDate.parse(dateString, formatter);
                            } catch (Exception e) {
                                // Incorrect Date
                                if (counter.incrementAndGet() == total){
                                    recyclerViewAdapter.notifyDataSetChanged();
                                    noPostText.setVisibility(friendlist.isEmpty() ? View.VISIBLE : View.GONE);
                                }
                                return;
                            }

                            if (postDate.isBefore(limitDate)) {
                                // Post older than 2 days -> Ignore
                                if (counter.incrementAndGet() == total){
                                    recyclerViewAdapter.notifyDataSetChanged();
                                    noPostText.setVisibility(friendlist.isEmpty() ? View.VISIBLE : View.GONE);
                                }
                                return;
                            }

                            // Add Post
                            friendlist.add(new FriendPostObject(friend.getUsername(), post));
                        }

                        if (counter.incrementAndGet() == total){
                            recyclerViewAdapter.notifyDataSetChanged();
                            noPostText.setVisibility(friendlist.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                    });
                }
            }
        });
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

        // Get today's date
        String formattedDate = currentDate.format(formatter);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Upload the post to the database
                // Lock button until next day (If entry in DB, not working)

                // If the user hasn't set a name yet (meaning also no UID), stop this
                if (preferencesHelper.getName() == null){
                    Toast.makeText(getContext(), "First choose a valid username :)", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if post exists, else create new
                if (tempPostObject == null){
                    tempPostObject = new Post();
                }
                // Check if the song and post content are set
                if (tempPostObject.getSong() == null || tempPostObject.getSong().isEmpty()){
                    Toast.makeText(getContext(), "Your song is missing", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if there has been a post today
                if (!formattedDate.equals(preferencesHelper.getUploadDAte())){
                    fillPostObject(input);

                    if (tempPostObject.getPostContent() == null || tempPostObject.getPostContent().isEmpty()){
                        Toast.makeText(getContext(), "Your post is missing", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (getOwnPost() != null){
                        preferencesHelper.saveUploadDate(formattedDate);
                        uploadPost(FirebaseAuth.getInstance().getCurrentUser().getUid(), getOwnPost());
                    }
                }
                else {
                    Toast.makeText(getContext(), "You already posted something today", Toast.LENGTH_SHORT).show();
                }
                clearInputStuff(displaySong, input);
            }
        });

        // Scrolls back to the top of the fragment
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

    /**
     * Clears the input fields and resets tempPostObject.
     * @param text TextView displaying the song
     * @param eText EditText input for the post content
     */
    private void clearInputStuff(TextView text, EditText eText){
        text.setText("Your song");
        eText.setText("");
        tempPostObject = null;
    }

    /**
     * Uploads the post to the database after deleting old posts for the user.
     * @param userID User ID
     * @param post Post to upload
     */
    private void uploadPost(String userID, Post post){
        DatabaseConnectorFirebase databaseConnectorFirebase = new DatabaseConnectorFirebase();
        databaseConnectorFirebase.deleteAllPostsForUser(userID);
        databaseConnectorFirebase.addPost(userID, post);
        refreshData();
    }

    /**
     * Shows the dialog to choose a song.
     */
    private void showDialog(){
        DialogFragment dialogFragment = new ChooseSongDialogFragment();
        dialogFragment.setTargetFragment(this,0);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "inputSong");
    }

    /**
     * Checks if the tempPostObject is valid and returns it.
     * @return valid tempPostObject or null if invalid
     */
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

    /**
     * Fills the tempPostObject with the user input from the EditText.
     * @param input EditText containing the user's post content
     */
    private void fillPostObject(EditText input){
        if (input.getText() != null && !input.getText().toString().isEmpty()){
            String formattedDate = currentDate.format(formatter);
            tempPostObject.setPostContent(formattedDate+"\n\n" + input.getText().toString());

            SharedPreferencesHelper helper = new SharedPreferencesHelper(getContext());
            tempPostObject.setAuthor(helper.getName());
        }
        else {
            tempPostObject.setPostContent("");
        }
    }

    /**
     * Called when the submit button in the choose song dialog is pressed.
     * Validates and sets the song name in tempPostObject.
     * @param songName The entered song name
     */
    @Override
    public void onDialogSubmit(String songName) {
        if (!songName.isEmpty()){
            songName = songName.replace(" ", "");
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