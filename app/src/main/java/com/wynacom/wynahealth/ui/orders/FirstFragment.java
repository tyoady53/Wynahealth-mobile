package com.wynacom.wynahealth.ui.orders;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.wynacom.wynahealth.DB_Local.GlobalVariable;
import com.wynacom.wynahealth.R;
import com.wynacom.wynahealth.adapter.patient.Adapter_Data_Patient;
import com.wynacom.wynahealth.adapter.patient.adapter_patient;
import com.wynacom.wynahealth.apihelper.BaseApiService;
import com.wynacom.wynahealth.apihelper.UtilsApi;
import com.wynacom.wynahealth.databinding.FragmentFirstBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private ArrayList<adapter_patient> List;
    String token,bearer,strFixedPosition,patient_id,snap,strTotal,strDoctor,strCompany,id_user,gender,
        booked,ol_patient_id,datapatient_id,ol_company_id,dokter,perusahaan,service_date,ol_invoice_id;
    Spinner Sp_order_city,Sp_order_time;
    EditText ET_order_date,ET_order_doctor,ET_order_address;
    GlobalVariable globalVariable;

    Button next1,next,prev;
    LinearLayout step1,step2;

    final Calendar myCalendar= Calendar.getInstance();

    private Adapter_Data_Patient dataAdapter = null;

    private BaseApiService mApiService,ApiGetMethod;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);

        globalVariable  = (GlobalVariable) getContext().getApplicationContext();
        token           = globalVariable.getToken();
        bearer          = "Bearer "+token;
        mApiService     = UtilsApi.getAPI();
        ApiGetMethod    = UtilsApi.getMethod();

        List            = new ArrayList<adapter_patient>();

        step1           = binding.linearLayout5;
        step2           = binding.orderLayout2;
        prev            = binding.prev;
        next            = binding.next;
        next1           = binding.next1;

        Sp_order_city   = binding.orderCitySpinner;//(R.id.order_city_spinner);
        Sp_order_time   = binding.orderTimeSpinner;//findViewById(R.id.order_time_spinner);

        ET_order_date   = binding.orderDateEt;//findViewById(R.id.order_date_et);
        ET_order_doctor = binding.orderDoctorEt;//findViewById(R.id.order_doctor_et);
        ET_order_address= binding.orderAddressEt;//findViewById(R.id.order_address_et);

        List<String> list = new ArrayList<>();
        list.add("Jakarta");
        list.add("Bandung");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item,list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        Sp_order_city.setAdapter(dataAdapter);

        List<String> list2 = new ArrayList<>();
        list2.add("08.00 - 10.00");
        list2.add("10.00 - 12.00");
        list2.add("12.00 - 14.00");
        list2.add("14.00 - 16.00");
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(getContext(), R.layout.spinner_item,list2);
        dataAdapter2.setDropDownViewResource(R.layout.spinner_item);
        Sp_order_time.setAdapter(dataAdapter2);

        prev.setVisibility(View.GONE);

        ET_order_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                //set time zone
                calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH,month);
                            myCalendar.set(Calendar.DAY_OF_MONTH,day);
                            updateLabel();
                        }
                    },myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

                //Set Today date to calendar
                final Calendar calendar2 = Calendar.getInstance();
                //Set Minimum date of calendar
                int tahun = Calendar.getInstance().get(Calendar.YEAR);
                int bulan = Calendar.getInstance().get(Calendar.MONTH);
                int tanggal = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                calendar2.set(tahun, bulan, tanggal);
                datePickerDialog.getDatePicker().setMinDate(calendar2.getTimeInMillis());
                datePickerDialog.setTitle("Select Date");
                datePickerDialog.show();
            }
        });

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
        next1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step1.setVisibility(View.GONE);
                step2.setVisibility(View.VISIBLE);
                next1.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                prev.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateLabel() {
        String myFormat="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.ENGLISH);
        ET_order_date.setText(dateFormat.format(myCalendar.getTime()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
