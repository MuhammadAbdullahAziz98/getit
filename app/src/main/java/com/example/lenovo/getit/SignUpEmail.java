package com.example.lenovo.getit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.view.View;

import org.w3c.dom.Text;

public class SignUpEmail extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private EditText passwordEditText,nameEditText;
    private DatabaseReference mDatabaseRef;
    String password,email,name;
    boolean passwordOn = true;
    ImageButton im;
    SignUpService service;
    boolean bound = false;
    CheckInternetBroadcast checker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_email);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);

        mFirebaseAuth = FirebaseAuth.getInstance();
        Intent intent= getIntent();
        email = intent.getStringExtra("email");
        passwordEditText = findViewById(R.id.getPassword);
        nameEditText = findViewById(R.id.getUsername);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        Intent i = new Intent(this,SignUpService.class);
        bindService(i,connection, Context.BIND_AUTO_CREATE);
        im = findViewById(R.id.showPass);
        im.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                if(passwordOn) {
                    im.setImageResource(R.drawable.hidpassword);
                    passwordOn = false;
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                    passwordEditText.setSelection(passwordEditText.getText().length());
                }
                else{
                    im.setImageResource(R.drawable.showpassword);
                    passwordOn = true;
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordEditText.setSelection(passwordEditText.getText().length());
                }

            }
        });
        final Button signUp = findViewById(R.id.signUpviamail);
        signUp.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                password = passwordEditText.getText().toString();
                name = nameEditText.getText().toString();
                final TextView error = findViewById(R.id.errorSignUp);
                view.setBackgroundColor(Color.parseColor("#5a0c0c"));
                signUp.setTextColor(Color.parseColor("#FFFFFF"));
                if(!TextUtils.isEmpty(name) && ! TextUtils.isEmpty(password)){
                    if(bound) {
                        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            service.saveProfile(name);
                                            Toast.makeText(getApplicationContext(), "User registered Successfully!", Toast.LENGTH_SHORT).show();
                                            error.setVisibility(android.view.View.INVISIBLE);

                                            Intent intent1 = new Intent(getApplicationContext(), SetProfilePicActivity.class);
                                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent1);

                                        } else if (!task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "An Error Occured While registering the user!", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Failed to start service", Toast.LENGTH_SHORT).show();
                    }


                }
                else
                {
                    error.setVisibility(android.view.View.VISIBLE);
                    error.setText("Kindly check if any field is empty");
                }

            }
        });

    }
    private ServiceConnection connection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName className, IBinder service){
            SignUpService.LocalBinder binder = (SignUpService.LocalBinder) service;
            SignUpEmail.this.service = binder.getService();
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
