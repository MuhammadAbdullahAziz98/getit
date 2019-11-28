package com.example.lenovo.getit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class AddPictures extends AppCompatActivity {

        private String profileUri;
        private Uri imageUris;
        //private ImageButton button;
        private ArrayList<String> imagePath = new ArrayList<>();
        CheckInternetBroadcast checker;

        private Button nextButton;
        private ImageView profile;
        private ImageView demoImage;
        private TextView textViewCounter;
        int counter = 0;
        LinearLayout linearLayout;
        ImageView imageView;
        boolean mainImage = true,otherimage = true,profileImageAdded = false;
        private ArrayList<Uri> imageUri = null;
        public static final int SELECTED_RESULT = 1;
        public static final int PICK_FROM_CAMERA = 2;
    int lightColor = (Integer.parseInt("8d6464", 16)+0xFF000000);
    int darkColor = (Integer.parseInt("5a0c0c", 16)+0xFF000000);

        String name,category,sub,newOrUsed,company,des;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_pictures);
            checker = new CheckInternetBroadcast();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(checker, filter);

            /////////////////////////////////////////////////////////
            Intent intent1 = getIntent();

            name = intent1.getStringExtra("name");
            category = intent1.getStringExtra("category");
            sub = intent1.getStringExtra("sub");
            newOrUsed = intent1.getStringExtra("neworused");
            company = intent1.getStringExtra("company");
            des = intent1.getStringExtra("desc");
            /////////////////////////////////////////////////////////

            ImageView backP = findViewById(R.id.backP);
            backP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            imageUri = new ArrayList<Uri>();
            profile = findViewById(R.id.adimage);

            linearLayout = findViewById(R.id.additional);

            imageView = new ImageView(this);
            imageView.setBackgroundResource(R.drawable.plussign);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dpPx(60), dpPx(60));
            imageView.setLayoutParams(layoutParams);
            imageView.setPadding(dpPx(10),dpPx(10),dpPx(10),dpPx(10));
            linearLayout.addView(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainImage = false;
                    if (counter <10 && otherimage)
                    {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent,SELECTED_RESULT);
                    }
                    else
                    {
                        Toast.makeText(AddPictures.this, "Only 10 pictures are allowed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            nextButton = findViewById(R.id.nextbottom);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (profileImageAdded && imageUri.size() > 1)
                    {
                        changeActivity();
                    }
                    else
                    {
                        if (!profileImageAdded)
                        {
                            Toast.makeText(AddPictures.this, "Enter thumbnail image!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(AddPictures.this, "enter some other images!" , Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            final String[] item = new String[] {"from sd card"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.select_dialog_item,item);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Image");
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                                                    //for canera intent
                    if (which == 0)
                    {
                        mainImage = true;
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent,SELECTED_RESULT);
                          dialog.cancel();
                    }
                }
            });

            final AlertDialog dialog = builder.create();

            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profileImageAdded = true;
//                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(intent,SELECTED_RESULT);
                    dialog.show();

                }
            });
        }
    private int convertDpToPx(int dp){
        return Math.round(dp*(getResources().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));
    }

    private void changeActivity() {

            Intent intent = new Intent(this,Price.class);
            intent.putStringArrayListExtra("imagePath" , imagePath);
            intent.putExtra("name",name);
            intent.putExtra("category",category);
            intent.putExtra("sub",sub);
            intent.putExtra("neworused",newOrUsed);
            intent.putExtra("company",company);
            intent.putExtra("desc",des);
            intent.putExtra("profile",profileUri);

        Toast.makeText(this, profileUri, Toast.LENGTH_SHORT).show();

            startActivity(intent);
    }

    public void insertImage ()
        {

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == SELECTED_RESULT) {
                if (resultCode == RESULT_OK) {
                    if (mainImage)
                    {
                        Uri uri1 = data.getData();

                        String[] projection1 = {MediaStore.Images.Media.DATA};

                        Cursor cursor1 = getContentResolver().query(uri1, projection1, null, null, null);
                        cursor1.moveToFirst();
                        int columnIndex1 = cursor1.getColumnIndex(projection1[0]);
                        String filePath1 = cursor1.getString(columnIndex1);
                        cursor1.close();
                        //Bitmap yourSelectedImage1 = BitmapFactory.decodeFile(filePath1);
                        //profile.setImageBitmap(yourSelectedImage1);
                        //imageUri.add(uri1);
                        profileUri = uri1.toString();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri1);
                            Bitmap resized = Bitmap.createScaledBitmap(bitmap,convertDpToPx(350),convertDpToPx(300),true);
                            profile.setImageBitmap(resized);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //profileUri = uri1.getPath();
                        mainImage = false;
                        nextButton.setBackgroundColor(darkColor);
                    }
                    else if (counter < 10)
                    {
                        Uri uri = data.getData();
                        imageUri.add(uri);
                        String[] projection = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(projection[0]);
                        String filePath = cursor.getString(columnIndex);
                        imagePath.add(uri.toString());
                        cursor.close();
                        linearLayout.removeView(imageView);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dpPx(60), dpPx(60));
                        demoImage = new ImageView(this);
                        demoImage.setLayoutParams(layoutParams);
                        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) demoImage.getLayoutParams();
                        marginParams.setMargins(dpPx(10),dpPx(10),dpPx(10),dpPx(10));
