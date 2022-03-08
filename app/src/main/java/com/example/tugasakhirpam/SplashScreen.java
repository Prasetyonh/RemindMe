package com.example.tugasakhirpam;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class SplashScreen extends AppCompatActivity {

    /**
     * Dalam method onCreate kondisi awal saat Activity baru diciptakan, biasanya dilakukan inisialisasi pada tahapan ini
     * @param savedInstanceState savedInstanceState digunakan untuk menyimpan state dari halaman sebelumnya
     */
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //untuk memanggil layout splash screen
        setContentView(R.layout.activity_splash_screen);

        //inisialisasi handler
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override

            public void run() {
                //setelah menampilkan splashscreen kemudian akan masuk ke MainActivity
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
            //durasi tampil splashscreen.
        }, 3000L); //3000 L = 3 detik
    }
}