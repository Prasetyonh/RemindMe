package com.example.tugasakhirpam;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//untuk melakukan broadcast pesan dari aplikasi lain atau dari sistem
public class Notifikasi extends BroadcastReceiver {

    //deklarasi dan  inisialisasi notification id
    /**
     * deklarasi dan  inisialisasi NOTIFICATION_ID
     */
    public static String NOTIFICATION_ID = "notification-id";


    //deklarasi dan inisialisasi notification
    /**
     * deklarasi dan inisialisasi NOTIFICATION
     */
    public static String NOTIFICATION = "notification";

    //untuk menerima pesan broadcast yang masuk

    /**
     * Method ini berfungsi untuk membuat notifikasi
     * @param context Sebuah Context memberikan akses informasi atas application state. Ia memperbolehkan Activity, Fragment, dan Service untuk mengakses file, gambar, theme/style, dan lokasi direktori eksternal. Context juga memberikan akses ke service yang terpasang di Android yang akan digunakan misalnya untuk layout inflation, keyboard, dan mencari content provider
     * @param intent mengarahkan ke halaman lain
     */
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(MainActivity.NOTIFICATION_CHANNEL_ID,
                    "Pengingat", importance);
            assert notificationManager != null;//untuk menentukan kondisi

            //membuat notifikasi berdasarkan channel
            notificationManager.createNotificationChannel(notificationChannel);
        }
        //untuk mendapatkan data
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        //jika notifikasi manager tidak null(kosong) maka jalankan code didalamnya(jalankan notifikasi)
        if (notificationManager != null) {
            notificationManager.notify(id, notification);
        }
    }
}