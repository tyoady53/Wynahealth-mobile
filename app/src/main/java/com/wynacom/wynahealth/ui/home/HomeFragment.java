package com.wynacom.wynahealth.ui.home;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.github.clans.fab.FloatingActionButton;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.DB_Local.Local_Data;
import com.wynacom.wynahealth.R;
import com.wynacom.wynahealth.adapter.patient.Adapter_Data_Patient;
import com.wynacom.wynahealth.adapter.patient.adapter_patient;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;
import com.wynacom.wynahealth.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    public static HomeFragment ma;
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    Local_Data local_data;
    protected Cursor cursor;
    String id_pelanggan, string_nama, string_umur,string_jk, string_hp, string_ktp, string_kota, string_kodepos,token,bearer;
    //private HomeFragment.MyCustomAdapter dataAdapter = null;
    private Adapter_Data_Patient dataAdapter = null;
    private ArrayList<adapter_patient> List;
    SwipeMenuListView listView;
    private BaseApiService mApiService,ApiGetMethod;
    ArrayList<String> names = new ArrayList<String>();
    LinearLayout linearLayout;
    Button buttonPatient;
    FloatingActionButton fab_home;

    public static boolean stringname(String Name) {
        return Name.length() > 0;
    }
    public static boolean stringdob(String Age) {
        return Age.length() > 0;
    }
    public static boolean stringnik(String Name) {
        return Name.length() > 10;
    }
    public static boolean isemail(String email) {
        return email.contains("@")
            && email.contains("gmail.com")
            || email.contains("ymail.com")
            || email.contains("yahoo.com")
            || email.contains("yahoo.co.id")
            || email.contains("hotmail.com")
            || email.contains("live.com");
    }
    public static boolean stringphone(String phone) {
        return phone.length() > 10;
    }
    public static boolean stringpostal(String postal) {
        return postal.length() > 4;
    }
    public static boolean stringage(String age) {
        return age.length() > 0;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mApiService = UtilsApi.getAPI();
        ApiGetMethod= UtilsApi.getMethod();
        local_data  = new Local_Data(getContext());
        List = new ArrayList<adapter_patient>();
        ma = this;

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
        token           = ((GlobalVariable) getContext().getApplicationContext()).getToken();
        bearer          = "Bearer "+token;
        homeViewModel   = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView     = binding.listpatient;

        final Button tambahpasien = binding.tambahpasien;

        final TextView textView = binding.textHome;
        final TextView IdPelanggan  = binding.idPelanggan;
        final TextView TV_Nama      = binding.tampilNama;
        final TextView TV_umur      = binding.tampilUmurjk;
        final TextView TV_hp        = binding.tampilNohp;
        final TextView TV_KTP       = binding.tampilKtp;
        final TextView TV_Kota      = binding.tampilKota;
        final TextView TV_kodepos   = binding.tampilKodepos;

        linearLayout    = binding.linearPatientList;
        buttonPatient   = binding.btnNewPatient;
        fab_home        = binding.fabHome;

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
                String tampilumur = ((GlobalVariable) getContext().getApplicationContext()).dateformat(string_umur);
                IdPelanggan .setText(id_pelanggan);
                TV_Nama     .setText(string_nama);
                TV_umur     .setText(tampilumur+", "+string_jk);
                TV_hp       .setText(string_hp);
                TV_KTP      .setText(string_ktp);
                TV_Kota     .setText(string_kota);
                TV_kodepos  .setText(string_kodepos);

                refreshList();
            }
        });

        buttonPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahdata();
            }
        });

        tambahpasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahdata();
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(90);
                // set a icon
                deleteItem.setIcon(R.drawable.vertor_icon_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        Cue.init().with(getContext()).setMessage("Delete Button").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.DANGER).show();
                        break;
//                    case 0:
//                        // open
//                        break;
//                    case 1:
//                        Cue.init().with(getContext()).setMessage("Delete Button").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.DANGER).show();
//                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

