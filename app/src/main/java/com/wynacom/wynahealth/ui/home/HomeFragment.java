package com.wynacom.wynahealth.ui.home;

import static android.content.Context.WINDOW_SERVICE;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
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
import com.google.zxing.WriterException;
import com.lakue.pagingbutton.LakuePagingButton;
import com.lakue.pagingbutton.OnPageSelectListener;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.DB_Local.Local_Data;
import com.wynacom.wynahealth.R;
import com.wynacom.wynahealth.adapter.patient.Adapter_Data_Patient;
import com.wynacom.wynahealth.adapter.patient.adapter_patient;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;
import com.wynacom.wynahealth.databinding.FragmentHomeBinding;
import com.wynacom.wynahealth.transaction.NewOrderActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    public static HomeFragment ma;
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    Local_Data local_data;
    protected Cursor cursor;

    String id_pelanggan, string_nama, string_umur,string_jk, string_hp, string_ktp, string_kota, string_kodepos,token,bearer,string_email,string_hash,
        editID,edit_name, edit_email,edit_handphone,edit_city,edit_postalcode,edit_sex,edit_dob,edit_nik,index_loop,edit_title;

    //private HomeFragment.MyCustomAdapter dataAdapter = null;
    private Adapter_Data_Patient dataAdapter = null;
    private ArrayList<adapter_patient> List;

    GlobalVariable globalVariable;

    SwipeMenuListView listView;
    private BaseApiService mApiService,ApiGetMethod;
    ArrayList<String> names = new ArrayList<String>();
    LinearLayout linearLayout,linearInfo,linearList,linearCard;
    CardView cardInfo;
    Button buttonPatient;
    FloatingActionButton fab_home;
    TextView textView_dataPatientTile;
    double cardWidth = 0;
    ProgressBar progress;
    LakuePagingButton lpb_buttonlist;
    String last_page;
    int int_last_page,max_page,NowPage;
    ImageView signature;

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
        globalVariable = (GlobalVariable) getContext().getApplicationContext();

