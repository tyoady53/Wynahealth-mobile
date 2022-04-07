package com.wynacom.wynahealth.DB_Local;

import android.app.Application;

public class GlobalVariable extends Application {
    private String token;

    public String getToken(){
        return token;
    }

    public void setToken(String avail_token){
        this.token = "Bearer "+avail_token;
    }
}
