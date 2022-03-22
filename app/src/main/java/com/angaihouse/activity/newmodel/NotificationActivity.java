package com.angaihouse.activity.newmodel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.angaihouse.R;
import com.angaihouse.activity.LoginActivity;
import com.angaihouse.databinding.ActivityNotificationBinding;
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

import adapter.AddressListAdapter;
import adapter.NotificationAdapter;
import okhttp3.ResponseBody;
import pojo.CategoryPojo;
import pojo.NotificationPojo;
import retrofit2.Call;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {


    private AppCompatActivity activity;
    private StoreUserData storeUserData;
    private ActivityNotificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity,R.layout.activity_notification);
        binding.back1.setOnClickListener( v -> finish() );
        binding.rvNotification.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvNotification.setNestedScrollingEnabled(false);
        binding.rvNotification.setHasFixedSize(true);
        notification();
        binding.title1.setText(Constants.NOTIFICATION);


    }

    private void notification() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

//        call = retrofitHelper.api().getNotification(
//          "3",
//                "mAZM7hg0BFMiFBE9",
//                "1",
//                "2"
//        );

        call = retrofitHelper.api().getNotification(
                storeUserData.getString( Constants.USER_ID ),
                storeUserData.getString( Constants.TOKEN),
                 Constants.COMPANY_ID,
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
                    Log.i("GET_NOTIFICATION", "onSuccess: " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    Reader reader = new StringReader(response);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers( Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    NotificationPojo data = gson.fromJson(reader, NotificationPojo.class);

                    if (jsonObject.getInt("status") == 1) {

                        if (data.responsedata.size() > 0 ){
                            binding.rvNotification.setVisibility( View.VISIBLE );
                            binding.llMessage.setVisibility( View.GONE );
                            binding.rvNotification.setAdapter(new NotificationAdapter(activity, data.responsedata ));
                        }else {
                            binding.message.setText( Constants.NO_NOTIFICATIONS_AVAILABLE );
                            binding.rvNotification.setVisibility( View.GONE );
                            binding.llMessage.setVisibility( View.VISIBLE );
                        }
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) { }

        });
    }
}