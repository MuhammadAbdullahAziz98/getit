package com.example.lenovo.getit;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.core.view.View;

public class GetEmailActivity extends AppCompatActivity {
    FirebaseAuth mFirebaseAuth;
    EditText email;
    TextView error;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    CheckInternetBroadcast checker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    Intent i = new Intent(getApplicationContext(),SubMain.class);
                    startActivity(i);
                    finish();
                    //goto main fragments:moiz part
                    //Intent
                }
            }
        };
        setContentView(R.layout.activity_get_email);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);
        email = findViewById(R.id.getEmail);
        error = findViewById(R.id.errorEmail);


    }

    public void check(android.view.View view) {
        if (!(email.getText().toString().contains(".")) || !(email.getText().toString().contains("@")) || email.getText().toString().contains("@.") || email.getText().toString().contains(" ") ||email.getText().toString().equals("")||email.getText().toString().startsWith("@")) {
            error.setVisibility(android.view.View.VISIBLE);
            error.setText("Incorrect email format");
            if(email.getText().toString().contains(" "))
            {
                Toast.makeText(getApplicationContext(),"Check your email for spaces (at the end in case you didnt type any)",Toast.LENGTH_SHORT).show();
            }
        } else {
            error.setVisibility(android.view.View.GONE);

            mFirebaseAuth.fetchSignInMethodsForEmail(email.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            boolean check = task.getResult().getSignInMethods().isEmpty();
                            if (check) {
                                Toast.makeText(getApplicationContext(), "Email Doesn't exist", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(),SignUpEmail.class);
                                i.putExtra("email",email.getText().toString().trim());
                                startActivity(i);

                            } else {
                                Toast.makeText(getApplicationContext(), "Email already exists", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(),LogInActivity.class);
                                i.putExtra("email",email.getText().toString().trim());
                                startActivity(i);
                            }
                        }
                    });
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checker);
    }

}
