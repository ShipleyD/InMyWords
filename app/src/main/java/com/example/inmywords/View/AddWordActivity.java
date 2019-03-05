package com.example.inmywords.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.inmywords.Model_Controller.AudioFileUtil;
import com.example.inmywords.R;
import com.example.inmywords.Model_Controller.WavRecorder;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;

public class AddWordActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private Button btnNext;
    private Button btnRecord;
    private Button btnPlayBack;
    private FirebaseFirestore db;
    private String filePath;
    int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 0;
    WavRecorder wavRecorder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        //set up app bar for this page
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Add Your Word");
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        //create on click listener for the next button
        btnNext= findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);
        //create on click listener for the record button
        btnRecord= findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(this);
        //create on click listener for the Play back button
        btnPlayBack= findViewById(R.id.btnPlayBack);
        btnPlayBack.setOnClickListener(this);

        AudioFileUtil fileUtil = new AudioFileUtil();
        filePath = (fileUtil.getRouteStorageDir("recWords")).getAbsolutePath();
        wavRecorder = new WavRecorder(filePath);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        requestStoragePermissions();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // ...
            case R.id.btnNext:
                next();
                break;
            // ...
            case R.id.btnPlayBack:
                if(btnPlayBack.getText() == getResources().getText(R.string.stop_recording)) {
                    stopRecording();
                }else if (btnPlayBack.getText() == getResources().getText(R.string.playBack)){
                    playRecording();
                }
                break;
            case R.id.btnRecord:
                startRecording();
                break;
            // ...
        }
    }

    private void playRecording() {
        //todo - remove audio if moved away from the page

        Uri myUri = Uri.fromFile(new File(filePath)); // initialize Uri here
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), myUri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    private void next() {
        startActivity(new Intent(AddWordActivity.this, TypedWordActivity.class));
    }

    private void stopRecording() {

        //check that the app is recording
        if(btnRecord.getText().toString() == getResources().getText(R.string.recording)){
            wavRecorder.stopRecording();
            btnRecord.setText(R.string.record);
            btnRecord.setBackgroundColor(getResources().getColor(R.color.colourPrimary));
            btnPlayBack.setText(R.string.playBack);
        }

    }

    private void startRecording() {

        //check that the app is not recording already
        if(btnRecord.getText().toString() == getResources().getText(R.string.record)){
            wavRecorder.startRecording();
            btnRecord.setText(R.string.recording);
            btnRecord.setBackgroundColor(getResources().getColor(R.color.colourHighlight));
            btnPlayBack.setText(R.string.stop_recording);
        }
    }

    private void requestStoragePermissions() {

// Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

}
