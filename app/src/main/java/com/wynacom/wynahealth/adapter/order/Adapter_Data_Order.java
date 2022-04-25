package com.wynacom.wynahealth.adapter.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wynacom.wynahealth.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Adapter_Data_Order extends ArrayAdapter<adapter_order> {
    private final ArrayList<adapter_order> stateList;
    public Adapter_Data_Order(@NonNull Context context, int list_patient, ArrayList<adapter_order> list) {
        super(context, list_patient,list);
        this.stateList = new ArrayList<adapter_order>();
        this.stateList.addAll(list);
    }

    private static class ViewHolder {
        TextView ViewName,ViewDiscount,ViewSubtotal,ViewCount,ViewProduct,ViewDescription;
        String viewPrice,viewNomDisc,viewSubTtl;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        convertView =  LayoutInflater.from(getContext()).inflate(R.layout.list_order_confirmation, parent, false);

        holder = new ViewHolder();
        holder.ViewProduct       = (TextView) convertView.findViewById(R.id.confirm_product_name);
        holder.ViewDescription   = (TextView) convertView.findViewById(R.id.confirm_product_desc);
        holder.ViewName          = (TextView) convertView.findViewById(R.id.confirm_product_price);
        holder.ViewDiscount      = (TextView) convertView.findViewById(R.id.confirm_product_discount);
        holder.ViewSubtotal      = (TextView) convertView.findViewById(R.id.confirm_product_subtotal);
        holder.ViewCount         = (TextView) convertView.findViewById(R.id.confirm_view_discount);

        final adapter_order state = stateList.get(position);

        Locale localeID = new Locale("in", "ID");
        NumberFormat nf = NumberFormat.getCurrencyInstance(localeID);

        holder.viewPrice    = nf.format(Double.parseDouble(state.getProduct_price()));
        holder.viewNomDisc  = nf.format(Double.parseDouble(state.getNomDiscount()));
        holder.viewSubTtl   = nf.format(Double.parseDouble(state.getSubtotal()));

        holder.ViewProduct       .setText(state.getTitle());
        holder.ViewDescription   .setText(state.getDescription());
        holder.ViewName          .setText(holder.viewPrice);
        holder.ViewDiscount      .setText(holder.viewNomDisc);
        holder.ViewSubtotal      .setText(holder.viewSubTtl);
        holder.ViewCount         .setText(state.getView_discount()+"%");
//        Locale localeID = new Locale("in", "ID");
//        NumberFormat nf = NumberFormat.getCurrencyInstance(localeID);
//        String c = nf.format(Integer.parseInt(state.getTotal()));
//        holder.Vtotal       .setText(c);

        return convertView;
    }
}
