package com.zeynitabq.weddingorganizer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BookingDetailActivity extends AppCompatActivity {

    TextView tvKode, tvNama, tvPaket, tvTotal, tvStatus;
    Button btnMarkLunas, btnPrintPdf;
    DatabaseHelper dbHelper;
    String idBooking, trxCode, clientName, pkgName, status;
    double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        // Ambil Data dari Intent (Dikirim dari List)
        idBooking = getIntent().getStringExtra("ID_BOOKING");
        trxCode = getIntent().getStringExtra("KODE_TRX");

        dbHelper = new DatabaseHelper(this);

        // Init Views
        tvKode = findViewById(R.id.tvDetailKode);
        tvNama = findViewById(R.id.tvDetailNama);
        tvPaket = findViewById(R.id.tvDetailPaket);
        tvTotal = findViewById(R.id.tvDetailTotal);
        tvStatus = findViewById(R.id.tvDetailStatus);
        btnMarkLunas = findViewById(R.id.btnMarkLunas);
        btnPrintPdf = findViewById(R.id.btnPrintPdf);

        loadDetailData();

        // 1. LOGIKA UPDATE LUNAS
        btnMarkLunas.setOnClickListener(v -> {
            dbHelper.updateStatusBooking(idBooking, "LUNAS");
            Toast.makeText(this, "Status Berhasil Diupdate!", Toast.LENGTH_SHORT).show();
            loadDetailData(); // Refresh tampilan
        });

        // 2. LOGIKA CETAK PDF
        btnPrintPdf.setOnClickListener(v -> {
            printInvoice();
        });
    }

    private void loadDetailData() {
        // Ambil detail lengkap dari DB berdasarkan Kode TRX
        Cursor cursor = dbHelper.getBookingByCode(trxCode);
        if (cursor.moveToFirst()) {
            clientName = cursor.getString(cursor.getColumnIndexOrThrow("client_name"));
            pkgName = cursor.getString(cursor.getColumnIndexOrThrow("pkg_name"));
            status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
            totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("total_price"));

            // Tampilkan ke Layar
            tvKode.setText("Kode Invoice: " + trxCode);
            tvNama.setText("Klien: " + clientName);
            tvPaket.setText("Paket: " + pkgName);
            tvTotal.setText("Total: Rp " + (int)totalPrice);
            tvStatus.setText("Status: " + status);

            // Sembunyikan tombol Update jika sudah Lunas
            if (status.equalsIgnoreCase("LUNAS")) {
                btnMarkLunas.setVisibility(View.GONE);
                tvStatus.setTextColor(Color.GREEN);
            } else {
                btnMarkLunas.setVisibility(View.VISIBLE);
                tvStatus.setTextColor(Color.RED);
            }
        }
    }

    // --- FITUR CETAK PDF MENGGUNAKAN ANDROID PRINT MANAGER ---
    private void printInvoice() {
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        String jobName = "Invoice Wedding " + trxCode;

        printManager.print(jobName, new MyPrintDocumentAdapter(this), null);
    }

    // Class Khusus untuk Menggambar PDF
    public class MyPrintDocumentAdapter extends PrintDocumentAdapter {
        Context context;

        public MyPrintDocumentAdapter(Context context) {
            this.context = context;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                             android.os.CancellationSignal cancellationSignal,
                             LayoutResultCallback callback, Bundle extras) {
            // Buat PDF baru
            android.print.PrintDocumentInfo info = new android.print.PrintDocumentInfo.Builder("invoice_" + trxCode + ".pdf")
                    .setContentType(android.print.PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .build();
            callback.onLayoutFinished(info, true);
        }

        @Override
        public void onWrite(android.print.PageRange[] pages, android.os.ParcelFileDescriptor destination,
                            android.os.CancellationSignal cancellationSignal,
                            WriteResultCallback callback) {

            PdfDocument pdfDocument = new PdfDocument();
            // Ukuran Halaman A4 standard (595 x 842 pixel kira-kira)
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();

            // --- MULAI MENGGAMBAR INVOICE ---

            // Judul
            paint.setColor(Color.BLACK);
            paint.setTextSize(24);
            paint.setFakeBoldText(true);
            canvas.drawText("JAY WEDDING ORGANIZER", 50, 50, paint);

            paint.setTextSize(18);
            paint.setFakeBoldText(false);
            canvas.drawText("INVOICE PEMBAYARAN", 50, 80, paint);

            // Garis
            paint.setStrokeWidth(2);
            canvas.drawLine(50, 100, 545, 100, paint);

            // Isi Data
            paint.setTextSize(14);
            int yPos = 140;

            canvas.drawText("Kode Transaksi : " + trxCode, 50, yPos, paint);
            yPos += 30;
            canvas.drawText("Nama Klien     : " + clientName, 50, yPos, paint);
            yPos += 30;
            canvas.drawText("Paket Dipilih  : " + pkgName, 50, yPos, paint);
            yPos += 30;
            canvas.drawText("Status Bayar   : " + status, 50, yPos, paint);

            // Kotak Total
            yPos += 50;
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(50, yPos-20, 545, yPos+40, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(20);
            paint.setFakeBoldText(true);
            canvas.drawText("TOTAL: Rp " + (int)totalPrice, 70, yPos+25, paint);

            // Footer
            paint.setTextSize(12);
            paint.setFakeBoldText(false);
            paint.setColor(Color.GRAY);
            canvas.drawText("Terima kasih telah mempercayakan pernikahan Anda pada kami.", 50, 800, paint);

            // Selesai Menggambar
            pdfDocument.finishPage(page);

            // Simpan File
            try {
                pdfDocument.writeTo(new java.io.FileOutputStream(destination.getFileDescriptor()));
            } catch (java.io.IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                pdfDocument.close();
            }

            callback.onWriteFinished(new android.print.PageRange[]{android.print.PageRange.ALL_PAGES});
        }
    }
}