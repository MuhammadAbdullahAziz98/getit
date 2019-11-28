package com.example.lenovo.getit;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class DiscAndCondition extends AppCompatActivity {

    String name,catagory,subCatagory;
    String des = "",newOrUsed = "",company = "";
    boolean b1Select = false,b2Select = false;
    CheckInternetBroadcast checker;

    int counting =0;
    int lightColor = (Integer.parseInt("8d6464", 16)+0xFF000000);
    int darkColor = (Integer.parseInt("5a0c0c", 16)+0xFF000000);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        catagory = intent.getStringExtra("selected");
        subCatagory = intent.getStringExtra("subSelect");

        //Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, catagory, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, subCatagory, Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_disc_and_condition);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);

        final GradientDrawable gd1 = new GradientDrawable();
        gd1.setColor(lightColor); // Changes this drawbale to use a single color instead of a gradient
        gd1.setCornerRadius(5);
        gd1.setStroke(4, darkColor );

        final EditText desc = findViewById(R.id.desDetail);
        final Button b1 = findViewById(R.id.newButton);
        final Button b2 = findViewById(R.id.usedButton);
        final Button button = findViewById(R.id.nextdetail);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //des = desc.getText().toString();
                if (newOrUsed.equals("") || des.equals("") || company.equals("") || des.length() < 20)
                {
                    Toast.makeText(DiscAndCondition.this, name+catagory+subCatagory+company +des+newOrUsed, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Log.v("company",company);
                    Log.v("company",des);
                    Intent intent1 = new Intent(DiscAndCondition.this,AddPictures.class);
                    intent1.putExtra("name",name);
                    intent1.putExtra("category",catagory);
                    intent1.putExtra("sub",subCatagory);
                    intent1.putExtra("neworused",newOrUsed);
                    intent1.putExtra("company",company);
                    intent1.putExtra("desc",des);
                    startActivity(intent1);
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b2Select)
                {
                    b2.setTextColor(Color.BLACK);
                    b2.setBackgroundColor(Color.WHITE);
                    newOrUsed = "New";
                    b1.setBackground(gd1);
                    b1.setTextColor(Color.WHITE);
                    b1Select = true;
                    b2Select = false;
                    if (newOrUsed.equals("") || company.equals(""))
                    {
                        button.setBackgroundColor(lightColor);
                    }
                    else
                    {
                        button.setBackgroundColor(darkColor);
                    }
                }
                else
                {
                    newOrUsed = "New";
                    b1.setBackground(gd1);
                    b1.setTextColor(Color.WHITE);
                    b1Select = true;
                    if (newOrUsed.equals("") || company.equals("") || des.equals(""))
                    {
                        button.setBackgroundColor(lightColor);
                    }
                    else
                    {
                        button.setBackgroundColor(darkColor);
                    }
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b1Select)
                {
                    b1.setTextColor(Color.BLACK);
                    b1.setBackgroundColor(Color.WHITE);
                    newOrUsed = "Used";
                    b2.setBackground(gd1);
                    b2.setTextColor(Color.WHITE);
                    b2Select = true;
                    b1Select = false;
                    if (newOrUsed.equals("") || company.equals("")|| des.equals(""))
                    {
                        button.setBackgroundColor(lightColor);
                    }
                    else
                    {
                        button.setBackgroundColor(darkColor);
                    }
                }
                else
                {
                    newOrUsed = "Used";
                    b2.setBackground(gd1);
                    b2.setTextColor(Color.WHITE);
                    b2Select = true;
                    if (newOrUsed.equals("") || company.equals("")|| des.equals(""))
                    {
                        button.setBackgroundColor(lightColor);
                    }
                    else
                    {
                        button.setBackgroundColor(darkColor);
                    }
                }
            }
        });

        final EditText com = findViewById(R.id.company);
        com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(DiscAndCondition.this,com);
                popupMenu.getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        company = (String) item.getTitle();
                        //Toast.makeText(DiscAndCondition.this, company, Toast.LENGTH_SHORT).show();
                        com.setText(company);
                        if (newOrUsed.equals("") || company.equals("")|| des.equals(""))
                        {
                            Toast.makeText(DiscAndCondition.this, newOrUsed+company+des, Toast.LENGTH_SHORT).show();
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
                TextView caution = findViewById(R.id.caution);
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
                    if (newOrUsed.equals("") || company.equals(""))
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
                TextView showCounter = (TextView) findViewById(R.id.counting);
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
