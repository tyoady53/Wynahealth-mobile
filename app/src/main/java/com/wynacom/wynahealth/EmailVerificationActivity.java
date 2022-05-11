package com.wynacom.wynahealth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

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

public class EmailVerificationActivity extends AppCompatActivity {
    WebView webView;
    Local_Data local_data;
    protected Cursor cursor2;
    String email,password;
    private ProgressDialog nDialog;
    Button check_verify;
    private BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        local_data      = new Local_Data(getApplicationContext());
        nDialog         = new ProgressDialog( EmailVerificationActivity.this);
        mApiService     = UtilsApi.getAPI();

        webView         = findViewById(R.id.WebView);
        check_verify    = findViewById(R.id.check);

        SQLiteDatabase dbU = local_data.getReadableDatabase();
        cursor2 = dbU.rawQuery("SELECT * FROM TB_User", null);
        cursor2.moveToFirst();
        if (cursor2.getCount()>0) {
            email       = cursor2.getString(8);
            password    = cursor2.getString(4);
        }

        check_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmail();
            }
        });

        webView.loadUrl("http://172.16.9.149:8000/login");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                if(url.equals("http://172.16.9.149:8000/login")){
                   // progressBar.dismiss();
                    webView.loadUrl("javascript:var email=document.getElementsByName('email')[0].value='" +email+"';var password=document.getElementsByName('password')[0].value='" +password+"'");
                    login();
                }else{
                    //progressBar.dismiss();
                    webView.loadUrl("http://172.16.9.149:8000/email/verify");
                }
                //
            }
        });
    }

    private void checkEmail() {
        mApiService.login(email, password).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                            if (jsonRESULTS.getString("success").equals("true")){
                                JSONObject subObject = jsonRESULTS.getJSONObject("user");
                                String email_verify = subObject.getString("email_verified_at");
                                create_dialog(email_verify);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(EmailVerificationActivity.this);
                        builder.setMessage("Tidak dapat terhubung ke server.\nApakah anda ingin mengulangi proses Login?");
                        builder.setTitle("Login Gagal");
                        builder.setCancelable(true);
                        builder.setNegativeButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                checkEmail();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(EmailVerificationActivity.this);
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
            });}

    private void create_dialog(String email_verify) {
        if(!email_verify.equals("null")){
            gotoLogin();
        }else{
            BuildAlert();
        }
    }

    private void BuildAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EmailVerificationActivity.this);
        builder.setMessage("Verifikasi email telah dikirim.\nHarap periksa email anda.");
        builder.setTitle("Verifikasi Email");
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

    private void gotoLogin() {
        Intent i = new Intent(EmailVerificationActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(i);
        finish();
    }


    private void login() {
        webView.loadUrl("javascript:document.getElementsByName('bt_login')[0].click()");
        //webView.loadUrl("javascript:WebForm_DoPostBackWithOptions( new WebForm_PostBackOptions('bt_login', '', true, '', '', false, true))");
    }

    private void Loading(){
        nDialog.setMessage("Loading..");
        nDialog.setTitle("Harap Tunggu");
        nDialog.setCancelable(false);
        nDialog.show();
    }
}
