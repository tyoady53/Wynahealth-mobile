package com.wynacom.wynahealth.apihelper;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by X260 on 09/09/2018.
 */

public interface BaseApiService {
// API routes
    @FormUrlEncoded
    @POST("patient/login?")
    Call<ResponseBody> login(
            @Field("email") String email,
            @Field("password") String password);

    @FormUrlEncoded
    @POST("patient/register?")
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

    @GET("patient/dashboard")
    Call<ResponseBody> getPosts(@Header("Authorization") String token);

    @GET("patient/invoices")
    Call<ResponseBody> getInvoices(@Header("Authorization") String token);

    @GET("patient/datapatient")
    Call<ResponseBody> getdatapatient(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("patient/datapatient?")
    Call<ResponseBody> datapatient(
        @Field("token") String token,
        @Field("name") String patient_name,
        @Field("email") String email,
        @Field("handphone") String handphone,
        @Field("city") String city,
        @Field("postal_code") String postal_code,
        @Field("sex") String sex,
        @Field("dob") String age,
        @Field("nik") String nik);

//WEB routes
    @GET("web/categories")
    Call<ResponseBody> getcategories(@Header("Authorization") String token);

    @GET("web/products")
    Call<ResponseBody> getproducts(@Header("Authorization") String token);

    @GET("web/carts")
    Call<ResponseBody> getcarts(@Header("Authorization") String token);
}
