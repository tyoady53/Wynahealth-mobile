package com.wynacom.wynahealth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {

    TextView TVversi;
    protected  String password,email;
    private static int SPLASH_TIME_OUT = 5000;
    Local_Data local_data;
    GlobalVariable globalVariable;
    protected Cursor cursor;
    private Handler mHandler = new Handler();
    private BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        TVversi     = findViewById(R.id.textView);
        local_data  = new Local_Data(getApplicationContext());
        mApiService = UtilsApi.getAPI();
        globalVariable = (GlobalVariable)getApplicationContext();

        try {
            String versionName = getApplicationContext().getPackageManager()
                .getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
            TVversi.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db1 = local_data.getReadableDatabase();
                cursor = db1.rawQuery("SELECT * FROM TB_User", null);
                cursor.moveToFirst();
                if (cursor.getCount()>0) {
                    email    = cursor.getString(8);
                    password     = cursor.getString(4);
                    relogin(email,password);
                }
                else {
                    Intent i = new Intent(SplashScreen.this, LandingPage.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    startActivity(i);
                }
            }
        }, SPLASH_TIME_OUT);
    }

    private void relogin(String email,String password) {
        if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password) ){
            mApiService.login(
                email,
                password)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("success").equals("true")){

                                    String token = jsonRESULTS.getString("token");
                                    local_data.UpdateToken(email,token);
                                    globalVariable.setToken(token);
                                    cekDB(token);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
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
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
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

    private void cekDB(String token) {
        SQLiteDatabase db1 = local_data.getReadableDatabase();
        cursor = db1.rawQuery("SELECT * FROM TB_User", null);
        cursor.moveToFirst();
        if (cursor.getCount()>0) {
            cursor.moveToLast();
            String lasttoken = cursor.getString(10);
            if(lasttoken.equals(token)){
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                startActivity(i);
                finish();
            }else{
                System.exit(0);
            }
        }
    }

    public void startRepeating() {
        mToastRunnable.run();
    }

    public void stopRepeating() {
        mHandler.removeCallbacks(mToastRunnable);
    }

    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {
            SQLiteDatabase db1 = local_data.getReadableDatabase();
            cursor = db1.rawQuery("SELECT * FROM TB_User", null);
            cursor.moveToFirst();
            if (cursor.getCount()>0) {
                cursor.moveToLast();
                gotomenu();
            }
            else {
                landingpage();
            }
            mHandler.postDelayed(this, 5000);
        }
    };

    private void landingpage() {
        stopRepeating();
        Intent i = new Intent(SplashScreen.this, LandingPage.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(i);
    }

    private void gotomenu() {
        stopRepeating();
        Intent i = new Intent(SplashScreen.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(i);
    }
}
