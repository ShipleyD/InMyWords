package com.example.inmywords.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.inmywords.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditWordActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "EditActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private String path;
    private CollectionReference wordListRef;
    private TextToSpeech tts;
    private TextView selectedWord;
    private Button deleteWordButton;
    private String word;
    private FirebaseStorage storage;
    private boolean fileDelete;
    private boolean documentDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_word);

        Bundle bundle = getIntent().getExtras();
        path = bundle.getString("path");

        selectedWord = findViewById(R.id.selectedWord);
        getData();

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Edit Word");

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        storage = FirebaseStorage.getInstance();

        deleteWordButton = findViewById(R.id.btnDeleteWord);
        deleteWordButton.setOnClickListener(this);
    }

    public void getData(){

        DocumentReference docRef = db.document(path);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        word = document.get("Word").toString();
                        selectedWord.setText(word);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            // ...
            case R.id.btnDeleteWord:
                deleteWord();
                break;

    }
}

    private void deleteWord() {

        fileDelete = false;
        documentDelete = false;

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference to the file to delete
        StorageReference wordRef = storageRef.child(email + "/words/" + word);

        // Delete the file
        wordRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "File successfully deleted!");
                fileDelete = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting file", e);
            }
        });

        db.collection("users").document(email)
                .collection("Words").document(word)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        documentDelete = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
        //todo - pop up to say word was deleted
            startActivity(new Intent(EditWordActivity.this, HomeActivity.class));
            finish();

    }
    }
