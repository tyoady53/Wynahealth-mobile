package com.wynacom.wynahealth.transaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.MainActivity;
import com.wynacom.wynahealth.R;
import com.wynacom.wynahealth.adapter.carts.Adapter_Data_Carts;
import com.wynacom.wynahealth.adapter.carts.adapter_carts;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartsActivity extends AppCompatActivity {

    ListView listView;
    GlobalVariable globalVariable;

    String token,bearer;

    private BaseApiService mApiService,ApiGetMethod;

    private Adapter_Data_Carts dataCarts = null;
    private ArrayList<adapter_carts> list_carts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carts);
        globalVariable  = (GlobalVariable) getApplicationContext();
        token           = globalVariable.getToken();

        list_carts      = new ArrayList<adapter_carts>();
        bearer          = "Bearer "+token;
        mApiService     = UtilsApi.getAPI();
        ApiGetMethod    = UtilsApi.getMethod();

        listView            = findViewById(R.id.carts_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter_carts state = list_carts.get(position);
                String booked   = state.getInvoice();
                String name     = state.getNames();
                String gender   = state.getGender();
                globalVariable.setGenerateGender(state.getGender());
                globalVariable.setOl_invoice_id(state.getID());
                globalVariable.setBooked(booked);
                globalVariable.setList_view("confirm");
                //Toast.makeText(getContext(), ids, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), OrderConfirmationActivity.class);
                intent.putExtra("type", "edit");
                intent.putExtra("name",             name);
                intent.putExtra("booked",           booked);
                intent.putExtra("gender",           gender);
                startActivity(intent);
                //Toast.makeText(CartsActivity.this,"booked : "+booked,Toast.LENGTH_SHORT).show();
            }
        });
        refreshList();
    }

    private void refreshList() {
        Call<ResponseBody> listCall = ApiGetMethod.getcarts(bearer);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            //JSONObject jsonObject   = jsonRESULTS.getJSONObject("data");
                            JSONArray jsonArray     = jsonRESULTS.getJSONArray("data");
//                            JSONArray jsonArray = jsonRESULTS.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.getJSONObject(i);
                                //JSONObject subjsonObject   = c.getJSONObject("datapatient");
                                String id            = c.getString("id");
                                String name          = c.getString("name");
                                String handphone     = c.getString("handphone");
                                String city          = c.getString("city");
                                String invoiceNumber = c.getString("booked");
                                String status        = c.getString("status");
                                String total         = c.getString("amount");
                                String snap          = c.getString("snap_token");
                                String gender        = c.getString("gender");
                                if(total.equals("null")){
                                    total="0";
                                }
                                    adapter_carts _states = new adapter_carts(id,name, invoiceNumber,handphone,city, status, total,gender, snap);
                                    list_carts.add(_states);
                                    bindData();
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
                    Cue.init().with(getApplicationContext()).setMessage("Tidak ada data pasien").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.PRIMARY).show();
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
                        refreshList();
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

    private void bindData() {
        dataCarts = new Adapter_Data_Carts(getApplicationContext(),R.layout.list_carts, list_carts);
        listView.setAdapter(dataCarts);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(intent);
    }
}
