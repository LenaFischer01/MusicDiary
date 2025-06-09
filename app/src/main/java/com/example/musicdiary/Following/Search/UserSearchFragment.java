package com.example.musicdiary.Following.Search;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicdiary.Container.FollowingInfo;
import com.example.musicdiary.MAIN.DatabaseConnectorFirebase;
import com.example.musicdiary.R;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import android.view.inputmethod.EditorInfo;
import android.widget.Toast;
import android.util.Log;

/**
 * Fragment to search for friends by username substring.
 */
public class UserSearchFragment extends Fragment {

    UserSearchRecyclerViewAdapter adapter;
    List<FollowingInfo> results = new ArrayList<>();
    TextView noResultText;
    EditText searchBar;
    private LocalDate currentDate;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public UserSearchFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        results.clear();
        noResultText.setVisibility(View.VISIBLE);
        refreshData();
    }

    /**
     * Notify adapter to refresh the displayed search results.
     */
    private void refreshData() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.searchusers, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewSearchResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new UserSearchRecyclerViewAdapter(results);
        recyclerView.setAdapter(adapter);

        searchBar = view.findViewById(R.id.editTextSearchFriends);
        currentDate = LocalDate.now();

        noResultText = view.findViewById(R.id.textViewNoSearchResult);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseConnectorFirebase databaseConnectorFirebase = new DatabaseConnectorFirebase();

        // Listener for search action in the input field
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_SEND ||
                    actionId == EditorInfo.IME_ACTION_GO ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                            && event.getAction() == KeyEvent.ACTION_DOWN)) {

                String usernameInput = searchBar.getText().toString().trim();
                if (usernameInput.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter something....", Toast.LENGTH_SHORT).show();
                    return true;
                }

                // Perform search with the entered username substring
                performSearch(usernameInput, databaseConnectorFirebase);

                return true;
            }
            return false;
        });
    }

    /**
     * Searches users whose usernames contain the given substring, excluding the current user.
     * @param substring The substring to search for in usernames
     * @param databaseConnectorFirebase Instance to access Firebase database
     */
    private void performSearch(String substring, DatabaseConnectorFirebase databaseConnectorFirebase) {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        results.clear();
        refreshData();

        databaseConnectorFirebase.getUsernamesContaining(substring, true, usernames -> {

            if (usernames.isEmpty()) {
                noResultText.setVisibility(View.VISIBLE);
                results.clear();
                refreshData();
                return;
            } else {
                noResultText.setVisibility(View.GONE);
            }

            List<FollowingInfo> tempResults = new ArrayList<>();
            final int expected = usernames.size();
            final int[] done = {0};

            for (String matchedUsername : usernames) {
                databaseConnectorFirebase.getUserDataByName(matchedUsername, (username, uid) -> {
                    // Do not show the user's own account
                    if (!uid.equals(myUid) && !uid.isEmpty() && !username.isEmpty()) {
                        tempResults.add(new FollowingInfo(uid, currentDate.format(formatter)));
                    }
                    done[0]++;
                    if (done[0] == expected) {
                        results.clear();
                        results.addAll(tempResults);
                        searchBar.setText("");
                        refreshData();
                    }
                });
            }
        });
    }
}