package com.example.lenovo.getit;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentDetails extends AppCompatActivity {

    TextView txtId, txtAmount, txtStatus;
    CheckInternetBroadcast checker;
    Button newFeed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);
        newFeed = findViewById(R.id.buttonNewsFeed);
        txtId = (TextView)findViewById(R.id.txtId);
        txtAmount = (TextView)findViewById(R.id.txtAmount);
        txtStatus = (TextView)findViewById(R.id.txtStatus);

        Intent i = getIntent();

        try{
            JSONObject jsonObject = new JSONObject(i.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response"),i.getStringExtra("PaymentAmount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDetails(JSONObject response, String paymentAmount) {
        try {
            txtId.setText(response.getString("id"));
            txtStatus.setText(response.getString("state"));
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("isPremium").setValue(1);
            txtAmount.setText("$"+paymentAmount);
            newFeed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newFeed.setBackgroundColor(Color.parseColor("#5a0c0c"));
                    Intent intNewsFeed = new Intent(getApplicationContext(),SubMain.class);
                    startActivity(intNewsFeed);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checker);
    }

}
