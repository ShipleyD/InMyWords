package com.example.inmywords.View;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Base64InputStream;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.inmywords.Model_Controller.AudioFileUtil;
import com.example.inmywords.Model_Controller.Word;
import com.example.inmywords.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.musicg.wave.Wave;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TypedWordActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "TypedWordActivity";
    private Button btnSave;
    private Button btnRecord;
    private Button btnPlayBack;
    private EditText etxtTypeWord;
    private FirebaseFirestore db;
    private String filePath;
    private AudioFileUtil fileUtil = new AudioFileUtil();
    private FirebaseStorage storage;
    private TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typed_word);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Word Details");

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        //create on click listener for the next button
        btnSave= findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        //create on click listener for the record button
        btnRecord= findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(this);
        //create on click listener for the Play back button
        btnPlayBack= findViewById(R.id.btnPlayBack);
        btnPlayBack.setOnClickListener(this);
        //create reference to edit text box
        etxtTypeWord = findViewById(R.id.etxtTypeWord);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        filePath = (fileUtil.getRouteStorageDir("recWord")).getAbsolutePath();


        //initialise tts text to speech
        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // ...
            case R.id.btnSave:
                save();
                break;
            // ...
            case R.id.btnPlayBack:
                if(btnPlayBack.getText() == getResources().getText(R.string.stop_recording)) {
                    stopRecording();
                }else if (btnPlayBack.getText() == getResources().getText(R.string.playBack)){
                    playWord();
                }
                break;
            // ... todo might not need
            case R.id.btnRecord:
                startRecording();
                break;
        }
    }

    private void playWord() {
        //play the typed text using text to speech
        String toSpeak = etxtTypeWord.getText().toString();
        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

    }

    private void stopRecording() {
        //todo - might not be needed
    }

    private void startRecording() {
        //todo - might not be needed

    }


    private void save() {

        //create the fingerprint to be stored
        Wave wave = new Wave(filePath);
        byte[] fingerprint;
        fingerprint = wave.getFingerprint();

//        String uploadPath = uploadFile();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        String list = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            list = Base64.getEncoder().encodeToString(fingerprint);
            byte[] fp = Base64.getDecoder().decode(list);
        }


        Map<String, Object> Word = new HashMap<>();
        String typedWord = etxtTypeWord.getText().toString();
        Word.put("Word", typedWord);
//   todo delete    Word.put("Path", uploadPath);
        Word.put("fingerprint", list);

        db.collection("users").document(email)
                .collection("Words").document(typedWord)
                .set(Word)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        // delete the local file
        File file = new File(filePath);
        file.delete();
        //todo
        //let the user know the word saved
//        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.myCoordinatorLayout), R.string.word_saved , Snackbar.LENGTH_SHORT);
//        mySnackbar.show();
        startActivity(new Intent(TypedWordActivity.this, HomeActivity.class));
        finish();
    }

    //todo might not be needed
    private String uploadFile() {
        String typedWord = etxtTypeWord.getText().toString();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        Uri file = Uri.fromFile(new File(filePath));
        StorageReference wordsRef = storageRef.child(email + "/words/" + typedWord);
        UploadTask uploadTask = wordsRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

        return wordsRef.getPath();

    }
}
