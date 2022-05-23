package com.wynacom.wynahealth.transaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.MainActivity;
import com.wynacom.wynahealth.R;
import com.wynacom.wynahealth.adapter.order.Adapter_Data_Order;
import com.wynacom.wynahealth.adapter.order.adapter_order;
import com.wynacom.wynahealth.adapter.product.Adapter_Data_Product;
import com.wynacom.wynahealth.adapter.product.adapter_product;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectProductActivity extends AppCompatActivity {

    boolean checked = false;

    GlobalVariable globalVariable;
    private BaseApiService ApiGetMethod;
    private Adapter_Data_Product dataProduct = null;
    private ArrayList<adapter_product> list_product;
    ListView listViewProduct;
    TextView TV_patient_name;
    String Name,booked,orderType,gender,token,bearer;
    Button next,prev;

    private Adapter_Data_Order dataOrder = null;
    private ArrayList<adapter_order> listOrder;
    Map<String, String> product_id;
    ArrayList<String> arrayListOfId = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);
        globalVariable  = (GlobalVariable) getApplicationContext();
        Name            = getIntent().getStringExtra("name");
        booked          = getIntent().getStringExtra("booked");
        orderType       = getIntent().getStringExtra("type");
        gender          = getIntent().getStringExtra("gender");
        ApiGetMethod    = UtilsApi.getMethod();
        list_product    = new ArrayList<adapter_product>();
        listOrder       = new ArrayList<adapter_order>();
        product_id      = new HashMap<String, String>();

        listViewProduct = findViewById(R.id.list_product_order);
        TV_patient_name = findViewById(R.id.patient_order);
        next            = findViewById(R.id.next);
        prev            = findViewById(R.id.prev);

        TV_patient_name.setText(Name);

        getCarts();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globalVariable.setList_view("confirmation");
                Intent intent = new Intent(SelectProductActivity.this, OrderConfirmationActivity.class);
                intent.putExtra("name",     Name);
                intent.putExtra("booked",   booked);
                intent.putExtra("gender",   gender);
                startActivity(intent);
            }
        });

        if (orderType.equals("edit")){
            prev.setVisibility(View.GONE);
        } else {
            prev.setVisibility(View.VISIBLE);
        }
    }

    private void getCarts() {
        token   = globalVariable.getToken();
        bearer  = "Bearer "+token;
        Call<ResponseBody> listCall = ApiGetMethod.getCartsDetail(bearer,globalVariable.getBooked());
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            String product_id;
                            JSONObject pass   = jsonRESULTS.getJSONObject("data");
                            JSONObject jsonObject   = pass.getJSONObject("data");
                            JSONArray carts =  jsonObject.getJSONArray("carts");
                            for (int i = 0; i < carts.length(); i++) {
                                if(carts.length()>0){
                                    JSONObject dataCarts = carts.getJSONObject(i);
                                    product_id  = dataCarts.getString("ol_product_id");
                                    arrayListOfId.add(product_id);
                                    //Toast.makeText(SelectProductActivity.this, id, Toast.LENGTH_SHORT).show();
                                }
                            }
                            JSONArray jsonArray = pass.getJSONArray("product_available");
                            //                           JSONArray jsonArray = jsonRESULTS.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.getJSONObject(i);
                                String id            = c.getString("id");
                                String title         = c.getString("title");
                                String ol_category_id= c.getString("ol_category_id");
                                String description   = c.getString("description");
                                String price         = c.getString("price");
                                String stock         = c.getString("stock");
                                String discount      = c.getString("discount");
                                String slug          = c.getString("slug");
                                String image         = c.getString("image");
                                String id_product = null;
                                checked = false;
                                for (int j = 0; j < arrayListOfId.size(); j++) {
                                    id_product = arrayListOfId.get(j);
                                    if(id_product==id){
                                        checked = true;
                                    }
                                }
                                //Toast.makeText(SelectProductActivity.this, "data : "+id + " with ID : "+id_product+" is "+ String.valueOf(checked), Toast.LENGTH_SHORT).show();
                                adapter_product _states = new adapter_product(id,title,ol_category_id,description,price,discount,slug,image,checked);
                                list_product.add(_states);
                                bindDataProduct();
                            }
                            //getProduct(gender);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SelectProductActivity.this);
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
            }
        });
    }

    private void getProduct(String s) {
        Call<ResponseBody> listCall = ApiGetMethod.getProducts(s);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            JSONObject jsonObject = jsonRESULTS.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            //                           JSONArray jsonArray = jsonRESULTS.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.getJSONObject(i);
                                String id            = c.getString("id");
                                String title         = c.getString("title");
                                String ol_category_id= c.getString("ol_category_id");
                                String description   = c.getString("description");
                                String price         = c.getString("price");
                                String stock         = c.getString("stock");
                                String discount      = c.getString("discount");
                                String slug          = c.getString("slug");
                                String image         = c.getString("image");
                                String id_product = null;
                                checked = false;
                                for (int j = 0; j < arrayListOfId.size(); j++) {
                                    id_product = arrayListOfId.get(j);
                                    if(id_product==id){
                                        checked = true;
                                    }
                                }
                                //Toast.makeText(SelectProductActivity.this, "data : "+id + " with ID : "+id_product+" is "+ String.valueOf(checked), Toast.LENGTH_SHORT).show();
                                adapter_product _states = new adapter_product(id,title,ol_category_id,description,price,discount,slug,image,checked);
                                list_product.add(_states);
                                bindDataProduct();
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
        dataProduct = new Adapter_Data_Product(getApplicationContext(),R.layout.list_product, list_product);
        listViewProduct.setAdapter(dataProduct);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(intent);
    }
}
