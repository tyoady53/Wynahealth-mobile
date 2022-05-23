package com.wynacom.wynahealth.DB_Local;

import android.app.Activity;
import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GlobalVariable extends Application {
    private String token,patient_id,UserID,orderId,discountTotal,priceTotal,order_size,list_view;
    String gender=null,booked=null,ol_patient_id=null,datapatient_id=null,ol_company_id=null,dokter=null,perusahaan=null,service_date=null,ol_invoice_id=null;
    Date d;
    private List<String> globalArrayList;
    ArrayList<String> selected = new ArrayList<>();
    double cardWidth = 0;
    protected Cursor cursor;

    Local_Data localData;

    public List<String> getGlobalArrayList() {
        return globalArrayList;
    }
    public void setGlobalArrayList(List<String> globalArrayList) {
        this.globalArrayList = globalArrayList;
    }

    public void setSelected(ArrayList<String> selected) {
        this.selected = selected;
    }

    public String getList_view() {
        return list_view;
    }
    public void setList_view(String list_view) {
        this.list_view = list_view;
    }

    public String getOrder_size() {
        return order_size;
    }
    public void setOrder_size(String order_size) {
        this.order_size = order_size;
    }

    public String getDiscountTotal() {
        return discountTotal;
    }
    public void setDiscountTotal(String discountTotal) {
        this.discountTotal = discountTotal;
    }

    public String getPriceTotal() {
        return priceTotal;
    }
    public void setPriceTotal(String priceTotal) {
        this.priceTotal = priceTotal;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getOrderId() {
        return orderId;
    }

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

    public String getUserID(){
        localData       = new Local_Data(getApplicationContext());
        SQLiteDatabase dbU = localData.getReadableDatabase();
        cursor = dbU.rawQuery("SELECT * FROM TB_User", null);
        cursor.moveToFirst();
        if (cursor.getCount()>0) {
            cursor.moveToPosition(0);
            UserID = cursor.getString(0);
        }
        return UserID;
    }

    public String getToken(){
        return token;
    }
    public void setToken(String avail_token){
        this.token = avail_token;
    }
    public void clearToken() {
        this.token = "";
    }

    public String getPatient_id(){
        return patient_id;
    }
    public void setPatient_id(String patientId){
        this.patient_id = patientId;
    }

    public String timestampToDate(String tanggal){
        final String NEW_FORMAT = "dd-MMM-yyyy";
        final String OLD_FORMAT = "yyyy-MM-ddHH:MM:ss";
        String newDateString;
        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        try {
            d = sdf.parse(tanggal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(NEW_FORMAT);
        newDateString = sdf.format(d);

        return newDateString;
    }

    public String getTimeTimeStamp(String tanggal){
        String parseTgl = tanggal.replace("T",",");
        final String NEW_FORMAT = "dd-MMM-yyyy, HH:MM";
        final String OLD_FORMAT = "yyyy-MM-dd,HH:MM:ss";
        String newDateString;
        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        try {
            d = sdf.parse(parseTgl);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(NEW_FORMAT);
        newDateString = sdf.format(d);

        return newDateString;
    }

    public String dateformat(String tanggal){
        final String NEW_FORMAT = "dd-MMM-yyyy";
        final String OLD_FORMAT = "yyyy-MM-dd";
        String newDateString;
        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        try {
            d = sdf.parse(tanggal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(NEW_FORMAT);
        newDateString = sdf.format(d);

        return newDateString;
    }

    public String reversedateformat(String tanggal){
        final String NEW_FORMAT = "dd-MMM-yyyy";
        final String OLD_FORMAT = "yyyy-MM-dd";
        String newDateString;
        SimpleDateFormat sdf = new SimpleDateFormat(NEW_FORMAT);
        try {
            d = sdf.parse(tanggal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(OLD_FORMAT);
        newDateString = sdf.format(d);

        return newDateString;
    }

    public String setGenerateGender(String gender){
        String string_jk;
        if (gender.equals("M")){
            string_jk   = "Laki-laki";
        }else{
            string_jk   = "Perempuan";
        }

        return string_jk;
    }

    public String reverseGender(String gender){
        String string_jk;
        if (gender.equals("Perempuan")){
            string_jk   = "F";
        }else{
            string_jk   = "M";
        }

        return string_jk;
    }

    public void clearList(){
        this.globalArrayList.clear();
        Toast.makeText(getApplicationContext(),"Clear List",Toast.LENGTH_SHORT).show();
    }

    public String quarterWidth(Activity context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        double width = displayMetrics.widthPixels;
        double WidthLess       = width-50;
        double x = 0.25*(WidthLess);
        int Q_width = (int)x;

        return String.valueOf(Q_width);
    }

    public String getWidth(Activity context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        double width = displayMetrics.widthPixels;
        cardWidth       = 0.8*width;
        double x = 0.625*(Double.parseDouble(String.valueOf(cardWidth)));
        int height = (int)x;
        String retHeight = String.valueOf(height);

        return retHeight;
    }

    public String toCurrency(String number){
        Locale localeID = new Locale("in", "ID");
        NumberFormat nf = NumberFormat.getCurrencyInstance(localeID);
        String c = nf.format(Double.parseDouble(number));

        return c;
    }
}
