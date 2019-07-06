package com.example.robinhood;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

    }

    public void clickSignOut(View view) {



        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        mAuth.signOut();
                        doneDeleting();
                        break;
                }
            }
        };

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to Sign Out?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    public void clickDeactivate(View view) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteAccount();
                        break;
                }
            }
        };

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to delete this account and all its offers?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();

    }


    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;

    private void deleteAccount() {

        setContentView(R.layout.activity_start_up);

        // Toast.makeText(this, "Querying...", Toast.LENGTH_SHORT).show();

        Query query = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("Offers");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Toast.makeText(SettingsActivity.this, "Done Querying...", Toast.LENGTH_SHORT).show();
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        database.getReference("Offers").child(snapshot.getKey()).setValue(null);
                    }
                } else {
                //    Toast.makeText(SettingsActivity.this, "No snapshot?", Toast.LENGTH_SHORT).show();
                }

                database.getReference("Users").child(currentUser.getUid()).setValue(null);

                String a = currentUser.getProviders().get(0);

                if(a.equals("google.com")) {
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(SettingsActivity.this);
                    if(account != null){
                        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                        currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    doneReauthentication();
                                } else {
                                    Toast.makeText(SettingsActivity.this, "Re-authentication failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else if(a.equals("password")){

                    AlertDialog.Builder alert = new AlertDialog.Builder(SettingsActivity.this);

                    alert.setTitle("Sign in for confirmation");
                    alert.setMessage("Enter your password");

                    final EditText et_password = new EditText(SettingsActivity.this);
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    alert.setView(et_password);

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String value = et_password.getText().toString();


                            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), value);
                            FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        doneReauthentication();
                                    } else {
                                        Toast.makeText(SettingsActivity.this, "Re-authentication failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).show();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void doneReauthentication() {
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SettingsActivity.this, "We are sorry to say goodbye! We hope you will rejoin our community in the near future", Toast.LENGTH_LONG).show();
                    doneDeleting();
                } else {
                    Toast.makeText(SettingsActivity.this, "Could not delete", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void doneDeleting() {
        finishAffinity();
    }
}