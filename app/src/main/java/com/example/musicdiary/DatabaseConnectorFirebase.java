package com.example.musicdiary;
import com.example.musicdiary.Container.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DatabaseConnectorFirebase {

    private DatabaseReference databaseReference;

    public DatabaseConnectorFirebase(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void addUser(String username){
        databaseReference.child("Users").child(username).setValue(username);
    }

    /**
     * Adds a post for a specific user to the database, consisting of both a post content and a song.
     *
     * Usage:
     * DatabaseConnector dbConnector = new DatabaseConnector();
     * dbConnector.addPost("username1", new Post("This is a post", "Imagine - John Lennon"));
     *
     * @param username The username associated with the post.
     * @param post The post object containing content and song.
     */
    public void addPost(String username, Post post) {
        databaseReference.child("Posts").child(username).setValue(post);
    }

    public void deleteUser(String username) {
        DatabaseReference userRef = databaseReference.child("Users").child(username);
        userRef.removeValue();
    }

    public interface UserExistsCallback {
        void onCallback(boolean exists);
    }

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
                callback.onCallback(false);
            }
        });
    }

    public interface PostCallback {
        void onCallback(Post post);
    }

    /**
     * Retrieves a post for a specified user from the database, including both content and song.
     * Since database operations are asynchronous, the result is returned via a callback.
     *
     * Usage:
     * DatabaseConnector dbConnector = new DatabaseConnector();
     * dbConnector.getPostForUser("username1", new DatabaseConnector.PostCallback() {
     *     @Override
     *     public void onCallback(Post post) {
     *         if (post != null) {
     *             System.out.println("Post content: " + post.postContent + ", Song: " + post.song);
     *         } else {
     *             System.out.println("No post found for the user.");
     *         }
     *     }
     * });
     *
     * @param username The username for which to retrieve the post.
     * @param callback The callback that receives the post content and song.
     */
    public void getPostForUser(String username, final PostCallback callback) {
        DatabaseReference postRef = databaseReference.child("Posts").child(username);

        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                callback.onCallback(post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCallback(null); // Return null if error occurs
            }
        });
    }

}