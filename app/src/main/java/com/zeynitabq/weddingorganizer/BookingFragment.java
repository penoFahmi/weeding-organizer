package com.zeynitabq.weddingorganizer;

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
import com.zeynitabq.weddingorganizer.adapter.BookingAdapter;
import com.zeynitabq.weddingorganizer.model.BookingModel;
import java.util.ArrayList;

public class BookingFragment extends Fragment {

    RecyclerView rvBooking;
    FloatingActionButton fabAddBooking;
    TextView tvEmptyBooking;

    DatabaseHelper dbHelper;
    ArrayList<BookingModel> listBooking;
    BookingAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Kita pakai layout yang sama dengan paket saja (daur ulang) karena isinya cuma Recycler + FAB
        // ATAU buat file baru `fragment_booking.xml` (Disarankan copy paste isi fragment_paket.xml lalu ganti ID-nya)
        View view = inflater.inflate(R.layout.fragment_paket, container, false);
        // Note: Pastikan di XML-nya ID nya sesuai atau pakai findViewById yang benar.
        // Agar aman, saya sarankan buat file `fragment_booking.xml` yang isinya sama persis dengan `fragment_paket.xml`
        // tapi ID RecyclerView diganti jadi `rvBooking` dan FAB jadi `fabAddBooking`.

        // Asumsi kamu pakai layout fragment_paket.xml yang didaur ulang:
        rvBooking = view.findViewById(R.id.rvPaket); // Reuse ID
        fabAddBooking = view.findViewById(R.id.fabAdd); // Reuse ID
        tvEmptyBooking = view.findViewById(R.id.tvEmpty); // Reuse ID

        // Ubah teks kosongnya biar sesuai konteks
        tvEmptyBooking.setText("Belum ada pesanan masuk.\nKlik + untuk Checkout.");

        dbHelper = new DatabaseHelper(getContext());
        listBooking = new ArrayList<>();

        rvBooking.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookingAdapter(getContext(), listBooking);
        rvBooking.setAdapter(adapter);

        // Tombol Tambah Booking (Checkout)
        fabAddBooking.setOnClickListener(v -> {
            // Pindah ke Form Checkout (Kita buat habis ini)
            Intent intent = new Intent(getContext(), BookingFormActivity.class);
            startActivity(intent);
        });

        loadBookingData();

        return view;
    }

    private void loadBookingData() {
        listBooking.clear();
        Cursor cursor = dbHelper.getAllBookings();

        if (cursor.getCount() == 0) {
            tvEmptyBooking.setVisibility(View.VISIBLE);
        } else {
            tvEmptyBooking.setVisibility(View.GONE);
            while (cursor.moveToNext()) {
                listBooking.add(new BookingModel(
                        cursor.getInt(0), // ID
                        cursor.getString(1), // TRX Code
                        cursor.getString(2), // Client Name
                        cursor.getString(3), // Phone
                        cursor.getString(4), // Address
                        cursor.getString(5), // Date
                        cursor.getString(6), // Pkg Name
                        cursor.getDouble(7), // Total
                        cursor.getString(8), // Method
                        cursor.getString(9)  // Status
                ));
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBookingData();
    }
}