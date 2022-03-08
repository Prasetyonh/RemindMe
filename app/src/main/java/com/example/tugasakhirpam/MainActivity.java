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

    //inisialisasi
    public static final String NOTIFICATION_CHANNEL_ID = "0";
    private final static String default_notification_channel_id = "default";
    private static final String TAG = "MainActivity";

    //deklarasi
    /**
     * inisialisasi dari class DBHelper
     */
    private DBHelper databaseHelper;
    /**
     * inisialisasi dari ListView
     */
    private ListView itemsListView;

    /**
     * inisialisasi dari FloatingActionButton
     */
    private FloatingActionButton fab;

    @Override

    /**
     * Method ini akan dipanggil bila sebelumnya tidak ada database
     *
     *
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DBHelper(this);

        //untuk menghubungkan variabel fab dengan komponen pada layout dengan id fab
        fab = findViewById(R.id.fab);
        //untuk menghubungkan variabel itemListView dengan komponen pada layout dengan id itemsList
        itemsListView = findViewById(R.id.itemsList);

        //memanggil method
        populateListView();
        onFabClick();

    }

    /**
     * Method yang berfungsi untuk mengatur notifikasi
     * @param notification parameter yang digunakan untuk
     * @param delay parameter ini berfungsi untuk mengatur delay notifikasi
     */
    //Mengatur notifikasi
    private void scheduleNotification(Notification notification, long delay) {
        //memanggil class Notifikasi
        Intent notificationIntent = new Intent(this, Notifikasi.class);
        //mengambil data Notifikasi Id
        notificationIntent.putExtra(Notifikasi.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(Notifikasi.NOTIFICATION, notification);
        //intent yang dikirim akan ditampilkan menurut waktu yang sudah didaftarkan
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getLayoutInflater().getContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
        }
    }

    /**
     * Method untuk menampilkan notifikasi
     * @param content parameter untuk menampilkan text sesuai dengan content
     * @return memunculkan notifikasi
     * @see Notification
     */
    private Notification getNotification(String content) {

        //Saat notifikasi di klik oleh user akan masuk ke MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //mengambil suara notifikasi dari deriktori Raw
        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.ringtone);
        //menginiaslisasi notificationBuilder
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getLayoutInflater().getContext(), default_notification_channel_id);
        notificationBuilder.setSmallIcon(R.drawable.iconapp)//menampilkan icon aplikasi didalam notifikasi
                .setContentTitle("PENGINGAT")//menampilkan judul notifikasi
                .setContentText(content)//menampilkan text sesuai dengan content
                .setAutoCancel(true);//untuk menutup notifikasi

        //agar notifikasi muncul di channel id yang sudah di tentukan
        notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
                .setSound(soundUri)//untuk memutar suara notifikasi
                .setContentIntent(pendingIntent);


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            if(soundUri != null){//jika soundUri tidak null maka akan menjalankan kode didalamnya

                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);//mengatur vibrate secara default
                // membuat AudioAtribut
                AudioAttributes audioAttributes = new AudioAttributes.Builder() //
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();

                // membuat Notification Channel
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Audio",NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setSound(soundUri,audioAttributes);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
        }
        return notificationBuilder.build();//mengembalikan nilai notificationBuilder.build()
    }


    /**
     * Method ini berfungsi untuk memasukkan data kedalam database
     * @param judul parameter untuk menyimpan judul yang diinputkan oleh user
     * @param tanggal parameter untuk menyimpan tanggal yang diinputkan oleh user
     * @param jam parameter untuk menyimpan jam yang diinputkan oleh user
     */
    //Memasukkan data ke dalam database
    private void insertDataToDb(String judul, String tanggal, String jam) {
        //mengirim variabel yang di input user ke databaseHelper untuk dimasukkan ke dalam database
        boolean insertData = databaseHelper.insertData(judul, tanggal, jam);
        if (insertData) {   //jika berhasil menambahkan data maka akan menjalankan kode didalamnya
            try {
                populateListView();
                toastMsg("Reminder ditambahkan");//menampilkan toast pesan
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else //jika gagal menambakan data akan menjalankan kode didalamnya
            toastMsg("Opps.. terjadi kesalahan saat menyimpan!");//menampilkan pesan toast
    }

    /**
     * Mengambil seluruh data dari database ke listview
     */
    //Mengambil seluruh data dari database ke listview
    private void populateListView() {
        try {
            //mengubah data didalam database menjadi arraylist
            ArrayList<Pengingat> items = databaseHelper.getAllData();
            //inisiasi Adapter
            Adapter adptr = new Adapter(this, items);
            //menampilkan data kedalam listview
            itemsListView.setAdapter(adptr);
            adptr.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Menampilkan dialog ketika fab diklik
     */
    private void onFabClick() { //saat buton fab diklik maka akan menjalankan kode didalamnya
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
    /**
     * Method ini berfungsi untuk menampilkan dialog untuk mengatur pengingat
     */
    private void showAddDialog() {  //menampilkan dialog
        //inisialisasi AlertDialog.Builder
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getLayoutInflater().getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams")
        //menghubungkan dengan layout dialog_pengingat

        final View dialogView = inflater.inflate(R.layout.dialog_pengingat, null);
        dialogBuilder.setView(dialogView);

        //deklarasi dan menghubungkan variabel judul dengan variabel didalam layout
        final EditText judul = dialogView.findViewById(R.id.edit_title);
        //deklarasi dan menghubungkan variabel tanggal dengan variabel didalam layout
        final TextView tanggal = dialogView.findViewById(R.id.date);
        //deklarasi dan menghubungkan variabel waktu dengan variabel didalam layout
        final TextView waktu = dialogView.findViewById(R.id.time);

        final long date = System.currentTimeMillis();
        //untuk menampilkan format date
        SimpleDateFormat dateSdf = new SimpleDateFormat("d MMMM");
        String dateString = dateSdf.format(date);
        tanggal.setText(dateString);

        //menampilkan format waktu
        SimpleDateFormat timeSdf = new SimpleDateFormat("hh : mm a");
        String timeString = timeSdf.format(date);
        waktu.setText(timeString);

        //mengatur calendar menjadi waktu realtime
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        //Set tanggal
        tanggal.setOnClickListener(new View.OnClickListener() {
            /**
             * Berfungsi untuk menampilkan dialog date picker
             *
             */
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override

            public void onClick(View v) {
                //inisialisasi DatePickerDialog
                final DatePickerDialog datePickerDialog = new DatePickerDialog(getLayoutInflater().getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override

                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String newMonth = getMonth(monthOfYear + 1);
                                tanggal.setText(dayOfMonth + " " + newMonth);
                                cal.set(Calendar.YEAR, year);//set tahun sesuai tahun saat ini
                                cal.set(Calendar.MONTH, monthOfYear);//set bulan sesuai bulan saat ini
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);//set tanggal sesuai tanggal saat ini
                            }
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();//menampilkan dialog date picker
                //minimal tanggal hari ini yang dapat dipilih(tidak dapat memilih tanggal sebelumny)
                datePickerDialog.getDatePicker().setMinDate(date);
            }
        });

        //Set Jam
        waktu.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                //inisialisasi dialog time picker
                TimePickerDialog timePickerDialog = new TimePickerDialog(getLayoutInflater().getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String time;
                                @SuppressLint("DefaultLocale")
                                        //format menit dalam 2 digit
                                String minTime = String.format("%02d", minute);
                                //menentukan waktu AM dan PM
                                if (hourOfDay >= 0 && hourOfDay < 12) {
                                    time = hourOfDay + " : " + minTime + " AM";
                                } else {
                                    if (hourOfDay != 12) {
                                        hourOfDay = hourOfDay - 12;
                                    }
                                    time = hourOfDay + " : " + minTime + " PM";
                                }
                                waktu.setText(time);
                                cal.set(Calendar.HOUR, hourOfDay);//set jam sesuai jam saat ini
                                cal.set(Calendar.MINUTE, minute);//set menit sesuai menit saat ini
                                cal.set(Calendar.SECOND, 0);//set detik menjadi 0
                                //pesan log jika berhasil
                                Log.d(TAG, "onTimeSet: Time has been set successfully");
                            }
                            //menerima input data dari user
                        }, cal.get(Calendar.HOUR), cal.get(MINUTE), false);
                timePickerDialog.show();//menampilkan dialog timepicker
            }
        });



        dialogBuilder.setTitle("Buat Pengingat Baru");//Judul dialog
        //positif button berfungsi untuk menambahkan pengingat yang sudah diisi oleh user
        dialogBuilder.setPositiveButton("Tambah", new DialogInterface.OnClickListener() {
            /**
             * Method untuk memastikan semua data terisi agar pengingat dapat tersimpan
             * @param dialog parameter yang menginisialisasi DialogInterface yang berfungsi untuk dialog yang dapat ditampilkan, ditutup, atau dibatalkan, dan mungkin memiliki tombol yang dapat diklik.
             * @param whichButton
             */
            public void onClick(DialogInterface dialog, int whichButton) {
                String title = judul.getText().toString();
                String date = tanggal.getText().toString();
                String time = waktu.getText().toString();
                //jika judul tidak kosong maka menjalankan kode dialamnya
                if (title.length() != 0) {
                    try {
                        insertDataToDb(title, date, time);
                        scheduleNotification(getNotification(title), cal.getTimeInMillis());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else { //jika judul kosong maka tidak tersimpan dan menampilkan toast pesan
                    toastMsg("Oops, Harus diisi Semua !");
                }
            }
        });
        //negatif button berfungsi untuk keluar dari dialog
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            /**
             * Method ini berfungsi untuk keluar dari dialog ketika button cancel diklik
             * @param dialog parameter yang menginisialisasi DialogInterface yang berfungsi untuk dialog yang dapat ditampilkan, ditutup, atau dibatalkan, dan mungkin memiliki tombol yang dapat diklik.
             * @param whichButton
             */
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        //inisialisasi AlertDialog
        AlertDialog b = dialogBuilder.create();
        b.show();//menampilkan dialog
    }

    //Metode pesan toast

    /**
     * Method ini berfungsi untuk menampilkan toast message
     * @param msg parameter ini berfungsi untuk menyimpan pesan yang akan ditampilkan pada toast
     */
    private void toastMsg(String msg) {
        Toast t = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER, 0,0);
        t.show();
    }


    /**
     * Berfungsi untuk mengambil data bulan
     * @param month parameter untuk menyimpan data bulan
     * @return  mengenkapsulasi data pemformatan tanggal-waktu yang dapat dilokalkan
     */
    private String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    //membuat menu

    /**
     * Method ini digunakan untuk membuat menu
     * @param menu menginisialisasi komponen Menu
     * @return menampilkan menu
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //memanggil item menu

    /**
     * Method yang berfungsi untuk memanggil item pada menu
     * @param item Parameter ini merupakan inisialisasi dari MenuItem yang berfungsi untuk akses langsung ke item menu yang dibuat sebelumnya
     * @return mengembalikan item pada menu
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.tentang){
            callAbout();//memanggil method
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Memanggil method About
     */
    public void callAbout(){
        //inisialisasi AlertDialog.Builder
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setMessage( //didalam dialog terdapat text seperti dibawah ini
                "About App : \n\n" +
                        "RemindMe \n" +
                        "Version 1.0 \n\n"+
                        "===========================\n\n" +
                        "Made with ❤ By Prasetyo N.H");
        builder.setCancelable(true);
        //membuat negatif button dengan text Ok
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            //ketika button Ok diklik maka akan menutup dialog
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        //menampilkan dialog
        builder.show();
    }
}