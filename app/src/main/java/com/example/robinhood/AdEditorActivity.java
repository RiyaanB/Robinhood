package com.example.robinhood;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;

public class AdEditorActivity extends AppCompatActivity {

    Spinner categorySpinner;
    ArrayAdapter<String> adapter;

    EditText title, description, name, phone, address;
    CheckBox active;

    Bundle myBundle;

    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference currentOfferDatabase;

    String old_title, old_description, old_name, old_phone, old_address;
    String new_title, new_description, new_name, new_phone, new_address;
    int old_category;
    int new_category;
    boolean old_active;
    boolean new_active;

    ArrayList<String> categories;
    String currentOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        categories = new ArrayList<>();
        categories.add("Books");
        categories.add("Bags");
        categories.add("Stationary");
        categories.add("Sports");
        categories.add("Games");
        categories.add("Clothes");
        categories.add("Electronics");

        myBundle = getIntent().getExtras();
        currentOffer = myBundle.getString("OFFER_ID");

        if(!currentOffer.equals("NO_OFFER")){
            currentOfferDatabase = database.getReference().child("Offers").child(currentOffer);
            currentOfferDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    old_title = (String) dataSnapshot.child("Title").getValue();
                    old_description = (String) dataSnapshot.child("Description").getValue();
                    old_name = (String) dataSnapshot.child("Name").getValue();
                    old_phone = (String) dataSnapshot.child("Phone").getValue();
                    old_address = (String) dataSnapshot.child("Address").getValue();
                    old_category = categories.indexOf(dataSnapshot.child("Category").getValue());
                    old_active = (Boolean) dataSnapshot.child("Active").getValue();

                    continueActivityStart();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {

            currentOffer = UUID.randomUUID().toString();
            currentOfferDatabase = database.getReference().child("Offers").child(currentOffer);

            old_title = "";
            old_description = "";
            old_name = "";
            old_phone = "";
            old_address = "";
            old_category = 0;
            old_active = true;

            continueActivityStart();
        }

    }

    private void continueActivityStart() {
        setContentView(R.layout.activity_ad_editor);

        title = findViewById(R.id.input_title);
        description = findViewById(R.id.input_description);
        name = findViewById(R.id.input_name);
        phone = findViewById(R.id.input_phone);
        address = findViewById(R.id.et_address);
        categorySpinner = findViewById(R.id.spn_category);
        active = findViewById(R.id.cb_active);

        title.setText(old_title);
        description.setText(old_description);
        name.setText(old_name);
        phone.setText(old_phone);
        address.setText(old_address);
        categorySpinner.setSelection(old_category);
        active.setChecked(old_active);


        adapter = new ArrayAdapter<>(AdEditorActivity.this, android.R.layout.simple_spinner_item, categories);
        categorySpinner.setAdapter(adapter);
    }

    public void clickDone(View view) {

        new_title = title.getText().toString();
        new_description = description.getText().toString();
        new_name = name.getText().toString();
        new_phone = phone.getText().toString();
        new_address = address.getText().toString();
        new_category = categorySpinner.getSelectedItemPosition();
        new_active = active.isChecked();

        currentOfferDatabase.child("Title").setValue(new_title);
        currentOfferDatabase.child("Description").setValue(new_description);
        currentOfferDatabase.child("Name").setValue(new_name);
        currentOfferDatabase.child("Phone").setValue(new_phone);
        currentOfferDatabase.child("Address").setValue(new_address);
        currentOfferDatabase.child("Category").setValue(new_category);
        currentOfferDatabase.child("Active").setValue(new_active);
        currentOfferDatabase.child("User").setValue(currentUser.getUid());

        database.getReference().child("Users").child(currentUser.getUid()).child("Offers").child(currentOffer).child("Status").setValue(new_active);
        finish();
    }
}
