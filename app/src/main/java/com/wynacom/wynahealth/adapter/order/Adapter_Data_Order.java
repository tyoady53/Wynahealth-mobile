package com.wynacom.wynahealth.adapter.order;

import android.content.Context;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.R;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;
import com.wynacom.wynahealth.transaction.OrderConfirmationActivity;

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

public class Adapter_Data_Order extends ArrayAdapter<adapter_order> {
    GlobalVariable globalVariable;
    String id_invoice,token,bearer;
    private BaseApiService mApiService;
    private final ArrayList<adapter_order> stateList;
    OrderConfirmationActivity convirmationActivity;
    public Adapter_Data_Order(@NonNull Context context, int list_patient, ArrayList<adapter_order> list) {
        super(context, list_patient,list);
        this.stateList  = new ArrayList<adapter_order>();
        this.stateList.addAll(list);
        globalVariable  = (GlobalVariable)getContext().getApplicationContext();
        id_invoice      = globalVariable.getOl_invoice_id();
        token           = globalVariable.getToken();
        bearer          = "Bearer "+token;
        mApiService     = UtilsApi.getAPI();
    }

    private static class ViewHolder {
        TextView ViewName,ViewDiscount,ViewSubtotal,ViewCount,ViewProduct,ViewDescription;
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

        final adapter_order state = stateList.get(position);
        final int count           = stateList.size();
        double discTotal = 0, priceTotal = 0;

        holder.list_type = globalVariable.getList_view();
        if(holder.list_type.equals("view")){
            holder.remove.setVisibility(View.GONE);
        }else{
            holder.remove.setVisibility(View.VISIBLE);
        }

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> jsonParams = new ArrayMap<>();
////put something inside the map, could be null
                jsonParams.put("ol_product_id", state.getProduct_id());
                jsonParams.put("ol_invoice_id", id_invoice);
                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(jsonParams)).toString());
                //ResponseBody formLogin = new ResponseBody(input.getText().toString(), password.getText().toString());
                Call<ResponseBody> listCall = mApiService.remove_itemCarts(bearer,body);
                listCall.enqueue(new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                            if(jsonRESULTS.getString("success").equals("true")){
                                convirmationActivity = new OrderConfirmationActivity();
                                convirmationActivity.getProduct();
                            }
                            Toast.makeText(getContext(), jsonRESULTS.getString("message"), Toast.LENGTH_SHORT).show();
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

        holder.viewPrice    = nf.format(Double.parseDouble(state.getProduct_price()));
        holder.viewNomDisc  = nf.format(Double.parseDouble(state.getNomDiscount()));
        holder.viewSubTtl   = nf.format(Double.parseDouble(state.getSubtotal()));

        double a = Double.parseDouble(state.getProduct_price());
        double b = Double.parseDouble(state.getNomDiscount());
        double d = a * (b/100);
        double Double_subTotal  = a - d;

        holder.ViewProduct       .setText(state.getTitle());
        holder.ViewDescription   .setText(state.getDescription());
        holder.ViewName          .setText(holder.viewPrice);
        holder.ViewDiscount      .setText("-"+nf.format(Double.parseDouble(state.getNomDiscount())));
        holder.ViewSubtotal      .setText(holder.viewSubTtl);
        holder.ViewCount         .setText(state.getView_discount()+"%");

        for (int i = 0; i < count; i++) {
            discTotal = discTotal+d;
            priceTotal= priceTotal+a;
        }
        ((GlobalVariable) getContext()).setDiscountTotal(String.valueOf(discTotal));
        ((GlobalVariable) getContext()).setPriceTotal(String.valueOf(priceTotal));

        return convertView;
    }
}
