package com.zeynitabq.weddingorganizer.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.zeynitabq.weddingorganizer.BookingFormActivity; // Import FormActivity
import com.zeynitabq.weddingorganizer.DatabaseHelper;     // Import Database
import com.zeynitabq.weddingorganizer.R;
import com.zeynitabq.weddingorganizer.model.BookingModel;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BookingModel> bookingList;
    private DatabaseHelper dbHelper; // Tambahkan DatabaseHelper

    public BookingAdapter(Context context, ArrayList<BookingModel> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
        this.dbHelper = new DatabaseHelper(context); // Init DB
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingModel model = bookingList.get(position);

        // Set Data Text... (Kode lama tetap sama)
        holder.tvTrxCode.setText(model.getTrxCode());
        holder.tvClientName.setText(model.getClientName());
        holder.tvPkgName.setText(model.getPkgName());
        holder.tvDate.setText(model.getWeddingDate());
        holder.tvStatus.setText(model.getStatus());

        // Format Rupiah
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.tvTotal.setText(formatRupiah.format(model.getTotalPrice()));

        // Warna Status
        if (model.getStatus().equalsIgnoreCase("LUNAS")) {
            holder.tvStatus.setTextColor(Color.parseColor("#388E3C"));
            holder.tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#F57C00"));
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FFF3E0"));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, com.zeynitabq.weddingorganizer.BookingDetailActivity.class);
            // Kirim data penting ke halaman detail agar bisa cetak PDF
            intent.putExtra("ID_BOOKING", String.valueOf(model.getId()));
            intent.putExtra("KODE_TRX", model.getTrxCode());
            context.startActivity(intent);
        });

        // --- FITUR 1: LOGIKA TOMBOL HAPUS ---
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Hapus Pesanan?")
                    .setMessage("Yakin ingin menghapus data pesanan " + model.getClientName() + "?")
                    .setPositiveButton("Ya, Hapus", (dialog, which) -> {
                        // 1. Hapus dari Database
                        dbHelper.deleteBooking(String.valueOf(model.getId()));

                        // 2. Hapus dari List Tampilan
                        bookingList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, bookingList.size());

                        Toast.makeText(context, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });

        // --- FITUR 2: LOGIKA TOMBOL EDIT ---
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookingFormActivity.class);
            // Kirim tanda bahwa ini MODE EDIT
            intent.putExtra("IS_EDIT_MODE", true);
            intent.putExtra("ID_BOOKING", String.valueOf(model.getId()));

            // Kirim data lama biar form terisi otomatis
            intent.putExtra("DATA_NAMA", model.getClientName());
            intent.putExtra("DATA_HP", model.getClientPhone());
            intent.putExtra("DATA_ALAMAT", model.getClientAddress());
            intent.putExtra("DATA_TANGGAL", model.getWeddingDate());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTrxCode, tvClientName, tvPkgName, tvDate, tvTotal, tvStatus;
        Button btnEdit, btnDelete; // Tambahkan Button

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTrxCode = itemView.findViewById(R.id.tvTrxCode);
            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvPkgName = itemView.findViewById(R.id.tvPkgName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            // Init Button
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}