// set creator
        listView.setMenuCreator(creator);

        return root;
    }

    private void tambahdata() {
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.prompt_addpatient,null);
        final Calendar myCalendar= Calendar.getInstance();
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        final EditText age         = promptsView.findViewById(R.id.prompt_umur);
        final EditText name        = promptsView.findViewById(R.id.prompt_nama);
        final EditText ktp         = promptsView.findViewById(R.id.prompt_noktp);
        final EditText postal      = promptsView.findViewById(R.id.prompt_postal);
        final EditText phone       = promptsView.findViewById(R.id.prompt_nohp);
        final EditText email       = promptsView.findViewById(R.id.prompt_email);

        final Spinner sp_title    = promptsView.findViewById(R.id.prompt_title);
        final Spinner sp_kelamin  = promptsView.findViewById(R.id.prompt_jeniskelamin);
        final Spinner sp_kota     = promptsView.findViewById(R.id.prompt_kota);

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
                        final String fsnama     = sp_title.getSelectedItem().toString()+name.getText().toString();
                        final String fsemail    = email.getText().toString();
                        final String fsgender   = sp_kelamin.getSelectedItem().toString();
                        final String fsktp      = ktp.getText().toString();
                        final String fskota     = sp_kota.getSelectedItem().toString();
                        final String fspostal   = postal.getText().toString();
                        final String fsphone    = phone.getText().toString();
                        final String fsdob      = age.getText().toString();
                        if (!stringname(fsnama)) {
                            name.setText("");
                            name.requestFocus();
                            Toast.makeText(getContext(), "Nama Tidak Valid", Toast.LENGTH_SHORT).show();
                        } else if (!stringnik(ktp.getText().toString())) {
                            ktp.setText("");
                            ktp.requestFocus();
                            Toast.makeText(getContext(), "NIP Tidak Sesuai", Toast.LENGTH_SHORT).show();
                        }else if (!stringphone(phone.getText().toString())) {
                            phone.setText("");
                            phone.requestFocus();
                            Toast.makeText(getContext(), "Nomor Telepon Tidak Valid", Toast.LENGTH_SHORT).show();
                        }else if (!isemail(email.getText().toString())){
                            email.setText("");
                            email.requestFocus();
                            Toast.makeText(getContext(), "Email TIdak Valid", Toast.LENGTH_SHORT).show();
                        }else if (!stringpostal(postal.getText().toString())) {
                            postal.setText("");
                            postal.requestFocus();
                            Toast.makeText(getContext(), "Kode Pos Tidak Valid", Toast.LENGTH_SHORT).show();
                        }else if (!stringage(age.getText().toString())) {
                            age.setText("");
                            age.requestFocus();
                            Toast.makeText(getContext(), "Harap Masukkan Tanggal Lahir", Toast.LENGTH_SHORT).show();
                        }else{
                            datapatient(fsnama,fsemail,fsgender,fsktp,fskota,fspostal,fsphone,fsdob);
                        }
                    }
                })
            .setNegativeButton("Batal", null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Cue.init().with(getContext()).setMessage("Button Tambah Pasien").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.PRIMARY).show();
    }

    private void refreshList() {
        Call<ResponseBody> listCall = ApiGetMethod.getdatapatient(bearer);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            JSONObject jsonObject = jsonRESULTS.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.getJSONObject(i);
                                String nama          = c.getString("name");
                                String handphone     = c.getString("handphone");
                                String sex           = c.getString("sex");
                                String dob           = c.getString("dob");
                                String nik           = c.getString("nik");
                                String city          = c.getString("city");
                                String postal_code   = c.getString("postal_code");
                                String tampiltanggal = ((GlobalVariable) getContext().getApplicationContext()).dateformat(dob);
                                adapter_patient _states = new adapter_patient(nama,handphone,sex,tampiltanggal,nik,city,postal_code);
                                List.add(_states);
                                Log.v("ConvertView ListCount", String.valueOf(nama));
                                bindData();
                            }
                        } else {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                            builder.setMessage("Data Patient Kosong.");
                            builder.setTitle("List Patient");
                            builder.setCancelable(true);
                            builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            android.app.AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Cue.init().with(getContext()).setMessage("Tidak ada data pasien").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.PRIMARY).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("debug", "onFailure: ERROR > " + t.toString());
            }
        });
    }

    public void bindData() {
        if (!(List ==null)){
            listView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            //Toast.makeText(getContext(), Integer.toString(i), Toast.LENGTH_SHORT).show();
            dataAdapter = new Adapter_Data_Patient(getContext(),R.layout.list_patient, List);
            listView.setAdapter(dataAdapter);
        }else {
            listView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    private void datapatient(String fnama,String femail,String fgender,String fktp,String fkota,String fpostal,String fphone,String fdob) {
        mApiService.datapatient(token,fnama,femail,fphone,fkota,fpostal,fgender,fdob,fktp)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                            if (jsonRESULTS.getString("success").equals("true")){
                                dataAdapter.clear();
                                listView.setAdapter(null);
                                Toast.makeText(getContext(),"Add data success.", Toast.LENGTH_SHORT).show();
                                refreshList();
                            } else {
                                Toast.makeText(getContext(),"Can not add data.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getContext(), "Add data FAILED!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("debug", "onFailure: ERROR > " + t.toString());
                }
            });
    }

    private class MyCustomAdapter extends ArrayAdapter<adapter_patient>
    {
        private ArrayList<adapter_patient> stateList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<adapter_patient> List) {
            super(context, textViewResourceId, List);
            this.stateList = new ArrayList<adapter_patient>();
            this.stateList.addAll(stateList);
        }

        private class ViewHolder
        {
            TextView Vnama,Vhandphone,Vsex,Vdob,Vnik,Vcity,Vpostal;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            ViewHolder holder = null;

            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null)
            {

                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                convertView = vi.inflate(R.layout.list_patient, null);

                holder = new HomeFragment.MyCustomAdapter.ViewHolder();
                holder.Vnama        = (TextView) convertView.findViewById(R.id.list_patient_name);
                holder.Vhandphone   = (TextView) convertView.findViewById(R.id.list_patient_phone);
                holder.Vsex         = (TextView) convertView.findViewById(R.id.list_patient_sex);
                holder.Vdob         = (TextView) convertView.findViewById(R.id.list_patient_dob);
                holder.Vnik         = (TextView) convertView.findViewById(R.id.list_patient_nik);
                holder.Vcity        = (TextView) convertView.findViewById(R.id.list_patient_city);
                holder.Vpostal      = (TextView) convertView.findViewById(R.id.list_patient_post);
            } else {
                holder = (HomeFragment.MyCustomAdapter.ViewHolder) convertView.getTag();
            }

            final adapter_patient state = List.get(position);

            holder.Vnama        .setText(state.getNama());
            holder.Vhandphone   .setText(state.getPhone());
            holder.Vsex         .setText(state.getGender());
            holder.Vdob         .setText(state.getDOB());
            holder.Vnik         .setText(state.getNIK());
            holder.Vcity        .setText(state.getCity());
            holder.Vpostal      .setText(state.getPostal());

            return convertView;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
