package com.example.tugasakhirpam.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tugasakhirpam.Pengingat;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static  String TAG = "DatabaseHelper";

    private static  String TABLE_NAME = "pengingat";
    private static  String COL1 = "ID";
    private static  String COL2 = "judul";
    private static  String COL3 = "tanggal";
    private static  String COL4 = "jam";

    public DBHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" + COL1 + " integer primary key, " +
                COL2 + " VARCHAR, " + COL3 + " DATE, "  + COL4 + " TIME" + ")";
        Log.d(TAG, "Creating table " + createTable);
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Memasukkan data ke database
    public boolean insertData(String item, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues nilai = new ContentValues();
        nilai.put(COL2, item);
        nilai.put(COL3, date);
        nilai.put(COL4, time);
        Log.d(TAG, "insertData: Inserting " + item + " to " + TABLE_NAME);
        long result = db.insert(TABLE_NAME, null, nilai);
        db.close();
        return result != -1;
    }

    //Menghapus data dari database
    public void deleteData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL1 + "=" + id, null);
    }

    //Memuat semua data ke listview
    public ArrayList<Pengingat> getAllData() {
        ArrayList<Pengingat> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String date = cursor.getString(2);
            String time = cursor.getString(3);
            Pengingat modelData = new Pengingat(id, title, date, time);
            arrayList.add(modelData);
        }
        db.close();
        return arrayList;
    }
}