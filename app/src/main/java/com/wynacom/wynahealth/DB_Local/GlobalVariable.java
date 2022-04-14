package com.wynacom.wynahealth.DB_Local;

import android.app.Application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GlobalVariable extends Application {
    private String token;
    Date d;
    private List<String> globalArrayList;

    public List<String> getGlobalArrayList() {
        return globalArrayList;
    }

    public void setGlobalArrayList(List<String> globalArrayList) {
        this.globalArrayList = globalArrayList;
    }

    public String getToken(){
        return token;
    }

    public void setToken(String avail_token){
        this.token = avail_token;
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

    public void clearToken() {
        this.token = "";
    }
}
