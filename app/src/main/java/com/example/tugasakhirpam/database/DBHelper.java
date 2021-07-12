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

    //deklarasi dan inisialisasi nama database
    private static  String TAG = "DatabaseHelper";

    //deklarasi dan inisialisasi nama tabel
    private static  String TABLE_NAME = "pengingat";

    private static  String COL1 = "ID"; //deklarasi dan inisialisasi kolom 1
    private static  String COL2 = "judul";  //deklarasi dan inisialisasi kolom 2
    private static  String COL3 = "tanggal";    //deklarasi dan inisialisasi kolom 3
    private static  String COL4 = "jam";    //deklarasi dan inisialisasi kolom 4

    public DBHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    //method untuk membuat database sqlite
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" + //create database dengan nama tabel yang sudah diinisialisasikan
                COL1 + " integer primary key, " + //kolom 1 (ID) dengan tipe data integer dan merupakan primary key
                COL2 + " VARCHAR, " + //kolom 2 (judul) dengan tipe data text
                COL3 + " DATE, "  + //kolom 3 (tanggal) dengan tipe data date
                COL4 + " TIME" + ")"; //kolom 4 (jam) dengan tipe data time
        Log.d(TAG, "Creating table " + createTable);
        db.execSQL(createTable);
    }

    @Override
    //Dipanggil ketika database perlu upgrade.
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Memasukkan data kedalam database
    public boolean insertData(String item, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();//menuliskan data kedalam database
        //inisialisasi CnvertValues
        ContentValues nilai = new ContentValues();
        nilai.put(COL2, item);  //input user pada item akan masuk ke dalam COL2
        nilai.put(COL3, date);  //input user pada date akan masuk ke dalam COL3
        nilai.put(COL4, time);  //input user pada time akan masuk ke dalam COL4
        Log.d(TAG, "insertData: Inserting " + item + " to " + TABLE_NAME);
        long result = db.insert(TABLE_NAME, null, nilai);
        db.close(); //tutup database
        return result != -1; //mengembalikan nilai ketika result tidak sama dengan -1
    }

    //Menghapus data dari database
    public void deleteData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //menghapus data berdasarkan id
        db.delete(TABLE_NAME, COL1 + "=" + id, null);
    }

    //Memuat semua data ke dalam listview
    public ArrayList<Pengingat> getAllData() {
        ArrayList<Pengingat> arrayList = new ArrayList<>();//data didalam Pengingat diubah menjadi arraylist
        SQLiteDatabase db = this.getReadableDatabase();//membaca data dalam database
        String query = "SELECT * FROM " + TABLE_NAME;//menampilkan semua data dalam tabel
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(query, null);//cursor untuk membaca data

        //perulangan pergerakan cursor dalam membaca data
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);//baca id dengan tipe data int dalam index 0
            String title = cursor.getString(1);//baca title dengan tipe data string dalam index 1
            String date = cursor.getString(2);//baca date dengan tipe data string dalam index 2
            String time = cursor.getString(3);//baca tine dengan tipe data string dalam index 3

            //menginisialisasi variabel modelData yang berisi data yang diinput oleh user
            Pengingat modelData = new Pengingat(id, title, date, time);

            //data yang ada di dalam variabel modelData di masukkan kedalam array list
            arrayList.add(modelData);
        }
        db.close();//menutup database
        return arrayList;//mengembalikan nilai arraylist
    }
}