package com.example.lenovo.getit;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class Catagory extends AppCompatActivity {

    Item item = new Item();
    String[] categories = {"Mobiles","Laptops","Computers","Processors","Electronics","Cameras","Games"};

    ArrayList<String> subCatagorisOfMobile = new ArrayList<String>();
    ArrayList<String> subCatagorisOfLaptops = new ArrayList<String>();
    ArrayList<String> subCatagorisOfComputers = new ArrayList<String>();
    ArrayList<String> subCatagorisOfProcessors = new ArrayList<String>();
    ArrayList<String> subCatagorisOfElectronics = new ArrayList<String>();
    ArrayList<String> subCatagorisOfCameras = new ArrayList<String>();
    ArrayList<String> subCatagorisOfGames = new ArrayList<String>();
    CheckInternetBroadcast checker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        item.setProductTitle(name);
        addCategories();
        //Toast.makeText(this, item.getName(), Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_catagory);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ListView listView = findViewById(R.id.listofcatagoris);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,categories);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cName = categories[position];
                Intent intent1 = new Intent(Catagory.this,SubCatagory.class);
                intent1.putExtra("selected",cName);
                if (cName.equals("Mobiles"))
                {
                    intent1.putExtra("array",subCatagorisOfMobile);
                }
                else if (cName.equals("Laptops"))
                {
                    intent1.putExtra("array",subCatagorisOfLaptops);
                }
                else if (cName.equals("Computers"))
                {
                    intent1.putExtra("array",subCatagorisOfComputers);
                }
                else if (cName.equals("Processors"))
                {
                    intent1.putExtra("array",subCatagorisOfProcessors);
                }
                else if (cName.equals("Electronics"))
                {
                    intent1.putExtra("array",subCatagorisOfElectronics);
                }
                else if (cName.equals("Cameras"))
                {
                    intent1.putExtra("array",subCatagorisOfCameras);
                }
                else if (cName.equals("Games"))
                {
                    Intent intent2 = new Intent(Catagory.this,GamesLayout.class);
                    intent2.putExtra("selected",cName);
                    String itemName = item.getProductTitle();
                    intent2.putExtra("name",itemName);
                    startActivity(intent2);
                }
                String itemName = item.getProductTitle();
                intent1.putExtra("name",itemName);
                startActivity(intent1);
            }
        });
    }

    public void addCategories()
    {
        //for mobiles
        subCatagorisOfMobile.add("Tablets");
        subCatagorisOfMobile.add("Accessories");
        subCatagorisOfMobile.add("Mobile Phones");

        //for Laptops
        subCatagorisOfLaptops.add("Core 2 Duo");
        subCatagorisOfLaptops.add("Core i3");
        subCatagorisOfLaptops.add("Core i5");
        subCatagorisOfLaptops.add("Core i7");

        //for Computers
        subCatagorisOfComputers.add("RAM");
        subCatagorisOfComputers.add("Mother Board");
        subCatagorisOfComputers.add("Hard Disks");
        subCatagorisOfComputers.add("CD ROM's");

        //for Processors
        subCatagorisOfProcessors.add("Core 2 Duo");
        subCatagorisOfProcessors.add("Core i3");
        subCatagorisOfProcessors.add("Core i5");
        subCatagorisOfProcessors.add("Core i7");

        //for Electronics
        subCatagorisOfElectronics.add("Washing Machines");
        subCatagorisOfElectronics.add("Ceiling Fans");
        subCatagorisOfElectronics.add("Refrigerators");
        subCatagorisOfElectronics.add("AC's");
        subCatagorisOfElectronics.add("Coolers");

        //for Cameras
        subCatagorisOfCameras.add("Camera Bodies");
        subCatagorisOfCameras.add("Lens");
        subCatagorisOfCameras.add("Flashes");
        subCatagorisOfCameras.add("Other Accessories");

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
