package com.wynacom.wynahealth.apihelper;

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
// API routes
    @FormUrlEncoded
    @POST("patient/login?")
    Call<ResponseBody> login(
            @Field("email")     String email,
            @Field("password")  String password);

    @FormUrlEncoded
    @POST("patient/register?")
    Call<ResponseBody> register(
        @Field("patient_name")  String patient_name,
        @Field("email")         String email,
        @Field("password")      String password,
        @Field("handphone")     String handphone,
        @Field("city")          String city,
        @Field("postal_code")   String postal_code,
        @Field("sex")           String sex,
        @Field("age")           String age,
        @Field("nik")           String nik,
        @Field("password_confirmation") String password_confirmation);

    @FormUrlEncoded
    @POST("patient/logout?")
    Call<ResponseBody> logout(
        @Field("token") String token);

    @GET("patient/dashboard")
    Call<ResponseBody> getPosts(@Header("Authorization") String token);

    @GET("patient/invoices")
    Call<ResponseBody> getInvoices(@Header("Authorization") String token);

    @GET("patient/datapatient")
    Call<ResponseBody> getdatapatient(
        @Header("Authorization")String token,
        @Query("page")          String page);

    @FormUrlEncoded
    @POST("patient/datapatient?")
    Call<ResponseBody> datapatient(
        @Field("token")         String token,
        @Field("name")          String patient_name,
        @Field("email")         String email,
        @Field("handphone")     String handphone,
        @Field("city")          String city,
        @Field("postal_code")   String postal_code,
        @Field("sex")           String sex,
        @Field("dob")           String age,
        @Field("nik")           String nik);

//WEB routes
    @GET("web/categories")
    Call<ResponseBody> getcategories(@Header("Authorization") String token);

    @GET("web/products")
    Call<ResponseBody> getProducts(@Header("Authorization") String token);

    @GET("web/carts")
    Call<ResponseBody> getcarts(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("web/carts?")
    Call<ResponseBody> postCarts(
        @Field("token")         String token,
        @Field("ol_product_id") String ol_product_id,
        @Field("datapatient_id")String datapatient_id,
        @Field("qty")           String qty,
        @Field("price")         String price,
        @Field("ol_patient_id") String ol_patient_id);

    @FormUrlEncoded
    @POST("web/checkout?")
    Call<ResponseBody> checkout(
        @Field("token")         String token,
        @Field("datapatient_id")String datapatient_id,
        @Field("ol_company_id") String ol_company_id,
        @Field("name")          String name,
        @Field("phone")         String phone,
        @Field("address")       String address,
        @Field("grand_total")   String grand_total,
        @Field("dokter")        String dokter,
        @Field("perusahaan")    String perusahaan);
}
