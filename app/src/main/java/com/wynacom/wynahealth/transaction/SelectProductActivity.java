package com.wynacom.wynahealth.transaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fxn.cue.Cue;
import com.fxn.cue.enums.Duration;
import com.fxn.cue.enums.Type;
import com.lakue.pagingbutton.LakuePagingButton;
import com.lakue.pagingbutton.OnPageSelectListener;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.DB_Local.Order_Data;
import com.wynacom.wynahealth.MainActivity;
import com.wynacom.wynahealth.R;
import com.wynacom.wynahealth.adapter.order.Adapter_Data_Order;
import com.wynacom.wynahealth.adapter.order.adapter_order;
import com.wynacom.wynahealth.adapter.product.adapter_product;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import dev.jai.genericdialog2.GenericDialog;
import dev.jai.genericdialog2.GenericDialogOnClickListener;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectProductActivity extends AppCompatActivity {

    boolean checked = false;

    GlobalVariable globalVariable;
    private BaseApiService ApiGetMethod;
    private MyCustomAdapter dataProduct = null;
    private ArrayList<adapter_product> list_product;
    ListView listViewProduct;
    TextView TV_patient_name;
    String Name,booked,orderType,gender,token,bearer,last_page,string_now_page,nowPage;
    Button next,prev;
    int int_last_page,max_page,NowPage;
    LakuePagingButton lpb_buttonlist;

    private Adapter_Data_Order dataOrder = null;
    private ArrayList<adapter_order> listOrder;
    Map<String, String> product_id;
    ArrayList<String> arrayListOfId = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);
        globalVariable  = (GlobalVariable) getApplicationContext();
        Name            = getIntent().getStringExtra("name");
        booked          = globalVariable.getBooked();
        orderType       = getIntent().getStringExtra("type");
        gender          = getIntent().getStringExtra("gender");
        max_page        = Integer.parseInt(getIntent().getStringExtra("count"));
        ApiGetMethod    = UtilsApi.getMethod();
        list_product    = new ArrayList<adapter_product>();
        listOrder       = new ArrayList<adapter_order>();
        product_id      = new HashMap<String, String>();
        int_last_page   = 0;

        listViewProduct = findViewById(R.id.list_product_order);
        TV_patient_name = findViewById(R.id.patient_order);
        next            = findViewById(R.id.next);
        prev            = findViewById(R.id.prev);
        lpb_buttonlist  = findViewById(R.id.lpb_buttonList);

        TV_patient_name.setText(Name);

        getCarts("1");

        lpb_buttonlist.setPageItemCount(4);
        lpb_buttonlist.addBottomPageButton(max_page,1);
        Toast.makeText(SelectProductActivity.this, ""+max_page, Toast.LENGTH_SHORT).show();
        lpb_buttonlist.setOnPageSelectListener(new OnPageSelectListener() {
            @Override
            public void onPageBefore(int now_page) {
                lpb_buttonlist.addBottomPageButton(max_page,now_page);
            }

            @Override
            public void onPageCenter(int now_page) {
                String nowPageStr = String.valueOf(now_page);
                if(dataProduct.getCount()>0){
                    dataProduct.clear();
                }
                getCarts(nowPageStr);
            }

            @Override
            public void onPageNext(int now_page) {
                lpb_buttonlist.addBottomPageButton(max_page,now_page);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globalVariable.setList_view("confirmation");
                Intent intent = new Intent(SelectProductActivity.this, OrderConfirmationActivity.class);
                intent.putExtra("name",     Name);
                intent.putExtra("booked",   booked);
                intent.putExtra("gender",   gender);
                startActivity(intent);
            }
        });

        if (orderType.equals("edit")){
            prev.setVisibility(View.GONE);
        } else {
            prev.setVisibility(View.VISIBLE);
        }
    }

    private void getCarts(String page) {
        token   = globalVariable.getToken();
        bearer  = "Bearer "+token;
        Call<ResponseBody> listCall = ApiGetMethod.getCartsDetail(bearer,globalVariable.getBooked(),page);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            String product_id;
                            JSONObject pass   = jsonRESULTS.getJSONObject("data");
                            JSONObject jsonObject   = pass.getJSONObject("data");
                            JSONArray carts =  jsonObject.getJSONArray("carts");
                            for (int i = 0; i < carts.length(); i++) {
                                if(carts.length()>0){
                                    JSONObject dataCarts = carts.getJSONObject(i);
                                    product_id  = dataCarts.getString("ol_product_id");
                                    arrayListOfId.add(product_id);
                                }
                            }
                            JSONObject product_object = pass.getJSONObject("product_available");
                            JSONArray jsonArray = product_object.getJSONArray("data");
                            //                           JSONArray jsonArray = jsonRESULTS.getJSONArray("data");
                            last_page               = product_object.getString("last_page");
                            String now              = product_object.getString("current_page");
                            NowPage                 = Integer.parseInt(page);
                            int_last_page           = Integer.parseInt(last_page);
                            //max_page                = int_last_page;
                            generatePagingButton();
                            Toast.makeText(SelectProductActivity.this, "Current Page : "+NowPage+"\nLast Page : "+int_last_page+"\nMax Page : "+max_page, Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.getJSONObject(i);
                                String id            = c.getString("id");
                                String title         = c.getString("title");
                                String ol_category_id= c.getString("ol_category_id");
                                String description   = c.getString("description");
                                String price         = c.getString("price");
                                String stock         = c.getString("stock");
                                String discount      = c.getString("discount");
                                String slug          = c.getString("slug");
                                String image         = c.getString("image");
                                String id_product = null;
                                checked = false;
                                for (int j = 0; j < arrayListOfId.size(); j++) {
                                    id_product = arrayListOfId.get(j);
                                    if(id_product==id){
                                        checked = true;
                                    }
                                }
                                //Toast.makeText(SelectProductActivity.this, "data : "+id + " with ID : "+id_product+" is "+ String.valueOf(checked), Toast.LENGTH_SHORT).show();
                                adapter_product _states = new adapter_product(id,title,ol_category_id,description,price,discount,slug,image,checked);
                                list_product.add(_states);
                                bindDataProduct();
                            }

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SelectProductActivity.this);
                            builder.setMessage("Data Patient Kosong.");
                            builder.setTitle("List Patient");
                            builder.setCancelable(true);
                            builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            android.app.AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Cue.init().with(getApplicationContext()).setMessage("Tidak ada data pasien").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.PRIMARY).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("debug", "onFailure: ERROR > getDataPatient" + t.toString());
            }
        });
    }

    private void generatePagingButton() {
    }

    private void getProduct(String s) {
        Call<ResponseBody> listCall = ApiGetMethod.getProducts(s);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            JSONObject jsonObject = jsonRESULTS.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            //                           JSONArray jsonArray = jsonRESULTS.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.getJSONObject(i);
                                String id            = c.getString("id");
                                String title         = c.getString("title");
                                String ol_category_id= c.getString("ol_category_id");
                                String description   = c.getString("description");
                                String price         = c.getString("price");
                                String stock         = c.getString("stock");
                                String discount      = c.getString("discount");
                                String slug          = c.getString("slug");
                                String image         = c.getString("image");
                                String id_product = null;
                                checked = false;
                                for (int j = 0; j < arrayListOfId.size(); j++) {
                                    id_product = arrayListOfId.get(j);
                                    if(id_product==id){
                                        checked = true;
                                    }
                                }
                                //Toast.makeText(SelectProductActivity.this, "data : "+id + " with ID : "+id_product+" is "+ String.valueOf(checked), Toast.LENGTH_SHORT).show();
                                adapter_product _states = new adapter_product(id,title,ol_category_id,description,price,discount,slug,image,checked);
                                list_product.add(_states);
                                bindDataProduct();
                            }
                        } else {
                            Cue.init().with(getApplicationContext()).setMessage("Tidak ada data pasien").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.PRIMARY).show();
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Cue.init().with(getApplicationContext()).setMessage("Tidak ada data pasien").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.PRIMARY).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("debug", "onFailure: ERROR > " + t.toString());
            }
        });
    }

    private void bindDataProduct() {
        dataProduct = new MyCustomAdapter(getApplicationContext(),R.layout.list_product, list_product);
        listViewProduct.setAdapter(dataProduct);
    }

    public class MyCustomAdapter extends ArrayAdapter<adapter_product> {
        private ArrayList<adapter_product> stateList;
        Order_Data orderData;

        public MyCustomAdapter(@NonNull Context context, int list_patient, ArrayList<adapter_product> list) {
            super(context, list_patient,list);
            this.stateList = new ArrayList<adapter_product>();
            this.stateList.addAll(list);
            orderData   = new Order_Data(getContext());

            ApiGetMethod    = UtilsApi.getMethod();
        }

        private class ViewHolder
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
                                    createToast(jsonRESULTS.getString("message"));
                                }else{
                                    finalHolder.btnAdd.setVisibility(View.VISIBLE);
                                    finalHolder.btnDelete.setVisibility(View.GONE);
                                }
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

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new GenericDialog.Builder(SelectProductActivity.this)
                        .setDialogTheme(R.style.GenericDialogTheme)
                        .setTitle(getString(R.string.delete_from_carts)).setTitleAppearance(R.color.colorPrimaryDark, 20)
                        //.setMessage("Data Collected Successfully")
                        .addNewButton(R.style.yes_option, new GenericDialogOnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Map<String, Object> jsonParams = new ArrayMap<>();
////put something inside the map, could be null
                                jsonParams.put("ol_product_id",       state.getID());
                                jsonParams.put("ol_invoice_id", globalVariable.getOl_invoice_id());
                                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),(new JSONObject(jsonParams)).toString());
                                //ResponseBody formLogin = new ResponseBody(input.getText().toString(), password.getText().toString());
                                Call<ResponseBody> listCall = mApiService.remove_itemCarts(bearer,body);
                                listCall.enqueue(new Callback<ResponseBody>() {

                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        try {
                                            JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                            if(jsonRESULTS.getString("success").equals("true")){
                                                finalHolder.btnAdd.setVisibility(View.VISIBLE);
                                                finalHolder.btnDelete.setVisibility(View.GONE);
                                            } else {
                                                finalHolder.btnAdd.setVisibility(View.GONE);
                                                finalHolder.btnDelete.setVisibility(View.VISIBLE);
                                            }
                                            //Toast.makeText(getContext(), jsonRESULTS.getString("message"), Toast.LENGTH_SHORT).show();
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
                        })
                        .addNewButton(R.style.no_option, new GenericDialogOnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        })
                        .setButtonOrientation(LinearLayout.HORIZONTAL)
                        .setCancelable(true)
                        .generate();
                }
            });

            Glide.with(getContext()).load(path).into(holder.ViewImage);

            holder.cbProduct.setTag(state);

            Log.v("ConvertView AdapterPos", String.valueOf(stateList.get(position)));

            return convertView;
        }
    }

    private void createToast(String rm) {
        Cue.init().with(getApplicationContext())
            .setMessage(rm)
            .setGravity(Gravity.CENTER_VERTICAL)
            .setTextSize(20).setType(Type.SUCCESS)
            .setDuration(Duration.SHORT)
            .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(intent);
    }
}
