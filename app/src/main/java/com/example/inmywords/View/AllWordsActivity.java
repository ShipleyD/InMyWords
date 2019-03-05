package com.example.inmywords.View;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.example.inmywords.Model_Controller.Word;
import com.example.inmywords.Model_Controller.WordAdapter;
import com.example.inmywords.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Locale;

public class AllWordsActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private CollectionReference wordListRef = db.collection("users").document(email).collection("Words");
    private TextToSpeech tts;

    private WordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_words);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("All Words");

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        setUpRecyclerView();

        //initialise tts text to speech
        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });

        adapter.setOnItemClickListener(new WordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, String pressedBtn) {

                Word mWord = documentSnapshot.toObject(Word.class);
                String button = pressedBtn;
                if(button == "play") {
                    String toSpeak = mWord.getWord();
                    if (toSpeak != null) {
                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }else if (button == "edit"){
                    String path = documentSnapshot.getReference().getPath();
                    Intent intent = new Intent(AllWordsActivity.this, EditWordActivity.class);
                    intent.putExtra("path", path);
                    startActivity(intent);
                }
                //Todo delete comments
//                Word mWord = documentSnapshot.toObject(Word.class);
//                String toSpeak = mWord.getWord();
//                if(toSpeak != null) {
//                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
//                }
            }
        });
    }

    private void setUpRecyclerView() {

        Query query = wordListRef.orderBy("Word", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Word> options = new FirestoreRecyclerOptions.Builder<Word>()
                .setQuery(query, Word.class)
                .build();

        adapter = new WordAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        adapter.stopListening();
    }


}
