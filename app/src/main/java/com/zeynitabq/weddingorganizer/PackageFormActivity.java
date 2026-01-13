package com.zeynitabq.weddingorganizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

public class PackageFormActivity extends AppCompatActivity {

    // Komponen UI
    ImageView imgPreview;
    Button btnSelectImage, btnSave;
    TextInputEditText etName, etPrice;
    RadioGroup rgType;
    RadioButton rbIndoor, rbOutdoor;
    CheckBox cbMc, cbDrone, cbMakeup;

    // Variabel Penampung Data
    Uri selectedImageUri;
    boolean isEditMode = false;
    String idPaketToUpdate = "";
    String oldImageUri = "";
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_form);

        // Hide Action Bar
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        dbHelper = new DatabaseHelper(this);

        // Inisialisasi Views
        imgPreview = findViewById(R.id.imgPreview);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSave = findViewById(R.id.btnSave);
        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        rgType = findViewById(R.id.rgType);
        rbIndoor = findViewById(R.id.rbIndoor);
        rbOutdoor = findViewById(R.id.rbOutdoor);
        cbMc = findViewById(R.id.cbMc);
        cbDrone = findViewById(R.id.cbDrone);
        cbMakeup = findViewById(R.id.cbMakeup);

        // 1. LOGIKA PILIH GAMBAR DARI GALERI
        // Setup Launcher untuk menangkap hasil pemilihan gambar
        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();

                        // Tampilkan gambar yang dipilih ke ImageView pakai Glide
                        Glide.with(this).load(selectedImageUri).into(imgPreview);

                        // Beri hak akses permanen agar gambar tetap bisa dibuka nanti
                        try {
                            getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        // Saat tombol Pilih Foto diklik
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*"); // Hanya gambar
            imagePickerLauncher.launch(intent);
        });

        // LOGIKA CEK EDIT MODE
        if (getIntent().getBooleanExtra("IS_EDIT_MODE", false)) {
            isEditMode = true;
            idPaketToUpdate = getIntent().getStringExtra("ID_PAKET");

            // Ganti Judul Tombol
            btnSave.setText("UPDATE PAKET");

            // 1. Isi Nama & Harga
            etName.setText(getIntent().getStringExtra("DATA_NAMA"));
            double harga = getIntent().getDoubleExtra("DATA_HARGA", 0);
            etPrice.setText(String.valueOf((long)harga)); // Ubah ke long biar gak ada koma .0

            // 2. Isi Kategori (Radio Button)
            String type = getIntent().getStringExtra("DATA_TIPE");
            if (type != null && type.contains("Outdoor")) {
                rbOutdoor.setChecked(true);
            } else {
                rbIndoor.setChecked(true);
            }

            // 3. Isi Fasilitas (Checkbox)
            String extras = getIntent().getStringExtra("DATA_EXTRAS");
            if (extras != null) {
                if (extras.contains("MC")) cbMc.setChecked(true);
                if (extras.contains("Drone")) cbDrone.setChecked(true);
                if (extras.contains("Makeup")) cbMakeup.setChecked(true);
            }

            // 4. Tampilkan Gambar Lama
            oldImageUri = getIntent().getStringExtra("DATA_GAMBAR");
            if (oldImageUri != null && !oldImageUri.isEmpty()) {
                selectedImageUri = Uri.parse(oldImageUri); // Set selectedUri jadi yg lama
                Glide.with(this).load(selectedImageUri).into(imgPreview);
            }
        }

        // 2. LOGIKA SIMPAN DATA
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String priceStr = etPrice.getText().toString();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Nama dan Harga wajib diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            String type = rbIndoor.isChecked() ? "Indoor Wedding" : "Outdoor Wedding";

            StringBuilder extras = new StringBuilder();
            if (cbMc.isChecked()) extras.append("MC, ");
            if (cbDrone.isChecked()) extras.append("Drone, ");
            if (cbMakeup.isChecked()) extras.append("Makeup");

            // Gambar: Kalau user gak pilih foto baru (selectedImageUri null), pake yg lama?
            // Tapi logika di ImagePicker, selectedImageUri diisi saat pilih.
            // Di mode edit di atas, kita sudah isi selectedImageUri dengan oldImageUri.
            // Jadi aman.
            String imageString = (selectedImageUri != null) ? selectedImageUri.toString() : "";

            boolean success;
            if (isEditMode) {
                // PANGGIL UPDATE
                success = dbHelper.updatePackage(idPaketToUpdate, name, price, type, extras.toString(), imageString);
            } else {
                // PANGGIL INSERT BARU
                success = dbHelper.addPackage(name, price, type, extras.toString(), imageString);
            }

            if (success) {
                String msg = isEditMode ? "Paket Berhasil Diupdate!" : "Paket Berhasil Disimpan!";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}