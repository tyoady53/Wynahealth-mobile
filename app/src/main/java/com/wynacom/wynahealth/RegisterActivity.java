package com.wynacom.wynahealth;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.DB_Local.Local_Data;
import com.wynacom.wynahealth.adapter.outlets.adapter_provinces;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class RegisterActivity extends AppCompatActivity {

    Spinner sp_title,sp_kelamin,sp_kota;
    Local_Data local_data;
    CheckBox checkBox;
    CardView cardcheck;
    Button btn_captcha;
    String SITE_KEY = "6LeqBcoeAAAAAL32z64UzIjN94LUAl2G1abTIX9f";
    String SECRET_KEY = "6LeqBcoeAAAAAN84pUQn7M3lO7zBCdyFSzqMjfan";
    String stringProvince;
    EditText email,name,ktp,passwords,passwordsrep,postal,phone,age;

    private ProgressDialog nDialog;
    private BaseApiService mApiService,ApiGetMethod;
    private Handler mHandler = new Handler();
    final Calendar myCalendar= Calendar.getInstance();

    private ArrayList<adapter_provinces> Provinces;

    GlobalVariable globalVariable;

    Button btn_regis,btn_login;

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
            || email.contains("outlook.com")
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
        globalVariable  = (GlobalVariable) getApplicationContext();
        mApiService = UtilsApi.getAPI();
        ApiGetMethod= UtilsApi.getMethod();
        Provinces   = new ArrayList<adapter_provinces>();

        checkBox    = findViewById(R.id.chckbox);
        cardcheck   = findViewById(R.id.card_chck);
        btn_captcha = findViewById(R.id.btn_captcha);

        sp_title    = findViewById(R.id.regis_title);
        sp_kelamin  = findViewById(R.id.regis_jeniskelamin);
        sp_kota     = findViewById(R.id.regis_kota);

        btn_login   = findViewById(R.id.back_login);

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

        getProvince();

//        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int day) {
//                myCalendar.set(Calendar.YEAR, year);
//                myCalendar.set(Calendar.MONTH,month);
//                myCalendar.set(Calendar.DAY_OF_MONTH,day);
//                updateLabel();
//            }
//        };

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

        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                //set time zone
                calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH,month);
                            myCalendar.set(Calendar.DAY_OF_MONTH,day);
                            updateLabel();
                        }
                    },myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));

                //datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

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

        sp_kelamin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sp_title.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, Login_Activity.class);
                startActivity(intent);
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
                                setProvinces();
                            }
                        } else {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getApplicationContext());
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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

    private void setProvinces() {
        java.util.List<String> list2 = new ArrayList<>();
        list2.add("Select City");
        for(int i = 0;i<Provinces.size();i++){
            final adapter_provinces state = Provinces.get(i);
            list2.add(state.getName());
        }
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item,list2);
        dataAdapter2.setDropDownViewResource(R.layout.spinner_item);
        sp_kota.setAdapter(dataAdapter2);
    }

    private void updateLabel(){
        String myFormat="dd-MMM-yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.ENGLISH);
        age.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void RegisterRequest() {
        Loading();
        String namalengkap,stringemail,stringjk,stringktp,stringkota,stringkodepos,stringhp,stringumur,passwd,passwdr,stringTitle;
        if(sp_kelamin.getSelectedItemPosition()==0){
            stringjk = "M";
        }else{
            stringjk = "F";
        }
        stringTitle = sp_title.getSelectedItem().toString();
        //stringjk    = globalVariable.reverseGender(sp_kelamin.getSelectedItem().toString());
        namalengkap = /*sp_title.getSelectedItem().toString() + " " + */name.getText().toString();
        stringemail = email.getText().toString();
        //stringjk    = sp_kelamin.getSelectedItem().toString();
        stringktp   = ktp.getText().toString();
        stringkodepos = postal.getText().toString();
        stringhp    = phone.getText().toString();
        stringumur  = globalVariable.reversedateformat(age.getText().toString());
        passwd      = passwords.getText().toString();
        passwdr     = passwordsrep.getText().toString();

        Map<String, Object> jsonParams = new ArrayMap<>();
        jsonParams.put("title",             stringTitle);
        jsonParams.put("patient_name",      namalengkap);
        jsonParams.put("email",             stringemail);
        jsonParams.put("password",          passwd);
        jsonParams.put("handphone",         stringhp);
        jsonParams.put("city",              stringProvince);
        jsonParams.put("postal_code",       stringkodepos);
        jsonParams.put("sex",               stringjk);
        jsonParams.put("age",               stringumur);
        jsonParams.put("nik",               stringktp);
        jsonParams.put("password_confirmation", passwdr);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(jsonParams)).toString());
        Call<ResponseBody> listCall = mApiService.register(body);
        //mApiService.register(namalengkap,stringemail,passwd,stringhp,stringkota,stringkodepos,stringjk,stringumur,stringktp,passwdr)
        listCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                            if (jsonRESULTS.getString("success").equals("true")){
                                createToast(jsonRESULTS.getString("message"),Type.SUCCESS);
                                Intent intent = new Intent(RegisterActivity.this, Login_Activity.class);
                                startActivity(intent);
                            } else {
                                createToast("Can not Create an Account! Error Connection.",Type.DANGER);
                            }
                        } catch (JSONException e) {
                            nDialog.dismiss();
                            e.printStackTrace();
                        } catch (IOException e) {
                            nDialog.dismiss();
                            e.printStackTrace();
                        }
                    } else {
                        nDialog.dismiss();
                        createToast("Can not Create an Account!",Type.DANGER);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    createToast("Can not Create an Account! Please Check your internet.",Type.DANGER);
                    nDialog.dismiss();
                    Log.e("debug", "onFailure: ERROR > " + t.toString());
                }
            });
    }

    private void createToast(String message, Type type) {
        Cue.init().with(getApplicationContext())
            .setMessage(message)
            .setGravity(Gravity.CENTER_VERTICAL)
            .setTextSize(20)
            .setType(type)
            .show();
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

    private void Loading(){
        nDialog.setMessage("Loading");
        nDialog.setTitle("Please Wait");
        nDialog.setCancelable(false);
        nDialog.show();
    }

    public void syaratketentuan(View view) {
    }
}
