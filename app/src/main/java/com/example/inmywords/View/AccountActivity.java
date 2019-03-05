package com.example.inmywords.View;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.inmywords.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivity extends AppCompatActivity implements
        View.OnClickListener{

    private static final String TAG = "AccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Account");

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        // Set on click listeners for the sign out and delete account buttons.
        Button signOutButton = findViewById(R.id.btnSignOut);
        signOutButton.setOnClickListener(this);

        Button deleteAccountButton = findViewById(R.id.btnDeleteAccount);
        deleteAccountButton.setOnClickListener(this);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address
            String userDisplayName = user.getDisplayName();
            String userEmail = user.getEmail();

            TextView displayName = findViewById(R.id.displayName);
            displayName.setText(userDisplayName);

            TextView email = findViewById(R.id.email);
            email.setText(userEmail);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // ...
            case R.id.btnSignOut:
                signOut();
                break;
            // ...
            case R.id.btnDeleteAccount:
                deleteAccount();
                break;
            // ...
        }
    }

    private void deleteAccount() {//todo figure this out



        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Deletion succeeded
                            loadHomePage();
                        } else {
                            // Deletion failed
                            //let the user know the word saved
                          Snackbar mySnackbar = Snackbar.make(findViewById(R.id.accountLayout), R.string.logout , Snackbar.LENGTH_LONG);
                          mySnackbar.show();
                        }
                    }
                });
    }

    private void loadHomePage(){
        // Loads the home page for after sign out or account deletion
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        if (i != null) {
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        startActivity(i);
        finish();
    }

    private void signOut() {
        //sign out using firebase's provided sign out method
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        loadHomePage();
                    }
                });
    }
}
