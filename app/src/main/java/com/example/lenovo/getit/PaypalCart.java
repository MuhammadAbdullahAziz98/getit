package com.example.lenovo.getit;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class PaypalCart extends AppCompatActivity {
    CheckInternetBroadcast checker;

    public static final int PAYPAL_REQUEST_CODE = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)   // using sandbox for testing
            .clientId(Config.PAYPAL_CLIENT_ID);
    Button btnPayNow;
    String amount ="";

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,PayPalService.class));
        super.onDestroy();
        unregisterReceiver(checker);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal_cart);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("isPremium").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Intent intent = new Intent(getApplicationContext(),PayPalService.class);
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
                    startService(intent);
                    try {
                        btnPayNow = (Button) findViewById(R.id.btnPayNowCart);

                        btnPayNow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                processPayment();
                            }
                        });
                    }catch(NullPointerException e){
                        Toast.makeText(getApplicationContext(),"NULL",Toast.LENGTH_SHORT);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //paypal service:
    }

    private void processPayment() {
        try {
            Intent intPay = getIntent();

            amount = intPay.getStringExtra("payment");
            PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "USD", "Payment", PayPalPayment.PAYMENT_INTENT_SALE);
            Intent i = new Intent(this, PaymentActivity.class);
            i.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            i.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
            startActivityForResult(i, PAYPAL_REQUEST_CODE);
        }catch(NullPointerException e){
            Toast.makeText(getApplicationContext(),"NULL PAYMENT",Toast.LENGTH_LONG).show();
        }
    }
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        //super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == PAYPAL_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation!= null){
                    try{
                        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Payment");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                        DBCart db = new DBCart(getApplicationContext());
                        String s = db.delete_entries(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        startActivity(new Intent(this,PaymentDetails.class)
                                .putExtra("PaymentDetails",paymentDetails)
                                .putExtra("PaymentAmount",amount)


                        );
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else if(resultCode == RESULT_CANCELED){
                    Toast.makeText(this,"Cancelled",Toast.LENGTH_LONG).show();
                }
            }
            else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
                Toast.makeText(this,"Invalid details provided",Toast.LENGTH_LONG).show();

        }
    }


}
