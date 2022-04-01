package com.wynacom.wynahealth.DB_Local;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Local_Data extends SQLiteOpenHelper {

    private static final String TAG = Local_Data.class.getSimpleName();

    public static final String DATABASE_NAME = "DB_LOCAL";
    public static final String TABLE_NAME = "TB_User";
    public static final String COL_0 = "patient_name";
    public static final String COL_1 = "handphone";
    public static final String COL_2 = "postal_code";
    public static final String COL_3 = "title_id";
    public static final String COL_4 = "city";
    public static final String COL_5 = "sex";
    public static final String COL_6 = "age";
    public static final String COL_7 = "email";
    public static final String COL_8 = "nik";
    public static final String COL_9 = "user_id";

    private SQLiteDatabase db;

    public Local_Data(Context context) {
        super(context, DATABASE_NAME, null, 2);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (" +
            "patient_id INTEGER primary key ASC autoincrement," +
            "patient_name TEXT," +
            "handphone TEXT," +
            "postal_code TEXT," +
            "title_id TEXT," +
            "city TEXT," +
            "sex TEXT," +
            "age TEXT," +
            "email TEXT," +
            "nik TEXT,"+
            "user_id TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void SimpanData(String patient_name, String handphone, String postal_code, String title_id, String city, String sex, String age, String email,String nik,String user_id) {
        ContentValues values = new ContentValues();
        values.put(COL_0, patient_name);
        values.put(COL_1, handphone);
        values.put(COL_2, postal_code);
        values.put(COL_3, title_id);
        values.put(COL_4, city);
        values.put(COL_5, sex);
        values.put(COL_6, age);
        values.put(COL_7, email);
        values.put(COL_8, nik);
        values.put(COL_9, user_id);
        db.insert(TABLE_NAME, null, values);
        //close();
    }

    public void UpdateToken(String email,String newtoken){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_9, newtoken);
        db.update(TABLE_NAME, values, "email=?", new String[]{email});
        db.close();
    }

    public void HapusData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }
}
