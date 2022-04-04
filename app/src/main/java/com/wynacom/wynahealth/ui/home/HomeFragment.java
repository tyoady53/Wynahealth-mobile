package com.wynacom.wynahealth.ui.home;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.wynacom.wynahealth.DB_Local.Local_Data;
import com.wynacom.wynahealth.R;
import com.wynacom.wynahealth.RegisterActivity;
import com.wynacom.wynahealth.databinding.FragmentHomeBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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

        final Button tambahpasien = binding.tambahpasien;

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
                TV_umur     .setText(string_umur+", "+string_jk);
                TV_hp       .setText(string_hp);
                TV_KTP      .setText(string_ktp);
                TV_Kota     .setText(string_kota);
                TV_kodepos  .setText(string_kodepos);
            }
        });

        tambahpasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(getContext());
                View promptsView = li.inflate(R.layout.prompt_addpatient,null);
                final Calendar myCalendar= Calendar.getInstance();
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);
                EditText age         = promptsView.findViewById(R.id.prompt_umur);
                EditText name        = promptsView.findViewById(R.id.prompt_nama);
                EditText ktp         = promptsView.findViewById(R.id.prompt_noktp);
                EditText postal      = promptsView.findViewById(R.id.prompt_postal);
                EditText phone       = promptsView.findViewById(R.id.prompt_nohp);
                EditText email       = promptsView.findViewById(R.id.prompt_email);

                Spinner sp_title    = promptsView.findViewById(R.id.prompt_title);
                Spinner sp_kelamin  = promptsView.findViewById(R.id.prompt_jeniskelamin);
                Spinner sp_kota     = promptsView.findViewById(R.id.prompt_kota);

                List<String> list = new ArrayList<>();
                list.add("Mr. ");
                list.add("Mrs. ");
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item,list);
                dataAdapter.setDropDownViewResource(R.layout.spinner_item);
                sp_title.setAdapter(dataAdapter);

                List<String> list2 = new ArrayList<>();
                list2.add("Laki-laki");
                list2.add("Perempuan");
                ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getContext(), R.layout.spinner_item,list2);
                dataAdapter2.setDropDownViewResource(R.layout.spinner_item);
                sp_kelamin.setAdapter(dataAdapter2);

                List<String> list3 = new ArrayList<>();
                list3.add("Jakarta");
                list3.add("Bandung");
                ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(getContext(), R.layout.spinner_item,list3);
                dataAdapter3.setDropDownViewResource(R.layout.spinner_item);
                sp_kota.setAdapter(dataAdapter3);

                DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH,month);
                        myCalendar.set(Calendar.DAY_OF_MONTH,day);
                        String myFormat="yyyy-MM-dd";
                        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.ENGLISH);
                        age.setText(dateFormat.format(myCalendar.getTime()));
                    }
                };

                age.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new DatePickerDialog(getContext(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                alertDialogBuilder
                    .setTitle("Tambah Pasien")
                    .setCancelable(false)
                    .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                //simpandata();
                            }
                        })
                    .setNegativeButton("Batal", null);

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                Cue.init().with(getContext()).setMessage("Button Tambah Pasien").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.PRIMARY).show();
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
