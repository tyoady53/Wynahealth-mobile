package com.wynacom.wynahealth;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.DB_Local.Local_Data;
import com.wynacom.wynahealth.DB_Local.Order_Data;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;
import com.wynacom.wynahealth.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    protected Cursor cursor,cursor2;
    boolean doubleBackToExitPressedOnce = false;
    Local_Data local_data;
    Order_Data order_data;
    private BaseApiService mApiService,ApiGetMethod;
    String token,bearer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        local_data  = new Local_Data(getApplicationContext());
        order_data  = new Order_Data(getApplicationContext());
        mApiService = UtilsApi.getAPI();
        ApiGetMethod= UtilsApi.getMethod();

        token           = ((GlobalVariable) getApplicationContext()).getToken();
        bearer          = "Bearer "+token;

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //cekDashboard();
        SQLiteDatabase dbU = order_data.getReadableDatabase();
        cursor2 = dbU.rawQuery("SELECT * FROM TB_Orders", null);
        cursor2.moveToFirst();
        if (cursor2.getCount()>0) {
            order_data.HapusData();
        }
    }

    private void cekDashboard() {
        SQLiteDatabase db1 = local_data.getReadableDatabase();
        cursor = db1.rawQuery("SELECT * FROM TB_User", null);
        cursor.moveToFirst();
        if (cursor.getCount()>0) {
            String token = "Bearer "+cursor.getString(10);
            createDashoardData(token);
        }
        else {
            System.exit(0);
        }
    }

//    private void createDashoard(String token) {
//        Call<List<Post>> listCall = ApiGetMethod.getPosts(token);
//        listCall.enqueue(new Callback<List<Post>>() {
//            @Override
//            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
//                if (!response.isSuccessful()) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                    builder.setMessage("Failed : "+response+"\nHeader : "+token);
//                    builder.setTitle("Gagal");
//                    builder.setCancelable(true);
//                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
//                    return;
//                }
//                List<Post> posts = response.body();
//                for (Post post : posts) {
//                    String content = "";
//                    content += "Pending: " + post.getPending() + "\n";
//                    content += "Success: " + post.getSuccess() + "\n";
//                    content += "Expired: " + post.getExpired() + "\n";
//                    content += "Failed: " + post.getFailed() + "\n\n";
//
//                    Toast.makeText(MainActivity.this, "Success : "+content, Toast.LENGTH_SHORT).show();
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Post>> call, Throwable t) {
////                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
////                    builder.setMessage("Failur : "+t+"\n"+t+"\nToken : "+token);
////                    builder.setTitle("Gagal");
////                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
////                        @Override
////                        public void onClick(DialogInterface dialog, int which) {
////                            dialog.dismiss();
////                        }
////                    });
////                    AlertDialog alertDialog = builder.create();
////                    alertDialog.show();
//                Toast.makeText(MainActivity.this, "Failur : "+t+"\nToken : "+token, Toast.LENGTH_SHORT).show();
//                Log.d("Failur","Message : "+t.toString());
//                return;
//            }
//        });
//    }

    private void createDashoardData(String token) {
        Call<ResponseBody> listCall = ApiGetMethod.getPosts(token);
        listCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                            if (jsonRESULTS.getString("success").equals("true")){
                                JSONObject subObject = jsonRESULTS.getJSONObject("data");
                                JSONObject subObject2 = subObject.getJSONObject("count");
                                String content = "";
                                content += "Pending: " + subObject2.getString("pending") + "\n";
                                content += "Success: " + subObject2.getString("success") + "\n";
                                content += "Expired: " + subObject2.getString("expired") + "\n";
                                content += "Failed : " + subObject2.getString("failed") + "\n\n";

                                Toast.makeText(MainActivity.this, "Success : "+content, Toast.LENGTH_SHORT).show();
                            } else {

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
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
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
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
                    Cue.init().with(getApplicationContext()).setMessage("Tidak dapat terhubung ke server."+t.toString()).setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setType(Type.PRIMARY).show();
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_cart:
                gotocart();
                return true;
            case R.id.action_about:
                return true;
            case R.id.action_settings:
                logoutdialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void gotocart() {
        Toast.makeText(getApplicationContext(),"Cart",Toast.LENGTH_SHORT).show();
    }

    private void logoutdialog() {
        new AlertDialog.Builder(  MainActivity.this)
            .setTitle("Logout Aplikasi")
            .setMessage("Anda yakin ingin Keluar?")
            .setCancelable(true)
            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    local_data.HapusData();
                    ((GlobalVariable) getApplicationContext()).clearToken();
                    logout();
                }
            })
            .setNegativeButton("Tidak",null)
            .show();
    }

    private void logout() {
        mApiService.logout(token)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                            if (jsonRESULTS.getString("success").equals("true")){
                                Intent login = new Intent(getApplicationContext(), Login_Activity.class);
                                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(login);
                            } else {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getApplicationContext());
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Tidak dapat login\nPeriksa email dan password anda.");
                        builder.setTitle("Login Gagal");
                        builder.setCancelable(true);
                        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
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
                    Cue.init().with(getApplicationContext()).setMessage("Tidak dapat terhubung ke server."+t.toString()).setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setType(Type.PRIMARY).show();
                }
            });
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
}
