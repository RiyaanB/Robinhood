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
        Toast.makeText(this, "NGO Requests", Toast.LENGTH_SHORT).show();
    }

    public void clickMyOffers(View view) {
        Toast.makeText(this, "My Offers", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MyOffersActivity.class));
    }

    public void clickSettings(View view) {
        Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
    }

    public void clickInformation(View view) {
        Toast.makeText(this, "Information", Toast.LENGTH_SHORT).show();
    }

    public void clickNewOffer(View view) {
        Toast.makeText(this, "New Offer", Toast.LENGTH_SHORT).show();
        Intent newOfferIntent = new Intent(this, AdEditorActivity.class);
        newOfferIntent.putExtra("OFFER_ID", "NO_OFFER");
        startActivity(newOfferIntent);
    }


}
