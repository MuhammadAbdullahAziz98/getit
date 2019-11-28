package com.example.lenovo.getit;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class MainMessages extends AppCompatActivity {

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference,mMessagesDatabaseReference;
    ArrayList<String> ids,dataArr;
    Intent intent;
    String viewText,mEmail,finalPassage;
    private FirebaseAuth mFireBaseAuth;
    ListView view;
    boolean done =false;
    String tMail;
    CheckInternetBroadcast checker;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    TextListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_messages);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);

        intent = getIntent();
        ids = new ArrayList<>();
        dataArr = new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Users");
//        mDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
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
        final String getChild1 = mFireBaseAuth.getCurrentUser().getUid();
        view = new ListView(getApplicationContext());
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),dataArr.get(position) , Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Messenger.class);
                intent.putExtra("key", dataArr.get(position));
                intent.putExtra("theirMail",tMail);
                startActivity(intent);
                finish();


            }
        });
        adapter = new TextListAdapter(getApplicationContext(), ids);
        final ProgressBar p = findViewById(R.id.msgsbar);
//        p.setVisibility(View.VISIBLE);
        view.setAdapter(adapter);
        ValueEventListener valueEventListener1 = mMessagesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //Toast.makeText(getApplicationContext(), "got child", Toast.LENGTH_SHORT).show();
                if (!done) {
                    done = true;
                    boolean notFound = true;
                    if (snapshot.hasChildren()) {
                        for (final DataSnapshot data : snapshot.getChildren()) {
                            if (data.hasChild("info")) {
                                //Toast.makeText(getApplicationContext(), data.getKey(), Toast.LENGTH_SHORT).show();
                                String s1 = data.child("info").child("tMail").getValue().toString();
                                String s2 = data.child("info").child("mEmail").getValue().toString();

                                if (s1 != null && s2 != null) {
                                    Toast.makeText(getApplicationContext(),"HELO!",Toast.LENGTH_SHORT).show();
                                    if (s1.equals(mFireBaseAuth.getCurrentUser().getUid())) {
                                        tMail = s2;
                                        Toast.makeText(getApplicationContext(),s1,Toast.LENGTH_SHORT).show();
                                        mDatabaseReference.child(s2).child("Name").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                ids.add(dataSnapshot.getValue().toString());
                                                adapter.add(dataSnapshot.getValue().toString());
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        dataArr.add(data.getKey());
                                    }
                                    else if(s2.equals(mFireBaseAuth.getCurrentUser().getUid())){
                                        tMail = s1;
                                        Toast.makeText(getApplicationContext(),s2,Toast.LENGTH_SHORT).show();
                                        mDatabaseReference.child(s1).child("Name").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
  //                                              ids.add(dataSnapshot.getValue().toString());
                                                adapter.add(dataSnapshot.getValue().toString());
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
//                                        ids.add(mDatabaseReference.child(s1).child("Name").toString());
                                        dataArr.add(data.getKey());
                                    }
                                }
                            }
                        }
                    }
                        p.setIndeterminate(false);
                        p.setVisibility(View.GONE);
                            LinearLayout linear = findViewById(R.id.mainLayout);
                            linear.removeAllViews();
                            linear.addView(view);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checker);
    }

}