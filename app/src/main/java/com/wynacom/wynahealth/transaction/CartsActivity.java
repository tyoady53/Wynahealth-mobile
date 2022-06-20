package com.wynacom.wynahealth.transaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.DB_Local.Local_Data;
import com.wynacom.wynahealth.MainActivity;
import com.wynacom.wynahealth.R;
import com.wynacom.wynahealth.adapter.carts.adapter_carts;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;
import com.wynacom.wynahealth.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import dev.jai.genericdialog2.GenericDialog;
import dev.jai.genericdialog2.GenericDialogOnClickListener;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartsActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    ImageView back_arrow;
    TextView title;

    protected Cursor cursor;
    Local_Data local_data;

    ListView listView;
    GlobalVariable globalVariable;

    String token,bearer,booked,name,gender,string_email,stringPassword,dataPatient_id,outlet_id,service_date,doctor,company;

    private BaseApiService mApiService,ApiGetMethod;

    //private Adapter_Data_Carts dataCarts = null;
    private MyCustomAdapter dataCarts = null;
    private ArrayList<adapter_carts> list_carts;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carts);
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
        globalVariable  = (GlobalVariable) getApplicationContext();
        token           = globalVariable.getToken();

        local_data      = new Local_Data(getApplicationContext());
        list_carts      = new ArrayList<adapter_carts>();
        bearer          = "Bearer "+token;
        mApiService     = UtilsApi.getAPI();
        ApiGetMethod    = UtilsApi.getMethod();

        progressBar     = findViewById(R.id.progress_bar_image);

        listView        = findViewById(R.id.carts_list);

        back_arrow      = findViewById(R.id.carts_back);
        title           = findViewById(R.id.carts_title);
        title.setText(getString(R.string.carts_title));
        //setSupportActionBar(binding.toolbar);

        SQLiteDatabase dbU = local_data.getReadableDatabase();
        cursor = dbU.rawQuery("SELECT * FROM TB_User", null);
        cursor.moveToFirst();
        if (cursor.getCount()>0) {
            cursor.moveToPosition(0);
            stringPassword  = cursor.getString(4);
            string_email    = cursor.getString(8);
        }

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter_carts state = list_carts.get(position);
                booked          = state.getInvoice();
                name            = state.getNames();
                gender          = state.getGender();
                outlet_id       = state.getCompany_id();
                dataPatient_id  = state.getData_patient_id();
                service_date    = state.getService_date();
                doctor          = state.getDoctor();
                company         = state.getCompanies();
                globalVariable.setGenerateGender(state.getGender());
                globalVariable.setOl_invoice_id(state.getID());
                globalVariable.setBooked(booked);
                globalVariable.setList_view("confirm");
                //Toast.makeText(getContext(), ids, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(true);
                getTotalProduct(gender);
            }
        });
        refreshList();
    }

    private void getTotalProduct(String gender) {
        Call<ResponseBody> listCall = ApiGetMethod.getProducts(gender);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            JSONObject jsonObject   = jsonRESULTS.getJSONObject("data");
                            String count = jsonObject.getString("last_page"); //String.valueOf(jsonArray.length());
                            Intent intent = new Intent(getApplicationContext(), NewOrderActivity.class);
                            intent.putExtra("type", "edit");
                            intent.putExtra("booked",           booked);
                            intent.putExtra("gender",           gender);
                            intent.putExtra("count",            count);
                            intent.putExtra("datapatient_id",   dataPatient_id);
                            startActivity(intent);
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

    private void refreshList() {
        progressBar.setVisibility(View.VISIBLE);
        Call<ResponseBody> listCall = ApiGetMethod.getcarts(bearer);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            progressBar.setVisibility(View.GONE);
                            JSONObject jsonObject   = jsonRESULTS.  getJSONObject("data");
                            JSONArray data          = jsonObject.   getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject c            = data.getJSONObject(i);
                                String total            = c.getString("gross_amount");
                                String id               = c.getString("id");
                                String invoiceNumber    = c.getString("booked");
                                String status           = c.getString("status");
                                String service_date     = c.getString("service_date");
                                String snap             = c.getString("snap_token");
                                String gender           = c.getString("gender");
                                String dokter           = c.getString("dokter");
                                String perusahaan       = c.getString("perusahaan");
                                JSONObject company      = c.getJSONObject("outlet");
                                String company_id       = company.getString("id");
                                String companyName      = company.getString("name");
                                String companyAddress   = company.getString("address");
                                JSONObject datapatient  = c.getJSONObject("datapatient");
                                String data_id          = datapatient.getString("id");
                                String name             = datapatient.getString("name");
                                String handphone        = datapatient.getString("handphone");
                                String city             = datapatient.getString("city");

                                    adapter_carts _states = new adapter_carts(id,name, invoiceNumber,handphone,city, status, total,gender, snap, service_date,companyName,companyAddress,data_id,company_id,dokter,perusahaan);
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
                        reLogin();
                        e.printStackTrace();
                    }
                } else {
                    Cue.init().with(getApplicationContext()).setMessage("Tidak ada data pasien").setGravity(Gravity.CENTER_VERTICAL).setTextSize(20).setType(Type.PRIMARY).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("debug", "onFailure: ERROR > getDataPatient" + t.toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(CartsActivity.this);
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
        dataCarts = new MyCustomAdapter(this,R.layout.list_carts, list_carts);
        listView.setAdapter(dataCarts);
    }

    private class MyCustomAdapter extends ArrayAdapter<adapter_carts>{
        private ArrayList<adapter_carts> stateList;
        String titles;
        public MyCustomAdapter(@NonNull Context context, int list_patient, ArrayList<adapter_carts> list) {
            super(context, list_patient,list);
            this.stateList  = new ArrayList<adapter_carts>();
            this.stateList.addAll(list);
        }

        private class ViewHolder {
            TextView name,Vphone,Vaddress,Vtotal,Vdate;
            View status_color;
            ImageView delete;
            String H_service_date;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;

            convertView =  LayoutInflater.from(getContext()).inflate(R.layout.list_carts, parent, false);

            holder = new ViewHolder();
            holder.name          = (TextView) convertView.findViewById(R.id.carts_name_list);
            holder.Vphone        = (TextView) convertView.findViewById(R.id.carts_phone);
            holder.Vdate         = (TextView) convertView.findViewById(R.id.carts_date);
            holder.Vaddress      = (TextView) convertView.findViewById(R.id.carts_address);
            holder.Vtotal        = (TextView) convertView.findViewById(R.id.carts_total);
            holder.delete        = (ImageView) convertView.findViewById(R.id.delete_order);

            final adapter_carts state = stateList.get(position);

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new GenericDialog.Builder(getContext())
                        .setDialogTheme(R.style.GenericDialogTheme)
                        .setTitle(getString(R.string.cancel_order)).setTitleAppearance(R.color.colorPrimaryDark, 20)
                        //.setMessage("Data Collected Successfully")
                        .addNewButton(R.style.yes_option, new GenericDialogOnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Map<String, Object> jsonParams = new ArrayMap<>();
////put something inside the map, could be null
                                jsonParams.put("invoice_id", state.getID());
                                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(jsonParams)).toString());
                                //ResponseBody formLogin = new ResponseBody(input.getText().toString(), password.getText().toString());
                                Call<ResponseBody> listCall = mApiService.Cancel_order(bearer,body);
                                listCall.enqueue(new Callback<ResponseBody>() {

                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        try {
                                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                            if(jsonRESULTS.getString("success").equals("true")){
                                                dataCarts.clear();
                                                refreshList();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(getApplicationContext(), "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .addNewButton(R.style.no_option, new GenericDialogOnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        })
                        .setButtonOrientation(LinearLayout.HORIZONTAL)
                        .setCancelable(true)
                        .generate();
                }
            });

            if (state.getGender().equals("M")){
                titles = "Mr. ";
            } else {
                titles = "Mrs. ";
            }
            holder.name         .setText(titles+state.getNames());
            holder.Vphone       .setText(state.getTelephone());
            holder.H_service_date= globalVariable.dateformat(state.getService_date());
            holder.Vdate        .setText(holder.H_service_date);
            holder.Vaddress     .setText(state.getCompany_name()+" "+state.getCompany_address());
            Locale localeID = new Locale("in", "ID");
            NumberFormat nf = NumberFormat.getCurrencyInstance(localeID);
            String c = nf.format(Integer.parseInt(state.getTotal()));
            holder.Vtotal       .setText(c);

            return convertView;
        }
    }

    private void reLogin() {
        if (!TextUtils.isEmpty(string_email) || !TextUtils.isEmpty(stringPassword) ){
            Map<String, Object> jsonParams = new ArrayMap<>();
//put something inside the map, could be null
            jsonParams.put("email", string_email);
            jsonParams.put("password", stringPassword);
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
                                String Stoken = jsonRESULTS.getString("token");
                                JSONObject userObj = jsonRESULTS.getJSONObject("user");
                                String hash  = userObj.getString("password");
                                local_data.UpdateToken(string_email,Stoken,hash);
                                globalVariable.setToken(Stoken);
                                token = Stoken;
                                bearer = "Bearer "+token;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshList();
                                    }
                                }, 2000);
                            } else {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CartsActivity.this);
                                builder.setMessage(getString(R.string.server_try_again));
                                builder.setTitle(getString(R.string.server_failed));
                                builder.setCancelable(true);
                                builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        reLogin();
                                    }
                                });
                                android.app.AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CartsActivity.this);
                        builder.setMessage(getString(R.string.connection_try_again));
                        builder.setTitle(getString(R.string.connection_failed));
                        builder.setCancelable(true);
                        builder.setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reLogin();
                            }
                        });
                        android.app.AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("debug", "onFailure: ERROR > " + t.toString());
                }
            });}
        else{

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(intent);
    }
}
