package com.example.inmywords;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity  implements
        View.OnClickListener{

    private Button btnSearch;
    private Button btnAdd;
    private Button btnFavourites;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo_inline_white);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btnSearch = findViewById(R.id.btnSearch);
        btnAdd= findViewById(R.id.btnAdd);
        btnFavourites= findViewById(R.id.btnFavourites);

        btnSearch.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnFavourites.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.account:
                startActivity(new Intent(HomeActivity.this, AccountActivity.class));
                return true;

            case R.id.viewAllWords:
                return true;

            case R.id.style:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // ...
            case R.id.btnSearch:
                search();
                break;
            // ...
            case R.id.btnAdd:
                add();
                break;
            // ...
            case R.id.btnFavourites:
                favourites();
                break;
        }
    }

    private void favourites() {
        startActivity(new Intent(HomeActivity.this, FavouritesActivity.class));
    }

    private void add() {
        startActivity(new Intent(HomeActivity.this, AddWordActivity.class));
    }

    private void search() {
        startActivity(new Intent(HomeActivity.this, SearchActivity.class));
    }
}
