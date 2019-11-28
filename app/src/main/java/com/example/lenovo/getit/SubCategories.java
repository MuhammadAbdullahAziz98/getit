package com.example.lenovo.getit;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class SubCategories extends AppCompatActivity {
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList;
    private ListView listView;
    private String category;
    CheckInternetBroadcast checker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_categories);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);

        try {
            Intent i = getIntent();
            category = i.getStringExtra("category");
        }catch(Exception e){
            category = null;
        }

        boolean flag = true;
        arrayList = new ArrayList<>();
        arrayList.clear();
        if(category.equals("mobiles")) {
            arrayList.add("Mobiles");
            arrayList.add("Tablets");
            arrayList.add("Accessories");
        }
        else if(category.equals("laptops")) {
            arrayList.add("Core 2 Duo Laptops");
            arrayList.add("Core i3 Laptops");
            arrayList.add("Core i5 Laptops");
            arrayList.add("Core i7 Laptops");
            arrayList.add("Accessories");
        }
        else if(category.equals("computers")) {
            arrayList.add("Computers");
            arrayList.add("Ram");
            arrayList.add("Motherboard");
            arrayList.add("Hard Disks");
            arrayList.add("CD Roms");
        }
        else if(category.equals("processors")) {
            arrayList.add("Core 2 Duo Processor");
            arrayList.add("Core i3 Processors");
            arrayList.add("Core i5 Processors");
            arrayList.add("Core i7 Processors");
        }
        else if(category.equals("electronics")) {
            arrayList.add("Washing Machines");
            arrayList.add("Ceiling Fans");
            arrayList.add("Refrigerators");
            arrayList.add("Air Conditioners");
            arrayList.add("Coolers");
        }
        else if(category.equals("miscellaneous")){
            arrayList.add("Miscellaneous");
        }
        else if(category.equals("cameras")) {
            arrayList.add("Camera Bodies");
            arrayList.add("Camera Lens");
            arrayList.add("Flashes");
            arrayList.add("Other Accessories");
        }
        else if(category.equals("games")) {
            arrayList.add("Xbox");
            arrayList.add("Play Station");
            arrayList.add("Video Games CDs");
            arrayList.add("Computer Games CDs");
        }
        else{
            flag = false;
        }

        if(flag) {
            listView = (ListView) findViewById(R.id.list_view);

            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HomeFragment.flag = true;
                    String s = category + " > " + arrayList.get(position);
                    Intent i = new Intent(SubCategories.this, SubMain.class);
                    i.putExtra("search", s);
                    startActivity(i);
                }
            });
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checker);
    }

}
