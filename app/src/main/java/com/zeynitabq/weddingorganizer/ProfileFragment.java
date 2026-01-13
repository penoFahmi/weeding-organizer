package com.zeynitabq.weddingorganizer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    Button btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnLogout = view.findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            // Dialog Konfirmasi sebelum keluar
            new AlertDialog.Builder(getContext())
                    .setTitle("Keluar Aplikasi")
                    .setMessage("Apakah Anda yakin ingin logout?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        // Proses Logout
                        Toast.makeText(getContext(), "Berhasil Logout", Toast.LENGTH_SHORT).show();

                        // Kembali ke Login Activity
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        // Hapus history activity agar user tidak bisa tekan 'Back' untuk kembali
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });

        return view;
    }
}