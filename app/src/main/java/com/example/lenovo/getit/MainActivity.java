package com.example.lenovo.getit;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mProfilePicDatabaseReference;
    private FirebaseAuth mFireBaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mProfilePicturesStorageReference;
    private ChildEventListener mChildEventListener;

    private String mEmail,mUsername;
    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER =  2;
    public static final String ANONYMOUS = "anonymous";
    Button signOut,getProfilePic;
    ImageView profilePicView;
    Handler handler;
    private ProgressBar mProgressBar;
    boolean checked = false,profilePicScreen=false;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mProfilePicDatabaseReference =mFirebaseDatabase.getReference().child("profilePics");

        mFirebaseStorage = FirebaseStorage.getInstance();
        mProfilePicturesStorageReference = mFirebaseStorage.getReference().child("profile_pics");
        //
        mFireBaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null)
                {
                    signedIn();
                    profilePicScreen = true;
                    Toast.makeText(getApplicationContext(),user.getUid(),Toast.LENGTH_LONG).show();
                    Query query = mFirebaseDatabase.getReference("profilePics").orderByChild("email").equalTo(user.getEmail());
                    query.addValueEventListener(valueEventListener);

                    //user is signed in

                    //Toast.makeText(getApplicationContext(),"Welcome!",Toast.LENGTH_SHORT).show();
                    onSignedInInitialize(user.getEmail(),user.getDisplayName());

                }
                else{
                    //user is signed out
                    onSignedOutCleanUp();
//                    profilePicScreen = false;
//                    auth();
                }
            }
        };
        if(!profilePicScreen){

            setContentView(R.layout.splash_screen);
            animateLogo();
        }
        else {
            signedIn();
        }

    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        try{
            profilePicScreen = (boolean) savedInstanceState.getSerializable("profilePicScreen");
            checked = (boolean) savedInstanceState.getSerializable("profilePicSet");
            path = (String) savedInstanceState.getSerializable("profilePicPath");
            signedIn();
            if(profilePicScreen)
                signedIn();
            if(checked)
            {
                setPic();
                Glide.with(profilePicView.getContext())
                        .load(path)
                        .into(profilePicView);
            }
        }
        catch(Exception ex){ }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        try {
            savedInstanceState.putSerializable("profilePicScreen", profilePicScreen);
            savedInstanceState.putSerializable("profilePicSet",  checked);
            savedInstanceState.putSerializable("profilePicPath",path);

        } catch (Exception ex) {
        }
    }
    void auth(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.EmailBuilder().build()
                        ))
                        .setLogo(R.drawable.getit)
                        .build(),
                RC_SIGN_IN);
    }
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Profile profile = snapshot.getValue(Profile.class);
                    if(profile ==null)
                        Toast.makeText(getApplicationContext(),"NO PROFILE",Toast.LENGTH_SHORT).show();
                    downloadPicToDisplay(profile);
                }
            }
  //          else
//                Toast.makeText(getApplicationContext(),"Wrong!",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            //Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
        }
    };
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Signed In!", Toast.LENGTH_SHORT).show();

                signedIn();
                /*if(mEmail!=ANONYMOUS) {
                    Query query = mFirebaseDatabase.getReference("profilePics").orderByChild("email").equalTo(mEmail);

                    query.addValueEventListener(valueEventListener);
                }
                else{
                    Toast.makeText(getApplicationContext(),"ANONYMOUS EMAIL",Toast.LENGTH_SHORT).show();
                }*/
                /*
                Query query = mFirebaseDatabase.getReference("profilePics").orderByChild("email").equalTo(mEmail);
                query.addValueEventListener(valueEventListener);

*/
                //Intent intent = new Intent()
                //mMessagesDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
                //         Query query = mFirebaseDatabase.getReference("messages").orderByChild("name").equalTo("Muhammad Abdullah");
                //query.addListenerForSingleValueEvent(valueEventListener);
                //      query.addValueEventListener(valueEventListener);
            } else if (resultCode == RESULT_CANCELED) {

                Toast.makeText(getApplicationContext(), "Sign in Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                profilePicView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"COuldn't get pic",Toast.LENGTH_SHORT).show();
            }
            mProgressBar.setIndeterminate(true);

            final StorageReference photoRef = mProfilePicturesStorageReference.child(selectedImageUri.getLastPathSegment());
            mProgressBar.setVisibility(View.VISIBLE);
            if(checked)
            {
                profilePicView.setImageResource(0);
                setPic();

            }
            photoRef.putFile(selectedImageUri).addOnProgressListener(this, new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    setPic();
                    checked = true;
                }
            }).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Intent intent  = new Intent(getApplicationContext(),MainApp.class);
                            // startActivity(intent);
                            Uri downloadUrl = uri;
