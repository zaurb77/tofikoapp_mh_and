package com.angaihouse.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityOrderDetailBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Modifier;

import adapter.OrderDetailAdapter;
import okhttp3.ResponseBody;
import pojo.OrderDetailPojo;
import retrofit2.Call;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    AppCompatActivity activity;
    StoreUserData storeUserData;
    ActivityOrderDetailBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_order_detail);
        binding.back.setOnClickListener(view -> finish());

        binding.rvOrderDetail.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvOrderDetail.setNestedScrollingEnabled(false);
        binding.rvOrderDetail.setHasFixedSize(true);

        orderDetail();

    }

    //TODO : GET ORDER DETAILS
    private void orderDetail() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().orderDetails(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                getIntent().getStringExtra("OrderNumber"),
                storeUserData.getString( Constants.APP_LANGUAGE)
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
                    Log.i("TAG", "OrderDetails: " + response);
                    Reader reader = new StringReader(response);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    OrderDetailPojo pojo = gson.fromJson(reader, OrderDetailPojo.class);


                    if (pojo.status == 1) {

                        String upperString = Constants.ADDRESS.substring(0, 1).toUpperCase() + Constants.ADDRESS.substring(1).toLowerCase();
                        binding.addressTv.setText(upperString);


                        binding.mobileNoTv.setText(Constants.MOBILE_NO);
                        binding.nameTv.setText(Constants.NAME);
                        binding.restaurantAddress.setText(Constants.RES_ADDRESS);
                        binding.deliveryTime.setText(Constants.DELIVERY_TIME);

                        if (pojo.responsedata.delivery_time.length()>0){
                            binding.llDeliveryTime.setVisibility(View.VISIBLE);
                        }

                        binding.name.setText(storeUserData.getString(Constants.USER_FNAME) + " " + storeUserData.getString(Constants.USER_LNAME));
                        binding.address.setText(pojo.responsedata.address_line);
                        binding.mobileNo.setText(storeUserData.getString(Constants.mobile_no));
                        binding.deliveryTimeTv.setText(pojo.responsedata.delivery_time);

                        if (pojo.responsedata.address_line.length() > 0) {
                            binding.llAddress.setVisibility(View.VISIBLE);
                        } else {
                            binding.llAddress.setVisibility(View.GONE);
                        }

                        binding.restaurantAddressTv.setText(pojo.responsedata.res_address);

                        binding.subtotal.setText(Constants.ITEM_TOTAL+" : "+storeUserData.getString( Constants.CURRENCY )+" " + pojo.responsedata.sub_total);
                        binding.deliveryCharge.setText(Constants.DELIVERY_CHARGE+" : "+storeUserData.getString( Constants.CURRENCY )+" " + pojo.responsedata.delivery_charge);
                        binding.total.setText(Constants.TOTAL_PAYABLE_AMT+" : "+storeUserData.getString( Constants.CURRENCY )+" " + pojo.responsedata.order_total);

                        if (pojo.responsedata.order_status.equalsIgnoreCase("in_prepare")) {
                            binding.status.setText(Constants.IN_PREPARE);
                        } else if (pojo.responsedata.order_status.equalsIgnoreCase("pending")) {
                            binding.status.setText(Constants.PENDING);
                        } else if (pojo.responsedata.order_status.equalsIgnoreCase("completed")) {
                            binding.status.setText(Constants.COMPLETED);
                        } else if (pojo.responsedata.order_status.equalsIgnoreCase("decline")) {
                            binding.status.setText(Constants.CANCELLED);
                        } else if (pojo.responsedata.order_status.equalsIgnoreCase("delivery")) {
                            binding.status.setText(Constants.DELIVERY);
                        } else {
                            binding.status.setText(pojo.responsedata.order_status);
                        }

                        if (pojo.responsedata.payment_type.equalsIgnoreCase("cod")) {
                            binding.typeOfPayment.setText(Constants.TYPE_OF_PAYMENT+" : "+Constants.CASH_ON_DELIVERY);

                        } else if (pojo.responsedata.payment_type.equalsIgnoreCase("stripe")) {
                            binding.typeOfPayment.setText(Constants.TYPE_OF_PAYMENT+" : Stripe");

                        } else if (pojo.responsedata.payment_type.equalsIgnoreCase("paypal")) {
                            binding.typeOfPayment.setText(Constants.TYPE_OF_PAYMENT+" : "+Constants.PAYPAL);

                        } else if (pojo.responsedata.payment_type.equalsIgnoreCase("satispay")) {
                            binding.typeOfPayment.setText(Constants.TYPE_OF_PAYMENT+" : SatisPay");

                        } else if (pojo.responsedata.payment_type.equalsIgnoreCase("bancomat")) {
                            binding.typeOfPayment.setText(Constants.TYPE_OF_PAYMENT+" : Bancomat");
                        }

                        binding.rvOrderDetail.setAdapter(new OrderDetailAdapter(activity, pojo.responsedata.items));

                        binding.llMain.setVisibility(View.VISIBLE);
                        binding.status.setVisibility(View.VISIBLE);

                    }


                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
                Log.e("ERROR", error);
            }
        });
    }
}
