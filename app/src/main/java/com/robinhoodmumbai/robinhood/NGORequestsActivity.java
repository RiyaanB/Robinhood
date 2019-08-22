package com.robinhoodmumbai.robinhood;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class NGORequestsActivity extends AppCompatActivity {

    ArrayList<RequestClass> requests;

    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    FirebaseDatabase database;

    EditText et_searchbar;

    HashSet<String> searchResults;

    int length;
    int downloaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngorequests);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        requests = new ArrayList<>();

        searchResults = new HashSet<>();

        // et_searchbar = findViewById(R.id.et_searchbar);

        onClickSearch(null);
    }

    public void onClickSearch(View view) {
        firstQuery();
        setContentView(R.layout.activity_start_up);
        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();
    }

    public void firstQuery(){
        //searchResults.clear();
        //String searchText = et_searchbar.getText().toString();
        
//        Query query = FirebaseDatabase.getInstance().getReference("Requests")
//                .orderByChild("Title")
//                .startAt(searchText)
//                .endAt(searchText+"\uf8ff");

        Query query = FirebaseDatabase.getInstance().getReference("Requests").orderByChild("Active").equalTo(true);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Toast.makeText(ViewMyOffersActivity.this, "Done Querying...", Toast.LENGTH_SHORT).show();
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        searchResults.add(snapshot.getKey());
                    }
                } else {
                    //    Toast.makeText(ViewMyOffersActivity.this, "No snapshot?", Toast.LENGTH_SHORT).show();
                }
                doneFetching();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void doneFetching() {
        length = searchResults.size();
        Toast.makeText(this, length+"", Toast.LENGTH_SHORT).show();
        requests.clear();

        if(length == 0){
            doneDownloading();
            return;
        }

        Iterator<String> searchResultIterator = searchResults.iterator();
        while(searchResultIterator.hasNext()){
            FirebaseDatabase.getInstance().getReference("Requests").child(searchResultIterator.next()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String title = (String) snapshot.child("Title").getValue(),
                            description = (String) snapshot.child("Description").getValue(),
                            name = (String) snapshot.child("Name").getValue(),
                            phone = (String) snapshot.child("Phone").getValue(),
                            address = (String) snapshot.child("Address").getValue();
                    long category = (Long) snapshot.child("Category").getValue();
                    boolean active = (boolean) snapshot.child("Active").getValue();

                    RequestClass request = new RequestClass(title, description, name, phone, address, (int) category, active);
                    requests.add(request);

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
            setContentView(R.layout.activity_ngorequests);

            if(length == 0)
                Toast.makeText(this, "There are no active requests", Toast.LENGTH_SHORT).show();

            ListView lv_requests = findViewById(R.id.lv_requests);
            RequestsAdapter adapter = new RequestsAdapter(this, R.layout.list_item_request, requests);
            lv_requests.setAdapter(adapter);
            Toast.makeText(this, "Here you go!", Toast.LENGTH_SHORT).show();
        }
    }


    class ViewHolder{
        TextView title, phone, category, description;
    }

    class RequestsAdapter extends ArrayAdapter<RequestClass> {
        public RequestsAdapter(Context context, int resource, List<RequestClass> objects){
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

            RequestClass current = requests.get(position);
            holder.title.setText(current.title);
            holder.phone.setText(current.phone);
            holder.category.setText(CategoriesClass.categories[current.category]);
            holder.description.setText(current.description);
            return convertView;
        }
    }
}