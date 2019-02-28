package com.example.inmywords.View;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.example.inmywords.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity  implements
        View.OnClickListener{

    private static final String TAG = "HomeActivity";
    private Button btnHSearch;
    private Button btnAdd;
    private Button btnFavourites;
    private FirebaseFirestore db;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseUser fUser;


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

        btnHSearch = findViewById(R.id.btnHSearch);
        btnAdd= findViewById(R.id.btnAdd);
        btnFavourites= findViewById(R.id.btnFavourites);

        btnHSearch.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnFavourites.setOnClickListener(this);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        initFirestore();

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null) {
            // User is signed in
        } else {
            // No user is signed in
            createSignInIntent();
        }


    }

    private void createSignInIntent() {

        //Firebase AuthUI sign in
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.in_my_words_logo_white)      // Set logo drawable
                        .setTheme(R.style.AppTheme)
                        .build(),
                RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                fUser = FirebaseAuth.getInstance().getCurrentUser();
                DocumentReference docRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                                addNewUserDoc();
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });



                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    private void addNewUserDoc() {

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("name", fUser.getDisplayName());
        user.put("email", fUser.getEmail());

        // Add a new document with a generated ID
        db.collection("users").document(fUser.getEmail())
                .set(user)
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
    }


    private void initFirestore() {

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
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
                startActivity(new Intent(HomeActivity.this, AllWordsActivity.class));
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
            case R.id.btnHSearch:
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
