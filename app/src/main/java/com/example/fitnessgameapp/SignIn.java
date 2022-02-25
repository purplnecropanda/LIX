package com.example.fitnessgameapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth firebaseInstance;
    private int RC_SIGN_IN = 1;
    DatabaseReference databaseReference;
    DatabaseReference userDataReference;
    DatabaseReference stepsReference;
    DatabaseReference levelReference;                           //Creating database references
    DatabaseReference expReference;
    DatabaseReference xpConvertReference;
    DatabaseReference emailReference;
    Model model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInButton = findViewById(R.id.sign_in_button);
        firebaseInstance = FirebaseAuth.getInstance();

        model = new Model();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("1047304043047-gms5gh0ihp82ucjnkqsu3tdjb1761ib8.apps.googleusercontent.com").requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {@Override
        public void onClick(View v) {
            signIn();
        }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();       //Creating the sign in intent
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task < GoogleSignInAccount > task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }

    }
    private void handleSignInResult(Task < GoogleSignInAccount > completedTask) {
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(SignIn.this, "Signed in Successfully", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }
        catch(ApiException e) {
            Toast.makeText(SignIn.this, "Sign in failed", Toast.LENGTH_SHORT).show();

        }
    }


    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {

        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseInstance.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener < AuthResult > () {@Override
        public void onComplete(@NonNull Task < AuthResult > task) {
            if (task.isSuccessful()) {

                startActivity(new Intent(SignIn.this, MainActivity.class));
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

                final String personID = account.getId();
                final String personName = account.getGivenName() + " " + account.getFamilyName();


                // firebase references

                databaseReference = FirebaseDatabase.getInstance().getReference();
                userDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(personID).child("UserData");
                stepsReference = FirebaseDatabase.getInstance().getReference().child("Users").child(personID).child("UserData").child("Steps");
                levelReference = FirebaseDatabase.getInstance().getReference().child("Users").child(personID).child("UserData").child("Level");
                expReference = FirebaseDatabase.getInstance().getReference().child("Users").child(personID).child("UserData").child("Exp");
                xpConvertReference = FirebaseDatabase.getInstance().getReference().child("Users").child(personID).child("UserData").child("XPConvert");
                emailReference = FirebaseDatabase.getInstance().getReference().child("Users").child(personID).child("UserData").child("Email");

                DatabaseReference users = databaseReference.child("Users");

                users.addListenerForSingleValueEvent(new ValueEventListener() {@Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.child(personID).exists()) {
                                                                 //If the user already exists, does nothing. If they do not exist it sets the default values for the user


                    } else {

                        model.setSteps(0);
                        model.setLevel(1);
                        model.setExp(MainActivity.xpForLevel);
                        model.setXpconvert(0);
                        model.setEmail(personName);

                        stepsReference.setValue(model.getSteps());
                        levelReference.setValue(model.getLevel());
                        expReference.setValue(model.getExp());
                        xpConvertReference.setValue(model.getXpconvert());
                        emailReference.setValue(model.getEmail());

                    }
                }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
            else {
                Toast.makeText(SignIn.this, "Failed", Toast.LENGTH_SHORT).show();

            }
        }
        });
    }


}