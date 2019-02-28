package com.example.inmywords.View;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.inmywords.R;

public class EditWordActivity extends AppCompatActivity implements
        View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_word);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Edit Word");

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        Button deleteWordButton = findViewById(R.id.btnDeleteWord);
        deleteWordButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
}
