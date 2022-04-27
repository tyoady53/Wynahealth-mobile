package com.wynacom.wynahealth.adapter.product;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.DB_Local.Order_Data;
import com.wynacom.wynahealth.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Adapter_Data_Product extends ArrayAdapter<adapter_product> {
    private ArrayList<adapter_product> stateList;
    Order_Data orderData;
    ArrayList<String> selectedStrings = new ArrayList<String>();
    public Adapter_Data_Product(@NonNull Context context, int list_patient, ArrayList<adapter_product> list) {
        super(context, list_patient,list);
        this.stateList = new ArrayList<adapter_product>();
        this.stateList.addAll(list);
        orderData  = new Order_Data(getContext());
    }

    private class ViewHolder
    {
        TextView Vname,Vprice,Vdesc;
        ImageView ViewImage;
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

            holder.ViewImage    = (ImageView) convertView.findViewById(R.id.product_image);

            holder.cbProduct    = (CheckBox) convertView.findViewById(R.id.cb_product);

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
        holder.Vprice       .setText(c);
        holder.Vdesc        .setText(state.getDescription());
        holder.cbProduct    .setChecked(state.isSelected());
        String id_patient       = ((GlobalVariable) getContext()).getPatient_id();
        holder.cbProduct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    String product_id       = state.getID();
                    String ol_category_id   = state.getCetegory();
                    String title            = state.getTitle();
                    String description      = state.getDescription();
                    String price            = state.getPrice();
                    String discount         = state.getDiscount();
                    String image            = state.getImage();
                    String slug             = state.getSlug();
                    orderData.SimpanData(id_patient,product_id,ol_category_id,title,description,price,discount,image,slug);
                    Toast.makeText(getContext(),"Add Product "+product_id,Toast.LENGTH_SHORT).show();
                    //list2.add(state.getID());
                }else{
                    String product_id       = state.getID();
                    orderData.HapusRow(id_patient,product_id);
                    //list2.remove(state.getID());
                }
            }
        });

//        ViewHolder finalHolder = holder;
//        Glide.with(getContext())
//            .asBitmap()
//            .load(path)
//            .into(new CustomTarget<Bitmap>() {
//                @Override
//                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                    finalHolder.ViewImage.setImageBitmap(resource);
//                }
//
//                @Override
//                public void onLoadCleared(@Nullable Drawable placeholder) {
//                }
//            });
        Glide.with(getContext()).load(path).into(holder.ViewImage);

        holder.cbProduct.setTag(state);

        Log.v("ConvertView AdapterPos", String.valueOf(stateList.get(position)));

        return convertView;
    }
}
