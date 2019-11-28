package com.example.lenovo.getit;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    FirebaseStorage mStorage;
    DatabaseReference ref;
    StorageReference refStorage;
    DatabaseReference mMessagesDatabaseReference;
    ImageView profilePic;
    String s = "";
    TextView name;
    ValueEventListener valueEventListener;
    boolean gone =false;
    String mCurrentUserId;
    Button message;
    private FirebaseAnalytics mFirebaseAnalytics;
    CheckInternetBroadcast checker;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checker);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);

        Intent i = getIntent();
        mCurrentUserId = i.getStringExtra("uid");
        message = findViewById(R.id.messageProfile);
        profilePic = findViewById(R.id.profilePicture);
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mMessagesDatabaseReference=mDatabase.getReference().child("messages");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        final FirebaseUser user = mAuth.getCurrentUser();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Profile");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        if(mCurrentUserId.equals(user.getUid()))
        {
            message.setVisibility(View.GONE);
            gone  =true;
            setData(user.getUid());
        }
        if(!gone){
            setData(mCurrentUserId);
            final String getChild1 = mAuth.getCurrentUser().getUid();
            final String getChild2 = mCurrentUserId;
            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Message Button");
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Button click");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            //Toast.makeText(getApplicationContext(), "got child", Toast.LENGTH_SHORT).show();
                            boolean notFound = true;
                            if (snapshot.hasChildren()) {
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    if (data.hasChild("info")) {
                                        Toast.makeText(getApplicationContext(), data.getKey(), Toast.LENGTH_SHORT).show();
                                        String s1 = data.child("info").child("tMail").getValue().toString();
                                        String s2 = data.child("info").child("mEmail").getValue().toString();
                                        if(data.child("info").child("mEmail").getValue().toString() != null && data.child("info").child("tMail").getValue().toString()!=null){
                                            if ((s1.equals(getChild1) && s2.equals(getChild2)) || (s1.equals(getChild2) && s2.equals(getChild1))) {
                                                Toast.makeText(getApplicationContext(),data.getKey().toString(),Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), Messenger.class);
                                                intent.putExtra("key", data.getKey().toString());
                                                if(s1.equals(user.getUid()))
                                                    intent.putExtra("theirMail", s2);
                                                else
                                                    intent.putExtra("theirMail", s1);
                                                notFound = false;
                                                startActivity(intent);
                                                mMessagesDatabaseReference.removeEventListener(valueEventListener);
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
                                    mMessagesDatabaseReference.removeEventListener(valueEventListener);
                                    finish();


                                }
                            } else {
                                Intent intent = new Intent(getApplicationContext(), Messenger.class);
                                intent.putExtra("key", "null");

                                intent.putExtra("mEmail", getChild1);
                                intent.putExtra("theirMail", getChild2);

                                startActivity(intent);
                                mMessagesDatabaseReference.removeEventListener(valueEventListener);
                                finish();


                            }
                        }




                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    mMessagesDatabaseReference.addValueEventListener(valueEventListener);
                }
            });
        }


    }
    public void setData(String userID){
        ref = mDatabase.getReference().child("Users").child(userID);
        refStorage = mStorage.getReference().child("profile_pics");
        name = findViewById(R.id.nameProfile);
        ref.child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                s = dataSnapshot.getValue().toString();

                name.setText(s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref.child("Profile Picture").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                s = dataSnapshot.getValue().toString();
                if(s.equals("null"))
                {
                    Glide.with(profilePic.getContext())
                            .load(R.drawable.profileblank)
                            .into(profilePic);

                }
                else{
                    Glide.with(profilePic.getContext())
                            .load(s)
                            .into(profilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
