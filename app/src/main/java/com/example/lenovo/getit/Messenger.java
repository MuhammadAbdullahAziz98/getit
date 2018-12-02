package com.example.lenovo.getit;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Messenger extends AppCompatActivity {

    public static final String ANONYMOUS = "anonymous"; //was used earlier for sending messages without authetication, now used when user logs-out
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;    //limit of msg size

    private ListView mMessageListView;  //list view for displaying
    private MessageAdapter mMessageAdapter; //adapter for adding messages
    private ProgressBar mProgressBar;   //ignore
    private ImageButton mPhotoPickerButton; //photo selecter button
    private EditText mMessageEditText;  // edit text to get written texts
    private Button mSendButton;

    private String mUsername;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesChildDatabaseReference;
    private DatabaseReference mMessagesDatabaseReference,mMessagesInfoReference;
    private ChildEventListener mChildEventListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;
    private static final int RC_PHOTO_PICKER =  2;
    private FirebaseAuth mFireBaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        FirebaseApp.initializeApp(getApplicationContext()); //initialize firebase for getting it's object's references in the app

        Intent intent = getIntent();
        String passage = intent.getStringExtra("key");
        if(!(passage.equals("null")))
            passage = intent.getStringExtra("key");
        else
            passage = null;
        mUsername = ANONYMOUS;  //initially username is anonymous until signup or log in

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFireBaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mUsername = mFireBaseAuth.getCurrentUser().getDisplayName();
        if(passage != null) {
            mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages").child(passage);
            mMessagesChildDatabaseReference =  mFirebaseDatabase.getReference().child("messages").child(passage).child("msgs");
            if(!mMessagesChildDatabaseReference.equals(null))
                onSignedInInitialize(mUsername);

        }else {
            String mEmail = intent.getStringExtra("mEmail");
            String tMail = intent.getStringExtra("theirMail");
            mEmail = mFireBaseAuth.getCurrentUser().getEmail();
            Info info = new Info(mEmail,tMail);
            mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages").push();
            mMessagesInfoReference = mMessagesDatabaseReference.child("info");
            mMessagesInfoReference.setValue(info);
            mMessagesChildDatabaseReference =  mMessagesDatabaseReference.child("msgs").push();
            onSignedInInitialize(mUsername);

        }

        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");


        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);
        List<Message> friendlyMessages = new ArrayList<>();

        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            //textwatcher ensures that no empty msgs are sent.
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message(mMessageEditText.getText().toString(), mUsername, null);
                mMessagesChildDatabaseReference = mMessagesDatabaseReference.child("msgs");
                mMessagesChildDatabaseReference.push().setValue(message);
                // Clear input box
                onSignedInInitialize(mUsername);
                mMessageEditText.setText("");
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null)
                {
                    //user is signed in
                    Toast.makeText(getApplicationContext(),"Welcome!",Toast.LENGTH_SHORT).show();
                    onSignedInInitialize(user.getDisplayName());
                }
                else{
                    //user is signed out
                    Toast.makeText(getApplicationContext(),"No user signed in",Toast.LENGTH_SHORT).show();
                    //onSignedOutCleanUp();
                }
            }
        };

    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            final StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
            photoRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {//checks if we have gotten the url of the picture uploaded to the server, can be got through query too.bu we will have to know user's name or email (unique key)
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;
                            Message message = new Message(null,mUsername,downloadUrl.toString());
                            mMessagesChildDatabaseReference = mMessagesDatabaseReference.child("msgs");
                            mMessagesChildDatabaseReference.push().setValue(message);
                            // Clear input box
                            onSignedInInitialize(mUsername);

                        }
                    });
                }
            });
        }
    }

    private void onSignedInInitialize(String username){
        mUsername = username;
        attachDatabaseReadListener();
    }

    private void onSignedOutCleanUp(){
        mUsername = ANONYMOUS;  //remove user name
        mMessageAdapter.clear();    //clear adapter from msgs
        detachDatabaseReadListener();   //remove the database listener, will not listen to msgs being added.
    }
    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {  //if not already detached , remove db listener
        //    mMessagesDatabaseReference.removeEventListener(mChildEventListener);    //stops checking for new entries in db
            mChildEventListener = null;
        }
    }
    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                //check for new msgs added in messages table:
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    Message message = dataSnapshot.getValue(Message.class);
                    mMessageAdapter.add(message);
                    //if any new msgs , display them.
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mMessagesChildDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

}
