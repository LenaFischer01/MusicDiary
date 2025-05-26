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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FeedFragment extends Fragment implements ChooseSongDialogFragment.ChooseSongDialogListener {

    FeedRecyclerViewAdapter recyclerViewAdapter;
    List<FriendPostObject> friendlist = new ArrayList<>();
    TextView noPostText;
    private Post tempPostObject;
    private TextView displaySong;
    private LocalDate currentDate;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy").withLocale(Locale.GERMANY);

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
        recyclerViewAdapter = new FeedRecyclerViewAdapter(friendlist);
        recyclerView.setAdapter(recyclerViewAdapter);

        noPostText = view.findViewById(R.id.noPostTextFeed);

        currentDate = LocalDate.now();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    // This will collect the list of friends from the Database and fetch the posts of the friends and add them to the friendlist
    private void refreshData() {
        friendlist.clear();

        DatabaseConnectorFirebase connectorFirebase = new DatabaseConnectorFirebase();
        List<FriendInfo> friendInfoSet = new ArrayList<>();

        String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        connectorFirebase.getFriendList(myUserID, new DatabaseConnectorFirebase.FriendListCallback() {
            @Override
            public void onCallback(Map<String, FriendInfo> friends) {

                friendInfoSet.addAll(friends.values());

                AtomicInteger counter = new AtomicInteger(0);
                int total = friendInfoSet.size() + 1; // friends + user

                // Get own post (with fresh username!)
                connectorFirebase.getUsernameByID(myUserID, (myUsername, uid) -> {
                    connectorFirebase.getPostForUser(myUserID, post -> {
                        if (post != null && post.getPostContent() != null) {
                            friendlist.add(new FriendPostObject(myUsername, post));
                        }
                        if (counter.incrementAndGet() == total) {
                            recyclerViewAdapter.notifyDataSetChanged();
                            noPostText.setVisibility(friendlist.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                    });
                });


                // Adds friend objects to the friendlist for all posts in DB
                for (FriendInfo friend : friendInfoSet) {
                    connectorFirebase.getPostForUser(friend.getUserID(), post -> {
                        if (post != null && post.getPostContent() != null) {
                            friendlist.add(new FriendPostObject(friend.getUsername(), post));
                        }
                        if (counter.incrementAndGet() == total) {
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

        Button uploadButton = view.findViewById(R.id.buttonUploadPost);
        Button chooseSong = view.findViewById(R.id.buttonChooseSong);
        Button scrollButton = view.findViewById(R.id.scrollButtonFeed);

        EditText input = view.findViewById(R.id.editTextPost);

        displaySong = view.findViewById(R.id.textView_song_feed);

        NestedScrollView scrollView = view.findViewById(R.id.scrollviewFeed);

        SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper(getContext());

        // Get todays date
        String formattedDate = currentDate.format(formatter);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the user hasn't set a name yet (Meaning also no UID) stop this
                String myUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Check if post exists, else make new
                if (tempPostObject == null) {
                    tempPostObject = new Post();
                }
                // Check if the song and post content are there
                if (tempPostObject.getSong() == null || tempPostObject.getSong().isEmpty()) {
                    Toast.makeText(getContext(), "Your song is missing", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if there has been a post today
                if (!formattedDate.equals(preferencesHelper.getUploadDAte())) {
                    fillPostObject(input, myUserID);

                    if (tempPostObject.getPostContent() == null || tempPostObject.getPostContent().isEmpty()) {
                        Toast.makeText(getContext(), "Your post is missing", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (getOwnPost() != null) {
                        preferencesHelper.saveUploadDate(formattedDate);
                        uploadPost(myUserID, getOwnPost());
                    }
                } else {
                    Toast.makeText(getContext(), "You already posted something today", Toast.LENGTH_SHORT).show();
                }
                clearInputStuff(displaySong, input);
            }
        });

        // Scrolls back to the top of the Fragment
        scrollButton.setOnClickListener(v -> scrollView.smoothScrollTo(0, 0));

        chooseSong.setOnClickListener(v -> showDialog());
    }

    private void clearInputStuff(TextView text, EditText eText) {
        text.setText("Your song");
        eText.setText("");
        tempPostObject = null;
    }

    private void uploadPost(String userID, Post post) {
        DatabaseConnectorFirebase databaseConnectorFirebase = new DatabaseConnectorFirebase();
        databaseConnectorFirebase.deleteAllPostsForUser(userID);
        databaseConnectorFirebase.addPost(userID, post);
        refreshData();
    }

    private void showDialog() {
        DialogFragment dialogFragment = new ChooseSongDialogFragment();
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "inputSong");
    }

    /** This method checks if the tempPostObject is null or empty and returns it. */
    private Post getOwnPost() {
        if (tempPostObject != null && !tempPostObject.getPostContent().isEmpty()) {
            if (tempPostObject.getSong() != null && !tempPostObject.getSong().isEmpty()) {
                return tempPostObject;
            } else {
                Toast.makeText(getContext(), "Your song is missing...", Toast.LENGTH_SHORT).show();
                return null;
            }
        } else {
            return null;
        }
    }

    /** This method fills the TempPostObject with the user input in the EditText,
     * and holt den aktuellen Nutzernamen aus der Datenbank (async gesetzt). */
    private void fillPostObject(EditText input, String myUserID) {
        if (input.getText() != null && !input.getText().toString().isEmpty()) {
            String formattedDate = currentDate.format(formatter);
            tempPostObject.setPostContent(formattedDate + "\n\n" + input.getText().toString());

            // Username asynchron aus DB holen:
            DatabaseConnectorFirebase db = new DatabaseConnectorFirebase();
            db.getUsernameByID(myUserID, (username, uid) -> {
                // Autor erst jetzt setzen
                tempPostObject.setAuthor(username);
            });
        } else {
            tempPostObject.setPostContent("");
        }
    }

    /** This method is triggered when the submit button in the dialog to choose the song is pressed. */
    @Override
    public void onDialogSubmit(String songName) {
        if (!songName.isEmpty()) {
            songName = songName.replace(" ", "");
            if (songName.matches("^\\s*.+\\s*-\\s*.+\\s*$")) {
                String[] song = songName.split("-");
                if (tempPostObject == null) {
                    tempPostObject = new Post();
                }
                tempPostObject.setSong(song[0] + " - " + song[1]);
                displaySong.setText(song[0] + " - " + song[1]);
            } else {
                Toast.makeText(getContext(), "Please use the correct syntax -> Song-Artist", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "You need to enter something...", Toast.LENGTH_SHORT).show();
        }
    }
}