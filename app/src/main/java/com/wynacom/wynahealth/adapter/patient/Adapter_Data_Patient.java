package com.wynacom.wynahealth.adapter.patient;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.wynacom.wynahealth.R;

import java.util.ArrayList;

public class Adapter_Data_Patient extends ArrayAdapter<adapter_patient> {
    private ArrayList<adapter_patient> stateList;
    public Adapter_Data_Patient(@NonNull Context context, int list_patient, ArrayList<adapter_patient> list) {
        super(context, list_patient,list);
        this.stateList = new ArrayList<adapter_patient>();
        this.stateList.addAll(list);
    }

    private class ViewHolder
    {
        TextView Vnama,Vhandphone,Vsex,Vdob,Vnik,Vcity,Vpostal,Vnumber;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        String gender;

        if (convertView == null) {
            convertView =  LayoutInflater.from(getContext()).inflate(R.layout.list_patient, parent, false);

            holder = new ViewHolder();
            holder.Vnama        = (TextView) convertView.findViewById(R.id.list_patient_name);
            holder.Vhandphone   = (TextView) convertView.findViewById(R.id.list_patient_phone);
            holder.Vsex         = (TextView) convertView.findViewById(R.id.list_patient_sex);
            holder.Vdob         = (TextView) convertView.findViewById(R.id.list_patient_dob);
            holder.Vnik         = (TextView) convertView.findViewById(R.id.list_patient_nik);
            holder.Vcity        = (TextView) convertView.findViewById(R.id.list_patient_city);
            holder.Vpostal      = (TextView) convertView.findViewById(R.id.list_patient_post);
            holder.Vnumber      = (TextView) convertView.findViewById(R.id.list_patient_number);

            holder.Vnumber.setHeight(holder.Vnumber.getWidth());

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

            final adapter_patient state = stateList.get(position);

            if(state.getGender().equals("M")){
                gender  = "Laki-laki";
            }else{
                gender  = "Perempuan";
            }
            holder.Vsex         .setText(gender);

            holder.Vnama        .setText(state.getNama());
            holder.Vhandphone   .setText(state.getPhone());
            holder.Vdob         .setText(state.getDOB());
            holder.Vnik         .setText(state.getNIK());
            holder.Vcity        .setText(state.getCity());
            holder.Vpostal      .setText(state.getPostal());
            holder.Vnumber      .setText(state.getNumber());
        Log.v("ConvertView AdapterPos", String.valueOf(stateList.get(position)));

        return convertView;
    }
}
