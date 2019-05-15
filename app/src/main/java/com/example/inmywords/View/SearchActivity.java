package com.example.inmywords.View;

import android.Manifest;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.inmywords.Model_Controller.AudioFileUtil;
import com.example.inmywords.Model_Controller.WavRecorder;
import com.example.inmywords.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.musicg.fingerprint.FingerprintManager;
import com.musicg.fingerprint.FingerprintSimilarityComputer;
import com.musicg.wave.Wave;

import java.io.IOException;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

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
    private static final String TAG = "SearchActivity";
    private HashMap<String, Float> rankedWords = new HashMap<String, Float>();

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
        filePath = (fileUtil.getRouteStorageDir("recSearch")).getAbsolutePath();
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

        String searchterm = searchWord.getText().toString();

        //check to see if a typed search has been entered, this is the prioritised search
        if(!searchterm.equals("")){
            Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);
            intent.putExtra("searchTerm", searchterm);
            startActivity(intent);
        }else{
            //todo retrieve all words for testing
            //retrieve all words stored by user
            db.collection("users").document(email)
                    .collection("Words")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    getResults(document);
                                }
                                SortedSet<Map.Entry<String, Float>> map = entriesSortedByValues(rankedWords);
                                String key = map.first().getKey();
                                Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);
                                intent.putExtra("searchTerm", key);
                                startActivity(intent);
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void getResults(QueryDocumentSnapshot document) {

        String list = document.getString("fingerprint");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            byte[] fingerprint = Base64.getDecoder().decode(list);
            //Testing using just fingerprints no wave
            FingerprintManager fpm = new FingerprintManager();
            Wave wave1 = new Wave("/storage/emulated/0/InMyWords/recSearch");
            byte fingerprint1[];
            fingerprint1 = wave1.getFingerprint();
            FingerprintSimilarityComputer fpsc = new FingerprintSimilarityComputer(fingerprint1, fingerprint);
            float similarityScore = fpsc.getFingerprintsSimilarity().getScore();

            rankedWords.put(document.getString("Word"), similarityScore);
        }
    }

    static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
                new Comparator<Map.Entry<K,V>>() {
                    @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        int res = e2.getValue().compareTo(e1.getValue());
                        return res != 0 ? res : 1; // Special fix to preserve items with equal values
                    }
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    private void requestStoragePermissions() {
//todo check/edit this
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
