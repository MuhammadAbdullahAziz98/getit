package com.example.lenovo.getit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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

import java.io.Serializable;
import java.util.ArrayList;

public class MainMessages extends AppCompatActivity {

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mProfilePicDatabaseReference,mMessagesDatabaseReference;
    ArrayList<String> emails;
    Intent intent;
    String viewText,mEmail,finalPassage;
    private FirebaseAuth mFireBaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_messages);
        intent = getIntent();
        emails = new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mProfilePicDatabaseReference = mFirebaseDatabase.getReference().child("profilePics");
        mProfilePicDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
        mMessagesDatabaseReference=mFirebaseDatabase.getReference().child("messages");
//        mProfilePicDatabaseReference.addValueEventListener(valueEventListener);

        mFireBaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null)
                {
                    //user is signed in
                    Toast.makeText(getApplicationContext(),"Welcome!",Toast.LENGTH_SHORT).show();
                }
                else{
                    //user is signed out
                    Toast.makeText(getApplicationContext(),"No user signed in",Toast.LENGTH_SHORT).show();
                    //onSignedOutCleanUp();
                }
            }
        };

    }
    boolean done =false;
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                int size = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Profile profile = snapshot.getValue(Profile.class);
                    emails.add(profile.getEmail());
                    size++;
                }
                if (size > 0) {
                    TextListAdapter adapter = new TextListAdapter(getApplicationContext(), emails);
                    ListView view = new ListView(getApplicationContext());
                    view.setAdapter(adapter);
                    view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TextView tv = (TextView) view.findViewById(R.id.email);
                            viewText = tv.getText().toString();
                            mEmail = intent.getStringExtra("userEmail");
//                            Toast.makeText(getApplicationContext(), mEmail, Toast.LENGTH_SHORT).show();
                            if(viewText!= null)
                                Toast.makeText(getApplicationContext(), viewText, Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(), "null second", Toast.LENGTH_SHORT).show();

                            final String getChild1 = mEmail;
                            final String getChild2 = viewText;
                            ValueEventListener valueEventListener1 = mMessagesDatabaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    //Toast.makeText(getApplicationContext(), "got child", Toast.LENGTH_SHORT).show();
                                    if (!done) {
                                        done =true;
                                        boolean notFound = true;
                                        if (snapshot.hasChildren()) {
                                            for (DataSnapshot data : snapshot.getChildren()) {
                                                if (data.hasChild("info")) {
                                                    Toast.makeText(getApplicationContext(), data.getKey(), Toast.LENGTH_SHORT).show();
                                                    if(data.child("info").child("mEmail").getValue() != null && data.child("info").child("tMail").getValue()!=null){
                                                    if ((data.child("info").child("mEmail").getValue().equals(getChild1) && data.child("info").child("tMail").getValue().equals(getChild2)) || (data.child("info").child("mEmail").getValue().equals(getChild2) && data.child("info").child("tMail").getValue().equals(getChild1))) {
                                                        Intent intent = new Intent(getApplicationContext(), Messenger.class);
                                                        intent.putExtra("key", data.getKey());
                                                        notFound = false;
                                                        startActivity(intent);
                                                        finish();

                                                    }}
                                                }
                                            }
                                            if(notFound)
                                            {
                                                Intent intent = new Intent(getApplicationContext(), Messenger.class);
                                                intent.putExtra("key", "null");

                                                intent.putExtra("mEmail", getChild1);
                                                intent.putExtra("theirMail", getChild2);

                                                startActivity(intent);

                                            }
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), Messenger.class);
                                            intent.putExtra("key", "null");

                                            intent.putExtra("mEmail", getChild1);
                                            intent.putExtra("theirMail", getChild2);

                                            startActivity(intent);

                                        }
                                    }
                                }



                             @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });

                    LinearLayout linear = findViewById(R.id.mainLayout);
                    linear.removeAllViews();
                    linear.addView(view);
                }
            }

        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

}