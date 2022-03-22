package com.angaihouse.utils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {

    String URLPrefix = "webservices/customer/";

    @FormUrlEncoded
    @POST(URLPrefix + "cancel_booking.php")
    Call<ResponseBody> cancelBooking(
            @Field("lang_id") String lang_id,
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("booking") String booking
    );

    @FormUrlEncoded
    @POST(URLPrefix + "book_table.php")
    Call<ResponseBody> bookTable(
            @Field("store_id") String store_id,
            @Field("lang_id") String lang_id,
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("date") String date,
            @Field("time") String time,
            @Field("guests") String guests,
            @Field("notes") String notes
    );


    @FormUrlEncoded
    @POST(URLPrefix + "get_slots_by_date.php")
    Call<ResponseBody> getTimeSlot(
            @Field("lang_id") String lang_id,
            @Field("store_id") String store_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "store_detail.php")
    Call<ResponseBody> getStoreDetail(
            @Field("lang_id") String lang_id,
            @Field("store_id") String store_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "store_list.php")
    Call<ResponseBody> getStoreList(
            @Field("lang_id") String lang_id,
            @Field("time") String time,
            @Field("day") String day,
            @Field("company_id") String company_id,
            @Field("search") String search
    );

    @FormUrlEncoded
    @POST(URLPrefix + "store_list.php")
    Call<ResponseBody> getStoreList(
            @Field("lang_id") String lang_id,
            @Field("time") String time,
            @Field("day") String day,
            @Field("company_id") String company_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "get_table_booking_list.php")
    Call<ResponseBody> getBookedTable(
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "notifications.php")
    Call<ResponseBody> getNotification(
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("company_id") String company_id,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "login.php")
    Call<ResponseBody> login(
            @Field("email") String email,
            @Field("password") String password,
            @Field("device_token") String device_token,
            @Field("lang_id") String lang_id,
            @Field("device_type") String device_type
    );

    @FormUrlEncoded
    @POST(URLPrefix + "reset_password.php")
    Call<ResponseBody> resetPassword(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST(URLPrefix + "check_user_exists.php")
    Call<ResponseBody> checkUserExists(
            @Field("mobile_no") String mobile_no,
            @Field("email") String email,
            @Field("referral_code") String referral_code
    );

    @FormUrlEncoded
    @POST(URLPrefix + "Pushnotification_Android/register1.php")
    Call<ResponseBody> notification(
                    @Query("name") String name,
                    @Field("email") String email,
                    @Field("regId") String regId,
                    @Field("user_id") String user_id,
                    @Field("user_type") String user_type,
                    @Field("device_id") String device_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "user_register.php")
    Call<ResponseBody> register(
            @Field("fname") String fname,
            @Field("lname") String lname,
            @Field("country_code") String country_code,
            @Field("mobile_no") String mobile_no,
            @Field("email") String email,
            @Field("password") String password,
            @Field("image") String image,
            @Field("device_token") String device_token,
            @Field("referral_code") String referral_code,
            @Field("device_type") String device_type
    );

    @FormUrlEncoded
    @POST(URLPrefix + "change_user_profile.php")
    Call<ResponseBody> updateProfile(
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("fname") String fname,
            @Field("lname") String lname,
            @Field("dob") String dob,
            @Field("country_code") String country_code,
            @Field("mobile_no") String mobile_no,
            @Field("email") String email,
            @Field("password") String password,
            @Field("gender") String gender,
            @Field("image") String image
    );

    @FormUrlEncoded
    @POST(URLPrefix + "change_user_address.php")
    Call<ResponseBody> updateUserAddress(
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("address") String address,
            @Field("address1") String address1,
            @Field("province") String province,
            @Field("city") String city,
            @Field("country") String country,
            @Field("zipcode") String zipcode,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude
    );

    @FormUrlEncoded
    @POST(URLPrefix + "change_user_company.php")
    Call<ResponseBody> updateUserCompany(
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("company_legal_email") String company_legal_email,
            @Field("unique_invoicing_code") String unique_invoicing_code,
            @Field("company_name") String company_name,
            @Field("vat_id") String vat_id,
            @Field("company_id") String company_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "forgot_password.php")
    Call<ResponseBody> forgotPassword(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST(URLPrefix + "delete_address.php")
    Call<ResponseBody> deleteAddress(
            @Field("user_id") String userId,
            @Field("auth_token") String authToken,
            @Field("address_id") String addressId
    );

    @FormUrlEncoded
    @POST(URLPrefix + "get_delivery_address.php")
    Call<ResponseBody> getDeliveryAddress(
            @Field("user_id") String userId,
            @Field("auth_token") String authToken,
            @Field("cart_id") String cart_id,
            @Field("address_id") String address_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "address_list.php")
    Call<ResponseBody> addressList(
            @Field("user_id") String user_id,
            @Field("lang_id") String lang_id,
            @Field("auth_token") String auth_token
    );

    @FormUrlEncoded
    @POST(URLPrefix + "update_push_counter.php")
    Call<ResponseBody> updatePushCounter(
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("device_token") String device_token,
            @Field("device_type") String device_type
    );

    @FormUrlEncoded
    @POST(URLPrefix + "update_address.php")
    Call<ResponseBody> updateAddress(
            @Field("address_id") String address_id,
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("address_type") String address_type,
            @Field("door_no") String door_no,
            @Field("address") String address,
            @Field("city") String city,
            @Field("zip_code") String zip_code,
            @Field("country") String country,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude
    );

    @FormUrlEncoded
    @POST(URLPrefix + "restaurant_list.php")
    Call<ResponseBody> getRestaurantList(
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("time") String time,
            @Field("day") String day,
            @Field("address_id") String address_id,
            @Field("order_type") String order_type,
            @Field("company_id") String company_id,
            @Field("lang_id") String lang_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "get_app_settings.php")
    Call<ResponseBody> getAppSettings(
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("cart_id") String cart_id
    );


    @FormUrlEncoded
    @POST(URLPrefix + "share_points.php")
    Call<ResponseBody> sharePoints(
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("type") String type
    );


    @FormUrlEncoded
    @POST(URLPrefix + "card_list.php")
    Call<ResponseBody> cardList(
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("cust_id") String cust_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "contact.php")
    Call<ResponseBody> contactUs(
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("message") String message,
            @Field("name") String name,
            @Field("email") String email,
            @Field("phone_no") String phone_no
    );



    @GET(URLPrefix + "contact_questions.php")
    Call<ResponseBody> questionList(
            @Query("lang_id") String lang_id
    );


    @FormUrlEncoded
    @POST(URLPrefix + "logout.php")
    Call<ResponseBody> logout(
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("device_token") String device_token,
            @Field("lang_id") String lang_id
    );


    @FormUrlEncoded
    @POST(URLPrefix + "points_details.php")
    Call<ResponseBody> pointDetails(
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token
    );

    @FormUrlEncoded
    @POST(URLPrefix + "offer_list.php")
    Call<ResponseBody> offerList(
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("time") String time,
            @Field("day") String day,
            @Field("company_id") String company_id,
            @Field("lang_id") String lang_id
    );



    @FormUrlEncoded
    @POST(URLPrefix + "checkout.php")
    Call<ResponseBody> checkout(
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("type") String type,
            @Field("cart_id") String cart_id,
            @Field("delivery_type") String delivery_type,
            @Field("delivery_note") String delivery_note,
            @Field("order_type") String order_type,
            @Field("is_invoice") String is_invoice,
            @Field("is_cutlery") String is_cutlery,
            @Field("address_id") String address_id,
            @Field("is_mangals") String is_mangals,
            @Field("card_id") String card_id
    );


    @FormUrlEncoded
    @POST(URLPrefix + "checkout.php")
    Call<ResponseBody> checkout(
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("type") String type,
            @Field("cart_id") String cart_id,
            @Field("delivery_type") String delivery_type,
            @Field("delivery_note") String delivery_note,
            @Field("delivery_time") String delivery_time,
            @Field("order_type") String order_type,
            @Field("is_invoice") String is_invoice,
            @Field("is_cutlery") String is_cutlery,
            @Field("address_id") String address_id,
            @Field("is_mangals") String is_mangals,
            @Field("card_id") String card_id
    );

    @FormUrlEncoded
    @POST(URLPrefix + "add_address.php")
    Call<ResponseBody> addAddress(
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("address_type") String address_type,
            @Field("address") String address,
            @Field("door_no") String door_no,
            @Field("city") String city,
            @Field("zip_code") String zip_code,
            @Field("country") String country,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude
    );


    @FormUrlEncoded
    @POST(URLPrefix + "address.php")
    Call<ResponseBody> address(
            @Field("type") String type,
            @Field("address_id") String address_id,
            @Field("user_id") String user_id,
            @Field("auth_token") String auth_token,
            @Field("address_type") String address_type,
            @Field("address") String address,
            @Field("address2") String address2,
            @Field("door_no") String door_no,
            @Field("city") String city,
            @Field("zip_code") String zip_code,
            @Field("country") String country,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude
    );


    @POST(URLPrefix + "user_favourite_list.php")
    Call<ResponseBody> favouriteList(
            @Query("user_id") String user_id,
            @Query("auth_token") String auth_token
    );

    @POST(URLPrefix + "order_history.php")
    Call<ResponseBody> orderHistory(
            @Query("user_id") String user_id,
            @Query("auth_token") String auth_token,
            @Query("type") String type,
            @Query("lang_id") String lang_id
    );

    @POST(URLPrefix + "restaurant_list.php")
    Call<ResponseBody> restaurantList(
            @Query("user_id") String user_id,
            @Query("auth_token") String auth_token,
            @Query("latitude") String latitude,
            @Query("longitude") String longitude,
            @Query("time") String time,
            @Query("day") String day
    );

    @POST(URLPrefix + "add_to_cart.php")
    Call<ResponseBody> addToCart(
            @Query("user_id") String user_id,
            @Query("auth_token") String auth_token,
            @Query("restaurant_id") String restaurant_id,
            @Query("item_id") String item_id,
            @Query("quantity") String quantity,
            @Query("paid_customization") String paid_customization,
            @Query("free_customization") String free_customization,
            @Query("cooking_grade") String cooking_grades,
            @Query("taste") String taste,
            @Query("cart_id") String cart_id,
            @Query("address_id") String address_id,
            @Query("order_type") String order_type
    );

    @POST(URLPrefix + "restaurant_detail.php")
    Call<ResponseBody> restaurantDetail(
            @Query("user_id") String user_id,
            @Query("auth_token") String auth_token,
            @Query("day") String day,
            @Query("restaurant_id") String restaurant_id
    );

    @POST(URLPrefix + "order_details.php")
    Call<ResponseBody> orderDetails(
            @Query("user_id") String user_id,
            @Query("auth_token") String auth_token,
            @Query("order_number") String order_number,
            @Query("lang_id") String lang_id
    );

    @POST(URLPrefix + "check_cart_availability.php")
    Call<ResponseBody> checkCartAvailability(
            @Query("user_id") String user_id,
            @Query("auth_token") String auth_token,
            @Query("cart_id") String cart_id,
            @Query("res_id") String res_id
    );

    @POST(URLPrefix + "static_page.php")
    Call<ResponseBody> staticPage(
            @Query("type") String type,
            @Query("company_id") String company_id
    );

    @POST(URLPrefix + "restaurant_menu_items.php")
    Call<ResponseBody> restaurantMenuItems(
            @Query("user_id") String user_id,
            @Query("auth_token") String auth_token,
            @Query("restaurant_id") String restaurant_id,
            @Query("cat_id") String cat_id,
            @Query("cart_id") String cart_id
    );


    @POST(URLPrefix + "cart_customization_items.php")
    Call<ResponseBody> cartCustomizationItems(
            @Query("user_id") String user_id,
            @Query("auth_token") String auth_token,
            @Query("item_id") String item_id,
            @Query("cart_id") String cart_id
    );


    @POST(URLPrefix + "notification_status.php")
    Call<ResponseBody> notificationStatus(
            @Query("type") String type,
            @Query("status") String status,
            @Query("user_id") String user_id,
            @Query("auth_token") String auth_token
    );


    @POST(URLPrefix + "get_nearest_restaurant.php")
    Call<ResponseBody> getNearestRestaurant(
            @Query("address_id") String addressId,
            @Query("time") String time,
            @Query("day") String day
    );


    @POST(URLPrefix + "item_details.php")
    Call<ResponseBody> itemDetails(
            @Query("item_id") String item_id,
            @Query("user_id") String user_id,
            @Query("auth_token") String auth_token,
            @Query("cart_id") String cart_id
    );


    @POST(URLPrefix + "add_card.php")
    Call<ResponseBody> addCard(
            @Query("user_id") String user_id,
            @Query("auth_token") String auth_token,
            @Query("cust_id") String cust_id,
            @Query("token") String token
    );

    @POST(URLPrefix + "add_order_rating.php")
    Call<ResponseBody> addReview(
            @Query("user_id") String user_id,
            @Query("auth_token") String auth_token,
            @Query("order_id") String order_id,
            @Query("rating") String rating,
            @Query("rating_text") String rating_text
    );


    @POST(URLPrefix + "change_quantity.php")
    Call<ResponseBody> changeQuantity(
            @Query("user_id") String user_id,
            @Query("auth_token") String auth_token,
            @Query("cart_item_id") String cart_item_id,
            @Query("cart_id") String cart_id,
            @Query("quantity") String quantity
    );



    @POST(URLPrefix + "cart_items.php")
    Call<ResponseBody> cartList(
            @Query("user_id") String user_id,
            @Query("auth_token") String auth_token,
            @Query("cart_id") String cart_id,
            @Query("is_mangals") String is_mangals,
            @Query("time") String time,
            @Query("day") String day
    );

    @POST(URLPrefix + "restaurant_category.php")
    Call<ResponseBody> restaurantCategory(
            @Query("time") String time,
            @Query("day") String day,
            @Query("restaurant_id") String restaurant_id,
            @Query("lang_id") String lang_id
    );

    @POST(URLPrefix + "reorder.php")
    Call<ResponseBody> reorder(
            @Query("time") String time,
            @Query("day") String day,
            @Query("user_id") String user_id,
            @Query("auth_token") String auth_token,
            @Query("order_id") String order_id,
            @Query("cart_id") String cart_id
    );


    @POST(URLPrefix + "languages.php")
    Call<ResponseBody> languages(
            @Query("user_id") String user_id,
            @Query("auth_token") String auth_token,
            @Query("lang_id") String lang_id
    );

    @POST(URLPrefix + "get_lang_keyword.php")
    Call<ResponseBody> getLangKeyword(
            @Query("lang_id") String lang_id
    );
}