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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.google.zxing.WriterException;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme;
import com.midtrans.sdk.corekit.models.BankType;
import com.midtrans.sdk.corekit.models.BillingAddress;
import com.midtrans.sdk.corekit.models.CustomerDetails;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.ShippingAddress;
import com.midtrans.sdk.corekit.models.snap.CreditCard;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;
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

public class OrderQRActivity extends AppCompatActivity implements TransactionFinishedCallback {

    String token,bearer,snap,strGender,paid_show,BILL_INFO_KEY,BILL_INFO_VALUE,user_name,user_phone,user_firstName,user_lastName,user_email,user_address,user_postal;
    String id,qty,subtotal,image,title,slug,description,product_price,discount,payment,status_order,snap_token,GrandTtl;
    private ImageView qrCodeIV;
    private EditText dataEdt;
    private Button generateQrBtn,Bt_Payment;
    private ListView listView;
    TextView TV_inv_date,TV_inv_time,TV_inv_patient,TV_inv_gender,TV_inv_dob,TV_inv_address,TV_inv_total,TV_invNo,TV_status,TV_gross,TV_discount,TV_service_date,TV_phone,TV_outlet;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    private BaseApiService mApiService,ApiGetMethod;

    GlobalVariable globalVariable;
    private ArrayList<adapter_order> List;
    TransactionRequest transactionRequest = null;
    adapter_order _states;

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
        TV_gross        = findViewById(R.id.gross_amount);
        TV_discount     = findViewById(R.id.discount_total);
        TV_service_date = findViewById(R.id.inv_service_date);
        TV_phone        = findViewById(R.id.inv_order_phone);
        TV_outlet       = findViewById(R.id.inv_outlet);

        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-vfcD9jB0fSaFu5AC") // client_key is mandatory
            .setContext(OrderQRActivity.this) // context is mandatory
            .setTransactionFinishedCallback(new TransactionFinishedCallback() {
                @Override
                public void onTransactionFinished(TransactionResult result) {
                    // Handle finished transaction here.
                }
            }) // set transaction finish callback (sdk callback)
            .setMerchantBaseUrl("http://172.169.149:8000") //set merchant url (required)
            .setColorTheme(new CustomColorTheme("#FFE51255", "#B61548", "#FFE51255")) // set theme. it will replace theme on snap theme on MAP ( optional)
            .setLanguage("en") //`en` for English and `id` for Bahasa
            .buildSDK();

        getInvoices();
        // initializing onclick listener for button.
        dataEdt.setText(snap);

        Bt_Payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(OrderQRActivity.this, "On Developing", Toast.LENGTH_SHORT).show();
                TransactionRequest transactionRequest = new TransactionRequest("E-Registation Payment",Double.parseDouble(GrandTtl));
                ArrayList<ItemDetails> itemDetailsList = new ArrayList<>();
                for (int l = 0; l < List.size(); l++) {
                    String number = String.valueOf(l);
                    String name = String.valueOf(_states.getTitle());
                    Double price = Double.parseDouble(_states.getSubtotal());
                    ItemDetails itemDetails1 = new ItemDetails(number, price, 1, name);
                    itemDetailsList.add(itemDetails1);
                }
                CreditCard creditCardOptions = new CreditCard();
// Set to true if you want to save card to Snap
                creditCardOptions.setSaveCard(false);
// Set to true to save card token as `one click` token
// Set bank name when using MIGS channel
                creditCardOptions.setBank(BankType.BRI);
// Set MIGS channel (ONLY for BCA, BRI and Maybank Acquiring bank)
                creditCardOptions.setChannel(CreditCard.MIGS);
// Set Credit Card Options
                transactionRequest.setCreditCard(creditCardOptions);

