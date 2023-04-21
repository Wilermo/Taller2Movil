package com.example.taller2compumovil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.taller2compumovil.Utils.AlertUtils;

import androidx.appcompat.app.AppCompatActivity;
import com.example.taller2compumovil.databinding.CamaraActivityBinding;
public class CamaraActivity extends AppCompatActivity{
    private static String TAG = CamaraActivity.class.getName();
    private CamaraActivityBinding binding;

    private final int CAMERA_PERMISSION_ID = 101;
    private final int GALLERY_PERMISSION_ID = 102;

    private final int CAMERA_VIDEO_PERMISSION_ID = 103;

    private final int GALLERY_VIDEO_PERMISSION_ID = 104;
    String camaraPerm = android.Manifest.permission.CAMERA;
    String storagePerm  = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

    String currentPhotoPath;
    String currentVideoPath;

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CamaraActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.image.setVisibility(View.VISIBLE);
        binding.video.setVisibility(View.INVISIBLE);
        binding.buttonTake.setOnClickListener(view -> {
            if(requestPermission(CamaraActivity.this, new String[]{camaraPerm, storagePerm}, CAMERA_PERMISSION_ID)){
                startCamera(binding.getRoot());
            }
        });
        binding.buttonGallery.setOnClickListener(view -> startGallery(binding.getRoot()));

        binding.videoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.image.setVisibility(View.INVISIBLE);
                    binding.video.setVisibility(View.VISIBLE);
                    binding.buttonTake.setOnClickListener(view -> {
                        if(requestPermission(CamaraActivity.this, new String[]{camaraPerm, storagePerm}, CAMERA_PERMISSION_ID)){
                            startCameraVideo(binding.getRoot());
                        }
                    });
                    binding.buttonGallery.setOnClickListener(view -> startGalleryVideo(binding.getRoot()));
                }else{
                    binding.image.setVisibility(View.VISIBLE);
                    binding.video.setVisibility(View.INVISIBLE);
                    binding.buttonTake.setOnClickListener(view -> {
                        if(requestPermission(CamaraActivity.this, new String[]{camaraPerm, storagePerm}, CAMERA_PERMISSION_ID)){
                            startCamera(binding.getRoot());
                        }
                    });
                    binding.buttonGallery.setOnClickListener(view -> startGallery(binding.getRoot()));
                }
            }
        });

    }

    private boolean requestPermission(Activity context, String[] permission, int id) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    permission,
                    id);
            Log.w(TAG, "requestPermission: HOLA");
            return false;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    permission,
                    id);
            Log.w(TAG, "requestPermission: HOLA");
            return false;
        }
        return true;
    }

    private void initView(){
        if (ContextCompat.checkSelfPermission(this, camaraPerm)
                != PackageManager.PERMISSION_GRANTED){
            Log.e(TAG, "initView: no pude obtener el permiso :(");
            AlertUtils.indefiniteSnackbar(binding.getRoot(), getString(R.string.permission_denied_label));
        }else {
            Log.i(TAG, "initView: si pude obtener el permiso :)");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_ID){
            startCamera(binding.getRoot());
        }
    }

    public void startCamera(View view){
        if (ContextCompat.checkSelfPermission(this, camaraPerm)
                == PackageManager.PERMISSION_GRANTED){
            openCamera();
        }else {
            AlertUtils.indefiniteSnackbar(binding.getRoot(), getString(R.string.permission_denied_label));
        }
    }
    public void startCameraVideo(View view){
        if (ContextCompat.checkSelfPermission(this, camaraPerm)
                == PackageManager.PERMISSION_GRANTED){

            openCameraVideo();


        }else {
            AlertUtils.indefiniteSnackbar(binding.getRoot(), getString(R.string.permission_denied_label));
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_PERMISSION_ID);
            }
        }
    }

    private void openCameraVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File videoFile = null;
            try {
                videoFile = createVideoFile();
            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (videoFile != null) {
                Uri videoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", videoFile);
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                startActivityForResult(takeVideoIntent, CAMERA_PERMISSION_ID);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "CAMARA_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.i(TAG, String.format("Ruta: %s", currentPhotoPath));
        return image;
    }
    private File createVideoFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String videoFileName = "VID_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File videoFile = File.createTempFile(
                videoFileName,  /* prefix */
                ".mp4",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentVideoPath = videoFile.getAbsolutePath();
        Log.i(TAG, String.format("Ruta: %s", currentVideoPath));
        return videoFile;
    }

    public void startGallery(View view){
        Intent pickGalleryImage = new Intent(Intent.ACTION_PICK);
        pickGalleryImage.setType("image/*");
        startActivityForResult(pickGalleryImage, GALLERY_PERMISSION_ID);
    }
    public void startGalleryVideo(View view){
        Intent pickGalleryVideo = new Intent(Intent.ACTION_PICK,MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickGalleryVideo, GALLERY_VIDEO_PERMISSION_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_PERMISSION_ID:
                    binding.image.setImageURI(Uri.parse(currentPhotoPath));
                    Log.i(TAG, "onActivityResult: imagen capturada correctamente");
                    break;
                case GALLERY_PERMISSION_ID:
                    Uri imageUri = data.getData();
                    binding.image.setImageURI(imageUri);
                    Log.i(TAG, "onActivityResult: imagen cargada correctamente.");
                    break;
                case CAMERA_VIDEO_PERMISSION_ID:
                    binding.video.setVideoURI(Uri.parse(currentVideoPath));
                    binding.video.start();
                    Log.i(TAG, "onActivityResult: video capturado correctamente");
                case GALLERY_VIDEO_PERMISSION_ID:
                    Uri videoUri = data.getData();
                    binding.video.setVideoURI(videoUri);
                    binding.video.start();
                    Log.i(TAG, "onActivityResult: video cargado correctamente.");
                    break;
            }
        }
    }

}