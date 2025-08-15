package com.prototypes.sabjiwala.shop_activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public String TABLE_NAME = "Cart";
    public String Col1 = "VEGETABLE_ID";
    public String Col2 = "VEGETABLE_CATEGORY";
    public String Col3 = "VEGETABLE_QUANTITY";

    public DatabaseHelper(@Nullable Context context, String DATABASE_NAME) {
        super(context, DATABASE_NAME + ".db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (" + Col1 + " TEXT PRIMARY KEY, " + Col2 + " TEXT, " + Col3 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String ID, String CATEGORY, String QUANTITY){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col1, ID);
        contentValues.put(Col2, CATEGORY);
        contentValues.put(Col3, QUANTITY);
        long progress = db.insert(TABLE_NAME, null, contentValues);
        if (progress == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public Integer deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, Col1 + " = ?", new String[] {id});
    }

    public boolean updateData(String id, String category, String quantity){
        SQLiteDatabase db = this .getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col1, id);
        contentValues.put(Col2, category);
        contentValues.put(Col3, quantity);
        db.update(TABLE_NAME, contentValues, Col1 + " = ?", new String[] {id});
        return true;
    }

    public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }
}
