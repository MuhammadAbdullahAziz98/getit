package com.example.lenovo.getit;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class GamesLayout extends AppCompatActivity {

    String name,catagory;
    String des = "",gName = "";
    int counting =0;
    int lightColor = (Integer.parseInt("8d6464", 16)+0xFF000000);
    int darkColor = (Integer.parseInt("5a0c0c", 16)+0xFF000000);
    CheckInternetBroadcast checker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        catagory = intent.getStringExtra("selected");

        setContentView(R.layout.activity_games_layout);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);

        final EditText desc = findViewById(R.id.gameDes);
        final EditText com = findViewById(R.id.gameName);
        //final Button button = findViewById(R.id.nextdetail);
        final Button button = findViewById(R.id.nextdetail);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //des = desc.getText().toString();
                if (des.equals("") || gName.equals(""))
                {
                    Toast.makeText(GamesLayout.this, name+catagory+des, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent1 = new Intent(GamesLayout.this,AddPictures.class);
                    intent1.putExtra("name",name);
                    intent1.putExtra("category",catagory);
                    intent1.putExtra("company",gName);
                    intent1.putExtra("desc",des);
                    startActivity(intent1);
                }
            }
        });


        com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(GamesLayout.this,com);
                popupMenu.getMenuInflater().inflate(R.menu.popupgame,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        gName = (String) item.getTitle();
                        //Toast.makeText(DiscAndCondition.this, gName, Toast.LENGTH_SHORT).show();
                        com.setText(gName);
                        if (des.equals(""))
                        {
                            Toast.makeText(GamesLayout.this, gName+des, Toast.LENGTH_SHORT).show();
                            button.setBackgroundColor(lightColor);
                        }
                        else
                        {
                            button.setBackgroundColor(darkColor);
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView caution = findViewById(R.id.cautionLine);
                //caution.setVisibility(View.INVISIBLE);
                TextView tit = findViewById(R.id.descrip);
                if (desc.getText().toString().length() < 20)
                {
                    button.setBackgroundColor(lightColor);
                    caution.setText("Atleast 5 charcters required..");
                    caution.setVisibility(View.VISIBLE);
                    caution.setTextColor(Color.RED);
                }
                else if (desc.getText().toString().length() > 4096)
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
                    if (gName.equals(""))
                    {
                        button.setBackgroundColor(lightColor);
                    }
                    else
                    {
                        button.setBackgroundColor(darkColor);
                    }
                    tit.setTextColor(darkColor);
                    caution.setVisibility(View.INVISIBLE);
                }
                String countString = counting+"/4096";
                counting = s.length();
                TextView showCounter = (TextView) findViewById(R.id.counter);
                showCounter.setText(countString);
                des = desc.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


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
