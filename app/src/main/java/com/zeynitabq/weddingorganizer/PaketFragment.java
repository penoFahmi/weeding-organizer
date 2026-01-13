package com.zeynitabq.weddingorganizer; // Sesuaikan package kamu

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zeynitabq.weddingorganizer.adapter.PackageAdapter;
import com.zeynitabq.weddingorganizer.model.PackageModel;
import java.util.ArrayList;

public class PaketFragment extends Fragment {

    RecyclerView rvPaket;
    FloatingActionButton fabAdd;
    TextView tvEmpty;
    DatabaseHelper dbHelper;
    ArrayList<PackageModel> listPaket;
    PackageAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paket, container, false);

        // Inisialisasi Komponen
        rvPaket = view.findViewById(R.id.rvPaket);
        fabAdd = view.findViewById(R.id.fabAdd);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        dbHelper = new DatabaseHelper(getContext());

        // Setup RecyclerView
        rvPaket.setLayoutManager(new LinearLayoutManager(getContext()));
        listPaket = new ArrayList<>();
        adapter = new PackageAdapter(getContext(), listPaket);
        rvPaket.setAdapter(adapter);

        // Aksi Tombol Tambah
        fabAdd.setOnClickListener(v -> {
            // Pindah ke Halaman Form (Nanti kita buat file ini)
            Intent intent = new Intent(getContext(), PackageFormActivity.class);
            startActivity(intent);
        });

        loadData(); // Panggil fungsi ambil data

        return view;
    }

    // Fungsi Load Data dari SQLite
    private void loadData() {
        listPaket.clear(); // Bersihkan list lama biar gak dobel
        Cursor cursor = dbHelper.getAllPackages();

        if (cursor.getCount() == 0) {
            tvEmpty.setVisibility(View.VISIBLE); // Tampilkan teks "Kosong"
        } else {
            tvEmpty.setVisibility(View.GONE);
            while (cursor.moveToNext()) {
                // Ambil data dari database per baris
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String extras = cursor.getString(cursor.getColumnIndexOrThrow("extras"));
                String imageUri = cursor.getString(cursor.getColumnIndexOrThrow("image_uri"));

                // Masukkan ke Model
                listPaket.add(new PackageModel(id, name, price, type, extras, imageUri));
            }
        }
        adapter.notifyDataSetChanged(); // Kabari adapter ada data baru
    }

    // Saat kembali ke halaman ini (habis nambah data), refresh otomatis
    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}