package com.wynacom.wynahealth.transaction;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anton46.stepsview.StepsView;
import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.DB_Local.Local_Data;
import com.wynacom.wynahealth.DB_Local.Order_Data;
import com.wynacom.wynahealth.OrderQRActivity;
import com.wynacom.wynahealth.R;
import com.wynacom.wynahealth.adapter.patient.Adapter_Data_Patient;
import com.wynacom.wynahealth.adapter.patient.adapter_patient;
import com.wynacom.wynahealth.adapter.product.Adapter_Data_Product;
import com.wynacom.wynahealth.adapter.product.adapter_product;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    StepsView stepsView;
    //final String[] descriptionData = {"Pilih Pasien & Lokasi","Pilih Pemeriksaan","Konfirmasi"};
    final String[] descriptionData = {"","",""};
    int currentState=0;String strView = "";

    private BaseApiService mApiService,ApiGetMethod;
    private Adapter_Data_Patient dataAdapter = null;

    private ArrayList<adapter_patient> List;
    private Adapter_Data_Product dataProduct = null;
    private ArrayList<adapter_product> list_product;

    GlobalVariable globalVariable;

    TextView TV_date,TV_time,TV_doctor,TV_address,TV_product;
    Button next,prev,process;
    String token,bearer,strFixedPosition,patient_id,snap,strTotal,cartsStatus;
    Spinner spinner,Sp_order_city,Sp_order_time;
    EditText ET_order_date,ET_order_doctor,ET_order_address;
    LinearLayout lineDataPatient,step2,step3,detail_order;
    ScrollView step1;
    TextView orderName,orderPhone,orderGender,orderDOB,orderNIK,orderCity,TV_total_orders;
    ListView listViewProduct,listViewOrder;

    Adapter_Data_Product myAppClass;
    Order_Data orderData;
    protected Cursor cursor,cursor2;

    Local_Data localData;

    final Calendar myCalendar= Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        globalVariable  = (GlobalVariable) getApplicationContext();
        token           = globalVariable.getToken();
        bearer          = "Bearer "+token;
        mApiService     = UtilsApi.getAPI();
        ApiGetMethod    = UtilsApi.getMethod();

        List            = new ArrayList<adapter_patient>();
        list_product    = new ArrayList<adapter_product>();

        myAppClass      = new Adapter_Data_Product(getApplicationContext(),R.layout.list_patient,list_product);

        orderData       = new Order_Data(getApplicationContext());
        localData       = new Local_Data(getApplicationContext());

        stepsView       = findViewById(R.id.StepView);
        next            = findViewById(R.id.next);
        prev            = findViewById(R.id.prev);
        process         = findViewById(R.id.process_order);

        TV_date         = findViewById(R.id.order_show_date);
        TV_time         = findViewById(R.id.order_show_time);
        TV_doctor       = findViewById(R.id.order_show_doctor);
        TV_address      = findViewById(R.id.order_show_address);
        TV_product      = findViewById(R.id.order_show_product);

        step1           = findViewById(R.id.order_layout1);
        step2           = findViewById(R.id.order_layout2);
        step3           = findViewById(R.id.order_layout3);

        spinner         = findViewById(R.id.spinner_patient_order);

        orderName       = findViewById(R.id.order_patient_name);
        orderPhone      = findViewById(R.id.order_patient_phone);
        orderGender     = findViewById(R.id.order_patient_sex);
        orderDOB        = findViewById(R.id.order_patient_dob);
        orderNIK        = findViewById(R.id.order_patient_nik);
        orderCity       = findViewById(R.id.order_patient_city);

        Sp_order_city   = findViewById(R.id.order_city_spinner);
        Sp_order_time   = findViewById(R.id.order_time_spinner);

        ET_order_date   = findViewById(R.id.order_date_et);
        ET_order_doctor = findViewById(R.id.order_doctor_et);
        ET_order_address= findViewById(R.id.order_address_et);

        listViewProduct = findViewById(R.id.list_product_order);

        listViewOrder   = findViewById(R.id.list_order_confirmation);
        TV_total_orders = findViewById(R.id.total_orders);

        lineDataPatient = findViewById(R.id.order_data_patient);
        lineDataPatient.setVisibility(View.GONE);

        detail_order    = findViewById(R.id.detail_order);

        getPatient();
        getProduct();

        stepsView.setLabels(descriptionData)
            .setBarColorIndicator(getApplicationContext().getResources().getColor(R.color.white))
            .setProgressColorIndicator(getApplicationContext().getResources().getColor(R.color.orange))
            .setLabelColorIndicator(getApplicationContext().getResources().getColor(R.color.white))
            .setCompletedPosition(0)
            .drawView();

        stepsView.setCompletedPosition(currentState);
        prev.setVisibility(View.GONE);
        step1.setVisibility(View.VISIBLE);
        step2.setVisibility(View.GONE);

        List<String> list = new ArrayList<>();
        list.add("Jakarta");
        list.add("Bandung");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item,list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        Sp_order_city.setAdapter(dataAdapter);

        List<String> list2 = new ArrayList<>();
        list2.add("08.00 - 10.00");
        list2.add("10.00 - 12.00");
        list2.add("12.00 - 14.00");
        list2.add("14.00 - 16.00");
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item,list2);
        dataAdapter2.setDropDownViewResource(R.layout.spinner_item);
        Sp_order_time.setAdapter(dataAdapter2);

        step2.setVisibility(View.GONE);
        step3.setVisibility(View.GONE);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!spinner.getSelectedItem().equals("Pilih")){
                    if(currentState<(descriptionData.length-1)){
                        currentState = currentState+1;
                        stepsView.setCompletedPosition(currentState).drawView();
                    }else{
                        next.setVisibility(View.GONE);
                    }
                    if(currentState>0){
                        prev.setVisibility(View.VISIBLE);
                    }
                    if(currentState==(descriptionData.length-1)){
                        next.setVisibility(View.GONE);
                    }
                    if(currentState==1){
                        step1.setVisibility(View.GONE);
                        step2.setVisibility(View.VISIBLE);
                        step3.setVisibility(View.GONE);
                        PatientOrder();
                    } else if(currentState==2){
                        step1.setVisibility(View.GONE);
                        step2.setVisibility(View.GONE);
                        process.setVisibility(View.VISIBLE);
                        step3.setVisibility(View.VISIBLE);
                        setdatalocal();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Silahkan Pilih Pasien",Toast.LENGTH_SHORT).show();
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentState<(descriptionData.length+1)){
                    currentState = currentState-1;
                    stepsView.setCompletedPosition(currentState).drawView();
                    if(currentState==0){
                        prev.setVisibility(View.GONE);
                        next.setVisibility(View.VISIBLE);
                    }
                    if(currentState<(descriptionData.length-1)){
                        next.setVisibility(View.VISIBLE);
                        TV_product.setText("");
                    }
                    if(currentState==0){
                        step1.setVisibility(View.VISIBLE);
                        step2.setVisibility(View.GONE);
                        step3.setVisibility(View.GONE);
                    }else if(currentState==1){
                        step1.setVisibility(View.GONE);
                        step2.setVisibility(View.VISIBLE);
                        process.setVisibility(View.GONE);
                        step3.setVisibility(View.GONE);
                    }
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int fixed_index = position-1;
                if(spinner.getSelectedItem().equals("Pilih")){
                    lineDataPatient.setVisibility(View.GONE);
                }else{
                    lineDataPatient.setVisibility(View.VISIBLE);
                    strFixedPosition = String.valueOf(fixed_index);
                    setDataVIew(fixed_index);
                    adapter_patient state = List.get(Integer.parseInt(strFixedPosition));
                    patient_id =state.getID();
                    Toast.makeText(getApplicationContext(), "Selected Spinner "+String.valueOf(patient_id), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };

        ET_order_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(OrderActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCarts();
                //sendData();
            }
        });
    }

    private void addCarts() {
        String userID = globalVariable.getUserID();
        String ol_product_id,datapatient_id,qty,price,ol_patient_id;
        SQLiteDatabase dbU = orderData.getReadableDatabase();
        cursor2 = dbU.rawQuery("SELECT * FROM TB_Orders", null);
        cursor2.moveToFirst();
        if (cursor2.getCount()>0) {
            cursor2.moveToPosition(0);
            cursor2.getCount();
            for (int i = 1; i <= cursor2.getCount(); i++) {
                datapatient_id  = cursor2.getString(0);
                ol_product_id   = cursor2.getString(1);
                qty             = "1";
                price           = cursor2.getString(5);
                ol_patient_id   = userID;
                sendCarts(datapatient_id,ol_product_id,qty,price,ol_patient_id);
                if(i == cursor2.getCount()){
                    sendData();
                }
//                do{
//
//                }while (i == cursor2.getCount());
            }
        }
    }

    private void sendCarts(String datapatient_id, String ol_product_id, String qty, String price, String ol_patient_id) {
        mApiService.postCarts(token,ol_product_id,datapatient_id,qty,price,ol_patient_id)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                            if (jsonRESULTS.getString("success").equals("true")){
                                cursor2.moveToNext();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
                        builder.setMessage("Gagal Kirim Data "+ol_product_id);
                        builder.setCancelable(true);
                        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("debug", "onFailure: ERROR > " + t.toString());
                }
            });
    }

    private void sendData() {
        strTotal = this.getTotal();
        mApiService.checkout(token,patient_id,"1",orderName.getText().toString(),orderPhone.getText().toString(),Sp_order_city.getSelectedItem().toString(),strTotal,ET_order_doctor.getText().toString(),ET_order_address.getText().toString())
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                            if (jsonRESULTS.getString("success").equals("true")){
                                JSONObject subObject = jsonRESULTS.getJSONObject("data");
                                snap         = subObject.getString("snap_token");
                                Intent intent = new Intent(OrderActivity.this, OrderQRActivity.class);
                                intent.putExtra("snap_token", snap);
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                                builder.setMessage("Gagal Kirim data ."+orderName.getText().toString());
                                builder.setCancelable(true);
                                builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
                        builder.setMessage("Tidak dapat login\nPeriksa email dan password anda.");
                        builder.setTitle("Login Gagal");
                        builder.setCancelable(true);
                        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("debug", "onFailure: ERROR > " + t.toString());
                }
            });
    }

    private void setdatalocal() {
        strView = "";
        TV_date.setText("Tanggal Datang : "+ET_order_date.getText());
        TV_time.setText("Waktu DDatang : "+Sp_order_time.getSelectedItem().toString());
        TV_doctor.setText("Dokter : "+ET_order_doctor.getText());
        TV_address.setText("Alamat Pasien : "+ET_order_address.getText());
        getDataOrder();
    }

    private void getProduct() {
        Call<ResponseBody> listCall = ApiGetMethod.getProducts(bearer);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            JSONObject jsonObject = jsonRESULTS.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            //                           JSONArray jsonArray = jsonRESULTS.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.getJSONObject(i);
                                String id            = c.getString("id");
                                String title         = c.getString("title");
                                String ol_category_id= c.getString("ol_category_id");
                                String description   = c.getString("description");
                                String price         = c.getString("price");
                                String stock         = c.getString("stock");
                                String discount      = c.getString("discount");
                                String slug          = c.getString("slug");
                                String image         = c.getString("image");
                                adapter_product _states = new adapter_product(id,title,ol_category_id,description,price,discount,slug,image,false);
                                list_product.add(_states);
                                bindDataProduct();
                            }
                        } else {
                            Cue.init().with(getApplicationContext()).setMessage("Tidak ada data pasien").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.PRIMARY).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Cue.init().with(getApplicationContext()).setMessage("Tidak ada data pasien").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.PRIMARY).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("debug", "onFailure: ERROR > " + t.toString());
            }
        });
    }

    private void bindDataProduct() {
        dataProduct = new Adapter_Data_Product(getApplicationContext(),R.layout.list_product, list_product);
        listViewProduct.setAdapter(dataProduct);
    }

    private void updateLabel() {
        String myFormat="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.ENGLISH);
        ET_order_date.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void setDataVIew(int fixed_index) {
        adapter_patient state = List.get(fixed_index);
        orderName   .setText(state.getNama());
        orderPhone  .setText(state.getPhone());
        orderGender .setText(state.getGender());
        orderDOB    .setText(state.getDOB());
        orderNIK    .setText(state.getNIK());
        orderCity   .setText(state.getCity());
    }

    private void getPatient() {
        Call<ResponseBody> listCall = ApiGetMethod.getdatapatient(bearer,"1");
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            JSONObject jsonObject = jsonRESULTS.getJSONObject("data");
//                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.getJSONObject(i);
                                String id            = c.getString("id");
                                String nama          = c.getString("name");
                                String handphone     = c.getString("handphone");
                                String sex           = c.getString("sex");
                                String dob           = c.getString("dob");
                                String nik           = c.getString("nik");
                                String city          = c.getString("city");
                                String postal_code   = c.getString("postal_code");
                                String tampiltanggal = globalVariable.dateformat(dob);

                                adapter_patient _states = new adapter_patient(id,nama,handphone,sex,tampiltanggal,nik,city,postal_code,String.valueOf(i+1));
                                List.add(_states);
                                setspinner();
                            }
                        } else {
                            Cue.init().with(getApplicationContext()).setMessage("Tidak ada data pasien").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.PRIMARY).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Cue.init().with(getApplicationContext()).setMessage("Tidak ada data pasien").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.PRIMARY).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("debug", "onFailure: ERROR > " + t.toString());
            }
        });
    }

    private void setspinner() {
        dataAdapter = new Adapter_Data_Patient(getApplicationContext(),R.layout.list_patient, List);
//        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
//        spinner.setAdapter(dataAdapter);
        int length = dataAdapter.getCount();
        List<String> list2 = new ArrayList<>();
        list2.add("Pilih");
        for(int i = 0;i<length;i++){
            final adapter_patient state = List.get(i);
            list2.add(state.getNama());
        }
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item,list2);
        dataAdapter2.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(dataAdapter2);
    }

    private void getDataOrder() {
        ArrayList<HashMap<String, String>> userList = orderData.GetUsers();
        ListAdapter adapter = new SimpleAdapter(OrderActivity.this, userList, R.layout.list_order_confirmation,
        new String[]{"name","description","price","discount","total"}, new int[]{R.id.confirm_product_name, R.id.confirm_product_desc, R.id.confirm_product_price,R.id.confirm_product_discount,R.id.confirm_product_subtotal});
        listViewOrder.setAdapter(adapter);
        TV_product.setText(strView);
        Locale localeID = new Locale("in", "ID");
        NumberFormat nf = NumberFormat.getCurrencyInstance(localeID);
        Double total = Double.parseDouble(this.getTotal());
        String s = nf.format(total);
        TV_total_orders.setText(s);
    }

    public String getTotal(){
        String product_id,ol_category_id,title,description,price,discount,image,slug = null;
        Double total = 0.0;
        SQLiteDatabase dbU = orderData.getReadableDatabase();
        cursor = dbU.rawQuery("SELECT * FROM TB_Orders", null);
        cursor.moveToFirst();
        if (cursor.getCount()>0) {
            cursor.moveToPosition(0);
            for (int i = 0; i < cursor.getCount(); i++) {
                price = cursor.getString(5);
                discount = cursor.getString(6);

                double a = Double.parseDouble(price);
                double b = Double.parseDouble(discount);
                double c = a * b;
                double subTotal = a - c;
                total = total + subTotal;
                cursor.moveToNext();
            }slug = String.valueOf(total);
        }
        return slug;
    }

    @Override
    public void onBackPressed() {
        //
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
        builder.setMessage("Kembali ke menu sebelumnya akan membatalkan pesanan.\nAnda ingin melanjutkan?");
        builder.setCancelable(true);
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                orderData.HapusData();
                OrderActivity.super.onBackPressed();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void PatientOrder(){
        globalVariable.setPatient_id(patient_id);
    }
}
