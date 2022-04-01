package com.wynacom.wynahealth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.wynacom.wynahealth.DB_Local.Local_Data;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;

public class RegisterActivity extends AppCompatActivity {

    Spinner sp_title,sp_kelamin,sp_kota;
    Local_Data local_data;
    CheckBox checkBox;
    CardView cardcheck;
    Button btn_captcha;
    String SITE_KEY = "6LeqBcoeAAAAAL32z64UzIjN94LUAl2G1abTIX9f";
    String SECRET_KEY = "6LeqBcoeAAAAAN84pUQn7M3lO7zBCdyFSzqMjfan";
    EditText email,name,ktp,passwords,passwordsrep,postal,phone,age;

    private ProgressDialog nDialog;
    private BaseApiService mApiService;
    private Handler mHandler = new Handler();
    final Calendar myCalendar= Calendar.getInstance();

    Button btn_regis;

    CheckBox checkBoxsk;

    RequestQueue queue;

    public static boolean Name(String Name) {
        return Name.length() > 0;
    }
    public static boolean Age(String Age) {
        return Age.length() > 0;
    }
    public static boolean NIK(String Name) {
        return Name.length() > 10;
    }
    public static boolean Passw(String Name) {
        return Name.length() > 7;
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
    public static boolean phone(String phone) {
        return phone.length() > 10;
    }
    public static boolean Postal(String phone) {
        return phone.length() > 4;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        local_data  = new Local_Data(getApplicationContext());
        nDialog = new ProgressDialog( RegisterActivity.this);
        mApiService = UtilsApi.getAPI();

        checkBox = findViewById(R.id.chckbox);
        cardcheck= findViewById(R.id.card_chck);
        btn_captcha = findViewById(R.id.btn_captcha);

        sp_title    = findViewById(R.id.regis_title);
        sp_kelamin  = findViewById(R.id.regis_jeniskelamin);
        sp_kota     = findViewById(R.id.regis_kota);

        email       = findViewById(R.id.regis_email);
        name        = findViewById(R.id.regis_nama);
        ktp         = findViewById(R.id.regis_noktp);
        postal      = findViewById(R.id.regis_postal);
        phone       = findViewById(R.id.regis_nohp);
        age         = findViewById(R.id.regis_umur);

        passwords   = findViewById(R.id.regis_password);
        passwordsrep= findViewById(R.id.regis_password_rep);

        btn_regis   = findViewById(R.id.register_request);
        checkBoxsk  = findViewById(R.id.regis_sk_chckbox);

        List<String> list = new ArrayList<>();
        list.add("Mr. ");
        list.add("Mrs. ");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item,list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        sp_title.setAdapter(dataAdapter);

        List<String> list2 = new ArrayList<>();
        list2.add("Laki-Laki");
        list2.add("Perempuan");
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item,list2);
        dataAdapter2.setDropDownViewResource(R.layout.spinner_item);
        sp_kelamin.setAdapter(dataAdapter2);

        List<String> list3 = new ArrayList<>();
        list3.add("Jakarta");
        list3.add("Bandung");
        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this, R.layout.spinner_item,list3);
        dataAdapter3.setDropDownViewResource(R.layout.spinner_item);
        sp_kota.setAdapter(dataAdapter3);

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(RegisterActivity.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btn_regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBoxsk.isChecked()){
                    String pass = passwords.getText().toString();
                    String passR= passwordsrep.getText().toString();
                    if (!Name(name.getText().toString())) {
                        name.setText("");
                        name.requestFocus();
                        Toast.makeText(getApplicationContext(), "Nama Tidak Valid", Toast.LENGTH_SHORT).show();
                    } else if (!NIK(ktp.getText().toString())) {
                        ktp.setText("");
                        ktp.requestFocus();
                        Toast.makeText(getApplicationContext(), "NIP Tidak Sesuai", Toast.LENGTH_SHORT).show();
                    }else if (!phone(phone.getText().toString())) {
                        phone.setText("");
                        phone.requestFocus();
                        Toast.makeText(getApplicationContext(), "Nomor Telepon Tidak Valid", Toast.LENGTH_SHORT).show();
                    }else if (!isemail(email.getText().toString())){
                        email.setText("");
                        email.requestFocus();
                        Toast.makeText(getApplicationContext(), "Email TIdak Valid", Toast.LENGTH_SHORT).show();
                    }else if (!Passw(passwords.getText().toString())) {
                        passwords.setText("");
                        passwords.requestFocus();
                        Toast.makeText(getApplicationContext(), "Password harus lebih dari 8 karakter", Toast.LENGTH_SHORT).show();
                    }else if (!pass.equals(passR)) {
                        passwords.setText("");passwordsrep.setText("");
                        passwords.requestFocus();
                        Toast.makeText(getApplicationContext(), "Password Tidak Sama", Toast.LENGTH_SHORT).show();
                    }else if (!Postal(postal.getText().toString())) {
                        postal.setText("");
                        postal.requestFocus();
                        Toast.makeText(getApplicationContext(), "Kode Pos Tidak Valid", Toast.LENGTH_SHORT).show();
                    }else{
                        RegisterRequest();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Silahkan setujui Syarat dan Ketentuan untuk melanjutkan", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateLabel(){
        String myFormat="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.ENGLISH);
        age.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void RegisterRequest() {
        String namalengkap,stringemail,stringjk,stringktp,stringkota,stringkodepos,stringhp,stringumur,passwd,passwdr;
        namalengkap = sp_title.getSelectedItem().toString() + " " + name.getText().toString();
        stringemail = email.getText().toString();
        stringjk    = sp_kelamin.getSelectedItem().toString();
        stringktp   = ktp.getText().toString();
        stringkota  = sp_kota.getSelectedItem().toString();
        stringkodepos = postal.getText().toString();
        stringhp    = phone.getText().toString();
        stringumur  = age.getText().toString();
        passwd      = passwords.getText().toString();
        passwdr     = passwordsrep.getText().toString();
        mApiService.register(namalengkap,stringemail,passwd,stringhp,stringkota,stringkodepos,stringjk,stringumur,stringktp,passwdr)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                            if (jsonRESULTS.getString("success").equals("true")){
                                Toast.makeText(getApplicationContext(),"Registered Successfully.",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, Login_Activity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(),"Can not Create an Account! Error Connection.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Can not Create an Account!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("debug", "onFailure: ERROR > " + t.toString());
                }
            });
    }

    protected  void handleSiteVerify(final String responseToken){
        String url = "https://www.google.com/recaptcha/api/siteverify";
        StringRequest request = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getBoolean("success")){
                            //code logic when captcha returns true Toast.makeText(getApplicationContext(),String.valueOf(jsonObject.getBoolean("success")),Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),String.valueOf(jsonObject.getString("error-codes")),Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception ex) {
                        Log.d("reCAPTCHA", "JSON exception: " + ex.getMessage());

                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("reCAPTCHA", "Error message: " + error.getMessage());
                }
            }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("secret", SECRET_KEY);
                params.put("response", responseToken);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
            50000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public void syaratketentuan(View view) {
    }
}
