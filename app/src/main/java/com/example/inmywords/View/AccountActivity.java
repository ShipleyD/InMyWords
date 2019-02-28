package com.example.inmywords.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.inmywords.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivity extends AppCompatActivity implements
        View.OnClickListener{


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

    private void deleteAccount() {
        //delete account using firebase's provided method
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // when account delete is complete go back to launch page
                        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                        if (i != null) {
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        }
                        startActivity(i);
                        finish();
                    }
                });
    }

    private void signOut() {
        //sign out using firebase's provided sign out method
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // when sign out is complete go back to launch page
                        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                        if (i != null) {
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        }
                        startActivity(i);
                        finish();
                    }
                });
    }
}
