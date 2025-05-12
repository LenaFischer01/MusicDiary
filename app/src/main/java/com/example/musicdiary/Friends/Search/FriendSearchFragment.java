package com.example.musicdiary.Friends.Search;

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

import com.example.musicdiary.Container.FriendInfo;
import com.example.musicdiary.MAIN.DatabaseConnectorFirebase;
import com.example.musicdiary.R;
import com.example.musicdiary.MAIN.SharedPreferencesHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

public class FriendSearchFragment extends Fragment {

    FriendSearchRecyclerViewAdapter adapter;
    List<FriendInfo> results = new ArrayList<>();
    TextView noResultText;
    EditText searchBar;
    private LocalDate currentDate;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public FriendSearchFragment() {
        //
    }

    @Override
    public void onResume() {
        super.onResume();
        results.clear();
        noResultText.setVisibility(View.VISIBLE);
        refreshData();
    }

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
        View view = inflater.inflate(R.layout.searchfriends, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewSearchResults);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FriendSearchRecyclerViewAdapter(results) {
        };
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
        SharedPreferencesHelper helper = new SharedPreferencesHelper(getContext());

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_SEND ||
                        actionId == EditorInfo.IME_ACTION_GO) {

                    // Get input as text
                    String username = searchBar.getText().toString().replace(" ", "");
                    if (username.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter something....", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    getUserCard(username, databaseConnectorFirebase, searchBar);

                    return true;
                }

                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN) {

                    String username = searchBar.getText().toString().replace(" ", "");
                    if (username.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter something....", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    getUserCard(username, databaseConnectorFirebase, searchBar);

                    return true;
                }

                return false;
            }
        });
    }

    private void getUserCard(String substring, DatabaseConnectorFirebase databaseConnectorFirebase, EditText searchBar) {
        SharedPreferencesHelper helper = new SharedPreferencesHelper(getContext());

        databaseConnectorFirebase.getUsernamesContaining(substring, true, new DatabaseConnectorFirebase.UsernameListCallback() {
            @Override
            public void onCallback(List<String> usernames) {

                if (usernames.isEmpty()) {
                    noResultText.setVisibility(View.VISIBLE);
                    results.clear();
                    refreshData();
                    return;
                } else {
                    noResultText.setVisibility(View.GONE);
                }

                List<FriendInfo> tempResults = new ArrayList<>();
                AtomicInteger responses = new AtomicInteger(0);

                for (String username : usernames) {
                    databaseConnectorFirebase.getUserDataByName(username, userString -> {
                        if (!userString.equals(helper.getUserID())) { // don't add self
                            FriendInfo friendInfo = new FriendInfo(userString, username, currentDate.format(formatter));
                            tempResults.add(friendInfo);
                        }
                        // Check if this is the last response
                        if (responses.incrementAndGet() == usernames.size()) {
                            // All async responses finished, update results and UI
                            results.clear();
                            results.addAll(tempResults);
                            searchBar.setText("");
                            refreshData();
                        }
                    });
                }
            }
        });
    }
}



