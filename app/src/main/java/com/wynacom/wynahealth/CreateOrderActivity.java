package com.wynacom.wynahealth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.DB_Local.Local_Data;
import com.wynacom.wynahealth.DB_Local.Order_Data;
import com.wynacom.wynahealth.adapter.patient.Adapter_Data_Patient;
import com.wynacom.wynahealth.adapter.patient.adapter_patient;
import com.wynacom.wynahealth.adapter.product.Adapter_Data_Product;
import com.wynacom.wynahealth.adapter.product.adapter_product;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;
import com.wynacom.wynahealth.databinding.ActivityCreateOrderBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrderActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityCreateOrderBinding binding;

    String token,bearer,id_user;

    Spinner spinner;

    Adapter_Data_Product myAppClass;
    Order_Data orderData;
    protected Cursor cursor,cursor2;

    Local_Data localData;
    SQLiteDatabase dbU;

    private BaseApiService mApiService,ApiGetMethod;
    private Adapter_Data_Patient dataAdapter = null;

    private ArrayList<adapter_patient> List;
    private Adapter_Data_Product dataProduct = null;
    private ArrayList<adapter_product> list_product;

    GlobalVariable globalVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        globalVariable  = (GlobalVariable) getApplicationContext();
        token           = globalVariable.getToken();
        bearer          = "Bearer "+token;
        mApiService     = UtilsApi.getAPI();
        ApiGetMethod    = UtilsApi.getMethod();
        orderData       = new Order_Data(getApplicationContext());

        spinner         = findViewById(R.id.spinner_patient_order);

        id_user         = getIntent().getStringExtra("index_position");

        getPatient();

        List            = new ArrayList<adapter_patient>();
        list_product    = new ArrayList<adapter_product>();

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_create_order);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
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
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_create_order);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateOrderActivity.this);
        builder.setMessage("Kembali ke menu sebelumnya akan membatalkan pesanan.\nAnda ingin melanjutkan?");
        builder.setCancelable(true);
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbU     = orderData.getReadableDatabase();
                cursor2 = dbU.rawQuery("SELECT * FROM TB_Orders", null);
                cursor2.moveToFirst();
                if (cursor2.getCount()>0) {
                    orderData.HapusData();
                }
                Intent i = new Intent(CreateOrderActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                startActivity(i);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
