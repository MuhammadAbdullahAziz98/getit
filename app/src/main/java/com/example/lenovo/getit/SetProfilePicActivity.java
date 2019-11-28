package com.example.lenovo.getit;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class SetProfilePicActivity extends AppCompatActivity {
    FirebaseDatabase mFirebaseDatabase;

    private FirebaseAuth mFireBaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mProfilePicturesStorageReference;
    boolean isOldPic = true;
    Button signOut, getProfilePic,skipPic,nextScreen;
    ImageView profilePicView;
    Handler handler;
    private ProgressBar mProgressBar;
    String path;
    private static final int RC_PHOTO_PICKER = 2;
    SetPicService service;
    boolean bound = false;
    CheckInternetBroadcast checker;


    ProgressBar pB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_pic);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        Intent i = getIntent();
        String name = i.getStringExtra("name");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mProfilePicturesStorageReference = mFirebaseStorage.getReference().child("profile_pics");
        mFireBaseAuth = FirebaseAuth.getInstance();
        signedIn();
        Intent intentPic = new Intent(this,SetPicService.class);
        bindService(intentPic, connection, Context.BIND_AUTO_CREATE);
        FirebaseUser user = mFireBaseAuth.getCurrentUser();
        String photoUrl = "null";
        Profile p = new Profile(name, photoUrl);
    }

    protected void signedIn() {
        mProgressBar = findViewById(R.id.progressBar1);
        skipPic = findViewById(R.id.skipPic);
        nextScreen = findViewById(R.id.nextScreenPic);
        pB = findViewById(R.id.progressBarForButton);

        skipPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (bound) {

                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        service.savePic("null");
                        Intent intent = new Intent(getApplicationContext(), SubMain.class);
                        startActivity(intent);
                    }
                });
                thread.start();
            }
            }
        });
        signOut = findViewById(R.id.signOut);
        signOut.setBackgroundResource(android.R.drawable.btn_default);
        profilePicView = findViewById(R.id.profilePicImageView);
        getProfilePic = findViewById(R.id.profilePicUpload);
        final Context mContext = this;
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bound) {

                    Thread thread = new Thread(new Runnable() {
                        public void run() {
                            service.savePic("null");
                            Intent intent = new Intent(getApplicationContext(), SubMain.class);
                            startActivity(intent);
                        }
                    });
                    thread.start();
                }
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

                FirebaseUser user = mFireBaseAuth.getCurrentUser();
                mGoogleSignInClient.signOut().addOnCompleteListener((Activity) mContext,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),"Logging Out",Toast.LENGTH_SHORT).show();
                            }
                        });
                mFireBaseAuth.signOut();
                Intent i = new Intent(getApplicationContext(),MainActivity2.class);
                startActivity(i);
                finish();
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
    private int convertDpToPx(int dp){
        return Math.round(dp*(getResources().getDisplayMetrics().xdpi/DisplayMetrics.DENSITY_DEFAULT));
        }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            try {
                skipPic.setVisibility(View.GONE);
                pB.setIndeterminate(true);
                pB.setVisibility(View.VISIBLE);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, convertDpToPx(350), convertDpToPx(300), true);
                profilePicView.setImageBitmap(resized);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "COuldn't get pic", Toast.LENGTH_SHORT).show();
            }
            mProgressBar.setIndeterminate(true);

            final StorageReference photoRef = mProfilePicturesStorageReference.child(selectedImageUri.getLastPathSegment());
            mProgressBar.setVisibility(View.VISIBLE);
            photoRef.putFile(selectedImageUri).addOnProgressListener(this, new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    setPic();
                }
            }).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final Uri downloadUrl = uri;
                            pB.setIndeterminate(false);
                            pB.setVisibility(View.GONE);
                            nextScreen.setVisibility(View.VISIBLE);
                            nextScreen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (bound) {

                                        Thread thread = new Thread(new Runnable() {
                                            public void run() {
                                                service.savePic(downloadUrl.toString());
                                                Intent intent = new Intent(getApplicationContext(), SubMain.class);
                                                startActivity(intent);
                                            }
                                        });
                                        thread.start();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(),"NOT BOUNDED",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

//                            final Profile profile = new Profile(mUsername,mEmail,downloadUrl.toString());
                            //                          mProfilePicDatabaseReference.push().setValue(profile);
//                            downloadPicToDisplay(path);

                        }
                    });
                }
            });
        }
    }

    protected void downloadPicToDisplay(final String photoUrl) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    //Profile p = profile;
                    FutureTarget<File> future = Glide.with(getApplicationContext()).load(photoUrl).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

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
                            mProgressBar.setIndeterminate(false);
                            mProgressBar.setVisibility(View.GONE);
                            Glide.with(profilePicView.getContext())
                                    .load(path).override(convertDpToPx(350),convertDpToPx(300))
                                    .into(profilePicView);

                        }
                    });
                } catch (Exception ex) {
                }
            }
        });
        thread.start();

    }

    protected void setPic() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.signOut);
        getProfilePic.setLayoutParams(params);
        getProfilePic.setText("Change Pic");
        getProfilePic.setBackgroundResource(android.R.drawable.btn_default);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.profilePicUpload);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        profilePicView.setLayoutParams(params);
        mProgressBar.setVisibility(View.VISIBLE);

    }
    private ServiceConnection connection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName className, IBinder service){
            SetPicService.LocalBinder binder = (SetPicService.LocalBinder) service;
            SetProfilePicActivity.this.service = binder.getService();
            bound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName className){
            bound = false;
            Toast.makeText(getApplicationContext(),"Service disconnected",Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checker);
    }

}