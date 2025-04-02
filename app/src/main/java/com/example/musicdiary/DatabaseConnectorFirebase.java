package com.example.musicdiary;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DatabaseConnectorFirebase {

    private DatabaseReference databaseReference;

    /**
     * Constructor that initializes the connection to the Firebase Realtime Database
     */
    public DatabaseConnectorFirebase(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Adds a user to the database.
     *
     * Usage:
     * DatabaseConnector dbConnector = new DatabaseConnector();
     * dbConnector.addUser("username1");
     *
     * @param username The username to be added to the database.
     */
    public void addUser(String username){
        databaseReference.child("Users").child(username).setValue(username);
    }

    /**
     * Adds a post for a specific user to the database.
     *
     * Usage:
     * DatabaseConnector dbConnector = new DatabaseConnector();
     * dbConnector.addPost("username1", "This is a post");
     *
     * @param username The username associated with the post.
     * @param postContent The content of the post to be added.
     */
    public void addPost(String username, String postContent) {
        databaseReference.child("Posts").child(username).setValue(postContent);
    }

    /**
     * Deletes a specified user from the database.
     *
     * Usage:
     * DatabaseConnector dbConnector = new DatabaseConnector();
     * dbConnector.deleteUser("usernameToDelete");
     *
     * @param username The username of the user to be deleted.
     */
    public void deleteUser(String username) {
        DatabaseReference userRef = databaseReference.child("Users").child(username);
        userRef.removeValue();
    }


    /**
     * Callback interface for user existence checking.
     * Implement this interface to define what occurs after the user check completes.
     */
    public interface UserExistsCallback {
        void onCallback(boolean exists);
    }

    /**
     * Checks if a user exists in the database.
     * Since database operations are asynchronous, the result is returned via a callback.
     *
     * Usage:
     * DatabaseConnector dbConnector = new DatabaseConnector();
     * dbConnector.userExists("username1", new DatabaseConnector.UserExistsCallback() {
     *     @Override
     *     public void onCallback(boolean exists) {
     *         if (exists) {
     *             System.out.println("The username already exists.");
     *         } else {
     *             System.out.println("The username is available.");
     *         }
     *     }
     * });
     *
     * @param username The username to check existence for.
     * @param callback The callback that receives the result of the check.
     */
    public void userExists(final String username, final UserExistsCallback callback) {
        DatabaseReference usersRef = databaseReference.child("Users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exists = false;

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String existingUser = userSnapshot.getValue(String.class);
                    if (existingUser != null && existingUser.equals(username)) {
                        exists = true;
                        break;
                    }
                }

                callback.onCallback(exists); // Return the result through the callback
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCallback(false); // Error occurred
            }
        });
    }

    /**
     * Retrieves a post for a specified user from the database.
     * Since database operations are asynchronous, the result is returned via a callback.
     *
     * Usage:
     * DatabaseConnector dbConnector = new DatabaseConnector();
     * dbConnector.getPostForUser("username1", new DatabaseConnector.PostCallback() {
     *     @Override
     *     public void onCallback(String postContent) {
     *         if (postContent != null) {
     *             System.out.println("Post content: " + postContent);
     *         } else {
     *             System.out.println("No post found for the user.");
     *         }
     *     }
     * });
     *
     * @param username The username for which to retrieve the post.
     * @param callback The callback that receives the post content.
     */
    public void getPostForUser(String username, final PostCallback callback) {
        DatabaseReference postRef = databaseReference.child("Posts").child(username);

        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String postContent = dataSnapshot.getValue(String.class);
                callback.onCallback(postContent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCallback(null); // Return null if error occurs
            }
        });
    }

    /**
     * Callback interface for retrieving post content.
     * Implement this interface to define what occurs after retrieving post data.
     */
    public interface PostCallback {
        void onCallback(String postContent);
    }

}