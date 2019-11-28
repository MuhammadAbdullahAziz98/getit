package com.example.lenovo.getit;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangeName extends AppCompatActivity {
    Button change;
    TextView name;
    String oldName;
    CheckInternetBroadcast checker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);

        change = findViewById(R.id.changeName);
        name = findViewById(R.id.changeNameTextBox);
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Name")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        oldName = dataSnapshot.getValue().toString();
                        name.setText(dataSnapshot.getValue().toString());
                        change.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(oldName.equals(name.getText().toString())){
                                    Toast.makeText(getApplicationContext(),"Name is same as before",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Name").setValue(name.getText().toString());
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString()).build();

                                    user.updateProfile(profileUpdates);

                                    Toast.makeText(getApplicationContext(),"Name Updated Successfully",Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getApplicationContext(),SubMain.class);
                                    startActivity(i);
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checker);
    }

}
