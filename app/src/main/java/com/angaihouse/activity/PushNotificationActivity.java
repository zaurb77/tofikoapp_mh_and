package com.angaihouse.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityPushNotificationBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class PushNotificationActivity extends AppCompatActivity {


    ActivityPushNotificationBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity,R.layout.activity_push_notification);
        binding.back.setOnClickListener(view -> finish());

        binding.orderNotification.setText(Constants.ORDER_NOTIFICATION.toUpperCase());
        binding.tvPush.setText(Constants.PUSH_NOTIFICATION.toUpperCase());
        binding.email.setText(Constants.EMAIL_NOTIFICATION.toUpperCase());
        binding.tvPUSH.setText(Constants.PUSH_NOTIFICATION.toUpperCase());
        binding.EMAIL.setText(Constants.EMAIL_NOTIFICATION.toUpperCase());
        binding.newsAndOffer.setText(Constants.NEWS_AND_OFFERS.toUpperCase());

        if (storeUserData.getInt(Constants.ORDER_PUSH_NOTIFICATION) == 1){
            binding.switchOrderPushNotification.setChecked(true);
        }else {
            binding.switchOrderPushNotification.setChecked(false);
        }


        binding.switchOrderPushNotification.setOnCheckedChangeListener((view, isChecked) -> {

            if (isChecked) {
                changeStatus("order_push","1");
            }else {
                changeStatus("order_push","0");
            }
        });


        if (storeUserData.getInt(Constants.ORDER_EMAIL_NOTIFICATION) == 1){
            binding.switchEmailOrderPushNotification.setChecked(true);
        }else {
            binding.switchEmailOrderPushNotification.setChecked(false);
        }

        binding.switchEmailOrderPushNotification.setOnCheckedChangeListener((view, isChecked) -> {

            if (isChecked) {
                changeStatus("order_email","1");
            }else {
                changeStatus("order_email","0");
            }
        });


        if (storeUserData.getInt(Constants.NEWS_PUSH_NOTIFICATION) == 1) {
            binding.switchNewsPushNotification.setChecked(true);
        }else {
            binding.switchNewsPushNotification.setChecked(false);
        }

        binding.switchNewsPushNotification.setOnCheckedChangeListener((view, isChecked) -> {

            if (isChecked) {
                changeStatus("news_push","1");
            }else {
                changeStatus("news_push","0");
            }
        });


        if (storeUserData.getInt(Constants.NEWS_EMAIL_NOTIFICATION) == 1) {
            binding.switchNewsEmailNotification.setChecked(true);
        }else {
            binding.switchNewsEmailNotification.setChecked(false);
        }

        binding.switchNewsEmailNotification.setOnCheckedChangeListener((view, isChecked) -> {

            if (isChecked) {
                changeStatus("news_email","1");
            }else {
                changeStatus("news_email","0");
            }
        });
    }

    private void changeStatus(String orderType,String status) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;
        call = retrofitHelper.api().notificationStatus(
                orderType,
                status,
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN)

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
                    Log.i("TAG", "ChangeStatus: " + response);
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("status") == 1){

                        if (orderType.equalsIgnoreCase("order_push")){
                            if (status.equalsIgnoreCase("1")){
                                storeUserData.setInt(Constants.ORDER_PUSH_NOTIFICATION, 1);
                            }else {
                                storeUserData.setInt(Constants.ORDER_PUSH_NOTIFICATION, 0);
                            }
                        }

                        if (orderType.equalsIgnoreCase("order_email")){
                            if (status.equalsIgnoreCase("1")){
                                storeUserData.setInt(Constants.ORDER_EMAIL_NOTIFICATION,1);
                            }else {
                                storeUserData.setInt(Constants.ORDER_EMAIL_NOTIFICATION,0);
                            }
                        }

                        if (orderType.equalsIgnoreCase("news_push")){
                            if (status.equalsIgnoreCase("1")){
                                storeUserData.setInt(Constants.NEWS_PUSH_NOTIFICATION,1);
                            }else {
                                storeUserData.setInt(Constants.NEWS_PUSH_NOTIFICATION,0);
                            }
                        }

                        if (orderType.equalsIgnoreCase("news_email")){
                            if (status.equalsIgnoreCase("1")){
                                storeUserData.setInt(Constants.NEWS_EMAIL_NOTIFICATION,1);
                            }else {
                                storeUserData.setInt(Constants.NEWS_EMAIL_NOTIFICATION,0);
                            }
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
}
