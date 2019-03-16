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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity implements
        View.OnClickListener{

    private static final String TAG = "AccountActivity";
    private FirebaseFunctions mFunctions;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //firebase cloud functions instance
        mFunctions = FirebaseFunctions.getInstance();

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

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

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


//    private void recursiveDelete(String mPath) {
//        // Create the arguments to the callable function.
//        Map<String, Object> data = new HashMap<>();
//        data.put("path", mPath);
//        data.put("push", true);
//
//        mFunctions.getHttpsCallable("recursiveDelete")
//                .call(data);
//    }

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user.getEmail();
        String userPath = db.collection("users").document(userEmail).getPath();

//        recursiveDelete(userPath);

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
                            //let the user know that they need to log out and back in
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
