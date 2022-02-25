package com.example.fitnessgameapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LevelMemory extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_data);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        String GoogleID = account.getId();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);                                                     //Setting up variables and recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference().child("Users").child(GoogleID).child("ImageData");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter < Model,
                ViewHolder > firebaseRecyclerAdapter = new FirebaseRecyclerAdapter < Model,
                ViewHolder > (
                Model.class, R.layout.row, ViewHolder.class, databaseReference) {@Override
        protected void populateViewHolder(ViewHolder viewHolder, Model model, int i) {
            viewHolder.setDetails(getApplicationContext(), model.getTitle(), model.getDescription(), model.getImage());
        }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
}