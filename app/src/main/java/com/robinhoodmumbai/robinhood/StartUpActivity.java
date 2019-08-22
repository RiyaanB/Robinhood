package com.robinhoodmumbai.robinhood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(final FirebaseUser currentUser) {
        Intent userIntent;
        if(currentUser == null) {
            // Toast.makeText(this, "There is no user", Toast.LENGTH_SHORT).show();
            userIntent = new Intent(this, SignInActivity.class);
            startActivity(userIntent);
        }
        else {
            // Toast.makeText(this, "There is a User", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(StartUpActivity.this, HomeActivity.class));
        }
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}