//        progress = new ProgressBar(getContext());
////        progress.setCanc(false); // disable dismiss by tapping outside of the dialog
////        progress.show();
//        progress.setVisibility(View.VISIBLE);

        SQLiteDatabase dbU = local_data.getReadableDatabase();
        cursor = dbU.rawQuery("SELECT * FROM TB_User", null);
        cursor.moveToFirst();
        if (cursor.getCount()>0) {
            cursor.moveToPosition(0);
            id_pelanggan    = cursor.getString(0);
            string_nama     = cursor.getString(1);
            string_umur     = cursor.getString(7);
            string_jk       = cursor.getString(6);
            string_hp       = cursor.getString(2);
            string_ktp      = cursor.getString(9);
            string_kota     = cursor.getString(5);
            string_kodepos  = cursor.getString(3);
            string_email    = cursor.getString(8);
            string_hash     = cursor.getString(13);

        }
        token           = globalVariable.getToken();
        bearer          = "Bearer "+token;
        int_last_page   = 0;
        homeViewModel   = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(HomeViewModel.class);

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
        final TextView TV_gender    = binding.tampilGender;

        lpb_buttonlist  = binding.lpbButtonlist;;

        listView        = binding.listpatient;
        linearLayout    = binding.linearPatientList;
        buttonPatient   = binding.btnNewPatient;
        fab_home        = binding.fabHome;
        linearInfo      = binding.linearInformation;
        cardInfo        = binding.cardViewInfo;
        linearList      = binding.linearLayout;
        linearCard      = binding.relLayout;
        signature       = binding.homeSignature;

        progress        = binding.progressCircular;
        linearCard.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);

        textView_dataPatientTile = binding.textTitleDataPatient;

        if(int_last_page==0){
            int_last_page = 1;
        }

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
                String tampilumur = ((GlobalVariable) getContext().getApplicationContext()).dateformat(string_umur);
                IdPelanggan .setText(id_pelanggan);
                TV_Nama     .setText(string_nama);
                TV_umur     .setText(tampilumur);
                TV_gender   .setText(globalVariable.setGenerateGender(string_jk));
                TV_hp       .setText(string_hp);
                TV_KTP      .setText(string_ktp);
                TV_Kota     .setText(string_kota);
                TV_kodepos  .setText(string_kodepos);
                String nowPage = String.valueOf(int_last_page);
                refreshList(nowPage);
            }
        });

        lpb_buttonlist.setOnPageSelectListener(new OnPageSelectListener() {
            //BeforeButton Click
            @Override
            public void onPageBefore(int now_page) {
                lpb_buttonlist.addBottomPageButton(max_page,now_page);
                //Toast.makeText(getContext(), ""+now_page, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageCenter(int now_page) {
                //Toast.makeText(getContext(), "Page Number : "+now_page, Toast.LENGTH_SHORT).show();
                String nowPage = String.valueOf(now_page);
                if(dataAdapter.getCount()>0){
                    dataAdapter.clear();
                }
                refreshList(nowPage);
                //lpb_buttonlist.addBottomPageButton(max_page,now_page);
            }

            //NextButton Click
            @Override
            public void onPageNext(int now_page) {
                //Toast.makeText(getContext(), ""+now_page, Toast.LENGTH_SHORT).show();
                lpb_buttonlist.addBottomPageButton(max_page,now_page);
            }
        });

        fab_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahdata("baru");
            }
        });

        tambahpasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahdata("baru");
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(
                    getContext());
                // set item background
                openItem.setBackground(new ColorDrawable(ContextCompat.getColor(getContext(),R.color.light_tosca)));
                // set item width
                openItem.setWidth(150);
                // set item title font color
                openItem.setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.vector_add_box,null));
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(ContextCompat.getColor(getContext(),R.color.dark25)));
                // set item width
                deleteItem.setWidth(150);
                // set a icon
                deleteItem.setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.vector_edit,null));
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        Intent i = new Intent(getContext(), NewOrderActivity.class);
                        i.putExtra("index_position", String.valueOf(position+1));
                        startActivity(i);
                        //Cue.init().with(getContext()).setMessage("Pemeriksaan "+position).setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.DANGER).show();
                        break;
                    case 1:
                        adapter_patient state = List.get(position);
                        editID          = state.getID();
                        edit_title      = state.getTitle();
                        edit_name       = state.getNama();
                        edit_email      = state.getEmail();
                        edit_handphone  = state.getPhone();
                        edit_city       = state.getCity();
                        edit_postalcode = state.getPostal();
                        edit_sex        = state.getGender();
                        edit_dob        = state.getDOB();
                        edit_nik        = state.getNIK();
                        tambahdata("edit");
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

