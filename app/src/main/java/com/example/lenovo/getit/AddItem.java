package com.example.lenovo.getit;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class AddItem extends AppCompatActivity {

    Item item = new Item();
    CheckInternetBroadcast checker;

    int counting = 0;
    EditText title;
    int lightColor = (Integer.parseInt("8d6464", 16)+0xFF000000);
    int darkColor = (Integer.parseInt("5a0c0c", 16)+0xFF000000);
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checker);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);



        TextView caution = (TextView) findViewById(R.id.textView8);
        caution.setVisibility(View.INVISIBLE);

        title = (EditText) findViewById(R.id.entertitle);
        String countString = 0+"/20";
        TextView showCounter = (TextView) findViewById(R.id.count);
        showCounter.setText(countString);
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView caution = (TextView) findViewById(R.id.textView8);
                //caution.setVisibility(View.INVISIBLE);
                TextView tit = (TextView) findViewById(R.id.abctitle);
                Button button = findViewById(R.id.next);
                if (title.getText().toString().length() < 5)
                {
                    button.setBackgroundColor(lightColor);
                    caution.setText("Atleast 5 charcters required..");
                    caution.setVisibility(View.VISIBLE);
                    caution.setTextColor(Color.RED);
                }
                else if (title.getText().toString().length() > 20)
                {
                    button.setBackgroundColor(lightColor);
                    //button.setBackgroundColor(Color.GREEN);
                    tit.setTextColor(Color.RED);
                    caution.setText("Title should be of 20 characters ..");
                    caution.setVisibility(View.VISIBLE);
                    caution.setTextColor(Color.RED);
                }
                else
                {
                    button.setBackgroundColor(darkColor);
                    tit.setTextColor(darkColor);
                    caution.setVisibility(View.INVISIBLE);
                }
                String countString = counting+"/20";
                counting = s.length();
                TextView showCounter = (TextView) findViewById(R.id.count);
                showCounter.setText(countString);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        Button next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().length() >= 5 && title.getText().toString().length() <= 20)
                {
                    getItemTitle();
                }
            }
        });

        ImageView cross = findViewById(R.id.imageView);
        cross.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onBackPressed();
                return true;
            }
        });
    }


    public void getItemTitle() {
        String name = title.getText().toString();
        item.setProductTitle(name);
        Intent intent = new Intent(this,Catagory.class);
        intent.putExtra("name",name);
        Log.v("name",name);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
