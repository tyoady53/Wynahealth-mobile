package com.wynacom.wynahealth.transaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Duration;
import com.fxn.cue.enums.Type;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.MainActivity;
import com.wynacom.wynahealth.OrderQRActivity;
import com.wynacom.wynahealth.R;
import com.wynacom.wynahealth.adapter.order.adapter_order;
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

public class OrderConfirmationActivity extends AppCompatActivity {

    GlobalVariable globalVariable;
    private BaseApiService ApiGetMethod,mApiService;
    int CartsLength;

    TextView TV_patient_name,TV_total,TV_discount,TV_grand,TV_service_date;
    String Name,token,bearer,booked,orderType,gender;
    Button next,prev;
    String total_price,total_disc,grand,count;
    //private Adapter_Data_Order dataOrder = null;
    private MyCustomAdapter dataOrder = null;
    private ArrayList<adapter_order> list_order;
    ListView listView;
    double discTotal = 0, priceTotal = 0;

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
        count           = getIntent().getStringExtra("count");
        ApiGetMethod    = UtilsApi.getMethod();
        mApiService     = UtilsApi.getAPI();
        list_order      = new ArrayList<adapter_order>();

        listView        = findViewById(R.id.list_order_confirmation);

        TV_patient_name = findViewById(R.id.spinner_patient_order);
        TV_total        = findViewById(R.id.total);
        TV_discount     = findViewById(R.id.total_discount);
        TV_grand        = findViewById(R.id.total_orders);
        TV_service_date = findViewById(R.id.order_show_time);

        prev            = findViewById(R.id.confirm_prev);
        next            = findViewById(R.id.next);

        TV_patient_name.setText(Name);

