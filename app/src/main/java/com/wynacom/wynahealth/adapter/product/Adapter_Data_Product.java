package com.wynacom.wynahealth.adapter.product;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.DB_Local.Order_Data;
import com.wynacom.wynahealth.R;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Adapter_Data_Product extends ArrayAdapter<adapter_product> {
    private ArrayList<adapter_product> stateList;
    Order_Data orderData;
    ArrayList<String> selectedStrings = new ArrayList<String>();
    private BaseApiService mApiService,ApiGetMethod;
    String bearer,token;
    GlobalVariable globalVariable;

    public Adapter_Data_Product(@NonNull Context context, int list_patient, ArrayList<adapter_product> list) {
        super(context, list_patient,list);
        this.stateList = new ArrayList<adapter_product>();
        this.stateList.addAll(list);
        orderData   = new Order_Data(getContext());

        globalVariable  = (GlobalVariable)getContext().getApplicationContext();

        ApiGetMethod    = UtilsApi.getMethod();
    }

    static class ViewHolder
    {
        TextView Vname,Vprice,Vdesc,cbProduct,discount_view;
        ImageView ViewImage;
        ImageButton btnAdd,btnDelete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        ViewHolder holder = null;
        BaseApiService mApiService = UtilsApi.getAPI();
        GlobalVariable myAppClass = (GlobalVariable)getContext();
        if (convertView == null) {
            convertView =  LayoutInflater.from(getContext()).inflate(R.layout.list_product, parent, false);

            holder = new ViewHolder();
            holder.Vname        = (TextView) convertView.findViewById(R.id.tv_product_name);
            holder.Vprice       = (TextView) convertView.findViewById(R.id.tv_product_price);
            holder.Vdesc        = (TextView) convertView.findViewById(R.id.tv_product_desc);

            holder.ViewImage    = (ImageView) convertView.findViewById(R.id.product_image);

            holder.cbProduct    = (TextView) convertView.findViewById(R.id.cb_product);

            holder.btnAdd       = (ImageButton) convertView.findViewById(R.id.order_add);
            holder.btnDelete    = (ImageButton) convertView.findViewById(R.id.order_delete);
            holder.discount_view= (TextView) convertView.findViewById(R.id.tv_discount_price);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final adapter_product state = stateList.get(position);
        holder.cbProduct    .setText(state.getTitle());
        Locale localeID = new Locale("in", "ID");
        NumberFormat nf = NumberFormat.getCurrencyInstance(localeID);
        String c = nf.format(Integer.parseInt(state.getPrice()));
        String path     = state.getImage();

        //holder.Vprice       .setText(c);
        StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        if(Integer.parseInt(state.getDiscount()) > 0){
            double harga = Double.parseDouble(state.getPrice());
            double diskon= Double.parseDouble(state.getDiscount());
            double total = harga - (harga*(diskon/100));
            ssb.append(globalVariable.toCurrency(state.getPrice()));
            ssb.setSpan(
                strikethroughSpan,
                0,
                ssb.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            holder.Vprice       .setText(ssb,TextView.BufferType.NORMAL);
            holder.discount_view.setVisibility(View.VISIBLE);
            holder.discount_view.setText(globalVariable.toCurrency(String.valueOf(total)));
        }else{
            holder.Vprice       .setText(globalVariable.toCurrency(state.getPrice()));
            holder.discount_view.setVisibility(View.GONE);
        }
        holder.Vdesc        .setText(state.getDescription());
        String id_patient       = globalVariable.getUserID();
        String id_invoice       = globalVariable.getOl_invoice_id();
        //String token            = ((GlobalVariable) getContext()).getToken();
        String token            = globalVariable.getToken();
        String bearer           = "Bearer "+token;
        String product_id       = state.getID();

        if(state.isSelected()){
            holder.btnAdd.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        }else{
            holder.btnAdd.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.GONE);
        }
        //Toast.makeText(getContext(), "Data "+position+" with ID "+state.getID()+" is "+state.isSelected(), Toast.LENGTH_SHORT).show();

        ViewHolder finalHolder = holder;
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ol_category_id   = state.getCetegory();
                String title            = state.getTitle();
                String description      = state.getDescription();
                String price            = state.getPrice();
                String discount         = state.getDiscount();
                String image            = state.getImage();
                String slug             = state.getSlug();
                Map<String, Object> jsonParams = new ArrayMap<>();
//put something inside the map, could be null
                jsonParams.put("ol_product_id", product_id);
                jsonParams.put("ol_patient_id", id_patient);
                jsonParams.put("qty", "1");
                jsonParams.put("price", price);
                jsonParams.put("ol_invoice_id", id_invoice);
                jsonParams.put("discount", discount);
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(jsonParams)).toString());
                //ResponseBody formLogin = new ResponseBody(input.getText().toString(), password.getText().toString());
                Call<ResponseBody> listCall = mApiService.postCarts(bearer,body);
                listCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                            if(jsonRESULTS.getString("success").equals("true")){
                                finalHolder.btnAdd.setVisibility(View.GONE);
                                finalHolder.btnDelete.setVisibility(View.VISIBLE);
                            }else{
                                finalHolder.btnAdd.setVisibility(View.VISIBLE);
                                finalHolder.btnDelete.setVisibility(View.GONE);
                            }
                            //Toast.makeText(myAppClass, jsonRESULTS.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(myAppClass, "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //selectedStrings.add(product_id);
//        holder.cbProduct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                String product_id       = state.getID();
//                if (isChecked) {
//                    String ol_category_id   = state.getCetegory();
//                    String title            = state.getTitle();
//                    String description      = state.getDescription();
//                    String price            = state.getPrice();
//                    String discount         = state.getDiscount();
//                    String image            = state.getImage();
//                    String slug             = state.getSlug();
//                    Map<String, Object> jsonParams = new ArrayMap<>();
////put something inside the map, could be null
//                    jsonParams.put("ol_product_id", product_id);
//                    jsonParams.put("ol_patient_id", id_patient);
//                    jsonParams.put("qty", "1");
//                    jsonParams.put("price", price);
//                    jsonParams.put("ol_invoice_id", id_invoice);
//                    RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(jsonParams)).toString());
//                    //ResponseBody formLogin = new ResponseBody(input.getText().toString(), password.getText().toString());
//                    Call<ResponseBody> listCall = mApiService.postCarts(bearer,body);
//                    listCall.enqueue(new Callback<ResponseBody>() {
//                        @Override
//                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            try {
//                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
//                                Toast.makeText(myAppClass, jsonRESULTS.getString("message"), Toast.LENGTH_SHORT).show();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        @Override
//                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//                            Toast.makeText(myAppClass, "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    //orderData.SimpanData(id_patient,product_id,ol_category_id,title,description,price,discount,image,slug);
//                }else{
//                    Map<String, Object> jsonParams = new ArrayMap<>();
////put something inside the map, could be null
//                    jsonParams.put("ol_product_id", product_id);
//                    jsonParams.put("ol_invoice_id", id_invoice);
//                    RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(jsonParams)).toString());
//                    //ResponseBody formLogin = new ResponseBody(input.getText().toString(), password.getText().toString());
//                    Call<ResponseBody> listCall = mApiService.remove_itemCarts(bearer,body);
//                    listCall.enqueue(new Callback<ResponseBody>() {
//
//                        @Override
//                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            try {
//                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
//                                Toast.makeText(myAppClass, jsonRESULTS.getString("message"), Toast.LENGTH_SHORT).show();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//                            Toast.makeText(myAppClass, "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    //orderData.HapusRow(id_invoice,product_id);
//                }
//            }
//        });

        Glide.with(getContext()).load(path).into(holder.ViewImage);

        holder.cbProduct.setTag(state);

        Log.v("ConvertView AdapterPos", String.valueOf(stateList.get(position)));

        return convertView;
    }
}
