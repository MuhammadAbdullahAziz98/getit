package com.example.lenovo.getit;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;

public class viewAdFromFeed extends AppCompatActivity {
    String name,category,sub,newOrUsed,company,des,profile,pid;
    ArrayList<String> otherimageURLs;
    int price,quantity;
    String mainImageURL;
    private FirebaseAnalytics mFirebaseAnalytics;
    CheckInternetBroadcast checker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ad_from_feed);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);

        try {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Post");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            Intent intent1 = getIntent();
            name = intent1.getStringExtra("name");          //title of product
            category = intent1.getStringExtra("category");  //category+subCategory
            newOrUsed = intent1.getStringExtra("neworused");//product type
            //company = intent1.getStringExtra("company");    //company
            des = intent1.getStringExtra("desc");           //description
            profile = intent1.getStringExtra("profileID");    //user ID
            otherimageURLs = intent1.getStringArrayListExtra("otherPics");  //array of other pics URL
            mainImageURL = intent1.getStringExtra("imagePath");             //mainPic URL
            price = intent1.getIntExtra("price", 0);             //Price
            pid = intent1.getStringExtra("productID");
        }catch(Exception e){
            Toast.makeText(this, "2",Toast.LENGTH_SHORT).show();
        }


        ///////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////

        //for hiding of category and sub category
        TextView categorybox = findViewById(R.id.categoryandsubviewAd);
        categorybox.setText(category);


        //LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(dpPx(100),dpPx(100));
        //for profile image
        final ImageView profileimage = findViewById(R.id.mainprofileimageViewAd);
        //profileimage.setLayoutParams(layoutParams1);
        Glide.with(getApplicationContext()).load(mainImageURL).into(profileimage);

        //for other images
        LinearLayout linearLayout = findViewById(R.id.otherimagesViewAd);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dpPx(80), dpPx(80));

        for (String s1:otherimageURLs)
        {
            final ImageView other = new ImageView(this);

                Glide.with(getApplicationContext()).load(s1).into(other);
                other.setLayoutParams(layoutParams);
                other.setPadding(dpPx(10),dpPx(10),dpPx(10),dpPx(10));
                linearLayout.addView(other);
        }

        //for back button
        ImageView back2 = findViewById(R.id.back3ViewAd);
        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //for title
        TextView textView = findViewById(R.id.titleofproductViewAd);
        textView.setText(name);

        //for condition
        TextView textView1 = findViewById(R.id.conditionSViewAd);
        textView1.setText(newOrUsed);

        //for company
        TextView textView2 = findViewById(R.id.companySViewAd);
        textView2.setText("");//company);

        //for description
        TextView textView3 = findViewById(R.id.desSViewAd);
        textView3.setText(des);

        //for price
        TextView textView4 = findViewById(R.id.priceSViewAd);
        String s2 = String.valueOf(price);
        Toast.makeText(this, s2, Toast.LENGTH_SHORT).show();
        textView4.setText(s2+" PKR");


        Button viewProfile = (Button) findViewById(R.id.viewprofile);
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent profileIntent = new Intent(this,ProfileActivity.class);
//                profileIntent.putExtra("usreID" , profile);
                Intent intentProfile = new Intent(getApplicationContext(), ProfileActivity.class);
                intentProfile.putExtra("uid",profile);
                startActivity(intentProfile);

                Toast.makeText(viewAdFromFeed.this, profile, Toast.LENGTH_SHORT).show();
//                startActivity(profileIntent);
            }
        });

        final Button AddtoCart = findViewById(R.id.addtocart);
        AddtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddtoCart.setVisibility(View.GONE);
                DBCart dbCart = new DBCart(getApplicationContext());
                boolean flag = dbCart.insert_cart(FirebaseAuth.getInstance().getCurrentUser().getUid(),pid);
                if (flag)
                {

                }
                else
                {
                    Toast.makeText(viewAdFromFeed.this, "Already in Cart !!", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
