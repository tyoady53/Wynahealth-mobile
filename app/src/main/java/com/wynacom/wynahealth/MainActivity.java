package com.wynacom.wynahealth;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wynacom.wynahealth.DB_Local.Local_Data;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;
import com.wynacom.wynahealth.databinding.ActivityMainBinding;
import com.wynacom.wynahealth.json_dashboard.Count;
import com.wynacom.wynahealth.json_dashboard.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    protected Cursor cursor;
    boolean doubleBackToExitPressedOnce = false;
    Local_Data local_data;
    private BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        local_data  = new Local_Data(getApplicationContext());
        mApiService = UtilsApi.getAPI();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        cekDashboard();
    }

    private void cekDashboard() {
        SQLiteDatabase db1 = local_data.getReadableDatabase();
        cursor = db1.rawQuery("SELECT * FROM TB_User", null);
        cursor.moveToFirst();
        if (cursor.getCount()>0) {

            Retrofit retrofit = new Retrofit.Builder().baseUrl("http://172.16.9.149:8000/api/patient/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
            BaseApiService jsonPlaceHolderApi = retrofit.create(BaseApiService.class);

            String token = cursor.getString(10);

            Call<List<Post>> listCall = jsonPlaceHolderApi.getPosts(token);
            listCall.enqueue(new Callback<List<Post>>() {
                @Override
                public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                    if (!response.isSuccessful()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Failed : "+response+"\nHeader : "+token);
                        builder.setTitle("Gagal");
                        builder.setCancelable(true);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        //Toast.makeText(MainActivity.this, "Failed : "+response, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<Post> posts = response.body();
//                    for (Post post : posts) {
//                        String content = "";
//                        content += "Pending: " + post.getPending() + "\n";
//                        content += "Success: " + post.getSuccess() + "\n";
//                        content += "Expired: " + post.getExpired() + "\n";
//                        content += "Failed: " + post.getFailed() + "\n\n";
//
//                        Toast.makeText(MainActivity.this, "Success : "+content, Toast.LENGTH_SHORT).show();
//
//                    }
                }

                @Override
                public void onFailure(Call<List<Post>> call, Throwable t) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Failur : "+t+"\nToken : "+token);
                    builder.setTitle("Gagal");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    //Toast.makeText(MainActivity.this, "Failed : "+response, Toast.LENGTH_SHORT).show();
                    return;
//                    Toast.makeText(MainActivity.this, "Failur : "+t, Toast.LENGTH_SHORT).show();
//                    return;
                }
            });
//            getDashboardData(c);
        }
        else {
            System.exit(0);
        }
    }

//    private void getDashboardData(String last_token) {
//        mApiService.dashboard(
//            last_token)
//            .enqueue(new Callback<List<Count>>() {
//                @Override
//                public void onResponse(Call<List<Count>> call, Response<List<Count>> response) {
//                    List<Count> posts = (List<Count>) response.body();
//
//                    for (Count post : posts) {
//                        String content = "";
//                        content += "Pending: " + post.getPending() + "\n";
//                        content += "Success: " + post.getSuccess() + "\n";
//                        content += "Failed : " + post.getFailed() + "\n";
//                        content += "Expired: " + post.getExpired() + "\n\n";
//                        Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<List<Count>> call, Throwable t) {
//                        Log.e("debug", "onFailure: ERROR > " + t.toString());
//                }
//            });
//    }

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
            case R.id.action_about:
                return true;
            case R.id.action_settings:
                logoutdialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logoutdialog() {
        new AlertDialog.Builder(  MainActivity.this)
            .setTitle("Logout Aplikasi")
            .setMessage("Anda yakin ingin Keluar?")
            .setCancelable(true)
            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    local_data.HapusData();
                    Intent login = new Intent(getApplicationContext(), Login_Activity.class);
                    login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(login);
                }
            })
            .setNegativeButton("Tidak",null)
            .show();
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