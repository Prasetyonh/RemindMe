package com.example.tugasakhirpam;

public class Pengingat {
    //deklarasi variable
    int id;
    private String title;
    private String date;
    private String time;

    // membuat konstruktor
    public Pengingat(int id, String title, String date, String time) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
    }

    //mengembalikan nilai dari variabel
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

}