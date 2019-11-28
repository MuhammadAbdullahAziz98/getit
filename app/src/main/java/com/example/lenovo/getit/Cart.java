package com.example.lenovo.getit;

import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity implements Serializable {

    LinearLayout MainUI;
    LinearLayout Top;
    LinearLayout Below;
    static public CheckBox checkBox;
    static TextView t5;
    static ListView listView;
    MyAdapter adapter;
    ArrayList<String> name;
    static ArrayList<String> price;
    ArrayList<String> cat,pictures;
    public static ArrayList<Boolean> flags;
    static public int totalprice;
    CheckInternetBroadcast checker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        checker = new CheckInternetBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(checker, filter);

        MainUI = (LinearLayout) findViewById(R.id.Main);
        Top = (LinearLayout) findViewById(R.id.Top);
        Below = (LinearLayout) findViewById(R.id.Below);
        listView = (ListView) findViewById(R.id.listview);
        name = new ArrayList<>();
        price = new ArrayList<>();
        cat = new ArrayList<>();
        flags = new ArrayList<>();
        pictures = new ArrayList<>();
        totalprice = 0;
        createUI();
        addnames();
        //        stService();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Toast.makeText(this, "Save!", Toast.LENGTH_SHORT).show();
        outState.putInt("TotalPrice",totalprice);
        outState.putStringArrayList("name",name);
        outState.putStringArrayList("cat",cat);
        outState.putStringArrayList("price",price);
        name.clear();
        cat.clear();
        price.clear();
        totalprice = 0;
        boolean[] flag = new boolean[flags.size()];
        for (int i=0; i<flags.size(); i++)
        {
            flag[i] = flags.get(i);
        }
        outState.putBooleanArray("Flags",flag);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Toast.makeText(this, "Restore!", Toast.LENGTH_SHORT).show();
        totalprice = savedInstanceState.getInt("TotalPrice");
        name.clear();
        cat.clear();
        price.clear();

        name = savedInstanceState.getStringArrayList("name");
        cat = savedInstanceState.getStringArrayList("cat");
        price = savedInstanceState.getStringArrayList("price");

        flags.clear();
        boolean[] flag = (boolean[]) savedInstanceState.getBooleanArray("Flags");
        for (int i=0; i<flag.length; i++)
        {
            flags.add(flag[i]);

        }
        adapter = new MyAdapter(getApplicationContext(),name,cat,price,flags,pictures);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        t5.setText(Integer.toString(totalprice));
    }

    public void addnames()
    {
        DBCart db = new DBCart(getApplicationContext());
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Cursor cursor = db.get_products(uid);
        if(cursor.getCount()>0){
          while(cursor.moveToNext()){
              FirebaseDatabase.getInstance().getReference().child("posts").child(cursor.getString(cursor.getColumnIndex("pid"))).addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      try {
                          name.add(dataSnapshot.child("productTitle").getValue().toString());
                          price.add(dataSnapshot.child("productPrice").getValue().toString());
                          cat.add(dataSnapshot.child("productCategory").getValue().toString());
                          pictures.add(dataSnapshot.child("productPic").getValue().toString());
                          flags.add(false);
                      } catch (NullPointerException e) {
                          Toast.makeText(getApplicationContext(), "NULL PRODUCT", Toast.LENGTH_SHORT).show();
                      }
                      if(name.size()>0 && price.size()>0&&cat.size()>0&&pictures.size()>0){
                          adapter = new MyAdapter(getApplicationContext(),name,cat,price,flags,pictures);
                          adapter.notifyDataSetChanged();
                          listView.setAdapter(adapter);
                          int am = totalprice;
                          //Toast.makeText(this, Integer.toString(price.size()), Toast.LENGTH_SHORT).show();
                          for (int i = 0; i < price.size(); i++) {
                              //TextView t = MainActivity.listView.getChildAt(i).findViewById(R.id.drs);
                              int a = Integer.parseInt(price.get(i));

                              am = am + a;
                          }
                          totalprice = am;
                      }
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {

                  }
              });

          }
        }
    }

    public void createUI() {
        TextView t1 = new TextView(this);
        t1.setText("Preferred Delivery Option");
        t1.setTextSize(17);
        Top.addView(t1);

        TextView t2 = new TextView(this);
        t2.setTextSize(17);
        t2.setText("Lahore,Pakistan");
        t2.setTextColor(Color.BLACK);
        t2.setPadding(30, 15, 0, 0);
        Top.addView(t2);


        LinearLayout l1 = new LinearLayout(this);
        l1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        checkBox = findViewById(R.id.cb);
        checkBox.setText("All");

        LinearLayout l2 = new LinearLayout(this);
        l2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView t4 = findViewById(R.id.str);
        t4.setText("Total Price: Rs.");
        t4.setTextColor(Color.RED);
        t4.setTextSize(16);

        t5 = findViewById(R.id.rs);
        t5.setText("0");
        t5.setTextColor(Color.RED);
        t5.setTextSize(16);
        ImageView imageButton = findViewById(R.id.paypal);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),t5.getText().toString(),Toast.LENGTH_LONG).show();
                Intent intPay = new Intent(getApplicationContext(),PaypalCart.class);
                intPay.putExtra("payment",t5.getText().toString());
                startActivity(intPay);
            }
        });

        //Below.setBackground(Color.WHITE);

        TextView t6 = new TextView(this);
        t6.setText("There are no Items selected");
        t6.setTextColor(Color.BLACK);
        t6.setTextSize(20);
        checkBox.setVisibility(View.GONE);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox c1;
                CheckBox c2;
                for (int i=0; i<name.size();i++)
                {
                    if (checkBox.isChecked())
                    {
                        flags.set(i,true);
                    }
                    else
                    {
                        flags.set(i,false);
                    }
                }
                if (checkBox.isChecked())
                {
//                    t5.refreshDrawableState();
                    if (!t5.getText().toString().equals("0"))
                    {
                        /*int am = totalprice;
                        //Toast.makeText(this, Integer.toString(price.size()), Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < price.size(); i++) {
                            //TextView t = MainActivity.listView.getChildAt(i).findViewById(R.id.drs);
                            int a = Integer.parseInt(price.get(i));

                            am = am + a;
                        }
                        totalprice = am;*/
                        int a = Integer.parseInt(t5.getText().toString());
                        totalprice = totalprice + a;
                        t5.setText(Integer.toString(totalprice));
                    }
                    else{
/*                        int am = totalprice;
                        //Toast.makeText(this, Integer.toString(price.size()), Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < price.size(); i++) {
                            //TextView t = MainActivity.listView.getChildAt(i).findViewById(R.id.drs);
                            int a = Integer.parseInt(price.get(i));

                            am = am + a;
                        }
                        totalprice = am;*/
                        t5.setText(Integer.toString(totalprice));
                    }

                }
                else
                {
                    totalprice = 0;
                    t5.setText(Integer.toString(totalprice));
                }
                totalprice =0;
                //              stService();
                int am = totalprice;
                //Toast.makeText(this, Integer.toString(price.size()), Toast.LENGTH_SHORT).show();
                for (int i = 0; i < price.size(); i++) {
                    //TextView t = MainActivity.listView.getChildAt(i).findViewById(R.id.drs);
                    int a = Integer.parseInt(price.get(i));

                    am = am + a;
                }
                totalprice = am;
                adapter.notifyDataSetChanged();
            }
        });


    }

    int dpp(int dp) {
        return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public class MyAdapter extends ArrayAdapter<String>
    {
        Context c;
        ArrayList<String> fnames;
        ArrayList<String> fcat;
        ArrayList<String> fprices;
        ArrayList<Boolean> fl;
        ArrayList<String> pics;
        public MyAdapter(Context context, ArrayList<String> names, ArrayList<String> cates, ArrayList<String> prices, ArrayList<Boolean> f,ArrayList<String>p) {
            super(context, R.layout.inflator,R.id.dname,price);
            this.c = context;
            this.fnames = names;
            this.fcat = cates;
            this.fprices = prices;
            this.fl = f;
            pics = p;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.inflator, parent, false);
            final CheckBox c1 = (CheckBox) convertView.findViewById(R.id.checkBox1);
            c1.setText(fcat.get(position));
            //final CheckBox c2 = (CheckBox) convertView.findViewById(R.id.checkBox2);
            TextView t1= (TextView) convertView.findViewById(R.id.dname);
            TextView t2 = (TextView) convertView.findViewById(R.id.dcat);
            TextView t3 = (TextView) convertView.findViewById(R.id.drs);
            ImageView picture = convertView.findViewById(R.id.productPicCart);
            Glide.with(getApplicationContext())
                    .load(pics.get(position))
                    .into(picture);
            t1.setText(fnames.get(position));
            t1.setTextColor(Color.BLACK);
            t2.setText(fcat.get(position));
            t3.setText(fprices.get(position));
            t3.setTextColor(Color.RED);

            c1.setChecked(fl.get(position));
            //c2.setChecked(flags.get(position));

            c1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (c1.isChecked()) {
                        int a = Integer.parseInt(price.get(position));
                        int b = Integer.parseInt(t5.getText().toString());
                        a = a + b;
                        t5.setText(Integer.toString(a));
                        totalprice = a;
                        flags.set(position,true);

                    }
                    else {
                        int a = Integer.parseInt(price.get(position));
                        int b = Integer.parseInt(t5.getText().toString());
                        b = b-a;
                        t5.setText(Integer.toString(b));
                        totalprice = b;
                        flags.set(position,false);
                    }
                }
            });



            notifyDataSetChanged();
            return convertView;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checker);
    }


}