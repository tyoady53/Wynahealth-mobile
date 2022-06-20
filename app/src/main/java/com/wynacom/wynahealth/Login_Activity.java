package com.wynacom.wynahealth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.DB_Local.Local_Data;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login_Activity extends AppCompatActivity {
    GlobalVariable globalVariable;
    protected Cursor cursor;
    EditText input,password;
    Button bt_login,bt_register;
    boolean doubleBackToExitPressedOnce = false;
    Local_Data local_data;
    String nama,email,Passwordsave,handphone,postal_code,city,sex,age,nik;
    private ProgressDialog nDialog;
    String email_verify;
    private BaseApiService mApiService;
    private Handler mHandler = new Handler();
    TextView register;

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
            || email.contains("outlook.com")
            || email.contains("live.com");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        globalVariable = (GlobalVariable)getApplicationContext();
        bt_login    = findViewById(R.id.btn_login);
        bt_register = findViewById(R.id.btn_regis);
        input       = findViewById(R.id.username);
        password    = findViewById(R.id.password);
        register    = findViewById(R.id.registerHere);

        nDialog = new ProgressDialog( Login_Activity.this);

        mApiService = UtilsApi.getAPI();

        local_data  = new Local_Data(getApplicationContext());

//        bt_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isemail(input.getText().toString())){
//                    input.setText("");
//                    input.requestFocus();
//                    Toast.makeText(getApplicationContext(), "Email not valid", Toast.LENGTH_SHORT).show();
//                }else if (!Passw(password.getText().toString())) {
//                    password.setText("");
//                    password.requestFocus();
//                    Toast.makeText(getApplicationContext(), "Password at least 8 character", Toast.LENGTH_SHORT).show();
//                }else{
//                    mtdLogin();
//                }
//            }
//        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Activity.this, RegisterActivity.class);
                startActivity(intent);
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
        Map<String, Object> jsonParams = new ArrayMap<>();
//put something inside the map, could be null
        jsonParams.put("email", input.getText().toString());
        jsonParams.put("password", password.getText().toString());
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
                            JSONObject subObject = jsonRESULTS.getJSONObject("user");
                            String id       = subObject.getString("id");
                            String title    = subObject.getString("title");
                            String getName  = subObject.getString("patient_name");
                            nama            = title+" "+getName;
                            email           = subObject.getString("email");
                            Passwordsave    = password.getText().toString();
                            handphone       = subObject.getString("handphone");
                            postal_code     = subObject.getString("postal_code");
                            city            = subObject.getString("city");
                            sex             = subObject.getString("sex");
                            age             = subObject.getString("age");
                            nik             = subObject.getString("nik");
                            email_verify    = subObject.getString("email_verified_at");
                            String token    = jsonRESULTS.getString("token");
                            String hash     = subObject.getString("password");
                            local_data.SimpanData(id,nama,handphone,postal_code,Passwordsave,city,sex,age,email,nik,token,hash);
                            globalVariable.setToken(token);
                            nDialog.dismiss();
                            cekDB(token);
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
                    } catch (JSONException | IOException e) {
                        nDialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    nDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login_Activity.this);
                    builder.setMessage("Tidak dapat terhubung ke server.\nApakah anda ingin mengulangi proses Login?");
                    builder.setTitle("Login Gagal");
                    builder.setCancelable(true);
                    builder.setNegativeButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mtdLogin();
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
            }
        });
    }

    private void cekDB(String token) {
        SQLiteDatabase db1 = local_data.getReadableDatabase();
        cursor = db1.rawQuery("SELECT * FROM TB_User", null);
        cursor.moveToFirst();
        if (cursor.getCount()>0) {
            cursor.moveToLast();
            String lasttoken = cursor.getString(10);
            if(lasttoken.equals(token)){
                if(!email_verify.equals("null")){
                    gotomenu();
                }else{
                    gotomenu();
                    //gotoEmailVerification();
                }
            }else{
                System.exit(0);
            }
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

    private void gotoEmailVerification() {
        //stopRepeating();
        Intent i = new Intent(Login_Activity.this, EmailVerificationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(i);
        finish();
    }

    private void gotomenu() {
        //stopRepeating();
        Intent i = new Intent(Login_Activity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(i);
        finish();
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
        nDialog.setMessage("Loading");
        nDialog.setTitle("Please Wait");
        nDialog.setCancelable(false);
        nDialog.show();
    }

    public void login(View view) {
        mtdLogin();
    }
}