                uiKitDetails(transactionRequest);
                transactionRequest.setItemDetails(itemDetailsList);
                MidtransSDK.getInstance().setTransactionRequest(transactionRequest);
                MidtransSDK.getInstance().startPaymentUiFlow(OrderQRActivity.this,snap_token);
            }
        });
    }

    private void uiKitDetails(TransactionRequest transactionRequest) {
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setCustomerIdentifier(BILL_INFO_KEY);
        customerDetails.setPhone(user_phone);
        customerDetails.setFirstName(user_firstName);
        customerDetails.setLastName(user_lastName);
        customerDetails.setEmail(user_email);

        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setAddress(user_address);
        shippingAddress.setCity(user_address);
        shippingAddress.setPostalCode(user_postal);
        customerDetails.setShippingAddress(shippingAddress);

        BillingAddress billingAddress = new BillingAddress();
        billingAddress.setAddress(user_address);
        billingAddress.setCity(user_address);
        billingAddress.setPostalCode(user_postal);
        customerDetails.setBillingAddress(billingAddress);
        transactionRequest.setCustomerDetails(customerDetails);
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
                            JSONObject jsonObject2          = jsonRESULTS.getJSONObject("data");
                            JSONObject jsonObject           = jsonObject2.getJSONObject("data");
                            JSONObject jsonPatient          = jsonObject.getJSONObject("patient");
                            JSONObject jsonOutlet           = jsonObject.getJSONObject("outlet");
                            String invoice_response         = jsonObject.getString("invoice_no");
                            String snap_response            = jsonObject.getString("booked");
                            snap_token                      = jsonObject.getString("snap_token");
                            //String invoice_response         = c.getString("invoice");
                            if(snap_response.equals(snap)){
                                JSONObject jsonDataPatient      = jsonObject.getJSONObject("datapatient");
                                dataEdt.setText("http://wynacom.com/"+invoice_response);
                                generateQR("http://wynacom.com/"+invoice_response);
                                String payment      = jsonObject.getString("payment");
                                String order_stts   = jsonObject.getString("status");
                                String tanggal      = jsonObject.getString("created_at");
                                String sex          = jsonDataPatient.getString("sex");
                                String dob          = jsonDataPatient.getString("dob");

                                user_firstName      = jsonPatient.getString("title");
                                user_lastName       = jsonPatient.getString("patient_name");
                                user_email          = jsonPatient.getString("email");
                                user_phone          = jsonPatient.getString("handphone");
                                user_address        = jsonPatient.getString("city");
                                user_postal         = jsonPatient.getString("postal_code");

                                //Toast.makeText(getApplicationContext(),tanggal,Toast.LENGTH_SHORT).show();
                                BILL_INFO_KEY       = jsonObject.getString("booked");
                                BILL_INFO_VALUE     = jsonObject2.getString("grand_total");
                                if(order_stts.equals("pending")){
                                    status_order = getString(R.string.pending);
                                    if(payment.equals("online")){
                                        Bt_Payment.setVisibility(View.VISIBLE);
                                    }
                                }else{
                                    if(order_stts.equals("success")){
                                        status_order = getString(R.string.success);
                                    }else if(order_stts.equals("failed")){
                                        status_order = getString(R.string.failed);
                                    }else{
                                        if(payment.equals("cancel")){
                                            status_order = getString(R.string.cancel);
                                        }else{
                                            status_order = getString(R.string.expired);
                                        }
                                    }
                                    Bt_Payment.setVisibility(View.GONE);
                                }
                                String paid_from = jsonObject.getString("payment");
                                strGender = globalVariable.setGenerateGender(sex);
                                if(paid_from.equals("cod")){
                                    paid_show = status_order+ " (" +getString(R.string.cod) +")";
                                }else if(paid_from.equals("online")){
                                    paid_show = status_order+ " (" +getString(R.string.online_pay) +")";
                                }else{
                                    paid_show = "Order Canceled";
                                }

                                TV_status       .setText(paid_show);
                                TV_invNo        .setText("Invoice Number : "+jsonObject.getString("invoice_no"));
                                TV_inv_patient  .setText(jsonDataPatient.getString("name"));
                                TV_inv_date     .setText(globalVariable.dateformat(tanggal));
                                TV_inv_time     .setText(globalVariable.getTimeTimeStamp(tanggal));
                                TV_inv_gender   .setText(strGender);
                                TV_inv_dob      .setText(globalVariable.dateformat(dob));
                                TV_inv_address  .setText(jsonDataPatient.getString("city"));
                                GrandTtl  = jsonObject2.getString("grand_total");
                                TV_service_date .setText(globalVariable.dateformat(jsonObject.getString("service_date")));
                                TV_inv_total    .setText(globalVariable.toCurrency(GrandTtl));
                                TV_phone        .setText(jsonDataPatient.getString("handphone"));
                                TV_gross        .setText(globalVariable.toCurrency(jsonObject2.getString("gross_amount")));
                                TV_discount     .setText("("+globalVariable.toCurrency(jsonObject2.getString("discount_total"))+")");
                                TV_inv_total    .setTextSize(2,20);
                                TV_outlet       .setText(jsonOutlet.getString("name")+" "+jsonOutlet.getString("address"));
                                TV_inv_total.setTypeface(TV_inv_total.getTypeface(), Typeface.BOLD);
                                JSONArray jsonArray = jsonObject.getJSONArray("orders");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = jsonArray.getJSONObject(i);
                                    JSONObject Data_OrderProduct = c.getJSONObject("product");
                                    String order_id = Data_OrderProduct.getString("id");
                                    subtotal        = c.getString("price");
                                    id              = c.getString("id");
                                    qty             = Data_OrderProduct.getString("title");
                                    image           = Data_OrderProduct.getString("image");
                                    title           = Data_OrderProduct.getString("title");
                                    slug            = Data_OrderProduct.getString("slug");
                                    description     = Data_OrderProduct.getString("description");
                                    product_price   = c.getString("price");
                                    discount        = c.getString("discount");
                                    String product_id = c.getString("ol_product_id");

                                    double a = Double.parseDouble(product_price);
                                    double b = Double.parseDouble(discount);
                                    double d = a * (b/100);
                                    double Double_subTotal  = a - d;
                                    String String_subTotal  = String.valueOf(Double_subTotal);
                                    String nomDiscount      = String.valueOf(d);
                                    _states   = new adapter_order(order_id,id,qty,String_subTotal,image,title,slug,description,product_price,discount,nomDiscount,product_id);
                                    List.add(_states);
                                }
                            }