// set creator
        listView.setMenuCreator(creator);

        //generateQR();

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateQR() {
        // below line is for getting
        // the windowmanager service.
        WindowManager manager = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);

        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();

        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);

        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = Math.min(width, height);
        dimen = dimen * 3 / 4;

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        qrgEncoder = new QRGEncoder(id_pelanggan+"/"+string_hash, null, QRGContents.Type.TEXT, dimen);
        try {
            // getting our qrcode in the form of bitmap.
            bitmap = qrgEncoder.encodeAsBitmap();
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            signature.setImageBitmap(bitmap);
        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }
    }

    private void refreshList(String string_nowPage) {
        Call<ResponseBody> listCall = ApiGetMethod.getdatapatient(bearer,string_nowPage);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            JSONObject jsonObject   = jsonRESULTS.getJSONObject("data");
                            String total            = jsonObject.getString("total");
                            last_page               = jsonObject.getString("last_page");
                            String now              = jsonObject.getString("current_page");
                            NowPage                 = Integer.parseInt(now);
                            int_last_page           = Integer.parseInt(last_page);
                            max_page                = int_last_page;
                            lpb_buttonlist.addBottomPageButton(max_page,NowPage);
                            JSONArray jsonArray     = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.getJSONObject(i);
                                String id            = c.getString("id");
                                String title         = c.getString("title");
                                String nama          = c.getString("name");
                                String handphone     = c.getString("handphone");
                                String sex           = globalVariable.setGenerateGender(c.getString("sex"));
                                String dob           = c.getString("dob");
                                String nik           = c.getString("nik");
                                String city          = c.getString("city");
                                String email         = c.getString("email");
                                String postal_code   = c.getString("postal_code");
                                String tampiltanggal = globalVariable.dateformat(dob);
                                index_loop           = String.valueOf(i);
//                                JSONObject jsonObjectPatient = c.getJSONObject("patient");
//                                String patientMain  = jsonObjectPatient.getString("email");
                                    adapter_patient _states = new adapter_patient(id,title,nama,handphone,sex,tampiltanggal,nik,city,postal_code,String.valueOf(i+1),email);
                                    List.add(_states);
                                    bindData();
                            }//setPaging(string_nowPage);
                            textView_dataPatientTile.setText(getString(R.string.data_patient)+" ("+total+")");
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
        String getReturn = ((GlobalVariable) getContext().getApplicationContext()).getWidth(getActivity());
        int height = Integer.parseInt(getReturn);
        ViewGroup.LayoutParams params = linearInfo.getLayoutParams();
        params.height = height;
        linearInfo.setLayoutParams(params);
        //Toast.makeText(getContext(), "Width = "+String.valueOf(cardWidth)+"\nHeight = "+String.valueOf(height), Toast.LENGTH_SHORT).show();
        if (!(List ==null)){
            linearCard.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            textView_dataPatientTile.setText(R.string.data_patient);
            //Toast.makeText(getContext(), Integer.toString(i), Toast.LENGTH_SHORT).show();
            dataAdapter = new Adapter_Data_Patient(getContext(),R.layout.list_patient, List);
            listView.setAdapter(dataAdapter);
        }else {
            listView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        }
        progress.setVisibility(View.GONE);
    }

    private void datapatient(String fnama,String femail,String fgender,String fktp,String fkota,String fpostal,String fphone,String fdob,String title) {
        String gender = globalVariable.reverseGender(fgender);
        Map<String, Object> jsonParams = new ArrayMap<>();
        //Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();
        jsonParams.put("title",      title);
        jsonParams.put("name",       fnama);
        jsonParams.put("email",      femail);
        jsonParams.put("handphone",  fphone);
        jsonParams.put("city",       fkota);
        jsonParams.put("postal_code",fpostal);
        jsonParams.put("sex",        gender);
        jsonParams.put("dob",        fdob);
        jsonParams.put("nik",        fktp);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(jsonParams)).toString());
        mApiService.datapatient(bearer,body)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                            if (jsonRESULTS.getString("success").equals("true")){
                                if(dataAdapter.getCount()>0) {
                                    dataAdapter.clear();
                                    //listView.setAdapter(null);
                                }
                                Cue.init().with(getContext()).setMessage("Add data success").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.SUCCESS).show();
                                //Toast.makeText(getContext(),"Add data success.", Toast.LENGTH_SHORT).show();
                                refreshList("1");
                            } else {
                                Cue.init().with(getContext()).setMessage("Can not add data").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.DANGER).show();
                                //Toast.makeText(getContext(),"Can not add data.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Cue.init().with(getContext()).setMessage("Add data FAILED!").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.DANGER).show();
                        //Toast.makeText(getContext(), "Add data FAILED!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("debug", "onFailure: ERROR > " + t.toString());
                }
            });
    }

    private void editPatient(String fnama,String femail,String fgender,String fktp,String fkota,String fpostal,String fphone,String fdob,String title) {
        String gender = globalVariable.reverseGender(fgender);
        Map<String, Object> jsonParams = new ArrayMap<>();
        //Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();
        jsonParams.put("title",      title);
        jsonParams.put("name",       fnama);
        jsonParams.put("email",      femail);
        jsonParams.put("handphone",  fphone);
        jsonParams.put("city",       fkota);
        jsonParams.put("postal_code",fpostal);
        jsonParams.put("sex",        gender);
        jsonParams.put("dob",        globalVariable.reversedateformat(fdob));
        jsonParams.put("nik",        fktp);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(jsonParams)).toString());
        mApiService.PatchPatient(bearer,editID,body)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                            if (jsonRESULTS.getString("success").equals("true")){
                                if(dataAdapter.getCount()>0) {
                                    dataAdapter.clear();
                                    //listView.setAdapter(null);
                                }
                                Cue.init().with(getContext()).setMessage("Edit data success").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.SUCCESS).show();
                                //Toast.makeText(getContext(),"Add data success.", Toast.LENGTH_SHORT).show();
                                refreshList("1");
                            } else {
                                Cue.init().with(getContext()).setMessage("Can not edit data").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.DANGER).show();
                                //Toast.makeText(getContext(),"Can not add data.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Cue.init().with(getContext()).setMessage("Edit data FAILED!").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.DANGER).show();
                        //Toast.makeText(getContext(), "Add data FAILED!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("debug", "onFailure: ERROR > " + t.toString());
                }
            });
    }

    private void tambahdata(String data_type) {
        String title = null;
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.prompt_addpatient,null);
        final Calendar myCalendar= Calendar.getInstance();
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        //Toast.makeText(getContext(),editID,Toast.LENGTH_SHORT).show();

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

        java.util.List<String> list = new ArrayList<>();
        list.add("Mr.");
        list.add("Mrs.");
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

        if(data_type.equals("edit")){
            String gender = globalVariable.reverseGender(edit_sex);
            int begin;
            if(gender.equals("M")){
                begin = 0;
            }else{
                begin = 1;
            }
            //Toast.makeText(getContext(), "Gender : "+gender+" index : "+begin, Toast.LENGTH_SHORT).show();
            sp_title.setSelection(begin);
            sp_kelamin.setSelection(begin);
            name.setText(edit_name);
            ktp.setText(edit_nik);
            phone.setText(edit_handphone);
            email.setText(edit_email);
            postal.setText(edit_postalcode);
            age.setText(edit_dob);
            title = "Edit Data Pasien.";
        }else{
            title = "Tambah Pasien";
        }

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

        sp_kelamin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sp_title.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_title.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sp_kelamin.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        alertDialogBuilder
            .setTitle(title)
            .setCancelable(false)
            .setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        final String fstitle    = sp_title.getSelectedItem().toString();
                        final String fsnama     = /*sp_title.getSelectedItem().toString()+*/name.getText().toString();
                        final String fsemail    = email.getText().toString();
                        final String fsgender   = sp_kelamin.getSelectedItem().toString();
                        final String fsktp      = ktp.getText().toString();
                        final String fskota     = sp_kota.getSelectedItem().toString();
                        final String fspostal   = postal.getText().toString();
                        final String fsphone    = phone.getText().toString();
                        final String fsdob      = age.getText().toString();
                        if(data_type.equals("edit")){
                            if (!stringname(fsnama)) {
                                name.setText(edit_name);
                                name.requestFocus();
                                Toast.makeText(getContext(), "Nama Tidak Valid", Toast.LENGTH_SHORT).show();
                            } else if (!stringnik(ktp.getText().toString())) {
                                ktp.setText(edit_nik);
                                ktp.requestFocus();
                                Toast.makeText(getContext(), "NIP Tidak Sesuai", Toast.LENGTH_SHORT).show();
                            }else if (!stringphone(phone.getText().toString())) {
                                phone.setText(edit_handphone);
                                phone.requestFocus();
                                Toast.makeText(getContext(), "Nomor Telepon Tidak Valid", Toast.LENGTH_SHORT).show();
                            }else if (!isemail(email.getText().toString())){
                                email.setText(edit_email);
                                email.requestFocus();
                                Toast.makeText(getContext(), "Email TIdak Valid", Toast.LENGTH_SHORT).show();
                            }else if (!stringpostal(postal.getText().toString())) {
                                postal.setText(edit_postalcode);
                                postal.requestFocus();
                                Toast.makeText(getContext(), "Kode Pos Tidak Valid", Toast.LENGTH_SHORT).show();
                            }else if (!stringage(age.getText().toString())) {
                                age.setText(edit_dob);
                                age.requestFocus();
                                Toast.makeText(getContext(), "Harap Masukkan Tanggal Lahir", Toast.LENGTH_SHORT).show();
                            }else{
                                editPatient(fsnama,fsemail,fsgender,fsktp,fskota,fspostal,fsphone,age.getText().toString(),fstitle);
                            }
                        }else {
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
                                datapatient(fsnama,fsemail,fsgender,fsktp,fskota,fspostal,fsphone,fsdob,fstitle);
                                Toast.makeText(getContext(), fstitle, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
            .setNegativeButton("Batal", null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        //Cue.init().with(getContext()).setMessage("Button Tambah Pasien").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.PRIMARY).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