//                            final Profile profile = new Profile(mUsername,mEmail,downloadUrl.toString());
  //                          mProfilePicDatabaseReference.push().setValue(profile);
    //                        downloadPicToDisplay(profile);

                        }
                    });
                }
            });
        }
    }
    protected void downloadPicToDisplay(final Profile profile){
        final Thread thread = new Thread (new Runnable(){
            public void run() {
                try {
                    Profile p = profile;
                    FutureTarget<File> future = Glide.with(getApplicationContext()).load(p.getPhotoUrl()).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

                    File file = null;
                    try {
                        file = future.get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    path = file.getAbsolutePath();
                    handler.post(new Runnable() {
                        public void run() {
                            Button msg = findViewById(R.id.msgs);
                            msg.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent i = new Intent(getApplicationContext(), MainMessages.class);
                                    i.putExtra("userEmail",mEmail);
                                    startActivity(i);
                                }
                            });
                            mProgressBar.setIndeterminate(false);
                            mProgressBar.setVisibility(View.GONE);
                            Glide.with(profilePicView.getContext())
                                    .load(path)
                                    .into(profilePicView);

                        }
                    });
                } catch (Exception ex) { }
            }
        });
        thread.start();

    }
    protected void setPic(){
        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.signOut);
        getProfilePic.setLayoutParams(params);
        getProfilePic.setText("Change Pic");
        getProfilePic.setBackgroundResource(android.R.drawable.btn_default);
        params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.profilePicUpload);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        profilePicView.setLayoutParams(params);
        mProgressBar.setVisibility(View.VISIBLE);

    }
    protected void signedIn(){
        setContentView(R.layout.activity_main);
        profilePicScreen = true;
        mProgressBar = findViewById(R.id.progressBar1);
        signOut = findViewById(R.id.signOut);
        signOut.setBackgroundResource(android.R.drawable.btn_default);
        profilePicView = findViewById(R.id.profilePicImageView);
        getProfilePic = findViewById(R.id.profilePicUpload);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(getApplicationContext());
                auth();
            }
        });
        getProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!checked)
            mFireBaseAuth.addAuthStateListener(mAuthStateListener);

    }

    void animateLogo(){
        final AnimatorSet animationSet = new AnimatorSet();
        final ImageView logo = findViewById(R.id.logo);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(logo,"rotation",180,0);
        final ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo,"scaleY", 0.5f, 1f);
        final ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo,"scaleX", 0.5f, 1f);
        final RotateAnimation rotation = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotation.setDuration(5000);
        rotation.setInterpolator(new LinearInterpolator());


        animationSet.setDuration(2000);
        animationSet.playTogether(scaleX, scaleY,rotate);
        animationSet.start();
        animationSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ImageView logoText = null;
                ImageView im = null;
                if(!profilePicScreen) {
                    logoText = findViewById(R.id.logoText);
                    if (logoText != null)
                        logoText.setVisibility(View.VISIBLE);
                    im = findViewById(R.id.arrow);
                    if (im != null)
                        im.setVisibility(View.VISIBLE);

                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.left_to_right);
                    im.startAnimation(animSlide);
                    im.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            auth();
                        }
                    });
                    animSlide.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if(!checked)
                                mFireBaseAuth.addAuthStateListener(mAuthStateListener);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    @Override
    protected void onPause(){
        super.onPause();
        if(mAuthStateListener != null)
            mFireBaseAuth.removeAuthStateListener(mAuthStateListener);
        detachDatabaseReadListener();
    }
    private void onSignedInInitialize(String email,String username){
        mEmail = email;
        mUsername = username;
        attachDatabaseReadListener();

    }
    private void onSignedOutCleanUp(){
        mEmail = ANONYMOUS;
        mUsername = ANONYMOUS;
        //mMessageAdapter.clear();
        detachDatabaseReadListener();
    }
    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
          //  mProfilePicDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
    private void attachDatabaseReadListener(){

    }

}
