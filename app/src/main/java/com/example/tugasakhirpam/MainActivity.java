package com.example.tugasakhirpam;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;


import com.example.tugasakhirpam.adapter.Adapter;
import com.example.tugasakhirpam.database.DBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.net.URI;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static java.util.Calendar.MINUTE;

public class MainActivity extends AppCompatActivity {

    public static final String NOTIFICATION_CHANNEL_ID = "0";
    private final static String default_notification_channel_id = "default";
    private static final String TAG = "MainActivity";
    private DBHelper databaseHelper;
    private ListView itemsListView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DBHelper(this);
        fab = findViewById(R.id.fab);
        itemsListView = findViewById(R.id.itemsList);
        populateListView();
        onFabClick();

    }

    //Mengatur notifikasi
    private void scheduleNotification(Notification notification, long delay) {
        Intent notificationIntent = new Intent(this, Notifikasi.class);
        notificationIntent.putExtra(Notifikasi.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(Notifikasi.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getLayoutInflater().getContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
        }
    }

    private Notification getNotification(String content) {

        //Saat notifikasi di klik di arahkan ke MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.ringtone);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getLayoutInflater().getContext(), default_notification_channel_id);
        notificationBuilder.setSmallIcon(R.drawable.iconapp)
                .setContentTitle("PENGINGAT")
                .setContentText(content)
                .setAutoCancel(true);
        notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            if(soundUri != null){

                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                // membuat AudioAtribut
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();

                // membuat Notification Channel
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Audio",NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setSound(soundUri,audioAttributes);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
        }
        return notificationBuilder.build();
    }


    //Memasukkan data ke database
    private void insertDataToDb(String judul, String tanggal, String jam) {
        boolean insertData = databaseHelper.insertData(judul, tanggal, jam);
        if (insertData) {
            try {
                populateListView();
                toastMsg("Reminder ditambahkan");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            toastMsg("Opps.. terjadi kesalahan saat menyimpan!");
    }

    //Mengambil seluruh data dari database ke listview
    private void populateListView() {
        try {
            ArrayList<Pengingat> items = databaseHelper.getAllData();
            Adapter adptr = new Adapter(this, items);
            itemsListView.setAdapter(adptr);
            adptr.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onFabClick() {
        try {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Fungsi dari tombol tambah
    @SuppressLint("SimpleDateFormat")
    private void showAddDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getLayoutInflater().getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams")
        final View dialogView = inflater.inflate(R.layout.dialog_pengingat, null);
        dialogBuilder.setView(dialogView);

        final EditText judul = dialogView.findViewById(R.id.edit_title);
        final TextView tanggal = dialogView.findViewById(R.id.date);
        final TextView waktu = dialogView.findViewById(R.id.time);

        final long date = System.currentTimeMillis();
        SimpleDateFormat dateSdf = new SimpleDateFormat("d MMMM");
        String dateString = dateSdf.format(date);
        tanggal.setText(dateString);

        SimpleDateFormat timeSdf = new SimpleDateFormat("hh : mm a");
        String timeString = timeSdf.format(date);
        waktu.setText(timeString);

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        //Set tanggal
        tanggal.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(getLayoutInflater().getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String newMonth = getMonth(monthOfYear + 1);
                                tanggal.setText(dayOfMonth + " " + newMonth);
                                cal.set(Calendar.YEAR, year);
                                cal.set(Calendar.MONTH, monthOfYear);
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            }
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMinDate(date);
            }
        });

        //Set Jam
        waktu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getLayoutInflater().getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String time;
                                @SuppressLint("DefaultLocale") String minTime = String.format("%02d", minute);
                                if (hourOfDay >= 0 && hourOfDay < 12) {
                                    time = hourOfDay + " : " + minTime + " AM";
                                } else {
                                    if (hourOfDay != 12) {
                                        hourOfDay = hourOfDay - 12;
                                    }
                                    time = hourOfDay + " : " + minTime + " PM";
                                }
                                waktu.setText(time);
                                cal.set(Calendar.HOUR, hourOfDay);
                                cal.set(Calendar.MINUTE, minute);
                                cal.set(Calendar.SECOND, 0);
                                Log.d(TAG, "onTimeSet: Time has been set successfully");
                            }
                        }, cal.get(Calendar.HOUR), cal.get(MINUTE), false);
                timePickerDialog.show();
            }
        });

        dialogBuilder.setTitle("Buat Pengingat Baru");
        dialogBuilder.setPositiveButton("Tambah", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String title = judul.getText().toString();
                String date = tanggal.getText().toString();
                String time = waktu.getText().toString();
                if (title.length() != 0) {
                    try {
                        insertDataToDb(title, date, time);
                        scheduleNotification(getNotification(title), cal.getTimeInMillis());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    toastMsg("Oops, Harus diisi Semua !");
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    //Metode pesan toast
    private void toastMsg(String msg) {
        Toast t = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER, 0,0);
        t.show();
    }

    //Mengkonversi bulan dari huruf menjadi angka
    private String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    //membuat menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.tentang){
            callAbout();
        }
        return super.onOptionsItemSelected(item);
    }

    public void callAbout(){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setMessage(
                "About App : \n\n" +
                        "RemindMe \n" +
                        "Version 1.0 \n\n"+
                        "===========================\n\n" +
                        "Made with ❤ By Prasetyo N.H");
        builder.setCancelable(true);
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}