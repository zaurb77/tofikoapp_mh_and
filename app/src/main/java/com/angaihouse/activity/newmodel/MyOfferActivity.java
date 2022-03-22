package com.angaihouse.activity.newmodel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityMyOfferBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import adapter.OfferAdapter;
import okhttp3.ResponseBody;
import pojo.OfferListPojo;
import retrofit2.Call;
import retrofit2.Response;

public class MyOfferActivity extends AppCompatActivity {

    StoreUserData storeUserData;
    AppCompatActivity activity;
    ActivityMyOfferBinding binding;
    ItemClickListener itemClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity,R.layout.activity_my_offer);

        binding.myMangals.setText(Constants.MY_MANGALS.toUpperCase());
        binding.points.setText(Constants.MY_MANGAL_POINT);
        binding.availableOfferTv.setText(Constants.AVAILABLE_OFFERS.toLowerCase());



        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.rvMyMangle.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvMyMangle.setNestedScrollingEnabled(false);
        binding.rvMyMangle.setHasFixedSize(true);

        itemClickListener = new ItemClickListener() {

            @Override
            public void onClick(OfferListPojo.ResponseData.OfferItems pojo) {
                if(pojo.is_order_accept.equalsIgnoreCase("1")) {
                    if (storeUserData.getString(Constants.ORDER_TYPE).length() != 0) {
                        if(pojo.is_offer_active.equalsIgnoreCase("1")) {
                            if(pojo.item_id.contains(",")) {
                                addToCart(pojo.restaurant_id, pojo.item_id);
                            }else {
                                activity.startActivity(new Intent(activity, DetailActivity.class)
                                        .putExtra("BANNER_ITEM", "BANNER_ITEM")
                                        .putExtra("BANNER_DELIVERY_TYPE", storeUserData.getString(Constants.ORDER_TYPE))
                                        .putExtra("ITEM_ID", pojo.id)
                                        .putExtra("BANNER_RESTAURANT_ID", pojo.restaurant_id)
                                        .putExtra("MENU_ACTIVITY", "MENU_ACTIVITY")
                                );
                            }
                        }

                    } else {
                        if(pojo.is_offer_active.equalsIgnoreCase("1")) {
                            if(pojo.item_id.contains(",")) {
                                activity.startActivity(new Intent(activity, OrderMenuActivity.class)
                                        .putExtra("BANNER_ITEM_COMBO", "BANNER_ITEM_COMBO")
                                        .putExtra("ITEM_ID", pojo.id)
                                        .putExtra("BANNER_RESTAURANT_ID", pojo.restaurant_id)
                                );
                                finish();
                            }else {
                                activity.startActivity(new Intent(activity, OrderMenuActivity.class)
                                        .putExtra("BANNER_ITEM", "BANNER_ITEM")
                                        .putExtra("ITEM_ID", pojo.id)
                                        .putExtra("BANNER_RESTAURANT_ID", pojo.restaurant_id)
                                );
                                finish();
                            }
                        }
                    }

                }else {
                    Utils.showTopMessageError(activity, pojo.res_open_error);
                }
            }
        };

        getOfferList();
    }

    private void getOfferList() {

        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().offerList(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                storeUserData.getDouble(Constants.LAT_STRING).toString(),
                storeUserData.getDouble(Constants.LNG_STRING).toString(),
                new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()),
                storeUserData.getString(Constants.DAY),
                Constants.COMPANY_ID,
                storeUserData.getString(Constants.APP_LANGUAGE)
        );


        retrofitHelper.callApi(activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                Utils.dismissProgress();
                try {
                    if (body.code() != 200) {
                        Utils.serverError(activity, body.code());
                        return;
                    }
                    String response = body.body().string();
                    Log.i("TAG", "OFFER_LIST: " + response);
                    Reader reader = new StringReader(response);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    OfferListPojo pojo = gson.fromJson(reader, OfferListPojo.class);

                    Glide.with(activity)
                            .load(pojo.responsedata.qr_image)
                            .into(binding.QrImage);

                    binding.points.setText("My Mangal Points ("+pojo.responsedata.points+")");

                    if (pojo.status==1) {
                        binding.rvMyMangle.setVisibility(View.VISIBLE);
                        binding.rvMyMangle.setAdapter(new OfferAdapter(activity, pojo.responsedata.offer_items, itemClickListener));
                    } else {
                        Utils.showTopMessageError(activity, pojo.message);
                    }

                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.e("ERROR", error);
            }
        });
    }

    private void addToCart(String resID, String itemId) {

        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().addToCart(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                resID,
                itemId,
                "1",
                "",
                "",
                "",
                "",
                storeUserData.getString(Constants.CART_ID),
                "",
                "pickup"
        );

        retrofitHelper.callApi(activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                try {
                    if (body.code() != 200) {
                        Utils.serverError(activity, body.code());
                        return;
                    }

                    String response = body.body().string();
                    Log.i("TAG", "addToCartItem: " + response);
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("status") == 0) {
                        Utils.dismissProgress();
                        Utils.showTopMessageError(activity, jsonObject.getString("message"));
                    } else {
                        JSONObject object = jsonObject.getJSONObject("responsedata");
                        if (object.has("isdiff_res")) {
                            Utils.dismissProgress();
                            if (object.getInt("isdiff_res") == 1) {
                                new AlertDialog.Builder(activity)
                                        .setTitle("Replace Cart Item ?")
                                        .setMessage("Your cart contains dishes from.Do you want to discard selection and add dishes from.")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", (dialog, which) -> {
                                            new StoreUserData(activity).setString(Constants.CART_ID, "");

                                            addToCart(resID, itemId);

                                            return;
                                        })
                                        .setNegativeButton("No", null)
                                        .setIcon(R.mipmap.ic_launcher)
                                        .show();
                            }
                        } else {
                            if (object.has("cart_id")) {
                                storeUserData.setString(Constants.CART_ID, object.getString("cart_id"));
                            }
                            Utils.showTopMessageSuccess(activity, jsonObject.getString("message"));
                            startActivity(new Intent(activity, CartNewActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.e("ERROR", error);
            }
        });
    }

    public interface ItemClickListener {
        void onClick(OfferListPojo.ResponseData.OfferItems pojo);
    }
}
