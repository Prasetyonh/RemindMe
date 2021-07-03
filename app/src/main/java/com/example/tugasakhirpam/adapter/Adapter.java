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

    private Context context;
    private ArrayList<Pengingat> arrayList;

    public Adapter(Context context, ArrayList<Pengingat> arrayList) {
        super();
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return this.arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

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
        TextView titleTextView = convertView.findViewById(R.id.title);
        TextView dateTextView = convertView.findViewById(R.id.dateTitle);
        TextView timeTextView = convertView.findViewById(R.id.timeTitle);
        final ImageView delImageView = convertView.findViewById(R.id.delete);
        delImageView.setTag(position);

        //Menghapus tugas dari database saat icon hapus di klik
        delImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pos = (int) v.getTag();
                deleteItem(pos);
            }
        });

        Pengingat modelData = arrayList.get(position);
        titleTextView.setText(modelData.getTitle());
        dateTextView.setText(modelData.getDate());
        timeTextView.setText(modelData.getTime());
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
            databaseHelper.deleteData(id);
            toastMsg("Tugas di hapus");
        } catch (Exception e) {
            e.printStackTrace();
            toastMsg("Oppss.. ada kesalahan saat menghapus");
        }
    }


    //Metodhod pesan toast
    private void toastMsg(String msg) {
        Toast t = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        t.show();
    }
}