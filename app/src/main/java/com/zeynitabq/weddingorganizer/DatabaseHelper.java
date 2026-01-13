package com.zeynitabq.weddingorganizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Info Database
    public static final String DATABASE_NAME = "wedding_organizer.db";
    public static final int DATABASE_VERSION = 1;

    // ==========================================
    // TABEL 1: PACKAGES (Katalog Jualan)
    // ==========================================
    public static final String TABLE_PACKAGES = "packages";
    public static final String COL_PKG_ID = "id";
    public static final String COL_PKG_NAME = "name";          // EditText (Nama Paket)
    public static final String COL_PKG_PRICE = "price";        // EditText (Harga)
    public static final String COL_PKG_TYPE = "type";          // RadioButton (Indoor/Outdoor)
    public static final String COL_PKG_EXTRAS = "extras";      // Checkbox (Fasilitas: MC, Drone, dll)
    public static final String COL_PKG_IMAGE = "image_uri";    // Upload Gambar

    // ==========================================
    // TABEL 2: BOOKINGS (Riwayat Pesanan / Invoice)
    // ==========================================
    public static final String TABLE_BOOKINGS = "bookings";
    public static final String COL_BOOK_ID = "id";

    // Data Invoice
    public static final String COL_BOOK_TRX_CODE = "trx_code"; // Kode Invoice (INV-001) - Penting buat PDF

    // Data Klien
    public static final String COL_BOOK_CLIENT = "client_name"; // EditText
    public static final String COL_BOOK_PHONE = "client_phone"; // EditText (No HP)
    public static final String COL_BOOK_ADDRESS = "client_address"; // EditText (Alamat)

    // Data Pesanan
    public static final String COL_BOOK_DATE = "wedding_date";  // DatePicker (Tanggal Resepsi)
    public static final String COL_BOOK_PKG_NAME = "pkg_name";  // Paket yang dipilih
    public static final String COL_BOOK_TOTAL = "total_price";  // Total yang harus dibayar

    // Data Pembayaran (Checkout)
    public static final String COL_BOOK_PAYMENT_METHOD = "payment_method"; // RadioButton (Cash/QRIS/Transfer)
    public static final String COL_BOOK_STATUS = "status";      // Lunas / DP

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Buat Tabel Packages
        String createTablePkg = "CREATE TABLE " + TABLE_PACKAGES + " (" +
                COL_PKG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PKG_NAME + " TEXT, " +
                COL_PKG_PRICE + " REAL, " +
                COL_PKG_TYPE + " TEXT, " +
                COL_PKG_EXTRAS + " TEXT, " +
                COL_PKG_IMAGE + " TEXT)";
        db.execSQL(createTablePkg);

        // 2. Buat Tabel Bookings (Lengkap untuk Invoice)
        String createTableBook = "CREATE TABLE " + TABLE_BOOKINGS + " (" +
                COL_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BOOK_TRX_CODE + " TEXT, " +
                COL_BOOK_CLIENT + " TEXT, " +
                COL_BOOK_PHONE + " TEXT, " +
                COL_BOOK_ADDRESS + " TEXT, " +
                COL_BOOK_DATE + " TEXT, " +
                COL_BOOK_PKG_NAME + " TEXT, " +
                COL_BOOK_TOTAL + " REAL, " +
                COL_BOOK_PAYMENT_METHOD + " TEXT, " +
                COL_BOOK_STATUS + " TEXT)";
        db.execSQL(createTableBook);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PACKAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        onCreate(db);
    }

    // ==========================================
    // CRUD PACKAGES (Produk)
    // ==========================================
    public boolean addPackage(String name, double price, String type, String extras, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PKG_NAME, name);
        values.put(COL_PKG_PRICE, price);
        values.put(COL_PKG_TYPE, type);
        values.put(COL_PKG_EXTRAS, extras); // Hasil gabungan Checkbox (misal: "MC, Video")
        values.put(COL_PKG_IMAGE, imageUri);
        long result = db.insert(TABLE_PACKAGES, null, values);
        return result != -1;
    }

    public Cursor getAllPackages() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PACKAGES, null);
    }

    // ==========================================
    // CRUD BOOKINGS (Checkout & History)
    // ==========================================

    // Fungsi Checkout (Simpan Transaksi)
    public boolean addBooking(String trxCode, String client, String phone, String address,
                              String date, String pkgName, double total,
                              String method, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOK_TRX_CODE, trxCode);
        values.put(COL_BOOK_CLIENT, client);
        values.put(COL_BOOK_PHONE, phone);
        values.put(COL_BOOK_ADDRESS, address);
        values.put(COL_BOOK_DATE, date);
        values.put(COL_BOOK_PKG_NAME, pkgName);
        values.put(COL_BOOK_TOTAL, total);
        values.put(COL_BOOK_PAYMENT_METHOD, method);
        values.put(COL_BOOK_STATUS, status);

        long result = db.insert(TABLE_BOOKINGS, null, values);
        return result != -1;
    }

    // Ambil Riwayat Pesanan
    public Cursor getAllBookings() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Urutkan dari yang terbaru (DESC)
        return db.rawQuery("SELECT * FROM " + TABLE_BOOKINGS + " ORDER BY " + COL_BOOK_ID + " DESC", null);
    }

    // Ambil Detail 1 Transaksi untuk Print PDF
    public Cursor getBookingByCode(String trxCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BOOKINGS + " WHERE " + COL_BOOK_TRX_CODE + "=?", new String[]{trxCode});
    }

    public void updateStatusBooking(String id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOK_STATUS, status);
        db.update(TABLE_BOOKINGS, values, "id=?", new String[]{id});
    }
    public boolean updateBookingFull(String id, String client, String phone, String address,
                                     String date, String pkgName, double total,
                                     String method, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOK_CLIENT, client);
        values.put(COL_BOOK_PHONE, phone);
        values.put(COL_BOOK_ADDRESS, address);
        values.put(COL_BOOK_DATE, date);
        values.put(COL_BOOK_PKG_NAME, pkgName);
        values.put(COL_BOOK_TOTAL, total);
        values.put(COL_BOOK_PAYMENT_METHOD, method);
        values.put(COL_BOOK_STATUS, status);

        long result = db.update(TABLE_BOOKINGS, values, "id=?", new String[]{id});
        return result != -1;
    }

    // HAPUS BOOKING (Sudah ada di kode sebelumnya, tapi pastikan ini ada)
    public Integer deleteBooking(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BOOKINGS, "id=?", new String[]{id});
    }

    public boolean updatePackage(String id, String name, double price, String type, String extras, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PKG_NAME, name);
        values.put(COL_PKG_PRICE, price);
        values.put(COL_PKG_TYPE, type);
        values.put(COL_PKG_EXTRAS, extras);
        values.put(COL_PKG_IMAGE, imageUri);
        long result = db.update(TABLE_PACKAGES, values, "id=?", new String[]{id});
        return result != -1;
    }

    public Integer deletePackage(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PACKAGES, "id=?", new String[]{id});
    }
}