package com.wynacom.wynahealth.apihelper;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by X260 on 09/09/2018.
 */

public interface BaseApiService {
// API routes
//    @FormUrlEncoded
//    @POST("patient/login?")
//    Call<ResponseBody> login(
//            @Field("email")     String email,
//            @Field("password")  String password);

    @POST("patient/login/")
    Call<ResponseBody> login(
        @Body RequestBody params);

    //@FormUrlEncoded@Body RequestBody params);
    @POST("patient/register?")
    Call<ResponseBody> register(
        @Body RequestBody params);
//        @Field("patient_name")  String patient_name,
//        @Field("email")         String email,
//        @Field("password")      String password,
//        @Field("handphone")     String handphone,
//        @Field("city")          String city,
//        @Field("postal_code")   String postal_code,
//        @Field("sex")           String sex,
//        @Field("age")           String age,
//        @Field("nik")           String nik,
//        @Field("password_confirmation") String password_confirmation);

    @FormUrlEncoded
    @POST("patient/logout?")
    Call<ResponseBody> logout(
        @Field("token") String token);

    @GET("patient/dashboard")
    Call<ResponseBody> getPosts(
        @Header("Authorization") String token);

    @GET("patient/invoices?")
    Call<ResponseBody> getInvoices(
        @Header("Authorization") String token,
        @Query("q")              String q,
        @Query("page")           String page);

    @POST("patient/invoices/generate")
    Call<ResponseBody> generateNew(
        @Header("Authorization") String token,
        @Body                    RequestBody params);

    @GET("patient/invoices/{booked}?")
    Call<ResponseBody> getInvoicesBySnap(
        @Header("Authorization")   String token,
        @Path("booked")            String snap);

    @GET("patient/datapatient")
    Call<ResponseBody> getdatapatient(
        @Header("Authorization")String token,
        @Query("page")          String page);

    @GET("patient/datapatient")
    Call<ResponseBody> getAllDataPatient(
        @Header("Authorization")String token);

    @POST("patient/datapatient?")
    Call<ResponseBody> datapatient(
        @Header("Authorization")String token,
        @Body                   RequestBody params);

    @PATCH("patient/datapatient/{id}/")
    Call<ResponseBody> PatchPatient(
        @Header("Authorization")String token,
        @Path("id")             String id,
        @Body                   RequestBody params);

//WEB routes
    @GET("web/categories")
    Call<ResponseBody> getcategories(
        @Header("Authorization") String token);

    @GET("web/products")
    Call<ResponseBody> getProducts(
        @Query("gender")          String gender);

    @GET("web/carts")
    Call<ResponseBody> getcarts(
        @Header("Authorization") String token);

    //@FormUrlEncoded
    @POST("web/carts?")
    Call<ResponseBody> postCarts(
        @Header("Authorization")    String token,
        @Body                       RequestBody params);

    @POST("patient/invoices/cancel")
    Call<ResponseBody> Cancel_order(
        @Header("Authorization")    String token,
        @Body                       RequestBody params);

    @GET("web/carts/detail/{booked}?")
    Call<ResponseBody> getCartsDetail(
        @Header ("Authorization") String token,
        @Path   ("booked")        String booked,
        @Query  ("page")          String page);

    @POST("web/carts/remove")
    Call<ResponseBody> remove_CartsItem(
        @Header("Authorization")    String token,
        @Body                       RequestBody params);

    @POST("web/carts/remove_item")
    Call<ResponseBody> remove_itemCarts(
        @Header("Authorization")    String token,
        @Body                       RequestBody params);

    //@FormUrlEncoded
    @POST("web/checkout?")
    Call<ResponseBody> checkout(
        @Header("Authorization")    String token,
        @Body                       RequestBody params);
}
