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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferencesHelper helper = new SharedPreferencesHelper(getApplicationContext());

        String themeName = helper.getTheme();
        if (themeName == null || themeName.isEmpty()){
            themeName = "AppTheme.Blue";
        }

        setTheme(getResources().getIdentifier(themeName, "style", getPackageName()));

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ViewPager2 viewPager2;
        TabLayout tabLayout;

        viewPager2 = (ViewPager2) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        ViewPageAdapter adapter = new ViewPageAdapter(this);
        viewPager2.setAdapter(adapter);
        viewPager2.setCurrentItem(0,true);

        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Feed");
                            break;
                        case 1:
                            tab.setText("Friends");
                            break;
                        case 2:
                            tab.setText("Settings");
                    }
                }).attach();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            String username = user.getDisplayName();
            initialiseUserInDB(uid, username);
            helper.setName(username);
        }

    }

        private void initialiseUserInDB(String uid, String username) {
            DatabaseConnectorFirebase db = new DatabaseConnectorFirebase();
            db.userExists(uid, exists -> {
                if (!exists) db.addOrUpdateUser(uid, username);
            });
        }

}