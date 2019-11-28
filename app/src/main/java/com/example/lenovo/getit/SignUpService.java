package com.example.lenovo.getit;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class SignUpService extends Service {
    private final IBinder binder = new LocalBinder();
    int REQUEST_CODE = 1;
    FirebaseAuth mFirebaseAuth;
    DatabaseReference mDatabaseRef;
    public void onCreate() {
    }
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_NOT_STICKY;
    }
    public class LocalBinder extends Binder {
        public SignUpService getService(){
            return SignUpService.this;
        }
    }
    public IBinder onBind(Intent intent){
        return binder;
    }
    public void saveProfile(String name) {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        final String nameFinal = name;
        String user_id = mFirebaseAuth.getCurrentUser().getUid();
        DatabaseReference currenUserdb = mDatabaseRef.child(user_id);
        currenUserdb.child("Name").setValue(nameFinal);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(nameFinal).build();

        user.updateProfile(profileUpdates);
        currenUserdb.child("isPremium").setValue(3);
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        currenUserdb.child("device_token").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(),"Token Stored",Toast.LENGTH_SHORT).show();
            }
        });


    }
}
