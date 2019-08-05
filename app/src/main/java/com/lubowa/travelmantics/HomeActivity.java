package com.lubowa.travelmantics;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseRecyclerOptions<Property> options;
    private FirebaseRecyclerAdapter<Property,PropertyHolder> firebaseRecyclerAdapter;
    private boolean userIsAdmin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerView = (RecyclerView) findViewById(R.id.property_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkUserType();
        DatabaseReference deals = FirebaseDatabase.getInstance().getReference();
        Query propertyQuery = deals.child("properties").orderByKey();

        options = new FirebaseRecyclerOptions.Builder<Property>()
                .setQuery(propertyQuery, Property.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Property, PropertyHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PropertyHolder holder, int position, @NonNull Property model) {
                holder.setDetails(getApplicationContext(), model.getTitle(), model.getDescription(), model.getPrice(), model.getImageUrl());
            }

            @NonNull
            @Override
            public PropertyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_property,
                        viewGroup, false);
                return new PropertyHolder(view);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
        checkUserType();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_one:
                startActivity(new Intent(this, CreateDealActivity.class));
                break;
            case R.id.item_two:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
    }

    private void checkUserType(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child("Admin");
        db.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                            if(snapshot.getValue() == FirebaseAuth.getInstance().getCurrentUser().getUid()){
                                Toast.makeText(HomeActivity.this, "Youre an admin", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(HomeActivity.this, "You're a customer", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }
}
