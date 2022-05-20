package com.wynacom.wynahealth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.google.zxing.WriterException;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.adapter.order.Adapter_Data_Order;
import com.wynacom.wynahealth.adapter.order.adapter_order;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderQRActivity extends AppCompatActivity {

    String token,bearer,snap,strGender;
    String id,qty,subtotal,image,title,slug,description,product_price,discount;
    private ImageView qrCodeIV;
    private EditText dataEdt;
    private Button generateQrBtn,Bt_Payment;
    private ListView listView;
    TextView TV_inv_date,TV_inv_time,TV_inv_patient,TV_inv_gender,TV_inv_dob,TV_inv_address,TV_inv_total,TV_invNo,TV_status;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    private BaseApiService mApiService,ApiGetMethod;

    GlobalVariable globalVariable;
    private ArrayList<adapter_order> List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_qr);

        mApiService     = UtilsApi.getAPI();
        ApiGetMethod    = UtilsApi.getMethod();

        globalVariable  = (GlobalVariable) getApplicationContext();

        snap            = getIntent().getStringExtra("booked");
        token           = globalVariable.getToken();
        bearer          = "Bearer "+token;

        List            = new ArrayList<adapter_order>();

        listView        = findViewById(R.id.inv_list_view);

        Bt_Payment      = findViewById(R.id.payment_gateway);

        qrCodeIV        = findViewById(R.id.idIVQrcode);
        dataEdt         = findViewById(R.id.idEdt);
        generateQrBtn   = findViewById(R.id.idBtnGenerateQR);

        TV_inv_date     = findViewById(R.id.inv_order_date);
        TV_inv_time     = findViewById(R.id.inv_order_time);
        TV_inv_patient  = findViewById(R.id.inv_order_patient);
        TV_inv_gender   = findViewById(R.id.inv_order_gender);
        TV_inv_dob      = findViewById(R.id.inv_order_dob);
        TV_inv_address  = findViewById(R.id.inv_order_address);
        TV_inv_total    = findViewById(R.id.inv_total);
        TV_invNo        = findViewById(R.id.inv_order_invNo);
        TV_status       = findViewById(R.id.inv_order_status);

        getInvoices();
        // initializing onclick listener for button.
        dataEdt.setText(snap);
    }

    private void getInvoices() {
        Call<ResponseBody> listCall = ApiGetMethod.getInvoicesBySnap(bearer,snap);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            JSONObject jsonObject           = jsonRESULTS.getJSONObject("data");
                            String invoice_response         = jsonObject.getString("invoice_no");
                            String snap_response            = jsonObject.getString("booked");
                            //String invoice_response         = c.getString("invoice");
                            if(snap_response.equals(snap)){
                                JSONObject jsonDataPatient      = jsonObject.getJSONObject("datapatient");
                                dataEdt.setText("http://wynacom.com/"+invoice_response);
                                generateQR("http://wynacom.com/"+invoice_response);
                                String order_stts   = jsonObject.getString("status");
                                String tanggal      = jsonObject.getString("created_at");
                                String sex          = jsonDataPatient.getString("sex");
                                String dob          = jsonDataPatient.getString("dob");
                                //Toast.makeText(getApplicationContext(),tanggal,Toast.LENGTH_SHORT).show();
                                if(order_stts.equals("pending")){
                                   Bt_Payment.setVisibility(View.VISIBLE);
                                }else{
                                    Bt_Payment.setVisibility(View.GONE);
                                }
                                strGender = globalVariable.setGenerateGender(sex);
                                TV_status       .setText(order_stts);
                                TV_invNo        .setText("Booking Number : "+jsonObject.getString("booked"));
                                TV_inv_patient  .setText(jsonDataPatient.getString("name"));
                                TV_inv_date     .setText(globalVariable.dateformat(tanggal));
                                TV_inv_time     .setText(globalVariable.getTimeTimeStamp(tanggal));
                                TV_inv_gender   .setText(strGender);
                                TV_inv_dob      .setText(globalVariable.dateformat(dob));
                                TV_inv_address  .setText(jsonDataPatient.getString("city"));
                                String GrandTtl = globalVariable.toCurrency(jsonObject.getString("grand_total"));
                                TV_inv_total    .setText(GrandTtl);
                                TV_inv_total.setTextSize(2,20);
                                TV_inv_total.setTypeface(TV_inv_total.getTypeface(), Typeface.BOLD);
                                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = jsonArray.getJSONObject(i);
                                    JSONObject Data_OrderProduct = c.getJSONObject("product");
                                    subtotal        = c.getString("price");
                                    id              = c.getString("id");
                                    qty             = Data_OrderProduct.getString("title");
                                    image           = Data_OrderProduct.getString("image");
                                    title           = Data_OrderProduct.getString("title");
                                    slug            = Data_OrderProduct.getString("slug");
                                    description     = Data_OrderProduct.getString("description");
                                    product_price   = Data_OrderProduct.getString("price");
                                    discount        = Data_OrderProduct.getString("discount");
                                    String product_id = c.getString("ol_product_id");

                                    double a = Double.parseDouble(product_price);
                                    double b = Double.parseDouble(discount);
                                    double d = a * (b/100);
                                    double Double_subTotal  = a - d;
                                    String String_subTotal  = String.valueOf(Double_subTotal);
                                    String nomDiscount      = String.valueOf(d);
                                    adapter_order _states   = new adapter_order(id,qty,String_subTotal,image,title,slug,description,product_price,discount,nomDiscount,product_id);
                                    List.add(_states);
                                }
                            }setListView();
                       } else {
                            AlertDialog.Builder builder = new android.app.AlertDialog.Builder(OrderQRActivity.this);
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
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("debug", "onFailure: ERROR > " + t.toString());
            }
        });
    }

    private void setListView() {
        Adapter_Data_Order dataAdapter = new Adapter_Data_Order(getApplicationContext(), R.layout.list_patient, List);
        listView.setAdapter(dataAdapter);
    }

    private void generateQR(String snap) {
        // below line is for getting
        // the windowmanager service.
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();

        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);

        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = Math.min(width, height);
        dimen = dimen * 3 / 4;

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        qrgEncoder = new QRGEncoder(dataEdt.getText().toString(), null, QRGContents.Type.TEXT, dimen);
        try {
            // getting our qrcode in the form of bitmap.
            bitmap = qrgEncoder.encodeAsBitmap();
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            qrCodeIV.setImageBitmap(bitmap);
        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(OrderQRActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void gotohome(View view) {
        Intent i = new Intent(OrderQRActivity.this, MainActivity.class);
        i.putExtra("from", "invoice_show");
        startActivity(i);
    }
}
