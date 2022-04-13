package com.wynacom.wynahealth.transaction;

import android.app.DatePickerDialog;
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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.anton46.stepsview.StepsView;
import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    StepsView stepsView;
    //final String[] descriptionData = {"Pilih Pasien & Lokasi","Pilih Pemeriksaan","Konfirmasi"};
    final String[] descriptionData = {"","",""};
    int currentState=0;
    private BaseApiService mApiService,ApiGetMethod;
    private Adapter_Data_Patient dataAdapter = null;
    private ArrayList<adapter_patient> List;
    Button next,prev;
    String token,bearer,strFixedPosition;
    Spinner spinner,Sp_order_city,Sp_order_time;
    EditText ET_order_date,ET_order_doctor;
    LinearLayout lineDataPatient;
    TextView orderName,orderPhone,orderGender,orderDOB,orderNIK,orderCity;
    final Calendar myCalendar= Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        token           = ((GlobalVariable) getApplicationContext()).getToken();
        bearer          = "Bearer "+token;
        mApiService     = UtilsApi.getAPI();
        ApiGetMethod    = UtilsApi.getMethod();

        List            = new ArrayList<adapter_patient>();

        stepsView       = findViewById(R.id.StepView);
        next            = findViewById(R.id.next);
        prev            = findViewById(R.id.prev);

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

        lineDataPatient = findViewById(R.id.order_data_patient);
        lineDataPatient.setVisibility(View.GONE);

        getPatient();

        stepsView.setLabels(descriptionData)
            .setBarColorIndicator(getApplicationContext().getResources().getColor(R.color.notwhite))
            .setProgressColorIndicator(getApplicationContext().getResources().getColor(R.color.buttom_green))
            .setLabelColorIndicator(getApplicationContext().getResources().getColor(R.color.white))
            .setCompletedPosition(0)
            .drawView();

        stepsView.setCompletedPosition(currentState);
        prev.setVisibility(View.GONE);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    //Toast.makeText(getApplicationContext(), "Selected index number "+String.valueOf(fixed_index), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
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

        Call<ResponseBody> listCall = ApiGetMethod.getdatapatient(bearer);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
//                            JSONObject jsonObject = jsonRESULTS.getJSONObject("data");
//                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            JSONArray jsonArray = jsonRESULTS.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.getJSONObject(i);
                                String nama          = c.getString("name");
                                String handphone     = c.getString("handphone");
                                String sex           = c.getString("sex");
                                String dob           = c.getString("dob");
                                String nik           = c.getString("nik");
                                String city          = c.getString("city");
                                String postal_code   = c.getString("postal_code");
                                String tampiltanggal = ((GlobalVariable) getApplicationContext()).dateformat(dob);

                                adapter_patient _states = new adapter_patient(nama,handphone,sex,tampiltanggal,nik,city,postal_code,String.valueOf(i+1));
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
}
