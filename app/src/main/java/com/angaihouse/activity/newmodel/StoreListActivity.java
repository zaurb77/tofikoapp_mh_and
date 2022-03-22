package com.angaihouse.activity.newmodel;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityStoreListBinding;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import adapter.StoreListAdapter;
import okhttp3.ResponseBody;
import pojo.RestaurantListPojo;
import retrofit2.Call;
import retrofit2.Response;

public class StoreListActivity extends AppCompatActivity {

    AppCompatActivity activity;
    StoreUserData storeUserData;
    ActivityStoreListBinding binding;
    StoreListAdapter storeListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity,R.layout.activity_store_list);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.tvBackToMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("MESSAGE_DATA_NOT_FOUND")) {
            binding.llMessage.setVisibility(View.VISIBLE);
            binding.message.setText(getIntent().getStringExtra("MESSAGE_DATA_NOT_FOUND"));
        }else {
            binding.llMessage.setVisibility(View.GONE);
            storeListApi(getIntent().getStringExtra("SELECTED_ADDRESS"));
        }

        binding.rvStoreListing.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvStoreListing.setNestedScrollingEnabled(false);
        binding.rvStoreListing.setHasFixedSize(true);

    }

    public void storeListApi(String restaurantId) {

        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;


        String lat, lng;
        if (storeUserData.getString(Constants.LAT_STRING).length() > 0) {
            lat = storeUserData.getString(Constants.LAT_STRING);
        } else {
            lat = "0.0";
        }

        if (storeUserData.getString(Constants.LNG_STRING).length() > 0) {
            lng = storeUserData.getString(Constants.LNG_STRING);
        } else {
            lng = "0.0";
        }


        call = retrofitHelper.api().getRestaurantList(
                lat,
                lng,
                new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()),
                storeUserData.getString(Constants.DAY),
                restaurantId,
                "pickup",
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
                    Log.i("STORE_LIST", "" + response);
                    Reader reader = new StringReader(response);
                    Log.i( "adSADFSAFDA",""+lat+"==>"+lng );


                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    RestaurantListPojo data = gson.fromJson(reader, RestaurantListPojo.class);


                    if (data.status == 1) {
                        storeListAdapter = new StoreListAdapter(activity,getIntent().getStringExtra("SELECTED_ADDRESS"),data.responsedata);
                        binding.rvStoreListing.setAdapter(storeListAdapter);
                    } else {
                        binding.llMessage.setVisibility(View.VISIBLE);
                        binding.message.setText(data.message);
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