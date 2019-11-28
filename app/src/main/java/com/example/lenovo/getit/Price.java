package com.example.lenovo.getit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class Price extends AppCompatActivity {

    private EditText price;
    private EditText quantity;
    private int prices;
    EditText p;

    int lightColor = (Integer.parseInt("8d6464", 16)+0xFF000000);
    int darkColor = (Integer.parseInt("5a0c0c", 16)+0xFF000000);

    String name,category,sub,newOrUsed,company,des,profile;
    ArrayList<String> imagePath;
    CheckInternetBroadcast checker;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);

        /////////////////////////////////////////////////////////////////////
        Intent intent1 = getIntent();

        name = intent1.getStringExtra("name");
        category = intent1.getStringExtra("category");
        sub = intent1.getStringExtra("sub");
        newOrUsed = intent1.getStringExtra("neworused");
        company = intent1.getStringExtra("company");
        des = intent1.getStringExtra("desc");
        profile = intent1.getStringExtra("profile");
        imagePath = intent1.getStringArrayListExtra("imagePath");
        /////////////////////////////////////////////////////////////////////


        price = findViewById(R.id.price);
        price.setInputType(InputType.TYPE_CLASS_NUMBER);
        price.setKeyListener(DigitsKeyListener.getInstance());

        p = findViewById(R.id.price);
        final Button button = findViewById(R.id.priceBottom);

        p.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0)
                {
                    if (!p.getText().toString().equals(""))
                    {
                        button.setBackgroundColor(darkColor);
                    }
                }
                else if (s.length() == 0)
                {
                    button.setBackgroundColor(lightColor);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFinal();
            }
        });

        ImageView back2 = findViewById(R.id.back2);
        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void callFinal() {

        if (p.getText().toString().equals(""))
        {
            Toast.makeText(this, "Please enter a price!", Toast.LENGTH_SHORT).show();
        }
        else if (Integer.parseInt(p.getText().toString()) == 0)
        {
            Toast.makeText(this, "Enter a price > 0", Toast.LENGTH_SHORT).show();
        }
        else
        {
            prices = Integer.parseInt(p.getText().toString());
            ////////////////////////////////////////////////////////////////////////////
            Intent intent = new Intent(this,ShowAd.class);

            intent.putStringArrayListExtra("imagePath" , imagePath);
            intent.putExtra("name",name);
            intent.putExtra("category",category);
            intent.putExtra("sub",sub);
            intent.putExtra("neworused",newOrUsed);
            intent.putExtra("company",company);
            intent.putExtra("desc",des);
            intent.putExtra("profile",profile);
            intent.putExtra("price",prices);
            startActivity(intent);
            ////////////////////////////////////////////////////////////////////////////
        }




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
