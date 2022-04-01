package com.wynacom.wynahealth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wynacom.wynahealth.DB_Local.Local_Data;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login_Activity extends AppCompatActivity {

    protected Cursor cursor;
    EditText input,password;
    Button bt_login,bt_register;
    boolean doubleBackToExitPressedOnce = false;
    Local_Data local_data;
    String nama,email,Passwordsave,handphone,postal_code,city,sex,age,nik;
    private ProgressDialog nDialog;

    private BaseApiService mApiService;
    private Handler mHandler = new Handler();

    public static boolean Passw(String Name) {
        return Name.length() > 0;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bt_login    = findViewById(R.id.btn_login);
        bt_register = findViewById(R.id.btn_regis);
        input       = findViewById(R.id.username);
        password    = findViewById(R.id.password);

        nDialog = new ProgressDialog( Login_Activity.this);

        mApiService = UtilsApi.getAPI();

        local_data  = new Local_Data(getApplicationContext());

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isemail(input.getText().toString())){
                    input.setText("");
                    input.requestFocus();
                    Toast.makeText(getApplicationContext(), "Email TIdak Valid", Toast.LENGTH_SHORT).show();
                }else if (!Passw(password.getText().toString())) {
                    password.setText("");
                    password.requestFocus();
                    Toast.makeText(getApplicationContext(), "Password harus lebih dari 8 karakter", Toast.LENGTH_SHORT).show();
                }else{
                    mtdLogin();
                }
            }
        });

        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Activity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void mtdLogin() {
        Loading();
        if (!TextUtils.isEmpty(input.getText()) || !TextUtils.isEmpty(password.getText()) ){
            mApiService.login(
                input.getText().toString(),
                password.getText().toString())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("success").equals("true")){
                                    JSONObject subObject = jsonRESULTS.getJSONObject("user");

                                    nama         = subObject.getString("patient_name");
                                    email        = subObject.getString("email");
                                    Passwordsave = password.getText().toString();
                                    handphone    = subObject.getString("handphone");
                                    postal_code  = subObject.getString("postal_code");
                                    city         = subObject.getString("city");
                                    sex          = subObject.getString("sex");
                                    age          = subObject.getString("age");
                                    nik          = subObject.getString("nik");

                                    String token = jsonRESULTS.getString("token");
                                    local_data.SimpanData(nama,handphone,postal_code,Passwordsave,city,sex,age,email,nik,token);
                                    cekDB();
                                } else {
                                    nDialog.dismiss();

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                                    builder.setMessage("Tidak dapat login\nPeriksa email dan password anda.");
                                    builder.setTitle("Login Gagal");
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
                                nDialog.dismiss();
                                e.printStackTrace();
                            } catch (IOException e) {
                                nDialog.dismiss();
                                e.printStackTrace();
                            }
                        } else {
                            nDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(Login_Activity.this);
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
                });}
        else{
            Toast.makeText(getApplicationContext(),"Silakan Masukkan Username/Email dan Password",Toast.LENGTH_SHORT).show();
        }
    }

    private void cekDB() {
        SQLiteDatabase db1 = local_data.getReadableDatabase();
        cursor = db1.rawQuery("SELECT * FROM TB_User", null);
        cursor.moveToFirst();
        if (cursor.getCount()>0) {
            cursor.moveToLast();
            nDialog.dismiss();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
        else {
            nDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(Login_Activity.this);
            builder.setMessage("Tidak dapat login\nPeriksa email dan password anda.");
            builder.setTitle("Login Gagal");
            builder.setCancelable(true);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    stopRepeating();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this,"Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
            System.exit(0);
            //super.onBackPressed();
            return;
        }
    }

    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {
            SQLiteDatabase db1 = local_data.getReadableDatabase();
            cursor = db1.rawQuery("SELECT * FROM TB_User", null);
            cursor.moveToFirst();
            if (cursor.getCount()>0) {
                cursor.moveToLast();
                Intent i = new Intent(Login_Activity.this, MainActivity.class);
                startActivity(i);
                stopRepeating();
            }
            else {
                Intent i = new Intent(Login_Activity.this, LandingPage.class);
                startActivity(i);
                mtdLogin();
            }
//            Toast.makeText(SplashScreen.this, "This is a delayed toast", Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(this, 5000);
        }
    };

    public void startRepeating() {
        mToastRunnable.run();
    }

    public void stopRepeating() {
        mHandler.removeCallbacks(mToastRunnable);
    }

    private void Loading(){
        nDialog.setMessage("Loading..");
        nDialog.setTitle("Harap Tunggu");
        nDialog.setCancelable(false);
        nDialog.show();
    }
}
