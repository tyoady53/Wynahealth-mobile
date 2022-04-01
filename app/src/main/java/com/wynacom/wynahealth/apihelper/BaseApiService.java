package com.wynacom.wynahealth.apihelper;

import com.wynacom.wynahealth.json_dashboard.Count;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by X260 on 09/09/2018.
 */

public interface BaseApiService {
    @FormUrlEncoded
    @POST("login?")
    Call<ResponseBody> login(
            @Field("email") String email,
            @Field("password") String password);

    @FormUrlEncoded
    @POST("register?")
    Call<ResponseBody> register(
        @Field("patient_name") String patient_name,
        @Field("email") String email,
        @Field("password") String password,
        @Field("handphone") String handphone,
        @Field("city") String city,
        @Field("postal_code") String postal_code,
        @Field("sex") String sex,
        @Field("age") String age,
        @Field("nik") String nik,
        @Field("password_confirmation") String password_confirmation);

    @FormUrlEncoded
    @GET("dashboard?")
    Call<Count> dashboard(
        @Header("Authorization") String token);
}
