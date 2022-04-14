package com.wynacom.wynahealth.ui.dashboard;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Type;
import com.github.clans.fab.FloatingActionButton;
import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.DB_Local.Local_Data;
import com.wynacom.wynahealth.R;
import com.wynacom.wynahealth.adapter.order.Adapter_Data_Order;
import com.wynacom.wynahealth.adapter.order.adapter_order;
import com.wynacom.wynahealth.adapter.patient.Adapter_Data_Patient;
import com.wynacom.wynahealth.adapter.patient.adapter_patient;
import com.wynacom.wynahealth.adapter.product.Adapter_Data_Product;
import com.wynacom.wynahealth.adapter.product.adapter_product;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;
import com.wynacom.wynahealth.databinding.FragmentDashboardBinding;
import com.wynacom.wynahealth.transaction.OrderActivity;

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
    TextView TV_success,TV_pending,TV_failed,TV_expired;

    private Adapter_Data_Product dataAdapter = null;
    private ArrayList<adapter_product> List;

    private Adapter_Data_Order dataOrder = null;
    private ArrayList<adapter_order> orderList;

    private Adapter_Data_Patient dataPatient = null;
    private ArrayList<adapter_patient> patientList;

    ListView listView;
    LinearLayout linearLayout;
    Button buttonOrder;
    FloatingActionButton fab_add;

    private BaseApiService mApiService,ApiGetMethod;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mApiService = UtilsApi.getAPI();
        ApiGetMethod= UtilsApi.getMethod();
        local_data  = new Local_Data(getContext());
        List        = new ArrayList<adapter_product>();
        orderList   = new ArrayList<adapter_order>();
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

        fab_add         = binding.fabOrder;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter_product state = List.get(position);
                String ids=state.getID();
                Toast.makeText(getContext(), ids, Toast.LENGTH_SHORT).show();
            }
        });
        getDashboard();
        refreshList();

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
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject c = jsonArray.getJSONObject(i);
                                String id            = c.getString("id");
                                String nama          = c.getString("name");
                                String invoice       = c.getString("invoice");
                                String phone         = c.getString("phone");
                                String address       = c.getString("address");
                                String status        = c.getString("status");
                                String grand_total   = c.getString("grand_total");

                                adapter_order _states = new adapter_order(id,nama,invoice,phone,address,status,grand_total);
                                orderList.add(_states);
                                bindData();
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
                Log.e("debug", "onFailure: ERROR > " + t.toString());
            }
        });
    }

    private void bindData() {
        if (!(orderList ==null)){
            dataOrder = new Adapter_Data_Order(getContext(), R.layout.list_order, orderList);
            listView.setAdapter(dataOrder);
        }else {
            listView.setVisibility(View.GONE);
        }
    }

    private void neworder() {
        Intent i = new Intent(getContext(), OrderActivity.class);
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
                            }else {
                                listView.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.VISIBLE);
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
