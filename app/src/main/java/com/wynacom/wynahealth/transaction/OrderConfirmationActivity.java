package com.wynacom.wynahealth.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.MainActivity;
import com.wynacom.wynahealth.R;
import com.wynacom.wynahealth.adapter.order.Adapter_Data_Order;
import com.wynacom.wynahealth.adapter.order.adapter_order;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderConfirmationActivity extends AppCompatActivity {

    GlobalVariable globalVariable;
    private BaseApiService ApiGetMethod,mApiService;

    TextView TV_patient_name,TV_total,TV_discount,TV_grand;
    String Name,token,bearer,booked,orderType,gender;
    Button next,prev;
    double total_price = 0,total_disc = 0,grand = 0;
    private Adapter_Data_Order dataOrder = null;
    private ArrayList<adapter_order> list_order;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);
        globalVariable  = (GlobalVariable) getApplicationContext();
        token           = globalVariable.getToken();
        bearer          = "Bearer "+token;
        //Toast.makeText(this, globalVariable.getOrderId(), Toast.LENGTH_SHORT).show();
        Name            = getIntent().getStringExtra("name");
        booked          = getIntent().getStringExtra("booked");
        orderType       = getIntent().getStringExtra("type");
        gender          = getIntent().getStringExtra("gender");
        ApiGetMethod    = UtilsApi.getMethod();
        mApiService     = UtilsApi.getAPI();
        list_order      = new ArrayList<adapter_order>();

        listView        = findViewById(R.id.list_order_confirmation);

        TV_patient_name = findViewById(R.id.spinner_patient_order);
        TV_total        = findViewById(R.id.total);
        TV_discount     = findViewById(R.id.total_discount);
        TV_grand        = findViewById(R.id.total_orders);

        prev            = findViewById(R.id.confirm_prev);
        next            = findViewById(R.id.next);

        TV_patient_name.setText(Name);

        getProduct(booked);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderConfirmationActivity.this, SelectProductActivity.class);
                intent.putExtra("name",     Name);
                intent.putExtra("booked",   booked);
                intent.putExtra("gender",   gender);
                intent.putExtra("type",     "new");
                //Toast.makeText(NewOrderActivity.this, "Gender = "+subObject.getString("gender"), Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> jsonParams = new ArrayMap<>();
////put something inside the map, could be null
                jsonParams.put("invoice_id", globalVariable.getOl_invoice_id());
                jsonParams.put("grand_total", TV_grand.getText().toString());
                jsonParams.put("payment", "cod");
                jsonParams.put("perusahaan", "-");
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(jsonParams)).toString());
                //ResponseBody formLogin = new ResponseBody(input.getText().toString(), password.getText().toString());
                Call<ResponseBody> listCall = mApiService.checkout(bearer,body);
                listCall.enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                            Toast.makeText(getApplicationContext(), jsonRESULTS.getString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                            startActivity(intent);
                            Toast.makeText(OrderConfirmationActivity.this,"booked : "+booked,Toast.LENGTH_SHORT).show();
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
        });
    }

    private void getProduct(String s) {
        Call<ResponseBody> listCall = ApiGetMethod.getCartsDetail(bearer,s);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            JSONArray jsonArray = jsonRESULTS.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.getJSONObject(i);
                                String invoice_id    = c.getString("id");
                                JSONArray ArrayCarts = c.getJSONArray("carts");
                                for (int j = 0; j < ArrayCarts.length(); j++) {
                                    JSONObject Carts = ArrayCarts.getJSONObject(j);
                                    String product_id   = Carts.getString("ol_product_id");
                                    String subtotal     = Carts.getString("price");
                                    String qty          = Carts.getString("qty");
                                    JSONObject product  = Carts.getJSONObject("product");
                                    String image        = product.getString("image");
                                    String title        = product.getString("title");
                                    String slug         = product.getString("slug");
                                    String description  = product.getString("description");
                                    String product_price= product.getString("price");
                                    String view_discount= product.getString("discount");
                                    double a = Double.parseDouble(product_price);
                                    double b = Double.parseDouble(view_discount);
                                    double disc = a * (b/100);
                                    total_price = total_price+a;
                                    total_disc  = total_disc+ disc;
                                    String nom_discount = String.valueOf(disc);
                                    adapter_order _states = new adapter_order(invoice_id, qty, subtotal, image, title, slug, description, product_price, view_discount, nom_discount,product_id);
                                    list_order.add(_states);
                                    bindDataProduct();
                                }
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

    private void bindDataProduct() {
        grand   = total_price-total_disc;
        String total    = String.valueOf(total_price);
        String disc     = String.valueOf(total_disc);
        String grandttl = String.valueOf(grand);
        TV_total.setText(globalVariable.toCurrency(total));
        TV_discount.setText(globalVariable.toCurrency(disc));
        TV_grand.setText(globalVariable.toCurrency(grandttl));
        dataOrder = new Adapter_Data_Order(getApplicationContext(),R.layout.list_product, list_order);
        listView.setAdapter(dataOrder);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(intent);
    }
}
