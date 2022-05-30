package com.wynacom.wynahealth.transaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.MainActivity;
import com.wynacom.wynahealth.R;
import com.wynacom.wynahealth.adapter.carts.adapter_carts;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;

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

    ListView listView;
    GlobalVariable globalVariable;

    String token,bearer;

    private BaseApiService mApiService,ApiGetMethod;

    //private Adapter_Data_Carts dataCarts = null;
    private MyCustomAdapter dataCarts = null;
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
                                String status        = c.getString("service_date");
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
            TextView name,Vphone,Vaddress,Vtotal;
            View status_color;
            ImageView delete;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;

            convertView =  LayoutInflater.from(getContext()).inflate(R.layout.list_carts, parent, false);

            holder = new ViewHolder();
            holder.name          = (TextView) convertView.findViewById(R.id.carts_name_list);
            holder.Vphone        = (TextView) convertView.findViewById(R.id.carts_phone);
            holder.Vaddress      = (TextView) convertView.findViewById(R.id.carts_address);
            holder.Vtotal        = (TextView) convertView.findViewById(R.id.carts_total);
            holder.delete        = (ImageView) convertView.findViewById(R.id.delete_order);

            final adapter_carts state = stateList.get(position);

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new GenericDialog.Builder(getContext())
                        .setDialogTheme(R.style.GenericDialogTheme)
                        .setTitle("Hapus Data?").setTitleAppearance(R.color.colorPrimaryDark, 20)
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
                                            //Toast.makeText(OrderConfirmationActivity.this,"booked : "+booked,Toast.LENGTH_SHORT).show();
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

            holder.Vaddress     .setText(state.getStatus());
            Locale localeID = new Locale("in", "ID");
            NumberFormat nf = NumberFormat.getCurrencyInstance(localeID);
            String c = nf.format(Integer.parseInt(state.getTotal()));
            holder.Vtotal       .setText(c);

            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(intent);
    }
}
