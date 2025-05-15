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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {

    private EditText titleInput, contentInput;
    private Button submitPostButton;
    private PostDatabaseHelper db;
    private String username;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;
    private ImageView postImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        titleInput = findViewById(R.id.titleInput);
        contentInput = findViewById(R.id.contentInput);
        submitPostButton = findViewById(R.id.submitPostButton);
        db = new PostDatabaseHelper(this);



        username = getIntent().getStringExtra("username");

        postImage = findViewById(R.id.postImage);
        Button captureButton = findViewById(R.id.captureImageButton);

        captureButton.setOnClickListener(v -> {
            dispatchTakePictureIntent();
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }



        submitPostButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String content = contentInput.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, getString(R.string.fillAllFields), Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("DEBUG", "username: " + username);
            Log.d("DEBUG", "title: " + title);
            Log.d("DEBUG", "content: " + content);
            Log.d("DEBUG", "imagePath: " + currentPhotoPath);


            boolean success = db.insertPost(username, title, content, currentPhotoPath);
            if (success) {
                Toast.makeText(this, getString(R.string.postUpload), Toast.LENGTH_SHORT).show();
                finish();  // Επιστρέφει στην MainActivity
            } else {
                Toast.makeText(this, getString(R.string.postFail), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Log.d("DEBUG", "1");
            File photoFile = null;
            try {
                Log.d("DEBUG", "2");
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Log.d("DEBUG", "3");
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.problemsinhouse.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
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
            postImage.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath));
        }
    }


}
