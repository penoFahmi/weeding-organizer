package com.zeynitabq.weddingorganizer;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class BookingFormActivity extends AppCompatActivity {

    // UI Components
    TextInputEditText etClientName, etClientPhone, etClientAddress;
    Button btnPickDate, btnCheckout;
    Spinner spinnerPaket;
    RadioGroup rgPayment;
    RadioButton rbCash, rbTransfer, rbQris;
    CheckBox cbLunas;
    LinearLayout layoutInfoBayar;
    TextView tvInfoRek;
    ImageView imgQris;

    DatabaseHelper dbHelper;

    // Variabel Bantuan
    String selectedDate = "";
    ArrayList<String> packageNames = new ArrayList<>(); // Untuk list di Spinner
    ArrayList<Double> packagePrices = new ArrayList<>(); // Untuk simpan harga paket
    boolean isEditMode = false;
    String idBookingToUpdate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_form);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        dbHelper = new DatabaseHelper(this);

        // Inisialisasi Views
        etClientName = findViewById(R.id.etClientName);
        etClientPhone = findViewById(R.id.etClientPhone);
        etClientAddress = findViewById(R.id.etClientAddress);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnCheckout = findViewById(R.id.btnCheckout);
        spinnerPaket = findViewById(R.id.spinnerPaket);
        rgPayment = findViewById(R.id.rgPayment);
        rbCash = findViewById(R.id.rbCash);
        rbTransfer = findViewById(R.id.rbTransfer);
        rbQris = findViewById(R.id.rbQris);
        cbLunas = findViewById(R.id.cbLunas);

        layoutInfoBayar = findViewById(R.id.layoutInfoBayar);
        tvInfoRek = findViewById(R.id.tvInfoRek);
        imgQris = findViewById(R.id.imgQris);

        // 1. SETUP SPINNER (Ambil Data Paket dari DB)
        loadPackagesToSpinner();

        // 2. SETUP DATE PICKER
        btnPickDate.setOnClickListener(v -> showDatePicker());

        // 3. LOGIKA CHECKOUT
        btnCheckout.setOnClickListener(v -> processCheckout());

        rgPayment.setOnCheckedChangeListener((group, checkedId) -> {
            layoutInfoBayar.setVisibility(View.VISIBLE);
            imgQris.setVisibility(View.GONE); // Default QRIS sembunyi

            if (checkedId == R.id.rbTransfer) {
                tvInfoRek.setText("BCA: 888-000-9999\na.n Zay Wedding Organizer");
            } else if (checkedId == R.id.rbQris) {
                tvInfoRek.setText("Scan QRIS berikut:");
                imgQris.setVisibility(View.VISIBLE); // Munculkan QRIS
                // Pastikan kamu punya gambar qris.png di drawable atau pakai ic_box sementara
            } else {
                // Kalau Cash
                layoutInfoBayar.setVisibility(View.GONE);
            }
        });
        if (getIntent().getBooleanExtra("IS_EDIT_MODE", false)) {
            isEditMode = true;
            idBookingToUpdate = getIntent().getStringExtra("ID_BOOKING");

            // Ubah Judul Tombol
            btnCheckout.setText("UPDATE DATA PESANAN");

            // Isi Form dengan Data Lama
            etClientName.setText(getIntent().getStringExtra("DATA_NAMA"));
            etClientPhone.setText(getIntent().getStringExtra("DATA_HP"));
            etClientAddress.setText(getIntent().getStringExtra("DATA_ALAMAT"));

            selectedDate = getIntent().getStringExtra("DATA_TANGGAL");
            btnPickDate.setText("TANGGAL: " + selectedDate);
        }
    }

    private void loadPackagesToSpinner() {
        Cursor cursor = dbHelper.getAllPackages();
        packageNames.clear();
        packagePrices.clear();

        if (cursor.getCount() == 0) {
            packageNames.add("Belum ada paket tersedia");
            packagePrices.add(0.0);
        } else {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                packageNames.add(name + " (Rp " + (int)price + ")"); // Tampilan di Spinner
                packagePrices.add(price); // Simpan harga asli di list terpisah
            }
        }

        // Pasang ke Spinner Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, packageNames);
        spinnerPaket.setAdapter(adapter);
    }


    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    // Format tanggal jadi DD-MM-YYYY
                    selectedDate = dayOfMonth + "-" + (month1 + 1) + "-" + year1;
                    btnPickDate.setText("TANGGAL: " + selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void processCheckout() {
        String name = etClientName.getText().toString();
        String phone = etClientPhone.getText().toString();
        String address = etClientAddress.getText().toString();

        // Validasi
        if (name.isEmpty() || phone.isEmpty() || selectedDate.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi data & tanggal!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (packageNames.get(0).equals("Belum ada paket tersedia")) {
            Toast.makeText(this, "Buat Paket dulu sebelum transaksi!", Toast.LENGTH_LONG).show();
            return;
        }

        // Ambil Data dari Pilihan User
        int selectedPkgIndex = spinnerPaket.getSelectedItemPosition();
        String pkgNameFull = packageNames.get(selectedPkgIndex); // "Paket Gold (Rp 5000)"
        String pkgNameClean = pkgNameFull.split("\\(")[0].trim(); // Ambil nama saja "Paket Gold"
        double finalPrice = packagePrices.get(selectedPkgIndex);

        // Ambil Metode Pembayaran
        String method = "Cash";
        if (rbTransfer.isChecked()) method = "Transfer";
        else if (rbQris.isChecked()) method = "QRIS";

        // Status Lunas
        String status = cbLunas.isChecked() ? "LUNAS" : "DP / BELUM LUNAS";

        // Generate Kode Invoice Unik (Misal: INV-20260112-99)
        String dateNow = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        int randomNum = new Random().nextInt(900) + 100;
        String trxCode = "INV-" + dateNow + "-" + randomNum;

//        // SIMPAN KE DATABASE
//        boolean success = dbHelper.addBooking(trxCode, name, phone, address, selectedDate, pkgNameClean, finalPrice, method, status);
//
//        if (success) {
//            Toast.makeText(this, "Checkout Berhasil! Kode: " + trxCode, Toast.LENGTH_LONG).show();
//            finish(); // Kembali ke List Booking
//        } else {
//            Toast.makeText(this, "Transaksi Gagal!", Toast.LENGTH_SHORT).show();
//        }
        boolean success;

        if (isEditMode) {
            // JIKA MODE EDIT -> UPDATE
            success = dbHelper.updateBookingFull(idBookingToUpdate, name, phone, address,
                    selectedDate, pkgNameClean, finalPrice, method, status);
        } else {
            // JIKA MODE BARU -> INSERT (Add)
            success = dbHelper.addBooking(trxCode, name, phone, address,
                    selectedDate, pkgNameClean, finalPrice, method, status);
        }

        if (success) {
            String msg = isEditMode ? "Data Berhasil Diupdate!" : "Checkout Berhasil!";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal memproses data!", Toast.LENGTH_SHORT).show();
        }
    }
}