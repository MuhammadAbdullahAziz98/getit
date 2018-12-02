package com.example.lenovo.getit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {

    boolean passwordOn;
    ImageButton im;
    EditText passwordEditText;
    String email,pass;
    Button login;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference mDataRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        passwordOn = true;
        passwordEditText = findViewById(R.id.getPasswordLogIn);
        Intent i = getIntent();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDataRef = FirebaseDatabase.getInstance().getReference().child("Users");
        passwordEditText = findViewById(R.id.getPasswordLogIn);
        email = i.getStringExtra("email");
        im = findViewById(R.id.showPassLogIn);
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
        login = findViewById(R.id.LogInviamail);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextView error = findViewById(R.id.errorLogIn);

                pass = passwordEditText.getText().toString().trim();
                if(!TextUtils.isEmpty(pass)){
                    mFirebaseAuth.signInWithEmailAndPassword(email,pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    error.setVisibility(android.view.View.GONE);
                                    checkUserExists();
                                }
                                else if (!task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "An Error Occured While Logging In!", Toast.LENGTH_SHORT).show();
                                        error.setVisibility(android.view.View.VISIBLE);
                                        error.setText("Incorrect Password or email");
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

    private void checkUserExists() {
        final String userId = mFirebaseAuth.getUid();
        mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userId)) {
                    String username = dataSnapshot.child(userId).child("Name").getValue().toString();
                    Intent i = new Intent(getApplicationContext(), SubMain.class);
                    i.putExtra("name",username);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
