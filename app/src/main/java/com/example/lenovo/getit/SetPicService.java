package com.example.lenovo.getit;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetPicService extends Service {
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
        public SetPicService getService(){
            return SetPicService.this;
        }
    }
    public IBinder onBind(Intent intent){
        return binder;
    }
    public void savePic(String path){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        String user_id = mFirebaseAuth.getCurrentUser().getUid();
        DatabaseReference currenUserdb = mDatabaseRef.child(user_id);
        currenUserdb.child("Profile Picture").setValue(path);

    }
}
