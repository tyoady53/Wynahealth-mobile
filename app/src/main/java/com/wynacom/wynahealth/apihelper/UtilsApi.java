package com.wynacom.wynahealth.apihelper;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by X260 on 09/09/2018.
 */

public class UtilsApi {
//    public static String BASE_URL = "http://192.168.18.26:8000/api/patient/";     //Riviera
    public static String BASE_URL = "http://172.16.9.149:8000/api/patient/";      //Kantor LC7

    public static BaseApiService getAPI(){
        return RetrofitClient.getClient(BASE_URL).create(BaseApiService.class);
    }

    public static BaseApiService getMethod(){
        return RetrofitClient.getRetrofit(BASE_URL).create(BaseApiService.class);
    }
}
