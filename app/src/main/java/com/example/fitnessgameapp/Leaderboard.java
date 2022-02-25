package com.example.fitnessgameapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Leaderboard extends AppCompatActivity {

    FirebaseDatabase FirebaseDatabase;
    DatabaseReference DatabaseRef;
    Model model;
    private ListView MyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        String GoogleID = account.getId();
        model = new Model();                                                             //Setting up links to my google account and firebase database

        FirebaseDatabase = FirebaseDatabase.getInstance();

        MyListView = (ListView) findViewById(R.id.LeaderboardList);

        DatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(GoogleID).child("UserData").child("Email");

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference forumsRef = rootRef.child("Users");
        ValueEventListener valueEventListener = new ValueEventListener() {@Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            List < String > UserList = new ArrayList < >();

            for (DataSnapshot ds: dataSnapshot.getChildren()) {
                String name = ds.child("UserData").child("Email").getValue(String.class);
                int Level = ds.child("UserData").child("Level").getValue(Integer.class);
                int Steps = ds.child("UserData").child("Steps").getValue(Integer.class);     //Here i get the name, level and steps of the person from firebase

                UserList.add("Level: " + Level + " " + "   Steps: " + Steps + "   User: " + name);

            }

            String[] values = UserList.toArray(new String[UserList.size()]);

            Arrays.sort(values, Collections.reverseOrder());                  //I sort the array in reverse order, meaning highest level will be on top

            ArrayAdapter < String > arrayAdapter = new ArrayAdapter < String > (
                    Leaderboard.this, android.R.layout.simple_list_item_1, values);

            MyListView.setAdapter(arrayAdapter);

        }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        forumsRef.addListenerForSingleValueEvent(valueEventListener);

    }

}