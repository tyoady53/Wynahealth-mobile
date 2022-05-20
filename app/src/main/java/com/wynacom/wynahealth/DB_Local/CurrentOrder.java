package com.wynacom.wynahealth.DB_Local;

import androidx.appcompat.app.AppCompatActivity;

public class CurrentOrder extends AppCompatActivity {
    String gender=null,booked=null,ol_patient_id=null,datapatient_id=null,ol_company_id=null,dokter=null,perusahaan=null,service_date=null,ol_invoice_id=null;

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBooked() {
        return booked;
    }
    public void setBooked(String booked) {
        this.booked = booked;
    }

    public String getOl_patient_id() {
        return ol_patient_id;
    }
    public void setOl_patient_id(String ol_patient_id) {
        this.ol_patient_id = ol_patient_id;
    }

    public String getDatapatient_id() {
        return datapatient_id;
    }
    public void setDatapatient_id(String datapatient_id) {
        this.datapatient_id = datapatient_id;
    }

    public String getOl_company_id() {
        return ol_company_id;
    }
    public void setOl_company_id(String ol_company_id) {
        this.ol_company_id = ol_company_id;
    }

    public String getDokter() {
        return dokter;
    }
    public void setDokter(String dokter) {
        this.dokter = dokter;
    }

    public String getPerusahaan() {
        return perusahaan;
    }
    public void setPerusahaan(String perusahaan) {
        this.perusahaan = perusahaan;
    }

    public String getService_date() {
        return service_date;
    }
    public void setService_date(String service_date) {
        this.service_date = service_date;
    }

    public String getOl_invoice_id() {
        return ol_invoice_id;
    }
    public void setOl_invoice_id(String ol_invoice_id) {
        this.ol_invoice_id = ol_invoice_id;
    }
}
