package com.example.lenovo.getit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_email);
        mFirebaseAuth = FirebaseAuth.getInstance();
        Intent intent= getIntent();
        email = intent.getStringExtra("email");
        passwordEditText = findViewById(R.id.getPassword);
        nameEditText = findViewById(R.id.getUsername);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
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
                    final String nameFinal = name;
                    mFirebaseAuth.createUserWithEmailAndPassword(email,password)

                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        error.setVisibility(android.view.View.GONE);
                                        String user_id = mFirebaseAuth.getCurrentUser().getUid();
                                        DatabaseReference currenUserdb = mDatabaseRef.child(user_id);
                                        currenUserdb.child("Name").setValue(nameFinal);
                                        Toast.makeText(getApplicationContext(),"User registered Successfully!",Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(),SubMain.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
                                    }
                                    else if(!task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(),"An Error Occured While registering the user!",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
                else
                {
                    error.setVisibility(android.view.View.VISIBLE);
                    error.setText("Kindly check if any field is empty");
                }

            }
        });

    }


}
