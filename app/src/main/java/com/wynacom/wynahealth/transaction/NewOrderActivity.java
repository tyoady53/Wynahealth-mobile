package com.wynacom.wynahealth.transaction;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.MainActivity;
import com.wynacom.wynahealth.R;
import com.wynacom.wynahealth.adapter.patient.Adapter_Data_Patient;
import com.wynacom.wynahealth.adapter.patient.adapter_patient;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;

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
import java.util.TimeZone;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewOrderActivity extends AppCompatActivity {

    private ArrayList<adapter_patient> List;
    String token,bearer,strFixedPosition,patient_id,snap,strTotal,strDoctor,strCompany,id_user,gender,
        booked,ol_patient_id,datapatient_id,ol_company_id,dokter,perusahaan,service_date,ol_invoice_id;
    Spinner Sp_order_city,Sp_order_time,spinner;
    EditText ET_order_date,ET_order_doctor,ET_order_address;
    GlobalVariable globalVariable;
    TextView orderName,orderPhone,orderGender,orderDOB,orderNIK,orderCity,TV_total_orders;
    Button next1,next,prev;
    LinearLayout step1,step2,lineDataPatient;
    int cartsStatus,fixed_index;

    final Calendar myCalendar= Calendar.getInstance();

    private Adapter_Data_Patient dataAdapter = null;

    private BaseApiService mApiService,ApiGetMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        globalVariable  = (GlobalVariable) getApplicationContext();
        token           = globalVariable.getToken();
        bearer          = "Bearer "+token;
        mApiService     = UtilsApi.getAPI();
        ApiGetMethod    = UtilsApi.getMethod();

        id_user         = getIntent().getStringExtra("index_position");

        List            = new ArrayList<adapter_patient>();

        next            = findViewById(R.id.next);

        spinner         = findViewById(R.id.spinner_patient_order);

        lineDataPatient = findViewById(R.id.order_data_patient);
        lineDataPatient.setVisibility(View.GONE);

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

        java.util.List<String> list = new ArrayList<>();
        list.add("Jakarta");
        list.add("Bandung");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item,list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        Sp_order_city.setAdapter(dataAdapter);

        List<String> list2 = new ArrayList<>();
        list2.add("08.00 - 10.00");
        list2.add("10.00 - 12.00");
        list2.add("12.00 - 14.00");
        list2.add("14.00 - 16.00");
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item,list2);
        dataAdapter2.setDropDownViewResource(R.layout.spinner_item);
        Sp_order_time.setAdapter(dataAdapter2);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ed_text = ET_order_date.getText().toString().trim();
                if(spinner.getSelectedItem().toString().equals("Pilih")){
                    Toast.makeText(getApplicationContext(),"Silahkan Pilih Pasien",Toast.LENGTH_SHORT).show();
                }else if(ed_text.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Silahkan Pilih Tanggal Datang",Toast.LENGTH_SHORT).show();
                }else{
                    globalVariable.setOrderId(patient_id);
                    generateBooking();
                }
            }
        });

        ET_order_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                //set time zone
                calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

                DatePickerDialog datePickerDialog = new DatePickerDialog(NewOrderActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH,month);
                            myCalendar.set(Calendar.DAY_OF_MONTH,day);
                            updateLabel();
                        }
                    },myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

                //Set Today date to calendar
                final Calendar calendar2 = Calendar.getInstance();
                //Set Minimum date of calendar
                int tahun = Calendar.getInstance().get(Calendar.YEAR);
                int bulan = Calendar.getInstance().get(Calendar.MONTH);
                int tanggal = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                calendar2.set(tahun, bulan, tanggal);
                datePickerDialog.getDatePicker().setMinDate(calendar2.getTimeInMillis());
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(OrderActivity.this, "Selected index : "+String.valueOf(position), Toast.LENGTH_SHORT).show();
                fixed_index = position-1;
                if(spinner.getSelectedItem().equals("Pilih")){
                    lineDataPatient.setVisibility(View.GONE);
                }else{
                    lineDataPatient.setVisibility(View.VISIBLE);
                    strFixedPosition = String.valueOf(fixed_index);
                    setDataVIew(fixed_index);
                    adapter_patient state = List.get(Integer.parseInt(strFixedPosition));
                    patient_id = state.getID();
                    //Toast.makeText(getApplicationContext(), "Selected Spinner "+String.valueOf(patient_id), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getPatient();
    }

    private void generateBooking() {
        if(ET_order_doctor.getText().toString().equals("")){
            strDoctor = "-";
        } else {
            strDoctor = ET_order_doctor.getText().toString();
        }
        if(ET_order_address.getText().toString().equals("")){
            strCompany = "-";
        } else {
            strCompany = ET_order_address.getText().toString();
        }
        Map<String, Object> jsonParams = new ArrayMap<>();
//put something inside the map, could be null
        jsonParams.put("datapatient_id" , patient_id);
        jsonParams.put("ol_company_id"  , "1");
        jsonParams.put("service_date"   , ET_order_date.getText().toString());
        jsonParams.put("dokter"         , strDoctor);
        jsonParams.put("perusahaan"     , strCompany);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(jsonParams)).toString());
        //ResponseBody formLogin = new ResponseBody(input.getText().toString(), password.getText().toString());
        Call<ResponseBody> listCall = mApiService.generateNew(bearer,body);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            JSONObject subObject = jsonRESULTS.getJSONObject("data");
                            globalVariable.setBooked             (subObject.getString("booked"));
                            globalVariable.setOl_patient_id      (subObject.getString("ol_patient_id"));
                            globalVariable.setDatapatient_id     (subObject.getString("datapatient_id"));
                            globalVariable.setOl_company_id      (subObject.getString("ol_company_id"));
                            globalVariable.setDokter             (subObject.getString("dokter"));
                            globalVariable.setPerusahaan         (subObject.getString("perusahaan"));
                            globalVariable.setService_date       (subObject.getString("service_date"));
                            globalVariable.setGender             (subObject.getString("gender"));
                            globalVariable.setOl_invoice_id      (subObject.getString("id"));
                            globalVariable.setPatient_id(patient_id);
                            Intent intent = new Intent(NewOrderActivity.this, SelectProductActivity.class);
                            intent.putExtra("name",     spinner.getSelectedItem().toString());
                            intent.putExtra("booked",   subObject.getString("booked"));
                            intent.putExtra("gender",   subObject.getString("gender"));
                            intent.putExtra("type",     "new");
                            Toast.makeText(NewOrderActivity.this, "Gender = "+subObject.getString("gender"), Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(NewOrderActivity.this);
                            builder.setMessage(jsonRESULTS.getString("message"));
                            builder.setCancelable(true);
                            builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(NewOrderActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                                    startActivity(intent);
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewOrderActivity.this);
                    builder.setMessage("Tidak dapat terhubung ke server.\nApakah anda ingin mengulangi proses Login?");
                    builder.setTitle("Login Gagal");
                    builder.setCancelable(true);
                    builder.setNegativeButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("Tidak, dan Keluar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            System.exit(0);
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("debug", "onFailure: ERROR > " + t.toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(NewOrderActivity.this);
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
            }
        });
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

                                adapter_patient _states = new adapter_patient(id,title,nama,handphone,sex,tampiltanggal,nik,city,postal_code,String.valueOf(i+1),email);
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
        int length = dataAdapter.getCount();
        java.util.List<String> list2 = new ArrayList<>();
        list2.add("Pilih");
        for(int i = 0;i<length;i++){
            final adapter_patient state = List.get(i);
            list2.add(state.getNama());
        }
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item,list2);
        dataAdapter2.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(dataAdapter2);
        if(!id_user.equals("")){
            spinner.setSelection(Integer.parseInt(id_user));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(intent);
    }
}
