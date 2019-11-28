package com.example.lenovo.getit;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

public class SubMain extends AppCompatActivity {
    FirebaseAuth mFirebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    HomeFragment homeFragment;
    ShopFragment shopFragment;
    CategoriesFragment categoriesFragment;
    AccountFragment accountFragment;
    int fragmentActive;
    private FirebaseAnalytics mFirebaseAnalytics;
    CheckInternetBroadcast checker;

    private BottomNavigationView main_nav;
    private FrameLayout main_frame;
    private BottomNavigationView.OnNavigationItemSelectedListener navListener;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //mContent = getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");
        fragmentActive = (int) savedInstanceState.getSerializable("fragmentActive");
        switch (fragmentActive){
            case 1:
                setFragment(homeFragment);
                break;
            case 2:
                setFragment(shopFragment);
                break;
            case 3:
                setFragment(categoriesFragment);
                break;
            case 4:
                setFragment(accountFragment);
                break;
            default:
                setFragment(homeFragment);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("fragmentActive", fragmentActive);
        //getSupportFragmentManager().putFragment(savedInstanceState, "myFragmentName", mContent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_main);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);

        homeFragment = new HomeFragment();
        shopFragment = new ShopFragment();
        categoriesFragment = new CategoriesFragment();
        accountFragment = new AccountFragment();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bun = new Bundle();
        bun.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Profile");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bun);
        String search = "";
        try{
            Intent i = getIntent();
            search = i.getStringExtra("search");
            if(!search.equals("")) {
                Bundle bundle = new Bundle();
                bundle.putString("search", search);
                homeFragment.setArguments(bundle);
            }
        }catch(Exception e){}
        mFirebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent i = getIntent();
        String name = i.getStringExtra("name");
        if(name != null && name != ""){
            Toast.makeText(this,"Welcome " + name,Toast.LENGTH_SHORT).show();
        }
        main_nav = (BottomNavigationView) findViewById(R.id.main_nav);
        main_frame = (FrameLayout) findViewById(R.id.main_frame);


        fragmentActive = 1;
        setFragment(homeFragment);

        navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch(item.getItemId()){
                    case R.id.nav_home:
                        fragmentActive = 1;
                        setFragment(homeFragment);
                        return true;
                    case R.id.nav_shop:
                        fragmentActive = 2;
                        setFragment(shopFragment);
                        return true;
                    case R.id.nav_categories:
                        fragmentActive = 3;
                        setFragment(categoriesFragment);
                        return true;
                    case R.id.nav_account:
                        fragmentActive = 4;
                        setFragment(accountFragment);
                        return true;
                    default:
                        return false;
                }
            }
        };

        main_nav.setOnNavigationItemSelectedListener(navListener);

    }
    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                mGoogleSignInClient.signOut().addOnCompleteListener(this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),"Logging Out",Toast.LENGTH_SHORT).show();
                            }
                        });
                mFirebaseAuth.signOut();
                Intent i = new Intent(getApplicationContext(),MainActivity2.class);
                startActivity(i);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checker);
    }

}