        getProduct();

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GenericDialog.Builder(OrderConfirmationActivity.this)
                    .setDialogTheme(R.style.GenericDialogTheme)
                    .setTitle(getString(R.string.back_to)).setTitleAppearance(R.color.colorPrimaryDark, 20)
                    //.setMessage("Data Collected Successfully")
                    .addNewButton(R.style.select_product, new GenericDialogOnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(OrderConfirmationActivity.this, SelectProductActivity.class);
                            intent.putExtra("name",     Name);
                            intent.putExtra("booked",   booked);
                            intent.putExtra("gender",   gender);
                            intent.putExtra("count",    count);
                            intent.putExtra("type",     "new");
                            startActivity(intent);
                        }
                    })
                    .addNewButton(R.style.back_home, new GenericDialogOnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(OrderConfirmationActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    })
                    .setButtonOrientation(LinearLayout.VERTICAL)
                    .setCancelable(true)
                    .generate();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CartsLength >0){
                    new GenericDialog.Builder(OrderConfirmationActivity.this)
                        .setDialogTheme(R.style.GenericDialogTheme)
                        .setIcon(R.drawable.vector_payments_1)
                        .setTitle(getString(R.string.select_payment_method)).setTitleAppearance(R.color.colorPrimaryDark, 20)
                        //.setMessage("Data Collected Successfully")
                        .addNewButton(R.style.cod, new GenericDialogOnClickListener() {
                            @Override
                            public void onClick(View view) {
                                BayarDiTempat();
                            }
                        })
                        .addNewButton(R.style.online, new GenericDialogOnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(OrderConfirmationActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setButtonOrientation(LinearLayout.VERTICAL)
                        .setCancelable(true)
                        .generate();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(OrderConfirmationActivity.this);
                    builder.setMessage("Silahkan Pilih Produk untuk Melakukan Checkout.");
                    builder.setTitle("Gagal");
                    builder.setCancelable(true);
                    builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("Pilih Produk", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(OrderConfirmationActivity.this, SelectProductActivity.class);
                            intent.putExtra("name",     Name);
                            intent.putExtra("booked",   booked);
                            intent.putExtra("gender",   gender);
                            intent.putExtra("type",     "new");
                            startActivity(intent);
                        }
                    });
                    android.app.AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }

    private void BayarDiTempat(){
        Map<String, Object> jsonParams = new ArrayMap<>();
////put something inside the map, could be null
        globalVariable.setList_view("view");
        jsonParams.put("invoice_id", globalVariable.getOl_invoice_id());
        jsonParams.put("grand_total", grand);
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
                    makeToast(jsonRESULTS.getString("message"));
                    //Toast.makeText(getApplicationContext(), jsonRESULTS.getString("message"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), OrderQRActivity.class);
                    intent.putExtra("booked",   booked);
                    startActivity(intent);
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

    public void getProduct() {
        Call<ResponseBody> listCall = ApiGetMethod.getCartsDetail(bearer,booked,"1");
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            JSONObject pass = jsonRESULTS.getJSONObject("data");
                            JSONObject data = pass.getJSONObject("data");
                                String invoice_id    = data.getString("id");
                                JSONArray ArrayCarts = data.getJSONArray("carts");
                                CartsLength = ArrayCarts.length();
                                if(ArrayCarts.length() == 0){
                                    String total = null,disc = null,grandttl = null;
                                    total       = globalVariable.toCurrency("0");
                                    disc        = globalVariable.toCurrency("0");
                                    grandttl    = globalVariable.toCurrency("0");
                                    //Toast.makeText(OrderConfirmationActivity.this, total, Toast.LENGTH_SHORT).show();
                                    TV_total.setText((total));
                                    TV_discount.setText((disc));
                                    TV_grand.setText((grandttl));
                                } else {
                                    total_price = pass.getString("gross_amount");
                                    total_disc = pass.getString("discount_total");
                                    grand = pass.getString("grand_total");
                                }
                                for (int j = 0; j < ArrayCarts.length(); j++) {
                                    JSONObject Carts = ArrayCarts.getJSONObject(j);
                                    String carts_id     = Carts.getString("id");
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
                                    String nom_discount = String.valueOf(disc);
                                    adapter_order _states = new adapter_order(carts_id, invoice_id, qty, subtotal, image, title, slug, description, product_price, view_discount, nom_discount,product_id);
                                    list_order.add(_states);
                                    // Toast.makeText(OrderConfirmationActivity.this, String.valueOf(total_disc), Toast.LENGTH_SHORT).show();
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
        //Toast.makeText(OrderConfirmationActivity.this, total, Toast.LENGTH_SHORT).show();
        TV_total.setText(globalVariable.toCurrency(total_price));
        TV_discount.setText("(" + globalVariable.toCurrency(total_disc) + ")");
        TV_grand.setText(globalVariable.toCurrency(grand));
        dataOrder = new MyCustomAdapter(this,R.layout.list_product, list_order);
        listView.setAdapter(dataOrder);
    }

    private class MyCustomAdapter extends ArrayAdapter<adapter_order> {
        private ArrayList<adapter_order> stateList;
        public MyCustomAdapter(@NonNull Context context, int list_patient, ArrayList<adapter_order> list) {
            super(context, list_patient,list);
            this.stateList  = new ArrayList<adapter_order>();
            this.stateList.addAll(list);
        }

        private class ViewHolder {
            TextView ViewName,ViewDiscount,ViewSubtotal,ViewCount,ViewProduct,ViewDescription,discounts;
            String viewPrice,viewNomDisc,viewSubTtl,list_type;
            ImageView remove;
            double subtotal;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;

            convertView =  LayoutInflater.from(getContext()).inflate(R.layout.list_order_confirmation, parent, false);

            holder = new ViewHolder();
            holder.remove            = (ImageView)convertView.findViewById(R.id.cancel_order);
            holder.ViewProduct       = (TextView) convertView.findViewById(R.id.confirm_product_name);
            holder.ViewDescription   = (TextView) convertView.findViewById(R.id.confirm_product_desc);
            holder.ViewName          = (TextView) convertView.findViewById(R.id.confirm_product_price);
            holder.ViewDiscount      = (TextView) convertView.findViewById(R.id.confirm_product_discount);
            holder.ViewSubtotal      = (TextView) convertView.findViewById(R.id.confirm_product_subtotal);
            holder.ViewCount         = (TextView) convertView.findViewById(R.id.confirm_view_discount);
            holder.discounts         = (TextView) convertView.findViewById(R.id.confirm_discount_price);

            final adapter_order state = stateList.get(position);
            final int count           = stateList.size();

            holder.list_type = globalVariable.getList_view();
//            if(holder.list_type.equals("view")){
//                holder.remove.setVisibility(View.GONE);
//            }else{
//                holder.remove.setVisibility(View.VISIBLE);
//            }

            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, Object> jsonParams = new ArrayMap<>();
////put something inside the map, could be null
                    jsonParams.put("cart_id",       state.getID());
                    jsonParams.put("ol_product_id", state.getProduct_id());
                    jsonParams.put("ol_invoice_id", state.getInvoice_id());
                    RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(jsonParams)).toString());
                    //ResponseBody formLogin = new ResponseBody(input.getText().toString(), password.getText().toString());
                    Call<ResponseBody> listCall = mApiService.remove_CartsItem(bearer,body);
                    listCall.enqueue(new Callback<ResponseBody>() {

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if(jsonRESULTS.getString("success").equals("true")){
                                    dataOrder.clear();
                                    getProduct();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getContext(), "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            Locale localeID = new Locale("in", "ID");
            NumberFormat nf = NumberFormat.getCurrencyInstance(localeID);

            StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            if(Double.parseDouble(state.getNomDiscount()) > 0){
                double harga = Double.parseDouble(state.getProduct_price());
                double diskon= Double.parseDouble(state.getNomDiscount());
                double total = harga - diskon;
                ssb.append(globalVariable.toCurrency(state.getProduct_price()));
                ssb.setSpan(
                    strikethroughSpan,
                    0,
                    ssb.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                holder.ViewName    .setText(ssb,TextView.BufferType.EDITABLE);
                holder.ViewName    .setTextColor(getResources().getColor(R.color.red,null));
                holder.discounts   .setVisibility(View.VISIBLE);
                holder.discounts   .setText(globalVariable.toCurrency(String.valueOf(total)));
            }else{
                holder.ViewName    .setText(globalVariable.toCurrency(state.getProduct_price()));
                holder.discounts   .setVisibility(View.GONE);
            }

            holder.ViewProduct       .setText(state.getTitle());
            holder.ViewDescription   .setText(state.getDescription());
            //holder.ViewName          .setText(holder.viewPrice);
            holder.ViewDiscount      .setText("-"+nf.format(Double.parseDouble(state.getNomDiscount())));
            holder.ViewSubtotal      .setText(holder.viewSubTtl);
            holder.ViewCount         .setText(state.getView_discount()+"%");

            return convertView;
        }
    }

    private void makeToast(String string) {
        Cue.init().with(getApplicationContext())
            .setMessage(string)
            .setGravity(Gravity.CENTER_VERTICAL)
            .setTextSize(20).setType(Type.SUCCESS)
            .setDuration(Duration.SHORT)
            .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(intent);
    }
}
