package com.example.musicdiary.MAIN;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicdiary.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;

import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

/**
 * Activity that handles user login with Firebase Authentication UI.
 */
public class LoginActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register launcher for the authentication result
        signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                this::onSignInResult
        );

        // Check if user is already logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is logged in, start main activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // No user logged in, start sign-in flow
            startSignInFlow();
        }
    }

    /**
     * Starts the Firebase UI sign-in flow with email provider.
     */
    private void startSignInFlow() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.AppTheme_Blue)
                .build();
        signInLauncher.launch(signInIntent);
    }

    /**
     * Handles the result from the sign-in flow.
     * @param result The FirebaseAuthUIAuthenticationResult object containing the result.
     */
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in, start main activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // Sign in failed or cancelled, close this activity
            finish();
        }
    }
}