//                        demoImage.setPadding();
                        linearLayout.addView(demoImage);
                        imageView.setBackgroundResource(R.drawable.plussign);
                        imageView.setLayoutParams(layoutParams);
                        ViewGroup.MarginLayoutParams marginParams1 = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
                        marginParams.setMargins(dpPx(10),dpPx(10),dpPx(10),dpPx(10));
//                        imageView.setPadding(dpPx(10),dpPx(10),dpPx(10),dpPx(10));
                        linearLayout.addView(imageView);
                        /*Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                        demoImage.setImageBitmap(yourSelectedImage);
                        */
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                            Bitmap resized = Bitmap.createScaledBitmap(bitmap,convertDpToPx(350),convertDpToPx(300),true);
                            demoImage.setImageBitmap(resized);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        counter++;
                        textViewCounter = findViewById(R.id.counter);
                        String s = counter+"/10";
                        textViewCounter.setText(s);
                        mainImage = true;
                    }
                    else
                    {
                        Toast.makeText(this, "Only 10 pictures are allowed!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
            else if (requestCode == PICK_FROM_CAMERA)
            {
                if (resultCode == RESULT_OK)
                {
                    if (resultCode == RESULT_OK)
                    {
//                        Toast.makeText(this, "get camera picture", Toast.LENGTH_SHORT).show();
//                        String path = Environment.getExternalStorageDirectory().getPath();
//                        path = path + "/image_"+count;
//                        File file = new File(path);
//                        boolean mkdirs = file.mkdirs();
//                        if (mkdirs)
//                        {
//                            Toast.makeText(this, "created!", Toast.LENGTH_SHORT).show();
//                        }
//                        else
//                        {
//                            Toast.makeText(this, "Not Created!", Toast.LENGTH_SHORT).show();
//                        }
//
//                        if (file.exists())
//                        {
//                            Toast.makeText(this, "Exist karti hay !", Toast.LENGTH_SHORT).show();
//                        }
//                        else
//                        {
//                            Toast.makeText(this, "Nai Exist karti hay !", Toast.LENGTH_SHORT).show();
//                        }
//                        count++;
//
//                        path = file.getAbsolutePath();
////                        String path = imageCaptured.getPath();
//////                        File file = new File(path);
//////                        if (!file.exists())
//////                        {
//////                            Toast.makeText(this, "not Exist", Toast.LENGTH_SHORT).show();
//////                            try {
//////                                file.createNewFile();
//////                                Toast.makeText(this, "file ceated", Toast.LENGTH_SHORT).show();
//////                            } catch (IOException e) {
//////                                Toast.makeText(this, "New file throwing exception", Toast.LENGTH_SHORT).show();
//////                                e.printStackTrace();
//////                            }
//////                        }
//////                        Uri fileuri = Uri.fromFile(file);
//////                        String filePath = fileuri.getPath();
                        //Bitmap bitmap = BitmapFactory.decodeFile(path);
                        //imageView.setImageBitmap(bitmap);
                    }
                }
            }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static int dpPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(checker);
    }

}
