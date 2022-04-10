package com.wynacom.wynahealth.DB_Local;

import android.app.Application;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GlobalVariable extends Application {
    private String token;
    Date d;
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
