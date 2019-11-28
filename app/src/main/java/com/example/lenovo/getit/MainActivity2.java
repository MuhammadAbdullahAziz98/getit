package com.example.lenovo.getit;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity2 extends BaseActivity implements
        View.OnClickListener {
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private static final int RC_SIGN_IN = 9001;
    private DatabaseReference mDatabaseRef;

    private GoogleSignInClient mGoogleSignInClient;
    SignUpService service;
    boolean bound = false;
    CheckInternetBroadcast checker;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checker);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);

        if(mAuth.getCurrentUser()!=null)
        {
            Intent intentLogIn = new Intent(this,SubMain.class);
            intentLogIn.putExtra("name",mAuth.getCurrentUser().getDisplayName());
            startActivity(intentLogIn);
            finish();
        }
        else{
            Intent i = new Intent(this, SignUpService.class);

            bindService(i, connection, Context.BIND_AUTO_CREATE);
        }
        setContentView(R.layout.activity_main2);
        animateLogo();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        findViewById(R.id.signInButton).setOnClickListener(this);
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        try {
            if (currentUser==null) {
                Toast.makeText(getApplicationContext(), "User Logout SuccessFul", Toast.LENGTH_SHORT).show();
            }
        }
        catch (NullPointerException e){
            Toast.makeText(getApplicationContext(), "WELCOME!", Toast.LENGTH_SHORT).show();
        }
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        if(bound) {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                final FirebaseUser user = mAuth.getCurrentUser();
                                final String name = user.getDisplayName();
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        if (snapshot.hasChild(user.getUid())) {
                                            Intent i = new Intent(getApplicationContext(), SubMain.class);
                                            String uidUser = mAuth.getCurrentUser().getUid();
                                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                            mDatabaseRef.child(uidUser).child("device_token").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getApplicationContext(),"Token Stored",Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            i.putExtra("name", name);
                                            startActivity(i);
                                            finish();

                                        }
                                        else{
                                            Intent intent1 = new Intent(getApplicationContext(), SetProfilePicActivity.class);
                                            service.saveProfile(user.getDisplayName());
                                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent1);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                // If sign in fails, display a message to the user.
                                Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            }

                            // [START_EXCLUDE]
                            hideProgressDialog();
                            // [END_EXCLUDE]
                        }
                    });
        }
        else{
            Toast.makeText(getApplicationContext(),"Failed to start service", Toast.LENGTH_SHORT).show();
        }
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    public void gotoEmail(View view) {
        Intent intent = new Intent(getApplicationContext(),GetEmailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signInButton) {
            signIn();
        }
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
                    logoText = findViewById(R.id.logoText);
                    if (logoText != null)
                        logoText.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    private ServiceConnection connection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName className, IBinder service){
            SignUpService.LocalBinder binder = (SignUpService.LocalBinder) service;
            MainActivity2.this.service = binder.getService();
            bound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName className){
            bound = false;
            Toast.makeText(getApplicationContext(),"Service disconnected",Toast.LENGTH_SHORT).show();
        }
    };

}
