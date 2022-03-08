package com.example.tugasakhirpam;

public class Pengingat {
    //deklarasi variable
    /**
     * Deklarasi variable id
     */
    int id;

    /**
     * Deklarasi variable title
     */
    private String title;

    /**
     * Deklarasi variable date
     */
    private String date;

    /**
     * Deklarasi variable time
     */
    private String time;

    // membuat konstruktor
    /**
     * Method ini berfungsi untuk membuat konstruktor
     *
     */
    public Pengingat(int id, String title, String date, String time) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
    }

    //mengembalikan nilai dari variabel

    /**
     * Method ini berfungsi
     * @return mengembalikan nilai dari variable id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return mengembalikan nilai dari variable title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @return mengembalikan nilai dari variable date
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @return mengembalikan nilai dari variable time
     */
    public String getTime() {
        return time;
    }

}