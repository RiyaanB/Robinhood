package com.robinhoodmumbai.robinhood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MyOffersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_offers);
    }

    public void clickActiveOffers(View view) {
        Intent viewMyOffersActivityIntent = new Intent(this, ViewMyOffersActivity.class);
        viewMyOffersActivityIntent.putExtra("Status", "Active");
        startActivity(viewMyOffersActivityIntent);
    }

    public void clickInactiveOffers(View view) {
        Intent viewMyOffersActivityIntent = new Intent(this, ViewMyOffersActivity.class);
        viewMyOffersActivityIntent.putExtra("Status", "Inactive");
        startActivity(viewMyOffersActivityIntent);
    }

}
