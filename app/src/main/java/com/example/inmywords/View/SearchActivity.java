package com.example.inmywords.View;

import android.Manifest;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.inmywords.Model_Controller.AudioFileUtil;
import com.example.inmywords.Model_Controller.WavRecorder;
import com.example.inmywords.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.wave.Wave;

import java.io.File;
import java.io.IOException;

//todo - fix not finding words if they contain different capitalisation
public class SearchActivity extends AppCompatActivity implements
        View.OnClickListener{

    private EditText searchWord;

    private FirebaseStorage storage;
    private Button btnSearch;
    private FirebaseFirestore db;
    private Button btnSearchRecord;
    private String filePath;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private StorageReference storageRef;
    private int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 0;
    private WavRecorder wavRecorder;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Search");

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        //set on click listener for the search button
        btnSearch= findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        //create on click listener for the record button
        btnSearchRecord= findViewById(R.id.btnSearchRecord);
        btnSearchRecord.setOnClickListener(this);

        AudioFileUtil fileUtil = new AudioFileUtil();
        filePath = (fileUtil.getRouteStorageDir("recWords")).getAbsolutePath();
        wavRecorder = new WavRecorder(filePath);

        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        //get instance of db
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        searchWord = findViewById(R.id.searchWord);

        requestStoragePermissions();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearchRecord:
                if(btnSearchRecord.getText() == getResources().getText(R.string.stop_recording)) {
                    stopRecording();
                }else if (btnSearchRecord.getText() == getResources().getText(R.string.record)){
                    startRecording();
                }
                break;
            // ...
            case R.id.btnSearch:
                try {
                    search();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void startRecording() {
        //check that the app is not recording already
        if(btnSearchRecord.getText().toString() == getResources().getText(R.string.record)){
            wavRecorder.startRecording();
            btnSearchRecord.setBackgroundColor(getResources().getColor(R.color.colourHighlight));
            btnSearchRecord.setText(R.string.stop_recording);
        }
    }

    private void stopRecording() {
        //check that the app is recording
        if(btnSearchRecord.getText().toString() == getResources().getText(R.string.stop_recording)){
            wavRecorder.stopRecording();
            btnSearchRecord.setText(R.string.record);
            btnSearchRecord.setBackgroundColor(getResources().getColor(R.color.colourPrimary));
        }
    }

    private void search() throws IOException {

        test();

//        StorageReference wordsRef = storageRef.child(email + "/words/Banana");
//        AudioFileUtil fileUtil = new AudioFileUtil();
//        String filePath2 = (fileUtil.getRouteStorageDir("recWords2")).getAbsolutePath();
//        File localFile =  new File(filePath2);
//
//        wordsRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                // Local temp file has been created
//                test(localFile.getAbsolutePath());
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        });
///////////////////////////////
        String searchterm = searchWord.getText().toString();





//        Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);
//        intent.putExtra("searchTerm", searchterm);
//        intent.putExtra("filePath", filePath);
//        startActivity(intent);


    }
//String path
    public void test(){
        StorageReference wordsRef = storageRef.child(email + "/words/apple");
        Uri file = Uri.fromFile(new File(filePath));

        Wave wave1 = new Wave("/sdcard/InMyWords/recWords");
        Wave wave2 = new Wave("/sdcard/InMyWords/recWords1");
//wordsRef.getStream().toString()
        FingerprintSimilarity similarity;

        similarity = wave1.getFingerprintSimilarity(wave2);
        System.out.println("clip is found at "

                + similarity.getsetMostSimilarTimePosition() + "s in "

                + wordsRef.getPath() +" with similarity " + similarity.getSimilarity());
    }
    private void requestStoragePermissions() {
//todo check this
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