//                            BillInfoModel billInfoModel = new BillInfoModel(BILL_INFO_KEY, BILL_INFO_VALUE);
//// Set the bill info on transaction details
//                            transactionRequest.setBillInfoModel(billInfoModel);
//                            MidtransSDK.getInstance().startPaymentUiFlow(OrderQRActivity.this, snap_token);
//                            MidtransSDK.getInstance().setTransactionRequest(transactionRequest);
                            setListView();
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

    @Override
    public void onTransactionFinished(TransactionResult result) {
        if(result.getResponse() != null){
            switch (result.getStatus()){
                case TransactionResult.STATUS_SUCCESS:
                    Toast.makeText(this, "Transaction Sukses " + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    break;
                case TransactionResult.STATUS_PENDING:
                    Toast.makeText(this, "Transaction Pending " + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    break;
                case TransactionResult.STATUS_FAILED:
                    Toast.makeText(this, "Transaction Failed" + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
                    break;
            }
            result.getResponse().getValidationMessages();
        }else if(result.isTransactionCanceled()){
            Toast.makeText(this, "Transaction Failed", Toast.LENGTH_LONG).show();
        }else{
            if(result.getStatus().equalsIgnoreCase((TransactionResult.STATUS_INVALID))){
                Toast.makeText(this, "Transaction Invalid" + result.getResponse().getTransactionId(), Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Something Wrong", Toast.LENGTH_LONG).show();
            }
        }
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
        globalVariable.setLast_open("home");
        Intent i = new Intent(OrderQRActivity.this, MainActivity.class);
        i.putExtra("from", "invoice_show");
        startActivity(i);
    }
}
