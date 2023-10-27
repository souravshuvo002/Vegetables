package com.sourav.vegetables.Api;

import com.sourav.vegetables.Model.Result;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    //get all banners
    @GET("getBanner")
    Call<Result> getBanners();

    // register call
    @FormUrlEncoded
    @POST("registerUser")
    Call<Result> registerUser(
            @Field("username") String username,
            @Field("password") String password,
            @Field("phone") String phone,
            @Field("email") String email,
            @Field("address") String address);


    // login call
    @FormUrlEncoded
    @POST("loginUser")
    Call<Result> loginUser(
            @Field("phone") String phone,
            @Field("password") String password);

    // reset password call
    @FormUrlEncoded
    @POST("resetPassword")
    Call<Result> resetPassword(
            @Field("phone") String phone,
            @Field("password") String password);

    // get user call
    @FormUrlEncoded
    @POST("getUser")
    Call<Result> getUser(
            @Field("phone") String phone);

    // update customer call
    @FormUrlEncoded
    @POST("updateUser/{id}")
    Call<Result> updateCustomerInfo(@Path("id") String id,
                                    @Field("username") String username,
                                    @Field("password") String password,
                                    @Field("phone") String phone,
                                    @Field("email") String email,
                                    @Field("address") String address);

    @Multipart
    @POST("updateUserWithImage")
    Call<Result> updateUserWithImage(
            @Part("id") RequestBody id,
            @Part("username") RequestBody username,
            @Part("password") RequestBody password,
            @Part("phone") RequestBody phone,
            @Part("email") RequestBody email,
            @Part("address") RequestBody address,
            @Part MultipartBody.Part file);

    //updating token for User
    @FormUrlEncoded
    @POST("updateUserAddress")
    Call<Result> updateUserAddress(
            @Field("id") String id,
            @Field("address") String address);

    //updating address for User
    @FormUrlEncoded
    @POST("updateUserToken")
    Call<Result> updateUserToken(
            @Field("id") String id,
            @Field("token") String token);

    //get all menu
    @GET("getMenu")
    Call<Result> getMenu();

    //get all foods
    @GET("getAllFoods")
    Call<Result> getAllFoods();

    //get all foods based on menu id
    @GET("getFoodByMenuID/{id_menu}")
    Call<Result> getFoodByMenuID(@Path("id_menu") String id_menu);

    // place order
    @FormUrlEncoded
    @POST("placeOrderItems")
    Call<Result> placeOrderItems(
            @Field("id_order") String id_order,
            @Field("id_food") String id_food,
            @Field("food_name") String food_name,
            @Field("food_price") String food_price,
            @Field("food_quantity") String food_quantity,
            @Field("food_total_price") String food_total_price,
            @Field("food_image_url") String food_image_url,
            @Field("food_min_unit_amount") String food_min_unit_amount,
            @Field("food_unit") String food_unit,
            @Field("id_menu") String id_menu,
            @Field("menu_name") String menu_name);

    // place order
    @FormUrlEncoded
    @POST("placeOrder")
    Call<Result> placeOrder(
            @Field("id_order") String id_order,
            @Field("id_user") String id_user,
            @Field("username") String username,
            @Field("phone") String phone,
            @Field("email") String email,
            @Field("address") String address,
            @Field("area") String area,
            @Field("total_price") String total_price,
            @Field("order_date") String order_date,
            @Field("comment") String comment,
            @Field("payment_method") String payment_method,
            @Field("payment_state") String payment_state,
            @Field("delivery_date") String delivery_date,
            @Field("delivery_time") String delivery_time);

    //Get all order history for
    @FormUrlEncoded
    @POST("getAllOrder")
    Call<Result> getAllOrder(
            @Field("id_user") String id_user);

    //get order details
    @FormUrlEncoded
    @POST("getOrderDetails")
    Call<Result> getOrderDetails(@Field("id_order") String id_order);

    //Get order Details for user
    @FormUrlEncoded
    @POST("getOrderItems")
    Call<Result> getOrderItems(
            @Field("id_order") String id_order);

    //Get Server Token
    @FormUrlEncoded
    @POST("getToken")
    Call<Result> getToken(
            @Field("username") String username,
            @Field("isServerToken") String isServerToken);

    //get all Area
    @GET("getArea")
    Call<Result> getArea();

    //Update order Status --> User PART for cancellation
    @FormUrlEncoded
    @POST("updateOrderCancel")
    Call<Result> updateOrderCancel(
            @Field("id_order") String id_order,
            @Field("id_user") String id_user,
            @Field("order_status") String order_status,
            @Field("reason") String reason);

    // Check Coupon Code
    @FormUrlEncoded
    @POST("CheckCouponCode")
    Call<Result> checkCouponCode(
            @Field("code") String code,
            @Field("id_user") String id_user);

    // push to tbl_coupon_history
    @FormUrlEncoded
    @POST("addCouponHistory")
    Call<Result> addCouponHistory(
            @Field("coupon_id") String coupon_id,
            @Field("order_id") String order_id,
            @Field("customer_id") String customer_id,
            @Field("amount") String amount,
            @Field("date_added") String date_added);

    // add review
    @FormUrlEncoded
    @POST("addReview")
    Call<Result> addReview(
            @Field("id_user") String id_user,
            @Field("id_order") String id_order,
            @Field("author") String author,
            @Field("text") String text,
            @Field("rating") String rating,
            @Field("status") String status,
            @Field("date_added") String date_added);

    // check review
    @FormUrlEncoded
    @POST("checkReview")
    Call<Result> checkReview(
            @Field("id_order") String id_order,
            @Field("id_user") String id_user);

    //get all time Slots
    @GET("getSlots")
    Call<Result> getSlots();

    //get all reviews
    @GET("getAllReviews")
    Call<Result> getAllReviews();

    //get all user reviews
    @GET("getUserReviews/{id_user}")
    Call<Result> getUserReviews(@Path("id_user") String id_user);

    //get Text scroll
    @GET("getTextScroll")
    Call<Result> getTextScroll();

}
