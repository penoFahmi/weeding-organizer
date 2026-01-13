package com.zeynitabq.weddingorganizer;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ProfileFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout untuk fragment ini
        // Ganti R.layout.fragment_home sesuai nama fragmentnya
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}