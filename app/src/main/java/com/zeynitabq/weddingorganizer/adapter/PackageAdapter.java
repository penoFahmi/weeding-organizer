package com.zeynitabq.weddingorganizer.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.zeynitabq.weddingorganizer.DatabaseHelper; // Import DB
import com.zeynitabq.weddingorganizer.PackageFormActivity; // Import Form
import com.zeynitabq.weddingorganizer.R;
import com.zeynitabq.weddingorganizer.model.PackageModel;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PackageModel> packageList;
    private DatabaseHelper dbHelper; // Tambah DatabaseHelper

    public PackageAdapter(Context context, ArrayList<PackageModel> packageList) {
        this.context = context;
        this.packageList = packageList;
        this.dbHelper = new DatabaseHelper(context); // Init DB
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_package, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PackageModel model = packageList.get(position);

        holder.tvName.setText(model.getName());
        holder.tvType.setText(model.getType());
        holder.tvExtras.setText(model.getExtras());

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.tvPrice.setText(formatRupiah.format(model.getPrice()));

        if (model.getImageUri() != null && !model.getImageUri().isEmpty()) {
            Glide.with(context).load(Uri.parse(model.getImageUri())).centerCrop().placeholder(R.drawable.ic_box).into(holder.imgPackage);
        } else {
            holder.imgPackage.setImageResource(R.drawable.ic_box);
        }

        // --- 1. LOGIKA HAPUS PAKET ---
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Hapus Paket?")
                    .setMessage("Yakin ingin menghapus " + model.getName() + "?")
                    .setPositiveButton("Hapus", (dialog, which) -> {
                        // Hapus dari Database
                        dbHelper.deletePackage(String.valueOf(model.getId()));
                        // Hapus dari Layar
                        packageList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, packageList.size());
                        Toast.makeText(context, "Paket Dihapus", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });

        // --- 2. LOGIKA EDIT PAKET ---
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, PackageFormActivity.class);
            intent.putExtra("IS_EDIT_MODE", true);
            intent.putExtra("ID_PAKET", String.valueOf(model.getId()));

            // Kirim Data Lama ke Form
            intent.putExtra("DATA_NAMA", model.getName());
            intent.putExtra("DATA_HARGA", model.getPrice()); // Double
            intent.putExtra("DATA_TIPE", model.getType());
            intent.putExtra("DATA_EXTRAS", model.getExtras());
            intent.putExtra("DATA_GAMBAR", model.getImageUri());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvType, tvExtras;
        ImageView imgPackage;
        Button btnEdit, btnDelete; // Tambah tombol

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvType = itemView.findViewById(R.id.tvType);
            tvExtras = itemView.findViewById(R.id.tvExtras);
            imgPackage = itemView.findViewById(R.id.imgPackage);

            // Init Tombol
            btnEdit = itemView.findViewById(R.id.btnEditPaket);
            btnDelete = itemView.findViewById(R.id.btnDeletePaket);
        }
    }
}