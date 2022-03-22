package com.angaihouse.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivitySelectRestaurantBinding;
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

import adapter.getBookingAdapter;
import okhttp3.ResponseBody;
import pojo.NotificationPojo;
import retrofit2.Call;
import retrofit2.Response;

public class SelectRestaurantActivity extends AppCompatActivity {

    AppCompatActivity activity;
    ActivitySelectRestaurantBinding binding;
    StoreUserData storeUserData;
    ItemClickListener itemClickListener;

    @Override
    protected void onResume() {
        super.onResume();
        getRestaurant(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        activity = this;
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_select_restaurant );
        storeUserData = new StoreUserData( activity );
        binding.tvAddBooking.setOnClickListener( v -> startActivity( new Intent(activity , RestaurantListActivity.class) ) );
        binding.back.setOnClickListener( v -> finish() );
        binding.tvAddBooking.setText(Constants.ADD_A_NEW_RESERVATION);

        itemClickListener = new ItemClickListener() {
            @Override
            public void cancelBooking(NotificationPojo.Responsedata pojo) {

                new AlertDialog.Builder(activity)
                        .setMessage(Constants.CANCEL_RESERVATION)
                        .setPositiveButton(Constants.YES_LABEL, (dialog, which) -> {
                            cancelTable(pojo.id);
                        })
                        .setNegativeButton(Constants.NO_LABEL, null)
                        .show();

            }
        };
    }

    private void getRestaurant(Boolean loader) {
        if (loader){
            Utils.showProgress(activity);
        }
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().getBookedTable(
                storeUserData.getString( Constants.USER_ID ),
                storeUserData.getString( Constants.TOKEN),
                storeUserData.getString( Constants.APP_LANGUAGE)
        );

        Log.i("GET_NOTIFICATION_DATA", "onSuccess: " +   storeUserData.getString( Constants.USER_ID )+ "     " + storeUserData.getString( Constants.TOKEN)+ "     " + storeUserData.getString( Constants.APP_LANGUAGE));

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
                            binding.rvList.setVisibility( View.VISIBLE );
                            binding.llMessage.setVisibility( View.GONE );
                            binding.rvList.setLayoutManager(new LinearLayoutManager(activity));
                            binding.rvList.setNestedScrollingEnabled(false);
                            binding.rvList.setHasFixedSize(true);
                            binding.rvList.setAdapter(new getBookingAdapter(activity, data.responsedata , itemClickListener ));
                        }else {
                            binding.llMessage.setVisibility( View.VISIBLE );
                            binding.rvList.setVisibility( View.GONE );
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

    private void cancelTable(String bookingId) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().cancelBooking(
                storeUserData.getString( Constants.APP_LANGUAGE),
                storeUserData.getString( Constants.USER_ID ),
                storeUserData.getString( Constants.TOKEN),
                bookingId
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
                    Log.i("GET_NOTIFICATION", "onSuccess: " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    Reader reader = new StringReader(response);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers( Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    NotificationPojo data = gson.fromJson(reader, NotificationPojo.class);
                    if (jsonObject.getInt("status") == 1) {
                        getRestaurant(false);
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
            }

        });
    }

    public interface ItemClickListener {

        void cancelBooking(NotificationPojo.Responsedata pojo);
    }
}