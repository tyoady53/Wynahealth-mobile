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
        this.stateList = new ArrayList<>();
        this.stateList.addAll(list);
    }

    private static class ViewHolder
    {
        TextView Vstatus,name,Vinvno,Vphone,Vaddress,Vtotal;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        convertView =  LayoutInflater.from(getContext()).inflate(R.layout.list_order, parent, false);

        holder = new ViewHolder();
        holder.name          = (TextView) convertView.findViewById(R.id.order_name_list);
        holder.Vstatus       = (TextView) convertView.findViewById(R.id.order_status);
        holder.Vinvno        = (TextView) convertView.findViewById(R.id.order_number);
        holder.Vphone        = (TextView) convertView.findViewById(R.id.order_phone);
        holder.Vaddress      = (TextView) convertView.findViewById(R.id.order_address);
        holder.Vtotal        = (TextView) convertView.findViewById(R.id.order_total);

        final adapter_order state = stateList.get(position);

        holder.name         .setText(state.getNames());
        holder.Vstatus      .setText(state.getStatus());
        holder.Vinvno       .setText(state.getInvoice());
        holder.Vphone       .setText(state.getTelephone());
        holder.Vaddress     .setText(state.getAddress());
        NumberFormat nf = NumberFormat.getInstance(Locale.ITALY);
        String c = nf.format(Integer.parseInt(state.getTotal()));
        holder.Vtotal       .setText("Total : Rp. "+c);

        return convertView;
    }
}
