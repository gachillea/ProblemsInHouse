package com.example.problemsinhouse;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        checkAndRequestPermissions();

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

            user = getIntent().getParcelableExtra("user");
            if (user == null) {
                Toast.makeText(this, getString(R.string.noLogin), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (currentPhotoPath == null) {
                Toast.makeText(this, getString(R.string.noPicture), Toast.LENGTH_SHORT).show();
                return;
            }

            File photoFile = new File(currentPhotoPath);
            if (!photoFile.exists()) {
                return;
            }
            submitPostButton.setEnabled(false);

            if (user.getLives()==0)
            {
                Log.d("lives", "lives");
                Toast.makeText(this, getString(R.string.zeroLives), Toast.LENGTH_LONG).show();
                finish();
                return;
            }

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

                                        FirestoreHelper.updateLives(user.getUsername(), -1, successful -> {
                                            if (successful) {
                                                finish();
                                            }
                                        });

                                    } else {
                                        Toast.makeText(this, getString(R.string.postFail), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }).addOnFailureListener(e -> {
                                submitPostButton.setEnabled(true);
                                Log.e("UPLOAD", "GetDownloadUrl failed", e);
                            });
                        })
                        .addOnFailureListener(e -> {
                            submitPostButton.setEnabled(true);
                            Log.e("UPLOAD", "Upload failed", e);
                        });

            } catch (FileNotFoundException e) {
                Log.e("UPLOAD", "File not found", e);
            }
        });
    }

    private void checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        boolean permissionsNeeded = false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded = true;
                break;
            }
        }

        if (permissionsNeeded) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CAMERA_PERMISSION);
        }
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
            }
        } else {
            Toast.makeText(this, getString(R.string.noCamera), Toast.LENGTH_SHORT).show();
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
                Log.e("UPLOAD", "File length = 0 or file missing");
            }
        }

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof TextView) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
