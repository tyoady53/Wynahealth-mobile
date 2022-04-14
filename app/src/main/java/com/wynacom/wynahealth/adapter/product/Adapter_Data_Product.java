package com.wynacom.wynahealth.adapter.product;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Adapter_Data_Product extends ArrayAdapter<adapter_product> {
    private ArrayList<adapter_product> stateList;
    ArrayList<String> selectedStrings = new ArrayList<String>();
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
        GlobalVariable myAppClass = (GlobalVariable)getContext();
        if (convertView == null) {
            convertView =  LayoutInflater.from(getContext()).inflate(R.layout.list_product, parent, false);

            holder = new ViewHolder();
            holder.Vname        = (TextView) convertView.findViewById(R.id.tv_product_name);
            holder.Vprice       = (TextView) convertView.findViewById(R.id.tv_product_price);
            holder.Vdesc        = (TextView) convertView.findViewById(R.id.tv_product_desc);

            holder.cbProduct    = (CheckBox) convertView.findViewById(R.id.cb_product);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final adapter_product state = stateList.get(position);
        List<String> list2 = new ArrayList<>();
        holder.cbProduct    .setText(state.getTitle());
        NumberFormat nf = NumberFormat.getInstance(Locale.ITALY);
        String c = nf.format(Integer.parseInt(state.getPrice()));
        holder.Vprice       .setText("Rp. "+c);
        holder.Vdesc        .setText(state.getDescription());
        holder.cbProduct    .setChecked(state.isSelected());
        holder.cbProduct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    list2.add(state.getID());
                    myAppClass.setGlobalArrayList(list2);
                    //Toast.makeText(getContext(), state.getID(), Toast.LENGTH_SHORT).show();
                }else{
                    list2.remove(state.getID());
                }
            }
        });
        holder.cbProduct.setTag(state);

        Log.v("ConvertView AdapterPos", String.valueOf(stateList.get(position)));

        return convertView;
    }
}
