package com.wynacom.wynahealth.ui.home;

import static android.content.Context.WINDOW_SERVICE;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
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
import com.wynacom.wynahealth.adapter.outlets.adapter_provinces;
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
        editID,edit_name, edit_email,edit_handphone,edit_city,edit_postalcode,edit_sex,edit_dob,edit_nik,index_loop,edit_title,stringPassword,stringProvince,
        flashName,flashTitle,flashEmail,flashKTP,flashCity,flashPostal,flashDOB,flashPhone,title,fsgender;

    //private HomeFragment.MyCustomAdapter dataAdapter = null;
    private Adapter_Data_Patient dataAdapter = null;
    private ArrayList<adapter_patient> List;
    private ArrayList<adapter_provinces> Provinces;

    EditText age, name, ktp, postal, phone, email;
    Spinner sp_title, sp_kelamin, sp_kota;

    GlobalVariable globalVariable;

    SwipeMenuListView listView;
    private BaseApiService mApiService,ApiGetMethod;
    ArrayList<String> names = new ArrayList<String>();
    LinearLayout linearLayout,linearList,linearCard;
    RelativeLayout linearInfo;
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
            || email.contains("outlook.com")
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
        Provinces   = new ArrayList<>();
        ma = this;
        globalVariable = (GlobalVariable) getContext().getApplicationContext();

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
            stringPassword  = cursor.getString(4);
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
                flashTitle = "Mr.";
                tambahdata("baru", "name");
            }
        });

        tambahpasien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahdata("baru", "name");
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(
                    getContext());
                // set item background
                //openItem.setBackground(new ColorDrawable(ContextCompat.getColor(getContext(),R.color.light_tosca)));
                openItem.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.swipe_background,null));
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
                //deleteItem.setBackground(new ColorDrawable(ContextCompat.getColor(getContext(),R.color.dark25)));
                deleteItem.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.swipe_background,null));
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
                adapter_patient state = List.get(position);
                switch (index) {
                    case 0:
                        Intent intent = new Intent(getContext(), NewOrderActivity.class);
                        intent.putExtra("booked",           "");
                        intent.putExtra("gender",           "");
                        intent.putExtra("count",            "");
                        intent.putExtra("type",             "new");
                        intent.putExtra("datapatient_id",   state.getID());
                        startActivity(intent);
                        break;
                    case 1:
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
                        globalVariable.setFlashTitle(edit_title);
                        globalVariable.setFlashName(edit_name);
                        globalVariable.setFlashEmail(edit_email);
                        globalVariable.setFlashKTP(edit_nik);
                        globalVariable.setFlashCity(edit_city);
                        globalVariable.setFlashPostal(edit_postalcode);
                        globalVariable.setFlashPhone(edit_handphone);
                        globalVariable.setFlashDOB(edit_dob);
                        flashTitle      = state.getTitle();
                        tambahdata("edit", "name");
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

        getProvince();

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
        globalVariable.setLast_open("home");
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
                                String sex           = c.getString("sex");
                                String dob           = c.getString("dob");
                                String nik           = c.getString("nik");
                                String email         = c.getString("email");
                                String postal_code   = c.getString("postal_code");
                                String tampiltanggal = globalVariable.dateformat(dob);
                                JSONObject cities    = c.getJSONObject("province");
                                String city          = cities.getString("name");
                                index_loop           = String.valueOf(i);
//                                JSONObject jsonObjectPatient = c.getJSONObject("patient");
//                                String patientMain  = jsonObjectPatient.getString("email");
                                    adapter_patient _states = new adapter_patient(id,title,nama,handphone,sex,tampiltanggal,nik,city,postal_code,String.valueOf(i+1),email);
                                    List.add(_states);
                                    bindData();
                            }//setPaging(string_nowPage);
                            textView_dataPatientTile.setText(getString(R.string.family_data)+" ("+total+")");
                        } else {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                            builder.setMessage("No Data available.");
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
                        SQLiteDatabase dbU = local_data.getReadableDatabase();
                        cursor = dbU.rawQuery("SELECT * FROM TB_User", null);
                        cursor.moveToFirst();
                        if (cursor.getCount()>0) {
                            cursor.moveToPosition(0);
                            stringPassword  = cursor.getString(4);
                            string_email    = cursor.getString(8);

                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                reLogin();
                            }
                        }, 2000);
                        Toast.makeText(getContext(), "Home : catch exception", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    createToast("No Data Available",Type.DANGER);
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
            textView_dataPatientTile.setText(R.string.family_data);
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
        jsonParams.put("dob",        globalVariable.reversedateformat(fdob));
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
                                createToast("Add data success",Type.SUCCESS);
                                refreshList("1");
                            } else {
                                createToast("Can not add data",Type.DANGER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        createToast("Data already exist!",Type.DANGER);
                        //Toast.makeText(getContext(), "Add data FAILED!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    createToast("Connection Error\nCheck your internet connection",Type.DANGER);
                    Log.e("debug", "onFailure: ERROR > " + t.toString());
                }
            });
    }

    private void editPatient(String fnama,String femail,String fgender,String fktp,String fkota,String fpostal,String fphone,String fdob,String title) {
        //String gender = globalVariable.reverseGender(fgender);
        Map<String, Object> jsonParams = new ArrayMap<>();
        //Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();
        jsonParams.put("title",      title);
        jsonParams.put("name",       fnama);
        jsonParams.put("email",      femail);
        jsonParams.put("handphone",  fphone);
        jsonParams.put("city",       fkota);
        jsonParams.put("postal_code",fpostal);
        jsonParams.put("sex",        fgender);
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
                                }
                                createToast("Edit data success",Type.SUCCESS);
                                refreshList("1");
                            } else {
                                createToast("Can not edit data",Type.DANGER);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        createToast("Edit data FAILED!",Type.DANGER);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    createToast("Connection Error\nCheck your internet connection",Type.DANGER);
                    Log.e("debug", "onFailure: ERROR > " + t.toString());
                }
            });
    }

    private void tambahdata(String data_type, String focus) {
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.prompt_addpatient,null);
        final Calendar myCalendar= Calendar.getInstance();
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);
        age         = promptsView.findViewById(R.id.prompt_umur);
        name        = promptsView.findViewById(R.id.prompt_nama);
        ktp         = promptsView.findViewById(R.id.prompt_noktp);
        postal      = promptsView.findViewById(R.id.prompt_postal);
        phone       = promptsView.findViewById(R.id.prompt_nohp);
        email       = promptsView.findViewById(R.id.prompt_email);

        sp_title    = promptsView.findViewById(R.id.prompt_title);
        sp_kelamin  = promptsView.findViewById(R.id.prompt_jeniskelamin);
        sp_kota     = promptsView.findViewById(R.id.prompt_kota);

        name    .setText(globalVariable.getFlashName());
        ktp     .setText(globalVariable.getFlashKTP());
        phone   .setText(globalVariable.getFlashPhone());
        email   .setText(globalVariable.getFlashEmail());
        postal  .setText(globalVariable.getFlashPostal());
        age     .setText(globalVariable.getFlashDOB());

        if(data_type.equals("edit")){
//            int begin;
//            if(edit_sex.equals("M")){
//                begin = 0;
//            }else{
//                begin = 1;
//            }
//            sp_title.setSelection(begin);
//            sp_kelamin.setSelection(begin);
            title = getString(R.string.edit_patient);
            sp_title.setEnabled(false);
            this.name.setEnabled(false);
            ktp.setEnabled(false);
            age.setEnabled(false);
        }else{
            title = getString(R.string.add_patient);
        }
        if(flashTitle.equals("Mr.")){
            sp_title.setSelection(0);
            sp_kelamin.setSelection(0);
        }else if (flashTitle.equals("Mrs.")){
            sp_title.setSelection(2);
        }

        java.util.List<String> list2 = new ArrayList<>();
        list2.add("Select City");
        for(int i = 0;i<Provinces.size();i++){
            final adapter_provinces state = Provinces.get(i);
            list2.add(state.getName());
        }
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getContext(), R.layout.spinner_item,list2);
        dataAdapter2.setDropDownViewResource(R.layout.spinner_item);
        sp_kota.setAdapter(dataAdapter2);

        sp_kota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    adapter_provinces state = Provinces.get(position-1);
                    stringProvince = state.getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(data_type.equals("edit")){
            for(int i = 0;i<Provinces.size();i++){
                final adapter_provinces state = Provinces.get(i);
                if (edit_city.equals(state.getName())){
                    sp_kota.setSelection(i+1);
                }
            }
        }

        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myFormat="dd-MMM-yyyy";
                SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.ENGLISH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH,month);
                            myCalendar.set(Calendar.DAY_OF_MONTH,day);
                            age.setText(dateFormat.format(myCalendar.getTime()));
                        }
                    },myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                //,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                //Set Today date to calendar

                final Calendar calendar2 = Calendar.getInstance();
                //Set Minimum date of calendar
                int tahun = Calendar.getInstance().get(Calendar.YEAR);
                int bulan = Calendar.getInstance().get(Calendar.MONTH);
                int tanggal = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                calendar2.set(tahun, bulan, tanggal);
                datePickerDialog.getDatePicker().setMaxDate(calendar2.getTimeInMillis());
                //datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
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
                sp_kelamin.setEnabled(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final int gender   = sp_kelamin.getSelectedItemPosition();
        if(gender==0){
            fsgender = "M";
        } else if (gender==1){
            fsgender = "F";
        }

        if(focus.equals("name")) {
            name.requestFocus();
        } else if(focus.equals("ktp")) {
            ktp.requestFocus();
        } else if(focus.equals("phone")) {
            phone.requestFocus();
        } else if(focus.equals("email")) {
            email.requestFocus();
        } else if(focus.equals("postal")) {
            postal.requestFocus();
        } else if(focus.equals("age")) {
            age.requestFocus();
        }

        alertDialogBuilder
            .setTitle(title)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.save),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        flashTitle  = sp_title.getSelectedItem().toString();
                        flashName   = name.getText().toString();
                        flashEmail  = email.getText().toString();
                        flashKTP    = ktp.getText().toString();
                        flashCity   = String.valueOf(sp_kota.getSelectedItemPosition());
                        flashPostal = postal.getText().toString();
                        flashPhone  = phone.getText().toString();
                        flashDOB    = age.getText().toString();

                        globalVariable.setFlashTitle(flashTitle);
                        globalVariable.setFlashName(flashName);
                        globalVariable.setFlashEmail(flashEmail);
                        globalVariable.setFlashKTP(flashKTP);
                        globalVariable.setFlashCity(flashCity);
                        globalVariable.setFlashPostal(flashPostal);
                        globalVariable.setFlashPhone(flashPhone);
                        globalVariable.setFlashDOB(flashDOB);

                        if(data_type.equals("edit")){
                            if (!stringname(flashName)) {
                                PromptAlert(getString(R.string.fill_name),Type.DANGER,data_type,"name");
                            }else if (!stringnik(flashKTP)) {
                                PromptAlert(getString(R.string.fill_ktp),Type.DANGER, data_type, "ktp");
                            }else if (!isemail(flashEmail)){
                                PromptAlert(getString(R.string.fill_email),Type.DANGER, data_type, "email");
                            }else if (!stringphone(flashPhone)) {
                                PromptAlert(getString(R.string.fill_phone),Type.DANGER, data_type, "phone");
                            }else if (!stringpostal(flashPostal)) {
                                PromptAlert(getString(R.string.fill_postal),Type.DANGER, data_type, "postal");
                            }else if (!stringage(flashDOB)) {
                                PromptAlert(getString(R.string.fill_dob),Type.DANGER, data_type, "age");
                            }else{
                                editPatient(flashName,flashEmail,fsgender,flashKTP,stringProvince,flashPostal,flashPhone,globalVariable.reversedateformat(flashDOB),flashTitle);
                            }
                        }else {
                            if (!stringname(flashName)) {
                                PromptAlert(getString(R.string.fill_name),Type.DANGER,data_type,"name");
                            }else if (!stringnik(flashKTP)) {
                                PromptAlert(getString(R.string.fill_ktp),Type.DANGER, data_type, "ktp");
                            }else if (!isemail(flashEmail)){
                                PromptAlert(getString(R.string.fill_email),Type.DANGER, data_type, "email");
                            }else if (!stringphone(flashPhone)) {
                                PromptAlert(getString(R.string.fill_phone),Type.DANGER, data_type, "phone");
                            }else if (!stringpostal(flashPostal)) {
                                PromptAlert(getString(R.string.fill_postal),Type.DANGER, data_type, "postal");
                            }else if (!stringage(flashDOB)) {
                                PromptAlert(getString(R.string.fill_dob),Type.DANGER, data_type, "age");
                            }else{
                                datapatient(flashName,flashEmail,fsgender,flashKTP,stringProvince,flashPostal,flashPhone,globalVariable.reversedateformat(flashDOB),flashTitle);
                            }
                        }
                    }
                })
            .setNegativeButton(getString(R.string.cancel_save), null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        //Cue.init().with(getContext()).setMessage("Button Tambah Pasien").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.PRIMARY).show();
    }

    private void reLogin() {
        if (!TextUtils.isEmpty(string_email) || !TextUtils.isEmpty(stringPassword) ){
            Map<String, Object> jsonParams = new ArrayMap<>();
//put something inside the map, could be null
            jsonParams.put("email", string_email);
            jsonParams.put("password", stringPassword);
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(jsonParams)).toString());
            //ResponseBody formLogin = new ResponseBody(input.getText().toString(), password.getText().toString());
            Call<ResponseBody> listCall = mApiService.login(body);
            listCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                            if (jsonRESULTS.getString("success").equals("true")){
                                String Stoken = jsonRESULTS.getString("token");
                                JSONObject userObj = jsonRESULTS.getJSONObject("user");
                                String hash  = userObj.getString("password");
                                local_data.UpdateToken(string_email,Stoken,hash);
                                globalVariable.setToken(Stoken);
                                token = Stoken;
                                bearer = "Bearer "+token;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshList("1");
                                    }
                                }, 2000);
                            } else {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                                builder.setMessage("Can't Login");
                                builder.setTitle("Failed");
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
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                        builder.setMessage("Tidak dapat login\nPeriksa email dan password anda.");
                        builder.setTitle("Login Gagal");
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
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("debug", "onFailure: ERROR > " + t.toString());
                }
            });}
        else{

        }
    }

    private void getProvince() {
        Call<ResponseBody> listCall = ApiGetMethod.getProvince();
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            JSONArray jsonArray   = jsonRESULTS.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.getJSONObject(i);
                                String id            = c.getString("id");
                                String nama          = c.getString("name");

                                adapter_provinces _states = new adapter_provinces(id,nama);
                                Provinces.add(_states);
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
                    createToast("Data not found",Type.DANGER);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("debug", "onFailure: ERROR > getDataPatient" + t.toString());
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                builder.setMessage("Failed loading data. Do you want to retry?");
                builder.setTitle("Error Load Data Order");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        System.exit(0);
                    }
                });
                android.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void createToast(String message, Type type) {
        Cue.init().with(getContext())
            .setMessage(message)
            .setGravity(Gravity.CENTER_VERTICAL)
            .setTextSize(20)
            .setType(type)
            .show();
    }

    private void PromptAlert(String message, Type type, String data_type, String focus) {
        Cue.init().with(getContext())
            .setMessage(message)
            .setGravity(Gravity.CENTER_VERTICAL)
            .setTextSize(20)
            .setType(type)
            .show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tambahdata(data_type,focus);
            }
        }, 500);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
