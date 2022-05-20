package com.wynacom.wynahealth.apihelper;


/**
 * Created by X260 on 09/09/2018.
 */

public class UtilsApi {
    //public static String BASE_URL = "http://172.16.8.104:8000/api/";     //Riviera
    //public static String BASE_URL = "http://172.16.9.149:8000/api/";      //Kantor LC7
    //public static String BASE_URL = "http://192.168.204.121:8000/api/";      //Hotspot
    public static String BASE_URL = "http://192.168.31.143:8000/api/";      //Hotspot

    public static BaseApiService getAPI(){
        return RetrofitClient.getClient(BASE_URL).create(BaseApiService.class);
    }

    public static BaseApiService getMethod(){
        return RetrofitClient.getRetrofit(BASE_URL).create(BaseApiService.class);
    }
}
