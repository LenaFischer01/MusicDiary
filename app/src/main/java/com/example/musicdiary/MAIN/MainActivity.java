package com.example.musicdiary.MAIN;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.musicdiary.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Main activity hosting the primary application UI with tabs for Feed, Friends, and Settings.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Retrieve theme from shared preferences, set default if none found
        SharedPreferencesHelper helper = new SharedPreferencesHelper(getApplicationContext());

        String themeName = helper.getTheme();
        if (themeName == null || themeName.isEmpty()){
            themeName = "AppTheme.Blue";
        }

        setTheme(getResources().getIdentifier(themeName, "style", getPackageName()));

        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Adjust padding for system bars (status/navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ViewPager2 viewPager2 = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        // Setup adapter for the ViewPager2 managing the app sections
        ViewPageAdapter adapter = new ViewPageAdapter(this);
        viewPager2.setAdapter(adapter);
        viewPager2.setCurrentItem(0,true);

        // Attach TabLayout with ViewPager2 for tab navigation
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Feed");
                            break;
                        case 1:
                            tab.setText("Follower");
                            break;
                        case 2:
                            tab.setText("Settings");
                    }
                }).attach();

        // Initialize user entry in database if logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseConnectorFirebase db = new DatabaseConnectorFirebase();
        if (user != null) {
            String uid = user.getUid();
            db.getUsernameByID(uid, (userString, userId) -> {
                initialiseUserInDB(uid, userString);
                helper.setName(userString);

            });
        }
    }

    /**
     * Checks if the user exists in the database, and adds them if not.
     * @param uid The user ID.
     * @param username The username.
     */
    private void initialiseUserInDB(String uid, String username) {
        DatabaseConnectorFirebase db = new DatabaseConnectorFirebase();
        db.userExists(uid, exists -> {
            if (!exists) db.addOrUpdateUser(uid, username);
        });
    }

}