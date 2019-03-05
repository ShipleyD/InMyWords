package com.example.inmywords.View;

import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.inmywords.R;
import com.google.firebase.firestore.FirebaseFirestore;

//todo - fix not finding words if they contain different capitalisation
public class SearchActivity extends AppCompatActivity implements
        View.OnClickListener{

    private EditText searchWord;
    private Button btnSearch;
    private FirebaseFirestore db;

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

        //get instance of db
        db = FirebaseFirestore.getInstance();

        searchWord = findViewById(R.id.searchWord);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // ...
            case R.id.btnSearch:
                search();
                break;
        }
    }

        private void search(){

        String searchterm = searchWord.getText().toString();


        Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);
        intent.putExtra("searchTerm", searchterm);
        startActivity(intent);

        }
}
