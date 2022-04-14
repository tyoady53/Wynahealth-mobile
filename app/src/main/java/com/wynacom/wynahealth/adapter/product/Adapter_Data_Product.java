package com.wynacom.wynahealth.adapter.product;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wynacom.wynahealth.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Adapter_Data_Product extends ArrayAdapter<adapter_product> {
    private ArrayList<adapter_product> stateList;
    public Adapter_Data_Product(@NonNull Context context, int list_patient, ArrayList<adapter_product> list) {
        super(context, list_patient,list);
        this.stateList = new ArrayList<adapter_product>();
        this.stateList.addAll(list);
    }

    private class ViewHolder
    {
        TextView Vname,Vprice,Vdesc;
        CheckBox cbProduct;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        ViewHolder holder = null;

//        Log.v("ConvertView AdapterCnt", String.valueOf(stateList.stream().count()));

        if (convertView == null) {
            //LayoutInflater vi = (LayoutInflater)convertView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView =  LayoutInflater.from(getContext()).inflate(R.layout.list_product, parent, false);

            holder = new ViewHolder();
            holder.Vname        = (TextView) convertView.findViewById(R.id.tv_product_name);
            holder.Vprice       = (TextView) convertView.findViewById(R.id.tv_product_price);
            holder.Vdesc        = (TextView) convertView.findViewById(R.id.tv_product_desc);

            holder.cbProduct    = (CheckBox) convertView.findViewById(R.id.cb_product);

            convertView.setTag(holder);

            holder.cbProduct.setOnClickListener( new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    CheckBox cb = (CheckBox) v;
                    adapter_product _state = (adapter_product) cb.getTag();

                    _state.setSelected(cb.isChecked());
                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final adapter_product state = stateList.get(position);

        holder.cbProduct    .setText(state.getTitle());
        NumberFormat nf = NumberFormat.getInstance(Locale.ITALY);
        String c = nf.format(Integer.parseInt(state.getPrice()));
        holder.Vprice       .setText("Rp. "+c);
        holder.Vdesc        .setText(state.getDescription());
        holder.cbProduct    .setChecked(state.isSelected());

        holder.cbProduct.setTag(state);
        Log.v("ConvertView AdapterPos", String.valueOf(stateList.get(position)));

        return convertView;
    }
}
