package com.example.fitnessgameapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView StepCount;
    TextView LevelCount;
    TextView XPCounter;
    ProgressBar ProgressBar;
    DatabaseReference userDataReference;
    DatabaseReference stepsReference;

    DatabaseReference levelReference;
    DatabaseReference expReference;
    DatabaseReference xpConvertReference;

    SensorManager sensorManager;                           //Setting up all the variables and references

    boolean walking = false;
    TextView personName;

    Model model;

    public static int xpForLevel = 50;
    int Steps = 0;
    public static int Level = 1;
    int ConvertedSteps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        String GoogleID = account.getId();
        String PersonForename = account.getGivenName();
        String PersonSurname = account.getFamilyName();
        String PersonFullName = PersonForename + " " + PersonSurname;
        setContentView(R.layout.activity_main);

        //setting up database references.

        userDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(GoogleID).child("UserData");
        stepsReference = FirebaseDatabase.getInstance().getReference().child("Users").child(GoogleID).child("UserData").child("Steps");
        levelReference = FirebaseDatabase.getInstance().getReference().child("Users").child(GoogleID).child("UserData").child("Level");
        expReference = FirebaseDatabase.getInstance().getReference().child("Users").child(GoogleID).child("UserData").child("Exp");
        xpConvertReference = FirebaseDatabase.getInstance().getReference().child("Users").child(GoogleID).child("UserData").child("XPConvert");
        model = new Model();

        Button levelMemoryBtn = (Button) findViewById(R.id.lvlMemory);
        Button leaderboardBtn = (Button) findViewById(R.id.leaderboard);
        Button exitBtn = (Button) findViewById(R.id.Exit_button);
        Button stepsToXpBtn = (Button) findViewById(R.id.stepsToXp);

        personName = (TextView) findViewById(R.id.Person_Name);

        Steps = model.getSteps();
        xpForLevel = 50;
        Level = 1;
        model.setLevel(Level);
        model.setExp(xpForLevel);

        StepCount = (TextView) findViewById(R.id.Step_Count);
        LevelCount = (TextView) findViewById(R.id.Current_Level);
        XPCounter = (TextView) findViewById(R.id.XPCounter);
        ProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        personName.setText(PersonFullName);
        LevelCount.setText("Level: " + String.valueOf(Level));
        StepCount.setText("Steps: " + String.valueOf(Steps));

        /*Below you will see a similar structure repeated several times for different values such as steps and level. What i am doing is essentially checking if that
        specfic child exists, and if it does then i set the corresponding model to be that value. This means that every time the user logs in and turns the app on,
        this code will check the database, take the value of whats its looking for and then set that variable on the users end. This way i dont need to save anything
        on the users device and instead everything will be taken from the database when the user logs in.
         */

        userDataReference.child("Steps").addListenerForSingleValueEvent(new ValueEventListener() {@Override

        public void onDataChange(DataSnapshot snapshot) {

            if (snapshot.exists()) {
                model.setSteps(snapshot.getValue(Integer.class));
                model.setSteps(model.getSteps());
                stepsReference.setValue(model.getSteps());
                Steps = model.getSteps();
                StepCount.setText("Steps: " + String.valueOf(Steps));

            }

        }@Override
        public void onCancelled(DatabaseError databaseError) {}
        });

        userDataReference.child("Level").addListenerForSingleValueEvent(new ValueEventListener() {@Override
        public void onDataChange(DataSnapshot snapshot) {

            if (snapshot.exists()) {

                model.setLevel(snapshot.getValue(Integer.class));
                model.setLevel(model.getLevel());
                levelReference.setValue(model.getLevel());
                Level = model.getLevel();
                LevelCount.setText("Level: " + String.valueOf(Level));

            }

        }@Override
        public void onCancelled(DatabaseError databaseError) {}
        });

        userDataReference.child("Exp").addListenerForSingleValueEvent(new ValueEventListener() {@Override
        public void onDataChange(DataSnapshot snapshot) {

            if (snapshot.exists()) {

                model.setExp(snapshot.getValue(Integer.class));
                model.setExp(model.getExp());
                expReference.setValue(model.getExp());
                xpForLevel = model.getExp();

            }

        }@Override
        public void onCancelled(DatabaseError databaseError) {}
        });

        userDataReference.child("XPConvert").addListenerForSingleValueEvent(new ValueEventListener() {@Override
        public void onDataChange(DataSnapshot snapshot) {

            if (snapshot.exists()) {
                model.setXpconvert(snapshot.getValue(Integer.class));

                xpConvertReference.setValue(model.getXpconvert());
                ConvertedSteps = model.getXpconvert();

                int XpLeftText = model.getExp() - snapshot.getValue(Integer.class);

                XPCounter.setText("Exp Needed: " + String.valueOf(XpLeftText));

            }

        }@Override
        public void onCancelled(DatabaseError databaseError) {}
        });

        levelMemoryBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, LevelMemory.class));

            }
        });

        leaderboardBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Leaderboard.class));

            }

        });

        exitBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                finish();
                System.exit(0);

            }

        });

        stepsToXpBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                userDataReference.child("XPConvert").addListenerForSingleValueEvent(new ValueEventListener() {@Override
                public void onDataChange(DataSnapshot snapshot) {

                    model.setXpconvert(snapshot.getValue(Integer.class));
                    model.setXpconvert(ConvertedSteps = ConvertedSteps + model.getSteps());
                    xpConvertReference.setValue(model.getXpconvert());
                    ConvertedSteps = model.getXpconvert();                       //This code is responsible for converting the steps to experience.
                                                                                    //It gets the current xp value from the database, this value then gets
                                                                                    //The value of steps subtracted from it to get the new xp needed value.
                    int XpLeftText = model.getExp() - model.getXpconvert();
                    XPCounter.setText("Exp Needed: " + String.valueOf(XpLeftText));

                    model.setSteps(0);
                    userDataReference.child("Steps").setValue(model.getSteps());         //I reset the value of steps to 0 as the steps have been converted.
                    Steps = model.getSteps();
                    StepCount.setText("Steps: " + String.valueOf(Steps));

                    CheckIfEnoughXP();

                }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

            }

        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        XPCounter.setText("Exp Needed: " + model.getExp());

    }

    public void CheckIfEnoughXP() {                      //If the number of converted steps is equal to or greater than how much exp is needed this gets triggered.

        if (ConvertedSteps >= xpForLevel) {
            startActivity(new Intent(MainActivity.this, TakingPicture.class));
            LevelUp();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        walking = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        }
        else {
            Toast.makeText(this, "Sensor not found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        walking = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (walking) {

            userDataReference.child("Steps").addListenerForSingleValueEvent(new ValueEventListener() {@Override
            public void onDataChange(DataSnapshot snapshot) {

                model.setSteps(snapshot.getValue(Integer.class));

                model.setSteps(model.getSteps() + 1);       //This code is triggered whenever a user takes a step. It adds 1 to the number of steps both locally and in firebase.
                stepsReference.setValue(model.getSteps());
                Steps = model.getSteps();
                StepCount.setText("Steps: " + String.valueOf(Steps));

            }@Override
            public void onCancelled(DatabaseError databaseError) {}
            });

            ProgressBar.setProgress(ProgressBar.getProgress() + 1);

        }

    }

    public void LevelUp() {

        model.setXpconvert(0);
        ConvertedSteps = model.getXpconvert();
        xpConvertReference.setValue(model.getXpconvert());

        userDataReference.child("Level").addListenerForSingleValueEvent(new ValueEventListener() {@Override
        public void onDataChange(DataSnapshot snapshot) {

            model.setLevel(snapshot.getValue(Integer.class));     //Here i am adjusting the level in the firebase database as the user has levelled up

            model.setLevel(model.getLevel() + 1);
            levelReference.setValue(model.getLevel());
            Level = model.getLevel();
            LevelCount.setText("Level: " + String.valueOf(Level));

        }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        userDataReference.child("Exp").addListenerForSingleValueEvent(new ValueEventListener() {@Override
        public void onDataChange(DataSnapshot snapshot) {

            model.setExp(snapshot.getValue(Integer.class));

            model.setExp(model.getExp() * 2);
            expReference.setValue(model.getExp());
            xpForLevel = model.getExp();

            XPCounter.setText("Exp Needed: " + String.valueOf(model.getExp()));

        }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}