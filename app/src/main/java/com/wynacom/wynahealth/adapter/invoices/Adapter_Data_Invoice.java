package com.wynacom.wynahealth.adapter.invoices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.wynacom.wynahealth.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Adapter_Data_Invoice extends ArrayAdapter<adapter_invoice> {
    private final ArrayList<adapter_invoice> stateList;
    public Adapter_Data_Invoice(@NonNull Context context, int list_patient, ArrayList<adapter_invoice> list) {
        super(context, list_patient,list);
        this.stateList = new ArrayList<>();
        this.stateList.addAll(list);
    }

    private static class ViewHolder
    {
        TextView Vstatus,name,Vinvno,Vphone,Vaddress,Vtotal;
        View status_color;
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
        holder.status_color  = (View) convertView.findViewById(R.id.status_color);

        final adapter_invoice state = stateList.get(position);

        holder.name         .setText(state.getNames());
        holder.Vstatus      .setText(state.getStatus());
        holder.Vinvno       .setText(state.getInvoice());
        holder.Vphone       .setText(state.getTelephone());
        holder.Vaddress     .setText(state.getAddress());
        Locale localeID = new Locale("in", "ID");
        NumberFormat nf = NumberFormat.getCurrencyInstance(localeID);
        String c = nf.format(Integer.parseInt(state.getTotal()));
        holder.Vtotal       .setText(c);

        if (holder.Vstatus.getText().toString().equals("Waiting For Payment")) {
            holder.status_color.setBackgroundColor(ResourcesCompat.getColor(convertView.getResources(),R.color.yellow,null));
        } else if (holder.Vstatus.getText().toString().equals("Order Paid")) {
            holder.status_color.setBackgroundColor(ResourcesCompat.getColor(convertView.getResources(), R.color.green, null));
        } else {
            holder.status_color.setBackgroundColor(ResourcesCompat.getColor(convertView.getResources(), R.color.red, null));
        }

        return convertView;
    }
}
