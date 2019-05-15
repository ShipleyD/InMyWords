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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.wave.Wave;

import java.util.Locale;

public class SearchResultsActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private String searchTerm;
    private String filePath;
    private CollectionReference wordListRef;
    private TextToSpeech tts;

    private WordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_words);

        Bundle bundle = getIntent().getExtras();
        searchTerm = bundle.getString("searchTerm");
        filePath = bundle.getString("filePath");

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Results");

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        //complete query and display results in a recycler view
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
                    Intent intent = new Intent(SearchResultsActivity.this, EditWordActivity.class);
                    intent.putExtra("path", path);
                    startActivity(intent);
                }
            }
        });
    }

    private void setUpRecyclerView() {

        if(searchTerm != "") {
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            wordListRef = db.collection("users").document(email).collection("Words");
            Query query = wordListRef.whereEqualTo("Word", searchTerm).orderBy("Word", Query.Direction.ASCENDING);

            FirestoreRecyclerOptions<Word> options = new FirestoreRecyclerOptions.Builder<Word>()
                    .setQuery(query, Word.class)
                    .build();

            adapter = new WordAdapter(options);

            RecyclerView recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        } else {



        }
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
