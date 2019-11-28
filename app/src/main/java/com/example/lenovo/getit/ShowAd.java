package com.example.lenovo.getit;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

public class ShowAd extends AppCompatActivity {

    int lightColor = (Integer.parseInt("8d6464", 16)+0xFF000000);
    int darkColor = (Integer.parseInt("5a0c0c", 16)+0xFF000000);

    Item item;
    String name,category,sub,newOrUsed,company,des,profile;
    String id,profilepic,username;
    int ispre;
    ArrayList<String> imagePath;
    int price, quantity;
    Uri imageUri,OtherUri;
    FirebaseDatabase mDatabase;
    FirebaseStorage mFirebaseStorage;
    FirebaseAuth mAuth;
    StorageReference mMainPic, otherPics;
    DatabaseReference dbRef;
    CheckInternetBroadcast checker;

    boolean bound;
    //String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ad);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);

        //onBackPressed();
        /////////////////////////////////////////////////////////////////////
        //firebase initialization start
        Intent intent1 = getIntent();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user  = mAuth.getCurrentUser();
        id = user.getUid();

        //////////////////////////////////////////////////

        FirebaseDatabase.getInstance().getReference("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = (String) dataSnapshot.child("Name").getValue();
                try{
                    profilepic = (String) dataSnapshot.child("Profile Picture").getValue();
                }
                catch (NullPointerException ex)
                {
                    profilepic = null;
                    ex.printStackTrace();
                }
                try{
                    ispre = dataSnapshot.child("isPremium").getValue(Integer.class);
                }
                catch (NullPointerException ex)
                {
                    ispre = 0;
                    ex.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //////////////////////////////////////////////////

        mDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        dbRef = mDatabase.getReference().child("posts");
        mMainPic = mFirebaseStorage.getReference().child("product_main_pic");
        otherPics = mFirebaseStorage.getReference().child("product_pics");

        //firebase initialization done

        name = intent1.getStringExtra("name");
        category = intent1.getStringExtra("category");
        sub = intent1.getStringExtra("sub");
        newOrUsed = intent1.getStringExtra("neworused");
        company = intent1.getStringExtra("company");
        des = intent1.getStringExtra("desc");
        profile = intent1.getStringExtra("profile");
        imagePath = intent1.getStringArrayListExtra("imagePath");
        price = intent1.getIntExtra("price",0);

        category = category + " > " +sub;

        /////////////////////////////////////////////////////////////////////

        //for hiding of category and sub category
        TextView categorybox = findViewById(R.id.categoryandsub);
        String s = category+" > "+sub;
        categorybox.setText(s);

        //for profile image
        ImageView profileimage = findViewById(R.id.mainprofileimage);
        imageUri = Uri.parse(profile);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap,dpPx(350),dpPx(300),true);
            profileimage.setImageBitmap(resized);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //for other images
        LinearLayout linearLayout = findViewById(R.id.otherimages);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dpPx(60), dpPx(60));

        for (String s1:imagePath)
        {
            final ImageView other = new ImageView(this);
            OtherUri = Uri.parse(s1);

            try {
                Bitmap others = MediaStore.Images.Media.getBitmap(this.getContentResolver(),OtherUri);
                Bitmap resized1 = Bitmap.createScaledBitmap(others,dpPx(350),dpPx(300),true);
                other.setImageBitmap(resized1);
                other.setLayoutParams(layoutParams);
                other.setPadding(dpPx(10),dpPx(10),dpPx(10),dpPx(10));
                linearLayout.addView(other);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //for back button
        ImageView back2 = findViewById(R.id.back3);
        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //for title
        TextView textView = findViewById(R.id.titleofproduct);
        textView.setText(name);

        //for condition
        TextView textView1 = findViewById(R.id.conditionS);
        textView1.setText(newOrUsed);

        //for company
        TextView textView2 = findViewById(R.id.companyS);
        textView2.setText(company);

        //for description
        TextView textView3 = findViewById(R.id.desS);
        textView3.setText(des);

        //for price
        TextView textView4 = findViewById(R.id.priceS);
        String s2 = String.valueOf(price);
        Toast.makeText(this, s2, Toast.LENGTH_SHORT).show();
        textView4.setText(s2+" PKR");

        ////////////////////////////////////////////////////////////////////////////////////////////////
        Button postItem = findViewById(R.id.post);
        postItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });

    }
    int i =0;
    boolean isFailure = false;
    Uri downloadUrl;
    ArrayList<String> otherUrls = new ArrayList<>();
    private void saveItem() {
        final ArrayList<Uri> otherpPicsURI = new ArrayList<Uri>();
        for(String c:imagePath)
        {
            otherpPicsURI.add(Uri.parse(c));
        }

        final StorageReference photoRef = mMainPic.child(imageUri.getLastPathSegment());
        photoRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadUrl = uri;
                        Toast.makeText(getApplicationContext(),downloadUrl.toString(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        final ProgressBar p = findViewById(R.id.addPBar);
        final int ii =0;
        for (Uri u: otherpPicsURI)
        {
            p.setVisibility(View.VISIBLE);
            p.bringToFront();
            p.setIndeterminate(true);
            final StorageReference otherphotoRef = otherPics.child(u.getLastPathSegment());

            otherphotoRef.putFile(u).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(ShowAd.this, "First on click listner !", Toast.LENGTH_SHORT).show();
                    otherphotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            otherUrls.add(uri.toString());
                            Toast.makeText(getApplicationContext(),"Second on click listner !",Toast.LENGTH_SHORT).show();
                            Log.v("last" , String.valueOf(otherpPicsURI.lastIndexOf(otherpPicsURI)));
                            if (i == otherpPicsURI.size()-1)
                            {
                                try{
                                    if (!isFailure) {

                                        Item item = new Item(id, username, profilepic, ispre, name, des, category, newOrUsed, price, quantity, downloadUrl.toString(), otherUrls);
                                        dbRef.push().setValue(item);
                                        Toast.makeText(ShowAd.this, "pictures" + i, Toast.LENGTH_SHORT).show();
                                    }
                                }catch (NullPointerException e){

                                }
                            }
                            i++;
                        }

                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    isFailure = true;
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (i == otherpPicsURI.size()-1)
                    {
                        p.setVisibility(View.GONE);
                        p.setIndeterminate(false);
                        Intent intent = new Intent(getApplicationContext(),SubMain.class);
                        startActivity(intent);

                    }
                }
            });

        }
    }

    public static int dpPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checker);
    }

}

