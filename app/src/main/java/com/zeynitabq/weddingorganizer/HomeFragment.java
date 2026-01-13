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
import java.text.NumberFormat;
import java.util.Locale;

public class HomeFragment extends Fragment {

    TextView tvTotalOmzet, tvCountPaket, tvCountBooking;
    DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Init Komponen
        tvTotalOmzet = view.findViewById(R.id.tvTotalOmzet);
        tvCountPaket = view.findViewById(R.id.tvCountPaket);
        tvCountBooking = view.findViewById(R.id.tvCountBooking);

        dbHelper = new DatabaseHelper(getContext());

        loadDashboardData();

        return view;
    }

    private void loadDashboardData() {
        // 1. HITUNG JUMLAH PAKET
        Cursor cursorPaket = dbHelper.getAllPackages();
        int jumlahPaket = cursorPaket.getCount();
        tvCountPaket.setText(String.valueOf(jumlahPaket));

        // 2. HITUNG BOOKING & OMZET
        Cursor cursorBooking = dbHelper.getAllBookings();
        int jumlahBooking = cursorBooking.getCount();
        tvCountBooking.setText(String.valueOf(jumlahBooking));

        double totalPendapatan = 0;

        // Loop semua data booking untuk hitung yang LUNAS saja
        if (cursorBooking.moveToFirst()) {
            do {
                String status = cursorBooking.getString(cursorBooking.getColumnIndexOrThrow("status"));
                double harga = cursorBooking.getDouble(cursorBooking.getColumnIndexOrThrow("total_price"));

                if (status.equalsIgnoreCase("LUNAS")) {
                    totalPendapatan += harga;
                }
            } while (cursorBooking.moveToNext());
        }

        // Format ke Rupiah
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        tvTotalOmzet.setText(formatRupiah.format(totalPendapatan));
    }

    // Refresh data saat balik ke halaman ini
    @Override
    public void onResume() {
        super.onResume();
        loadDashboardData();
    }
}