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
    public static String NOTIFICATION_ID = "notification-id";
    //deklarasi dan inisialisasi notification
    public static String NOTIFICATION = "notification";

    //untuk menerima pesan broadcast yang masuk
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