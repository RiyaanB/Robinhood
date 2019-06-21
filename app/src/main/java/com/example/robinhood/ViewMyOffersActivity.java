package com.example.robinhood;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ViewMyOffersActivity extends AppCompatActivity {

    boolean activeAds;

    ArrayList<String> offersIDs;
    ArrayList<OfferClass> offers;

    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference currentUserOffers;

    int length;
    int downloaded;

    ArrayList<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        categories = new ArrayList<>();
        categories.add("Books");
        categories.add("Bags");
        categories.add("Stationary");
        categories.add("Sports");
        categories.add("Games");
        categories.add("Clothes");
        categories.add("Electronics");




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        String a = (String) getIntent().getExtras().get("Status");
        activeAds = a.equals("Active");

        offersIDs = new ArrayList<>();
        offers = new ArrayList<>();

        Toast.makeText(this, "Querying...", Toast.LENGTH_SHORT).show();

        Query query = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("Offers").orderByChild("Status").equalTo(activeAds);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Toast.makeText(ViewMyOffersActivity.this, "Done Querying...", Toast.LENGTH_SHORT).show();
                offersIDs.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        offersIDs.add(snapshot.getKey());
                    }
                } else {
                //    Toast.makeText(ViewMyOffersActivity.this, "No snapshot?", Toast.LENGTH_SHORT).show();
                }
                doneFetchingNames();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void doneFetchingNames() {
        length = offersIDs.size();

        if(length == 0){
            doneDownloading();
            return;
        }

        for(String offerID:offersIDs){
            FirebaseDatabase.getInstance().getReference("Offers").child(offerID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String title = (String) snapshot.child("Title").getValue(),
                                description = (String) snapshot.child("Description").getValue(),
                                name = (String) snapshot.child("Name").getValue(),
                                phone = (String) snapshot.child("Phone").getValue(),
                                address = (String) snapshot.child("Address").getValue();
                    long category = (Long) snapshot.child("Category").getValue();
                    boolean active = (boolean) snapshot.child("Active").getValue();

                    OfferClass offer = new OfferClass(title, description, name, phone, address, (int) category, active);
                    offers.add(offer);
                    downloaded++;

                    doneDownloading();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void doneDownloading() {
        if(length == downloaded){
            setContentView(R.layout.activity_view_my_offers);

            if(length == 0)
                ((TextView)findViewById(R.id.tv_fillmestatus)).setText(activeAds ? "You have no Active Offers" : "You have no Inactive Offers");
            else
                ((TextView)findViewById(R.id.tv_fillmestatus)).setText(activeAds ? "Your Active Offers" : "Your Inactive Offers");

            ListView lv_offers = findViewById(R.id.lv_offers);
            OffersAdapter adapter = new OffersAdapter(this, R.layout.list_item_offer, offers);
            lv_offers.setAdapter(adapter);

            lv_offers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent editOfferIntent = new Intent(ViewMyOffersActivity.this, AdEditorActivity.class);
                    editOfferIntent.putExtra("OFFER_ID", offersIDs.get(position));
                    startActivity(editOfferIntent);
                    finish();
                }
            });
        }
    }

    class OffersAdapter extends ArrayAdapter<OfferClass>{
        public OffersAdapter(Context context, int resource, List<OfferClass> objects){
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if(convertView == null){
                LayoutInflater li = getLayoutInflater();
                convertView = li.inflate(R.layout.list_item_offer, null);
                holder = new ViewHolder();
                holder.title = convertView.findViewById(R.id.tv_title);
                holder.phone = convertView.findViewById(R.id.tv_phone);
                holder.category = convertView.findViewById(R.id.tv_category);
                holder.description = convertView.findViewById(R.id.tv_description);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            OfferClass current = offers.get(position);
            holder.title.setText(current.title);
            holder.phone.setText(current.phone);
            holder.category.setText(categories.get(current.category));
            holder.description.setText(current.description);
            return convertView;
        }
    }

    class ViewHolder{
        TextView title, phone, category, description;
    }
}


//                        String title = (String) snapshot.child("Title").getValue(),
//                                description = (String) snapshot.child("Description").getValue(),
//                                name = (String) snapshot.child("Name").getValue(),
//                                phone = (String) snapshot.child("Phone").getValue(),
//                                address = (String) snapshot.child("Address").getValue();
//                        int category = (Integer) snapshot.child("Category").getValue();
//                        boolean active = (boolean) snapshot.child("Active").getValue();

//                        OfferClass offer = new OfferClass(title, description, name, phone, address, category, active);