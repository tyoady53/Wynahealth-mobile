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

import androidx.appcompat.app.AppCompatActivity;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.google.zxing.WriterException;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;

import org.json.JSONArray;
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

    String token,bearer,email;
    private ImageView qrCodeIV;
    private EditText dataEdt;
    private Button generateQrBtn;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;
    private BaseApiService mApiService,ApiGetMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_qr);

        mApiService = UtilsApi.getAPI();
        ApiGetMethod= UtilsApi.getMethod();
        qrCodeIV = findViewById(R.id.idIVQrcode);
        dataEdt = findViewById(R.id.idEdt);
        generateQrBtn = findViewById(R.id.idBtnGenerateQR);

        email =getIntent().getStringExtra("emailKey");
        token           = ((GlobalVariable) getApplicationContext()).getToken();
        bearer          = "Bearer "+token;
        getInvoices();
        // initializing onclick listener for button.
        dataEdt.setText(email);
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
        Call<ResponseBody> listCall = ApiGetMethod.getInvoices(bearer);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            JSONObject jsonObject = jsonRESULTS.getJSONObject("data");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
//                            JSONArray jsonArray = jsonRESULTS.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.getJSONObject(i);
                                String snap_response            = c.getString("snap_token");
                                String invoice_response            = c.getString("invoice");
                                if(snap_response.equals(email)){
                                    dataEdt.setText("http://wynacom.com/"+invoice_response);
                                    generateQR(invoice_response);
                                }
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
