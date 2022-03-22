package com.angaihouse.activity.newmodel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.activity.AddAddressActivity;
import com.angaihouse.databinding.ActivityOrderMenuBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class OrderMenuActivity extends AppCompatActivity {

    StoreUserData storeUserData;
    AppCompatActivity activity;
    ActivityOrderMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_order_menu);

        binding.tvDelivery.setText(Constants.DELIVERY);
        binding.tvTakeAwayFromStore.setText(Constants.TAKEAWAY_FROM_STORE);
        binding.titleText.setText(Constants.HOW_WOULD_LIKE.toUpperCase()+"\n"+Constants.RECEIVE_ORDER.toUpperCase());

        binding.back.setOnClickListener(view -> finish());

        binding.deliveryOption.setOnClickListener(view -> {

            if(storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")){
                startActivity(new Intent(activity, StoreListActivity.class));
                finish();
            }else {
                if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("BANNER_ITEM")) {

                    startActivity(new Intent(activity, AddAddressActivity.class)
                            .putExtra("BANNER_ITEM", "BANNER_ITEM")
                            .putExtra("BANNER_ORDER_TYPE", "delivery")
                            .putExtra("ITEM_ID", getIntent().getStringExtra("ITEM_ID"))
                            .putExtra("BANNER_RESTAURANT_ID", getIntent().getStringExtra("BANNER_RESTAURANT_ID"))
                    );
                    finish();
                } else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("BANNER_ITEM_COMBO")) {

                    startActivity(new Intent(activity, AddAddressActivity.class)
                            .putExtra("BANNER_ITEM_COMBO", "BANNER_ITEM_COMBO")
                            .putExtra("BANNER_ORDER_TYPE", "delivery")
                            .putExtra("ITEM_ID", getIntent().getStringExtra("ITEM_ID"))
                            .putExtra("BANNER_RESTAURANT_ID", getIntent().getStringExtra("BANNER_RESTAURANT_ID"))
                    );
                    finish();

                } else {

                    startActivity(new Intent(activity, AddAddressActivity.class)
                            .putExtra("ORDERTYPE", "delivery")
                    );

                }
            }
        });

        binding.pickupStore.setOnClickListener(view -> {


            if(storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")){

                startActivity(new Intent(activity, MapActivity.class)
                        .putExtra("ORDERTYPE", "pickup")
                );
                finish();

            }else {

                if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("BANNER_ITEM")) {

                    startActivity(new Intent(activity, DetailActivity.class)
                            .putExtra("BANNER_ITEM", "BANNER_ITEM")
                            .putExtra("BANNER_ADDRESS_ID", "")
                            .putExtra("BANNER_DELIVERY_TYPE", "pickup")
                            .putExtra("ITEM_ID", getIntent().getStringExtra("ITEM_ID"))
                            .putExtra("BANNER_RESTAURANT_ID", getIntent().getStringExtra("BANNER_RESTAURANT_ID"))
                    );
                    finish();

                } else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("BANNER_ITEM_COMBO")) {
                    addToCart(getIntent().getStringExtra("BANNER_RESTAURANT_ID"), getIntent().getStringExtra("ITEM_ID"), "1","pickup");
                } else {

                    startActivity(new Intent(activity, MapActivity.class)
                            .putExtra("ORDERTYPE", "pickup")
                    );
                    finish();
                }


            }
        });
    }


    private void addToCart(String resID, String id, String qty,String orderType) {

        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().addToCart(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                resID,
                id,
                qty,
                "",
                "",
                "",
                "",
                storeUserData.getString(Constants.CART_ID),
                "",
                orderType
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

                    String response = Objects.requireNonNull(body.body()).string();
                    Log.i("TAG", "addToCartItem: " + response);
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("status") == 0) {
                        Utils.dismissProgress();
                        Utils.showTopMessageError(activity, jsonObject.getString("message"));
                    } else {

                        JSONObject object = jsonObject.getJSONObject("responsedata");

                        if (storeUserData.getString(Constants.CART_ID).equalsIgnoreCase("")) {
                            storeUserData.setString(Constants.CART_ID, object.getString("cart_id"));
                        }

                        Utils.showTopMessageSuccess(activity, jsonObject.getString("message"));

                        new Handler().postDelayed(() -> {
                            startActivity(new Intent(activity, CartNewActivity.class));
                            finish();
                        },2000);

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
