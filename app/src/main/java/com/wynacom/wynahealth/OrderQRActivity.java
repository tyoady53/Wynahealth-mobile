package com.wynacom.wynahealth;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.google.zxing.WriterException;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderQRActivity extends AppCompatActivity {

    String token,bearer,snap,strGender;
    private ImageView qrCodeIV;
    private EditText dataEdt;
    private Button generateQrBtn;
    TextView TV_inv_date,TV_inv_time,TV_inv_patient,TV_inv_gender,TV_inv_dob,TV_inv_address,TV_inv_total;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    private BaseApiService mApiService,ApiGetMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_qr);

        mApiService     = UtilsApi.getAPI();
        ApiGetMethod    = UtilsApi.getMethod();
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

        snap            = getIntent().getStringExtra("snap_token");
        token           = ((GlobalVariable) getApplicationContext()).getToken();
        bearer          = "Bearer "+token;
        getInvoices();
        // initializing onclick listener for button.
        dataEdt.setText(snap);
//        generateQrBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(dataEdt.getText().toString())) {
//
//                    // if the edittext inputs are empty then execute
//                    // this method showing a toast message.
//                    Toast.makeText(OrderQRActivity.this, "Enter some text to generate QR Code", Toast.LENGTH_SHORT).show();
//                } else {
//                    // below line is for getting
//                    // the windowmanager service.
//                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
//
//                    // initializing a variable for default display.
//                    Display display = manager.getDefaultDisplay();
//
//                    // creating a variable for point which
//                    // is to be displayed in QR Code.
//                    Point point = new Point();
//                    display.getSize(point);
//
//                    // getting width and
//                    // height of a point
//                    int width = point.x;
//                    int height = point.y;
//
//                    // generating dimension from width and height.
//                    int dimen = width < height ? width : height;
//                    dimen = dimen * 3 / 4;
//
//                    // setting this dimensions inside our qr code
//                    // encoder to generate our qr code.
//                    qrgEncoder = new QRGEncoder(dataEdt.getText().toString(), null, QRGContents.Type.TEXT, dimen);
//                    try {
//                        // getting our qrcode in the form of bitmap.
//                        bitmap = qrgEncoder.encodeAsBitmap();
//                        // the bitmap is set inside our image
//                        // view using .setimagebitmap method.
//                        qrCodeIV.setImageBitmap(bitmap);
//                    } catch (WriterException e) {
//                        // this method is called for
//                        // exception handling.
//                        Log.e("Tag", e.toString());
//                    }
//                }
//            }
//        });
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
                            JSONObject jsonDataPatient      = jsonObject.getJSONObject("datapatient");
                            String invoice_response         = jsonObject.getString("invoice");
                            String snap_response            = jsonObject.getString("snap_token");
                            //String invoice_response         = c.getString("invoice");
                            if(snap_response.equals(snap)){
                                dataEdt.setText("http://wynacom.com/"+invoice_response);
                                generateQR("http://wynacom.com/"+invoice_response);
                                String tanggal      = jsonObject.getString("created_at");
                                String DisplayDate  = tanggal.substring(0,9);
                                String jam          = tanggal.substring(11,19);
                                String sex          = jsonDataPatient.getString("sex");
                                String dob          = jsonDataPatient.getString("dob");
                                if(sex.equals("M")){
                                    strGender = "Laki-laki";
                                }else{
                                    strGender = "Perempuan";
                                }
                                TV_inv_patient  .setText(jsonObject.getString("name"));
                                TV_inv_date     .setText(((GlobalVariable) getApplicationContext()).dateformat(DisplayDate));
                                TV_inv_time     .setText(jam);
                                TV_inv_gender   .setText(strGender);
                                TV_inv_dob      .setText(((GlobalVariable) getApplicationContext()).dateformat(dob));
                                TV_inv_address  .setText(jsonDataPatient.getString("city"));
                                TV_inv_total    .setText(jsonObject.getString("grand_total"));
                            }
                        } else {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getApplicationContext());
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
                Log.e("debug", "onFailure: ERROR > " + t.toString());
            }
        });
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
        int dimen = width < height ? width : height;
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
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity(i);
    }
}
