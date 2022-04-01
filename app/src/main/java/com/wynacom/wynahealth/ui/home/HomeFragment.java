package com.wynacom.wynahealth.ui.home;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wynacom.wynahealth.DB_Local.Local_Data;
import com.wynacom.wynahealth.R;
import com.wynacom.wynahealth.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    Local_Data local_data;
    protected Cursor cursor;
    String id_pelanggan, string_nama, string_umur,string_jk, string_hp, string_ktp, string_kota, string_kodepos;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        local_data  = new Local_Data(getContext());

        SQLiteDatabase dbU = local_data.getReadableDatabase();
        cursor = dbU.rawQuery("SELECT * FROM TB_User", null);
        cursor.moveToFirst();
        if (cursor.getCount()>0){
            cursor.moveToPosition(0);
            id_pelanggan    = cursor.getString(0);
            string_nama     = cursor.getString(1);
            string_umur     = cursor.getString(7);
            string_jk       = cursor.getString(6);
            string_hp       = cursor.getString(2);
            string_ktp      = cursor.getString(9);
            string_kota     = cursor.getString(5);
            string_kodepos  = cursor.getString(3);
        }
        homeViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        final TextView IdPelanggan  = binding.idPelanggan;
        final TextView TV_Nama      = binding.tampilNama;
        final TextView TV_umur      = binding.tampilUmurjk;
        final TextView TV_hp        = binding.tampilNohp;
        final TextView TV_KTP       = binding.tampilKtp;
        final TextView TV_Kota      = binding.tampilKota;
        final TextView TV_kodepos   = binding.tampilKodepos;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
                IdPelanggan .setText(id_pelanggan);
                TV_Nama     .setText(string_nama);
                TV_umur     .setText(string_umur+" Tahun, "+string_jk);
                TV_hp       .setText(string_hp);
                TV_KTP      .setText(string_ktp);
                TV_Kota     .setText(string_kota);
                TV_kodepos  .setText(string_kodepos);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
