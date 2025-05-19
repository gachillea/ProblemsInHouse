package com.example.problemsinhouse;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {

    private EditText titleInput, contentInput;
    private Button submitPostButton;
    private ImageView postImage;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private String currentPhotoPath;
    private Uri imageUri;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        titleInput = findViewById(R.id.titleInput);
        contentInput = findViewById(R.id.contentInput);
        submitPostButton = findViewById(R.id.submitPostButton);
        postImage = findViewById(R.id.postImage);
        Button captureButton = findViewById(R.id.captureImageButton);

        // Ζήτα άδεια κάμερας αν δεν έχει δοθεί ήδη
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        captureButton.setOnClickListener(v -> {
            try {
                dispatchTakePictureIntent();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        submitPostButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String content = contentInput.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, getString(R.string.fillAllFields), Toast.LENGTH_SHORT).show();
                return;
            }

            User user = getIntent().getParcelableExtra("user");
            if (user == null) {
                Toast.makeText(this, "Πρέπει να κάνεις login πρώτα", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentPhotoPath == null) {
                Toast.makeText(this, "Πρέπει να τραβήξεις μια φωτογραφία", Toast.LENGTH_SHORT).show();
                return;
            }

            File photoFile = new File(currentPhotoPath);
            if (!photoFile.exists()) {
                Toast.makeText(this, "Το αρχείο εικόνας δεν βρέθηκε", Toast.LENGTH_SHORT).show();
                return;
            }
            submitPostButton.setEnabled(false);

            try {
                InputStream stream = new FileInputStream(photoFile);
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference imageRef = storageRef.child("images/" + photoFile.getName());

                imageRef.putStream(stream)
                        .addOnSuccessListener(taskSnapshot -> {
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                FirestoreHelper.insertPost(user.getUsername(), title, content, downloadUrl, success -> {
                                    submitPostButton.setEnabled(true);
                                    if (success) {
                                        Toast.makeText(this, getString(R.string.postUpload), Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(this, getString(R.string.postFail), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }).addOnFailureListener(e -> {
                                submitPostButton.setEnabled(true);
                                Toast.makeText(this, "Αποτυχία λήψης URL εικόνας", Toast.LENGTH_SHORT).show();
                                Log.e("UPLOAD", "GetDownloadUrl failed", e);
                            });
                        })
                        .addOnFailureListener(e -> {
                            submitPostButton.setEnabled(true);
                            Toast.makeText(this, "Αποτυχία ανεβάσματος εικόνας", Toast.LENGTH_SHORT).show();
                            Log.e("UPLOAD", "Upload failed", e);
                        });

            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Το αρχείο εικόνας δεν βρέθηκε", Toast.LENGTH_SHORT).show();
                Log.e("UPLOAD", "File not found", e);
            }
        });
    }

    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(
                        this,
                        "com.example.problemsinhouse.fileprovider",
                        photoFile);
            }
            //photoFile = new File(currentPhotoPath);

            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "com.example.problemsinhouse.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, "Το αρχείο εικόνας είναι null", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Καμία κάμερα διαθέσιμη", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            file = new File(currentPhotoPath);
            if (file.exists() && file.length() > 0) {
                imageUri = FileProvider.getUriForFile(this, "com.example.problemsinhouse.fileprovider", file);
                postImage.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath));
            } else {
                Toast.makeText(this, "Η εικόνα δεν αποθηκεύτηκε σωστά", Toast.LENGTH_SHORT).show();
                Log.e("UPLOAD", "File length = 0 or file missing");
            }
        }

    }
}
