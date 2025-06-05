package com.example.problemsinhouse;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.*;

import java.util.*;

public class FirestoreHelper {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface Callback<T> {
        void onResult(T result);

    }

    public interface CommentCallback {
        void onSuccess(List<Comment> comments);

        void onResult(boolean b);
    }


    public interface PostsCallback {
        void onResult(List<Post> posts);
    }


    // Δημιουργία χρήστη
    public static void insertUser(String username, String password, Callback<Boolean> callback) {
        FirestoreHelper.userExists(username, success->{
            if (success){
                return;
            }
            else {
                Map<String, Object> user = new HashMap<>();
                user.put("username", username);
                user.put("password", password);
                user.put("lives",3);

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
                        if(password.equals(storedPassword)) {
                            Long lives = (Long) documentSnapshot.get("lives");
                            callback.onResult(new User(username, password, lives));
                        }
                    } else {
                        callback.onResult(null);
                    }
                })
                .addOnFailureListener(e -> callback.onResult(null));
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
                .addOnSuccessListener(documentReference -> {
                    String id = documentReference.getId();
                    documentReference.update("id", id)
                            .addOnSuccessListener(aVoid -> callback.onResult(true))
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                callback.onResult(false);
                            });
                })
                .addOnFailureListener(e -> callback.onResult(false));
    }

    public static void getUserPosts(String username, PostsCallback callback) {
        Log.d("FirestoreHelper", "Querying posts for username: " + username);
        db.collection("posts")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Post> posts = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Post post = new Post(
                                doc.getString("id"),
                                doc.getString("username"),
                                doc.getString("title"),
                                doc.getString("content"),
                                doc.getString("imagePath")
                        );
                        posts.add(post);
                    }
                    callback.onResult(posts);
                });
    }

    // Λήψη όλων των post εκτός από του ίδιου user
    public static void getAllPosts(String notusername, final PostsCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts")
                .whereNotEqualTo("username", notusername)
                .get()
                .addOnCompleteListener(task -> {
                    List<Post> postList = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Post post = new Post(
                                    document.getString("id"),
                                    document.getString("username"),
                                    document.getString("title"),
                                    document.getString("content"),
                                    document.getString("imagePath")
                            );

                            postList.add(post);
                        }
                        callback.onResult(postList); // Επιστρέφει τη λίστα όταν είναι έτοιμη
                    } else {
                        callback.onResult(postList); // άδεια λίστα αν αποτύχει
                    }
                });
    }


    public static void addCommentToPost(String postId, String username, String commentText, Callback<Boolean> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").document(postId).collection("comments")
                .add(new Comment(username, commentText))
                .addOnSuccessListener(documentReference -> {
                    callback.onResult(true);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreHelper", "Failed to add comment", e);
                    callback.onResult(false);
                });
    }


    public static void getCommentsForPost(String postId, CommentCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("posts")
                .document(postId)
                .collection("comments")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Comment> comments = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Comment comment = doc.toObject(Comment.class);
                        if (comment != null) {
                            comment.setId(doc.getId());
                            comments.add(comment);
                        }
                    }
                    callback.onSuccess(comments);
                })
                .addOnFailureListener(e -> callback.onSuccess(null));
        }

    public static void deletePostAndComments(String postId, Callback<Boolean> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("DELETE", "Deleting post: " + postId);
        // Διαγραφή των σχολίων πρώτα
        db.collection("posts").document(postId).collection("comments")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    WriteBatch batch = db.batch();

                    for (DocumentSnapshot doc : querySnapshot) {
                        batch.delete(doc.getReference());
                    }

                    batch.commit()
                            .addOnSuccessListener(aVoid -> {
                                // Διαγραφή του ίδιου του post
                                db.collection("posts").document(postId)
                                        .delete()
                                        .addOnSuccessListener(aVoid2 -> callback.onResult(true))
                                        .addOnFailureListener(e -> callback.onResult(false));
                            })
                            .addOnFailureListener(e -> callback.onResult(false));
                })
                .addOnFailureListener(e -> callback.onResult(false));
    }

    public static void getUserNotifications(String username, Callback<List<Notification>> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(username)
                .collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Notification> notifications = new ArrayList<>();
                    for (var doc : snapshot.getDocuments()) {
                        Notification item = doc.toObject(Notification.class);
                        if (item != null) {
                            notifications.add(item);
                        }
                    }
                    callback.onResult(notifications);
                })
                .addOnFailureListener(e -> {
                    callback.onResult(null);
                });
    }





}


