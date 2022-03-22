package com.angaihouse.activity.newmodel;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityOrderHistoryBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
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

import adapter.OrderHistoryAdapter;
import okhttp3.ResponseBody;
import pojo.OrderHistoryPojo;
import retrofit2.Call;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity {

    StoreUserData storeUserData;
    AppCompatActivity activity;
    ActivityOrderHistoryBinding binding;
    private ItemClickListener itemClickListener;
    String orderType = "completed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity,R.layout.activity_order_history);

        binding.tvOrderHistory.setText(Constants.ORDER_HISTORY);
        binding.tvCompleted.setText(Constants.COMPLETED);
        binding.tvPrepare.setText(Constants.IN_PREPARE);
        binding.tvPending.setText(Constants.PENDING);

        binding.back.setOnClickListener(view -> finish());

        binding.tvPrepare.setOnClickListener(view -> {
            clearBg(binding.tvPrepare, binding.imgPrepare);
            orderType = "in_prepare";
            getOrderHistory(orderType);
        });

        binding.tvPending.setOnClickListener(view -> {
            clearBg(binding.tvPending, binding.imgPending);
            orderType = "pending";
            getOrderHistory(orderType);

        });

        binding.tvCompleted.setOnClickListener(view -> {
            clearBg(binding.tvCompleted, binding.imgCompleted);
            orderType = "completed";
            getOrderHistory(orderType);
        });



        binding.rvOrderHistory.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvOrderHistory.setNestedScrollingEnabled(false);
        binding.rvOrderHistory.setHasFixedSize(true);

        itemClickListener = (cartId, resName) -> new AlertDialog.Builder(activity)
                .setTitle(Constants.REPLACE_CART_ITEM)
                .setMessage(Constants.WANT_TO_REORDER1+ resName +"\n"+Constants.WANT_TO_REORDER2)
                .setPositiveButton(Constants.YES_LABEL, (dialog, which) -> {
                    reorder(cartId);
                    return;
                })
                .setNegativeButton(Constants.NO_LABEL, null)
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOrderHistory(orderType);
    }

    public void clearBg(TextView view, ImageView imageView) {

        binding.tvPending.setTextColor(ContextCompat.getColor(activity, R.color.colorLightGreen));
        binding.tvPrepare.setTextColor(ContextCompat.getColor(activity, R.color.colorLightGreen));
        binding.tvCompleted.setTextColor(ContextCompat.getColor(activity, R.color.colorLightGreen));

        binding.tvPending.setBackgroundResource(R.drawable.round_green_border);
        binding.tvPrepare.setBackgroundResource(R.drawable.round_green_border);
        binding.tvCompleted.setBackgroundResource(R.drawable.round_green_border);

        binding.imgCompleted.setVisibility(View.INVISIBLE);
        binding.imgPending.setVisibility(View.INVISIBLE);
        binding.imgPrepare.setVisibility(View.INVISIBLE);

        view.setTextColor(ContextCompat.getColor(activity, R.color.white));
        view.setBackgroundResource(R.drawable.round_green_solid);
        imageView.setVisibility(View.VISIBLE);
    }


    private void getOrderHistory(String orderType) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().orderHistory(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                orderType,
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
                    Log.i("TAG", "OrderHistory: " + response);
                    Reader reader = new StringReader(response);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    OrderHistoryPojo pojo = gson.fromJson(reader, OrderHistoryPojo.class);

                    if (pojo.status == 1) {
                        binding.rvOrderHistory.setVisibility(View.VISIBLE);
                        binding.message.setText("");
                        binding.rvOrderHistory.setAdapter(new OrderHistoryAdapter(activity, pojo.responsedata,itemClickListener,orderType));
                        binding.llMessage.setVisibility(View.GONE);
                    } else {
                        binding.rvOrderHistory.setVisibility(View.GONE);
                        binding.llMessage.setVisibility(View.VISIBLE);
                        binding.message.setText(pojo.message);
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


    //TODO : Reorder
    private void reorder(String orderId) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().reorder(
                new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()),
                storeUserData.getString(Constants.DAY),
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                orderId,
                storeUserData.getString(Constants.CART_ID)
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
                    Log.i("TAG", "reorder: " + response);

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("status") == 0) {
                        Utils.showTopMessageError(activity, jsonObject.getString("message"));
                    } else {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("responsedata");
                        if (jsonObject1.getString("isdiff_res").equalsIgnoreCase("1")){
                            new AlertDialog.Builder(activity)
                                    .setTitle(Constants.REPLACE_CART_ITEM)
                                    .setMessage("Your old cart is from "+ jsonObject1.getString("diff_res_name") +"\n"+"Do you want to reorder?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        storeUserData.setString(Constants.CART_ID, "");
                                        reorder(orderId);
                                        return;
                                    })
                                    .setNegativeButton("No", null)
                                    .setIcon(R.mipmap.ic_launcher)
                                    .show();
                        }else {
                            storeUserData.setString(Constants.CART_ID, jsonObject1.getString("cart_id"));
                            startActivity(new Intent(activity,CartNewActivity.class));
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
        void onClick(String orderId,String resName);
    }
}
