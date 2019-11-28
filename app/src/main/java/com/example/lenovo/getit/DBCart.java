package com.example.lenovo.getit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBCart extends SQLiteOpenHelper {

    public static final String Database_Name = "CartDB.db";
    public static final int Database_Version = 1;

    DBCart(Context context)
    {
        super(context,Database_Name,null,Database_Version);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        String sql = "CREATE TABLE Cart (UserID Text, " + "pid Text, "+"Primary Key (UserID, pid))";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("DROP Table IF EXISTS Cart");
        onCreate(sqLiteDatabase);
    }

    public boolean insert_cart(String uid, String pid)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("UserID",uid);
        contentValues.put("pid",pid);
        long temp = sqLiteDatabase.insert("Cart",null,contentValues);
        if (temp == -1)
            return false;
        else
            return true;
    }

    public Cursor get_products(String uid)
    {
        Cursor cursor;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "Select * from Cart where UserID = '" +uid+"'";
        cursor = db.rawQuery(sql,null);
        return cursor;
    }

    public String delete_entries(String uid)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String sql = "Delete from Cart where UserID = '" + uid + "'";
            db.execSQL(sql);
            return "done";
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            return ex.getMessage();
        }
//        return false;
    }
}
