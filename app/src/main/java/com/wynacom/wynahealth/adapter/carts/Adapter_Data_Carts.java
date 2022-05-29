package com.wynacom.wynahealth.adapter.carts;

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

public class Adapter_Data_Carts extends ArrayAdapter<adapter_carts> {
    private final ArrayList<adapter_carts> stateList;
    public Adapter_Data_Carts(@NonNull Context context, int list_patient, ArrayList<adapter_carts> list) {
        super(context, list_patient,list);
        this.stateList = new ArrayList<adapter_carts>();
        this.stateList.addAll(list);
    }

    private static class ViewHolder {
        TextView name,Vphone,Vaddress,Vtotal;
        View status_color;
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

        final adapter_carts state = stateList.get(position);

        holder.name         .setText(state.getNames());
        holder.Vphone       .setText(state.getAddress()


        );
        holder.Vaddress     .setText(state.getAddress());
        Locale localeID = new Locale("in", "ID");
        NumberFormat nf = NumberFormat.getCurrencyInstance(localeID);
        String c = nf.format(Integer.parseInt(state.getTotal()));
        holder.Vtotal       .setText(c);

        return convertView;
    }
}
