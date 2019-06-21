package com.example.robinhood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void clickNGORequests(View view) {
        startActivity(new Intent(this, NGORequestsActivity.class));
    }

    public void clickMyOffers(View view) {
        startActivity(new Intent(this, MyOffersActivity.class));
    }

    public void clickSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void clickInformation(View view) {
        startActivity(new Intent(this, InformationActivity.class));
    }

    public void clickNewOffer(View view) {
        Intent newOfferIntent = new Intent(this, AdEditorActivity.class);
        newOfferIntent.putExtra("OFFER_ID", "NO_OFFER");
        startActivity(newOfferIntent);
    }


}
