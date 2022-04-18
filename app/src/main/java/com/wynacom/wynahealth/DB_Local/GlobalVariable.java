package com.wynacom.wynahealth.DB_Local;

import android.app.Activity;
import android.app.Application;
import android.util.DisplayMetrics;
import android.widget.ProgressBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GlobalVariable extends Application {
    private String token;
    Date d;
    private List<String> globalArrayList;
    double cardWidth = 0;

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
}
