package com.wynacom.wynahealth.ui.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.github.clans.fab.FloatingActionButton;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.DB_Local.Local_Data;
import com.wynacom.wynahealth.OrderQRActivity;
import com.wynacom.wynahealth.R;
import com.wynacom.wynahealth.adapter.invoices.Adapter_Data_Invoice;
import com.wynacom.wynahealth.adapter.invoices.adapter_invoice;
import com.wynacom.wynahealth.adapter.patient.Adapter_Data_Patient;
import com.wynacom.wynahealth.adapter.patient.adapter_patient;
import com.wynacom.wynahealth.adapter.product.Adapter_Data_Product;
import com.wynacom.wynahealth.adapter.product.adapter_product;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;
import com.wynacom.wynahealth.databinding.FragmentDashboardBinding;
import com.wynacom.wynahealth.transaction.NewOrderActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    Local_Data local_data;
    protected Cursor cursor;
    String string_pending, string_success,string_failed, string_expired, string_ktp, string_kota, string_kodepos,token,bearer;
    String nama_pasien,handphone,sex,dob,nik,city,postal_code,tampiltanggal,patient_id;
    TextView TV_success,TV_pending,TV_failed,TV_expired;

    GlobalVariable globalVariable;

    private Adapter_Data_Product dataAdapter = null;
    private ArrayList<adapter_product> List;

    private Adapter_Data_Invoice dataOrder = null;
    private ArrayList<adapter_invoice> orderList;

    private Adapter_Data_Patient dataPatient = null;
    private ArrayList<adapter_patient> patientList;

    int arrayCount = 0;
    ListView listView;
    LinearLayout linearLayout,linearInfo;
    Button buttonOrder;
    FloatingActionButton fab_add;
    double cardWidth = 0;
    ProgressBar progress;
    CardView CardSuccess,CarsPending,CardFailed,CardExpired;

    private BaseApiService mApiService,ApiGetMethod;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        globalVariable = (GlobalVariable) getContext().getApplicationContext();
        mApiService = UtilsApi.getAPI();
        ApiGetMethod= UtilsApi.getMethod();
        local_data  = new Local_Data(getContext());
        List        = new ArrayList<adapter_product>();
        orderList   = new ArrayList<adapter_invoice>();
        patientList = new ArrayList<adapter_patient>();

        token           = ((GlobalVariable) getContext().getApplicationContext()).getToken();
        bearer          = "Bearer "+token;

        dashboardViewModel =
                new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TV_success      = binding.tvSuccess;
        TV_pending      = binding.tvPending;
        TV_failed       = binding.tvFailed;
        TV_expired      = binding.tvExpired;
        buttonOrder     = binding.btnNewOrder;
        listView        = binding.listOrder;
        linearLayout    = binding.linearOrderList;
        linearInfo      = binding.listPatientNumber;

        CardSuccess     = binding.cardSuccess;
        CarsPending     = binding.cardPending;
        CardFailed      = binding.cardFailed;
        CardExpired     = binding.cardExpired;

        fab_add         = binding.fabOrder;

        progress        = binding.progressCircular;
        listView.setVisibility(View.GONE);

        getDashboard();
        refreshList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                globalVariable.setList_view("view");
                adapter_invoice state = orderList.get(position);
                String snap=state.getInvoice();
                //Toast.makeText(getContext(), ids, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), OrderQRActivity.class);
                intent.putExtra("booked", snap);
                startActivity(intent);
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neworder();
            }
        });

        buttonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neworder();
            }
        });
        return root;
    }

    private void refreshList() {
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
                            arrayCount = jsonArray.length();
                            //Toast.makeText(getContext(), String.valueOf(arrayCount), Toast.LENGTH_SHORT).show();
                            if(arrayCount>0){
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = jsonArray.getJSONObject(i);
                                    String id            = c.getString("id");
                                    String invoice       = c.getString("booked");
                                    String status        = c.getString("status");
                                    String payment       = c.getString("payment");
                                    String grand_total   = c.getString("grand_total");
                                    String snap          = c.getString("snap_token");
                                    patient_id           = c.getString("datapatient_id");
                                    String status_order;
                                    if(status.equals("pending")){
                                        status_order = "Waiting For Payment";
                                    }else{
                                        if(status.equals("success")){
                                            status_order = "Order Paid";
                                        }else if(status.equals("failed")){
                                            if(payment.equals("canceled")){
                                                status_order = "Order Canceled";
                                            }else{
                                                status_order = "Payment Failed";
                                            }
                                        }else{
                                            status_order = "Transaction Expired";
                                        }
                                    }
                                    getDataPatient(id,invoice,status_order,grand_total,snap,patient_id);
                                }
                            }
                        } else {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
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
                    Cue.init().with(getContext()).setMessage("Tidak ada data pasien").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.PRIMARY).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("debug", "onFailure: ERROR > refreshList" + t.toString());
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                builder.setMessage("Failed loading data. Do you want to retry?");
                builder.setTitle("Error Load Data Order");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        refreshList();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        System.exit(0);
                    }
                });
                android.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void getDataPatient(String id, String invoice, String status, String grand_total, String snap,String id_patient) {
        Call<ResponseBody> listCall = ApiGetMethod.getAllDataPatient(bearer);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            JSONObject jsonObject   = jsonRESULTS.getJSONObject("data");
                            JSONArray jsonArray     = jsonObject.getJSONArray("data");
//                            JSONArray jsonArray = jsonRESULTS.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.getJSONObject(i);
                                nama_pasien   = c.getString("name");
                                handphone     = c.getString("handphone");
                                sex           = c.getString("sex");
                                dob           = c.getString("dob");
                                nik           = c.getString("nik");
                                city          = c.getString("city");
                                postal_code   = c.getString("postal_code");
                                String id_P   = c.getString("id");
                                tampiltanggal = globalVariable.dateformat(dob);
                                if(id_patient.equals(id_P)){
                                    adapter_invoice _states = new adapter_invoice(id,nama_pasien,invoice,handphone, city,status,grand_total,snap);
                                    orderList.add(_states);
                                }
                            }
                            progress.setVisibility(View.GONE);
                            bindData();
                        } else {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
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
                    Cue.init().with(getContext()).setMessage("Tidak ada data pasien").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.PRIMARY).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("debug", "onFailure: ERROR > getDataPatient" + t.toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Failed loading data. Do you want to retry?");
                builder.setTitle("Error Load Data Order");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        refreshList();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        System.exit(0);
                    }
                });
                android.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void bindData() {
        String getReturn = ((GlobalVariable) getContext().getApplicationContext()).quarterWidth(getActivity());
        int quarterWidth = Integer.parseInt(getReturn);
        ViewGroup.LayoutParams successHeight = CardSuccess.getLayoutParams();
        ViewGroup.LayoutParams pendingHeight = CarsPending.getLayoutParams();
        ViewGroup.LayoutParams failedHeight  = CardFailed .getLayoutParams();
        ViewGroup.LayoutParams expiredHeight = CardExpired.getLayoutParams();
        successHeight.width = quarterWidth;
        pendingHeight.width = quarterWidth;
        failedHeight .width = quarterWidth;
        expiredHeight.width = quarterWidth;
        CardSuccess.setLayoutParams(successHeight);
        CarsPending.setLayoutParams(pendingHeight);
        CardFailed.setLayoutParams(failedHeight);
        CardExpired.setLayoutParams(expiredHeight);
        //Toast.makeText(getContext(),String.valueOf(quarterWidth),Toast.LENGTH_SHORT).show();
        if (arrayCount != 0 && arrayCount > 0){
            progress.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            linearInfo.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            dataOrder = new Adapter_Data_Invoice(getContext(), R.layout.list_order, orderList);


            listView.setAdapter(dataOrder);
        }else if (arrayCount == 0){
            linearInfo.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    private void neworder() {
        Intent i = new Intent(getContext(), NewOrderActivity.class);
        i.putExtra("index_position", "");
        startActivity(i);
    }

    private void getDashboard() {
        Call<ResponseBody> listCall = ApiGetMethod.getPosts(bearer);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        if (jsonRESULTS.getString("success").equals("true")){
                            JSONObject subObject = jsonRESULTS.getJSONObject("data");
                            JSONObject subObject2 = subObject.getJSONObject("count");
                            string_pending = subObject2.getString("pending");
                            string_success = subObject2.getString("success");
                            string_expired = subObject2.getString("expired");
                            string_failed  = subObject2.getString("failed");
                            TV_success.setText(string_success);
                            TV_pending.setText(string_pending);
                            TV_expired.setText(string_expired);
                            TV_failed .setText(string_failed);
                            int int_pending = Integer.parseInt(String.valueOf(string_pending));
                            int int_success = Integer.parseInt(String.valueOf(string_success));
                            int int_expired = Integer.parseInt(String.valueOf(string_expired));
                            int int_failed  = Integer.parseInt(String.valueOf(string_failed));
                            if(int_pending>0||int_failed>0||int_expired>0||int_success>0){
                                listView.setVisibility(View.VISIBLE);
                                linearLayout.setVisibility(View.GONE);
                            } else {
                                listView.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.VISIBLE);
                                progress.setVisibility(View.GONE);
                            }
                            //Toast.makeText(getContext(), "Success : "+content, Toast.LENGTH_SHORT).show();
                        } else {

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                            builder.setMessage("Tidak dapat login\nPeriksa email dan password anda.");
                            builder.setTitle("Login Gagal");
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
                    Cue.init().with(getContext()).setMessage("Belum Ada transaksi").setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setTextSize(20).setType(Type.PRIMARY).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("debug", "onFailure: ERROR > " + t.toString());
                Cue.init().with(getContext()).setMessage("Tidak dapat terhubung ke server."+t.toString()).setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM).setType(Type.PRIMARY).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
