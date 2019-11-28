package com.example.lenovo.getit;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SubCatagory extends AppCompatActivity {

    String name,category,subCategory;
    CheckInternetBroadcast checker;

    ArrayList<String> sub = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        category = intent.getStringExtra("selected");
        sub = intent.getStringArrayListExtra("array");

        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, category, Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_sub_catagory);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);

        TextView heading = findViewById(R.id.selectedCategory);
        heading.setText(category);
        //heading.setPadding();

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        ListView listView = findViewById(R.id.listofsubcatagoris);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,sub);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cName = sub.get(position);
                Intent intent1 = new Intent(SubCatagory.this,DiscAndCondition.class);
                intent1.putExtra("name",name);
                intent1.putExtra("selected",category);
                intent1.putExtra("subSelect",cName);
                Log.v("name",cName);
                startActivity(intent1);
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
