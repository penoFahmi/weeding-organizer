package com.zeynitabq.weddingorganizer;

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
import com.zeynitabq.weddingorganizer.adapter.BookingAdapter;
import com.zeynitabq.weddingorganizer.model.BookingModel;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    // Komponen UI
    TextView tvTotalOmzet, tvCountPaket, tvCountBooking;
    RecyclerView rvUpcoming; // Tambahan baru

    DatabaseHelper dbHelper;
    ArrayList<BookingModel> listUpcoming;
    BookingAdapter adapterUpcoming;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Init Komponen
        tvTotalOmzet = view.findViewById(R.id.tvTotalOmzet);
        tvCountPaket = view.findViewById(R.id.tvCountPaket);
        tvCountBooking = view.findViewById(R.id.tvCountBooking);
        rvUpcoming = view.findViewById(R.id.rvUpcoming); // Init RecyclerView

        dbHelper = new DatabaseHelper(getContext());

        // Setup RecyclerView Dashboard
        listUpcoming = new ArrayList<>();
        // Kita pakai adapter booking yang lama (BookingAdapter) karena tampilannya sama
        adapterUpcoming = new BookingAdapter(getContext(), listUpcoming);
        rvUpcoming.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUpcoming.setAdapter(adapterUpcoming);

        loadDashboardData();

        return view;
    }

    private void loadDashboardData() {
        // 1. STATISTIK (Kode Lama)
        Cursor cursorPaket = dbHelper.getAllPackages();
        tvCountPaket.setText(String.valueOf(cursorPaket.getCount()));

        Cursor cursorBooking = dbHelper.getAllBookings();
        tvCountBooking.setText(String.valueOf(cursorBooking.getCount()));

        double totalPendapatan = 0;
        listUpcoming.clear(); // Bersihkan list sebelum isi ulang

        // 2. LOOP DATA BOOKING UNTUK OMZET & LIST UPCOMING
        if (cursorBooking.moveToFirst()) {
            do {
                // Ambil Data
                int id = cursorBooking.getInt(0);
                String trx = cursorBooking.getString(1);
                String client = cursorBooking.getString(2);
                String phone = cursorBooking.getString(3);
                String address = cursorBooking.getString(4);
                String dateStr = cursorBooking.getString(5);
                String pkg = cursorBooking.getString(6);
                double total = cursorBooking.getDouble(7);
                String method = cursorBooking.getString(8);
                String status = cursorBooking.getString(9);

                // Hitung Omzet Lunas
                if (status.equalsIgnoreCase("LUNAS")) {
                    totalPendapatan += total;
                }

                // Masukkan ke List untuk ditampilkan di Dashboard
                listUpcoming.add(new BookingModel(id, trx, client, phone, address, dateStr, pkg, total, method, status));

            } while (cursorBooking.moveToNext());
        }

        // Tampilkan Total Omzet
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        tvTotalOmzet.setText(formatRupiah.format(totalPendapatan));

        // 3. FITUR SORTIR: URUTKAN BERDASARKAN TANGGAL TERDEKAT
        // Kita pakai Comparator untuk membandingkan tanggal string "DD-MM-YYYY"
        Collections.sort(listUpcoming, new Comparator<BookingModel>() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("d-M-yyyy", Locale.getDefault());

            @Override
            public int compare(BookingModel o1, BookingModel o2) {
                try {
                    Date date1 = dateFormat.parse(o1.getWeddingDate());
                    Date date2 = dateFormat.parse(o2.getWeddingDate());
                    // Urutkan Ascending (Tanggal kecil/dekat duluan)
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        // Optional: Kalau mau tampilkan cuma 3 teratas, bisa dipotong list-nya
        // Tapi menampilkan semua juga tidak masalah karena ada scrollview

        adapterUpcoming.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboardData();
    }
}