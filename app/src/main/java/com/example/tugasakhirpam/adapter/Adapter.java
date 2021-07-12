package com.example.tugasakhirpam.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.tugasakhirpam.Pengingat;
import com.example.tugasakhirpam.R;
import com.example.tugasakhirpam.database.DBHelper;


import java.util.ArrayList;

public class Adapter extends BaseAdapter {

    //deklarasi
    private Context context;
    private ArrayList<Pengingat> arrayList;

    public Adapter(Context context, ArrayList<Pengingat> arrayList) {
        super();
        this.context = context;
        this.arrayList = arrayList;
    }

    //untuk mengembalikan jumlah data yang akan ditampilkan pada list
    @Override
    public int getCount() {
        return this.arrayList.size();
    }

    //mendapatkan posisi data dalam array list
    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    //mengembalikan nilai dari posisi item ke adapter
    @Override
    public long getItemId(int position) {
        return position;
    }


    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        convertView = layoutInflater.inflate(R.layout.list_pengingat, null);

        //untuk menghubungkan variable dengan xml pada layout
        TextView titleTextView = convertView.findViewById(R.id.title);//titleTextview dengan title
        TextView dateTextView = convertView.findViewById(R.id.dateTitle);//dateTextView dengan dateTitle
        TextView timeTextView = convertView.findViewById(R.id.timeTitle);//timeTextView dengan timeTitle
        final ImageView delImageView = convertView.findViewById(R.id.delete);//delImageView dengan delete
        delImageView.setTag(position);

        //Menghapus pengingat dari database saat icon hapus di klik
        delImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            //ketika user mengklik maka code di dalamnya akan dijalankan
            public void onClick(View v) {
                //menerima posisi data
                final int pos = (int) v.getTag();
                //posisi data yang diterima akan dihapus
                deleteItem(pos);
            }
        });

        //menginisialisasi Pengingat dengan posisi data dalam array
        Pengingat modelData = arrayList.get(position);

        //menampilkan data kedalam layout
        titleTextView.setText(modelData.getTitle());
        dateTextView.setText(modelData.getDate());
        timeTextView.setText(modelData.getTime());

        //mengembalikan nilai
        return convertView;
    }

    //Menghapus pengingat dari listview
    private void deleteItem(int position) {
        deleteItemFromDb(arrayList.get(position).getId());
        arrayList.remove(position);
        notifyDataSetChanged();
    }

    //Menghapus pengingat dari database
    private void deleteItemFromDb(int id) {
        DBHelper databaseHelper = new DBHelper(context);
        try {
            //hapus data menurut id
            databaseHelper.deleteData(id);
            //kemudian akan muncul pesan
            toastMsg("Pengingat di hapus");

            //jika terdapat error
        } catch (Exception e) {
            e.printStackTrace();
            //maka akan muncul pesan
            toastMsg("Oppss.. ada kesalahan saat menghapus");
        }
    }


    //method pesan toast
    private void toastMsg(String msg) {
        Toast t = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        //menampilkan pesan
        t.show();
    }
}