package com.wynacom.wynahealth.DB_Local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Order_Data extends SQLiteOpenHelper {

    private static final String TAG = Local_Data.class.getSimpleName();
    public static final String DATABASE_NAME = "DB_ORDER";
    public static final String TABLE_NAME = "TB_Orders";
    public static final String COL_0 = "id_patient";
    public static final String COL_1 = "product_id";
    public static final String COL_2 = "ol_category_id";
    public static final String COL_3 = "title";
    public static final String COL_4 = "description";
    public static final String COL_5 = "price";
    public static final String COL_6 = "discount";
    public static final String COL_7 = "image";
    public static final String COL_8 = "slug";

    private SQLiteDatabase db;

    public Order_Data(Context context) {
        super(context, DATABASE_NAME, null, 2);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (" +
            "id_patient TEXT," +
            "product_id TEXT," +
            "ol_category_id TEXT," +
            "title TEXT," +
            "description TEXT," +
            "price TEXT," +
            "discount TEXT," +
            "image TEXT," +
            "slug TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public ArrayList<HashMap<String, String>> GetUsers(){
        Locale localeID = new Locale("in", "ID");
        NumberFormat nf = NumberFormat.getCurrencyInstance(localeID);
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT * FROM "+ TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            String price            = cursor.getString(5);
            String discount         = cursor.getString(6);
            user.put("name",        cursor.getString(3));
            user.put("description", cursor.getString(4));
            double a = Double.parseDouble(price);
            double b = Double.parseDouble(discount);
            String p = nf.format(Integer.parseInt(price));
            user.put("price",       p);
            double c = a*(b/100);
            double subTotal = a-c;
            String s = nf.format(subTotal);
            String d = "-"+nf.format(c);
            user.put("viewDisc",    discount+"%");
            user.put("discount",    d);
            user.put("total",       s);
            userList.add(user);
        }
        return  userList;
    }

    public void SimpanData(String id_patient, String product_id, String ol_category_id, String title,
                           String description, String price, String discount, String image,String slug) {
        ContentValues values = new ContentValues();
        values.put(COL_0, id_patient);
        values.put(COL_1, product_id);
        values.put(COL_2, ol_category_id);
        values.put(COL_3, title);
        values.put(COL_4, description);
        values.put(COL_5, price);
        values.put(COL_6, discount);
        values.put(COL_7, image);
        values.put(COL_8, slug);
        db.insert(TABLE_NAME, null, values);
        //close();
    }

    public void UpdateToken(String email,String newtoken){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(COL_9, newtoken);
        db.update(TABLE_NAME, values, "email=?", new String[]{email});
        db.close();
    }

    public void HapusRow(String id_patient,String product_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COL_0 + " = " + id_patient + " AND " + COL_1 + " = " + product_id);
    }

    public void HapusData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
        db.close();
    }

    public void CloseDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.close();
    }
}
