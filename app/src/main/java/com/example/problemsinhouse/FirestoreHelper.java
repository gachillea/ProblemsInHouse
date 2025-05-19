package com.example.problemsinhouse;

import com.google.firebase.firestore.*;

import java.util.*;

public class FirestoreHelper {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface Callback<T> {
        void onResult(T result);
    }

    public interface PostsCallback {
        void onCallback(List<Post> posts);
    }


    // Δημιουργία χρήστη
    public static void insertUser(String username, String password, Callback<Boolean> callback) {
        FirestoreHelper.userExists(username, success->{
            if (success){
                return;
            }
            else
            { Map<String, Object> user = new HashMap<>();
                user.put("username", username);
                user.put("password", password);

                db.collection("users").document(username)
                        .set(user)
                        .addOnSuccessListener(unused -> callback.onResult(true))
                        .addOnFailureListener(e -> callback.onResult(false));

        }});
    }

    // Έλεγχος αν υπάρχει χρήστης με σωστό password
    public static void checkUser(String username, String password, Callback<User> callback) {
        db.collection("users").document(username).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String storedPassword = documentSnapshot.getString("password");
                        callback.onResult(password.equals(storedPassword));
                    } else {
                        callback.onResult(false);
                    }
                })
                .addOnFailureListener(e -> callback.onResult(false));
    }

    // Έλεγχος αν υπάρχει username
    public static void userExists(String username, Callback<Boolean> callback) {
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(querySnapshot -> callback.onResult(!querySnapshot.isEmpty()))
                .addOnFailureListener(e -> callback.onResult(false));
    }


    // Εισαγωγή post
    public static void insertPost(String username, String title, String content, String imagePath, Callback<Boolean> callback) {
        Map<String, Object> post = new HashMap<>();
        post.put("username", username);
        post.put("title", title);
        post.put("content", content);
        post.put("imagePath", imagePath);

        db.collection("posts")
                .add(post)
                .addOnSuccessListener(documentReference -> callback.onResult(true))
                .addOnFailureListener(e -> callback.onResult(false));
    }

    // Λήψη όλων των post
    public static void getAllPosts(final PostsCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts")
                .get()
                .addOnCompleteListener(task -> {
                    List<Post> postList = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String username = document.getString("username");
                            String title = document.getString("title");
                            String content = document.getString("content");
                            String imagePath = document.getString("imagePath");

                            Post post = new Post(username, title, content, imagePath);
                            postList.add(post);
                        }
                        callback.onCallback(postList); // Επιστρέφει τη λίστα όταν είναι έτοιμη
                    } else {
                        callback.onCallback(postList); // άδεια λίστα αν αποτύχει
                    }
                });
    }

}
