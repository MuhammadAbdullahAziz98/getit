package com.example.lenovo.getit;

import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ThreadPoolExecutor;

public class CreatePostActivity extends AppCompatActivity {

    EditText post_title_edit_text;
    EditText post_description_edit_text;
    Button create_post_button;
    CheckInternetBroadcast checker;

    FirebaseAuth mFirebaseAuth;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);

        mFirebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        post_title_edit_text = (EditText) findViewById(R.id.post_title_edit_text);
        post_description_edit_text = (EditText) findViewById(R.id.post_description_edit_text);
        create_post_button = (Button) findViewById(R.id.create_post_button);
        create_post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!post_title_edit_text.getText().toString().equals("") && !post_description_edit_text.getText().toString().equals("")){
                    addPost();
                    Toast.makeText(getApplicationContext(), "Post Created", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Field(s) can not be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addPost(){
        String userId = mFirebaseAuth.getUid().trim();
        String productTitle = post_title_edit_text.getText().toString().trim();
        String productDescription = post_description_edit_text.getText().toString().trim();
        final String id = FirebaseDatabase.getInstance().getReference("posts").push().getKey();
        FirebaseDatabase.getInstance().getReference("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databaseReference.child(id).child("username").setValue(dataSnapshot.child("Name").getValue());
                databaseReference.child(id).child("profilePic").setValue(dataSnapshot.child("Profile Picture").getValue());
                databaseReference.child(id).child("isPremium").setValue(dataSnapshot.child("isPremium").getValue());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child(id).child("userId").setValue(userId);
        databaseReference.child(id).child("productTitle").setValue(productTitle);
        databaseReference.child(id).child("productDescription").setValue(productDescription);
        Toast.makeText(getApplicationContext(), userId, Toast.LENGTH_SHORT).show();
        //DatabaseReference temp = databaseReference.child(id);
        //databaseReference.child(id).setValue(new TextualPost(profilePic, postedWho, productTitle, productDescription, productPriority));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checker);
    }

}
