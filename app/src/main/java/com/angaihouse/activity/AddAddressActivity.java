package com.angaihouse.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.angaihouse.R;
import com.angaihouse.activity.newmodel.CartNewActivity;
import com.angaihouse.activity.newmodel.DetailActivity;
import com.angaihouse.activity.newmodel.MenuActivity;
import com.angaihouse.activity.newmodel.StoreListActivity;
import com.angaihouse.databinding.ActivityAddAddressBinding;
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

import adapter.AddressListAdapter;
import okhttp3.ResponseBody;
import pojo.AddressListPojo;
import pojo.RestaurantListPojo;
import retrofit2.Call;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity {

    public int selectedAddress = -1, addressId = -1;
    String addressLine;
    ActivityAddAddressBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;
    private ItemClickListener itemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_add_address);

        binding.CTextView2.setText(Constants.DELIVERY_ADDRESS);
        binding.addressTitle.setText(Constants.ADDRESS);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("CHANGE_ADDRESS")) {
            binding.continueBtn.setText(Constants.SAVE);
        }

        binding.back.setOnClickListener(view -> {
            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("CHANGE_ADDRESS")) {
                startActivity(new Intent(activity, CartNewActivity.class));
            }
            finish();
        });

        binding.addAddress.setOnClickListener(view -> startActivity(new Intent(activity, AddAddressTypeActivity.class)));
        binding.rvAddressList.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvAddressList.setNestedScrollingEnabled(false);
        binding.rvAddressList.setHasFixedSize(true);

        //TODO : USING THIS INTERFACE SELECT YOUR ADDRESS
        itemClickListener = new ItemClickListener() {
            @Override
            public void onClick(int addressIds, String address) {
                addressId = addressIds;
                addressLine = address;
                if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("CHANGE_ADDRESS")) {
                    getDeliverAddressApi();
                }
            }

            @Override
            public void deleteAddress(int addressId) {

                new AlertDialog.Builder(activity)
                        .setMessage("Are you sure you want to delete address?")
                        .setPositiveButton(Constants.YES_LABEL, (dialog, which) -> {
                            deleteAddressApi("" + addressId);
                        })
                        .setNegativeButton(Constants.NO_LABEL, null)
                        .show();
            }
        };


        binding.continueBtn.setOnClickListener(view -> {
            if (addressId == -1) {
                Utils.showTopMessageError(activity, Constants.DELIVERY_ADDRESS);
            } else {
                if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("BANNER_ITEM")) {

                    startActivity(new Intent(activity, DetailActivity.class)
                            .putExtra("BANNER_ITEM", "" + getIntent().getStringExtra("BANNER_ITEM"))
                            .putExtra("BANNER_ADDRESS_ID", "" + addressId)
                            .putExtra("BANNER_DELIVERY_TYPE", getIntent().getStringExtra("BANNER_ORDER_TYPE"))
                            .putExtra("ITEM_ID", getIntent().getStringExtra("ITEM_ID"))
                            .putExtra("BANNER_RESTAURANT_ID", getIntent().getStringExtra("BANNER_RESTAURANT_ID"))
                    );
                    finish();

                } else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("BANNER_ITEM_COMBO")) {
                    addToCart(getIntent().getStringExtra("BANNER_RESTAURANT_ID"), getIntent().getStringExtra("ITEM_ID"), "1");
                } else {
                    if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("CHANGE_ADDRESS")) {
                        getNearestRestaurantApi();
                    } else {
                        storeListApi();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAddressList(true);
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("CHANGE_ADDRESS")) {
            startActivity(new Intent(activity, CartNewActivity.class));
        }
        finish();
    }

    //TODO : GET USER ADDRESS LIST FROM WEB
    private void getAddressList(boolean progress) {
        if (progress) {
            Utils.showProgress(activity);
        }
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().addressList(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.APP_LANGUAGE),
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
                    Log.i("TAG", "AddressList: " + response);
                    Reader reader = new StringReader(response);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    AddressListPojo pojo = gson.fromJson(reader, AddressListPojo.class);
                    addressId = -1;
                    if (pojo.status == 1) {

                        binding.continueBtn.setVisibility(View.VISIBLE);
                        binding.CTextView2.setVisibility(View.VISIBLE);
                        binding.rvAddressList.setVisibility(View.VISIBLE);
                        binding.message.setText("");
                        binding.llNext.setVisibility(View.VISIBLE);
                        binding.rvAddressList.setAdapter(new AddressListAdapter(activity, pojo.responsedata, itemClickListener));
                        binding.addressTitle.setVisibility(View.VISIBLE);

                    } else {

                        binding.addressTitle.setVisibility(View.GONE);
                        Utils.dismissProgress();
                        binding.llNext.setVisibility(View.GONE);
                        binding.rvAddressList.setVisibility(View.GONE);
                        binding.continueBtn.setVisibility(View.INVISIBLE);
                        binding.CTextView2.setVisibility(View.INVISIBLE);
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

    private void deleteAddressApi(String id) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().address(
                "delete",
                id,
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                ""
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
                    Log.i("TAG", "DeleteAddress" + response);

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        getAddressList(false);
                    }

                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
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

    private void getDeliverAddressApi() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().getDeliveryAddress(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                storeUserData.getString(Constants.CART_ID),
                "" + addressId
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
                    Log.i("getDeliveryAddress", "getDeliveryAddress" + response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {

                        JSONObject jsonObject1 = jsonObject.getJSONObject("responsedata");
                        addressId = jsonObject1.getInt("address_id");
                        selectedAddress = jsonObject1.getInt("address_id");
                        addressLine = jsonObject1.getString("address_line");

                    } else {

                        addressId = -1;
                        selectedAddress = -1;
                        addressLine = "";
                        Utils.showTopMessageError(activity, jsonObject.getString("message"));
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
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

    private void getNearestRestaurantApi() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;
        call = retrofitHelper.api().getNearestRestaurant(
                "" + addressId,
                new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()),
                storeUserData.getString(Constants.DAY)
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
                    Log.i("TAG", "getNearestRestaurant: " + response);

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 0) {
                        Utils.showTopMessageError(activity, jsonObject.getString("message"));
                    } else {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("responsedata");
                        storeUserData.setString(Constants.NEAR_RES_ID, jsonObject1.getString("res_id"));

                        if (selectedAddress == -1) {
                            Utils.showTopMessageError(activity, "Please select a delivery address.");
                        } else {
                            storeUserData.setString(Constants.ORDER_TYPE, "delivery");
                            startActivity(new Intent(activity, CartNewActivity.class));
                            finish();
                        }


                    }

                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
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

    private void addToCart(String resID, String id, String qty) {

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
                "" + addressId,
                getIntent().getStringExtra("BANNER_ORDER_TYPE")
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

                        startActivity(new Intent(activity, CartNewActivity.class));
                        finish();

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

    public void storeListApi() {

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
                "" + addressId,
                "delivery",
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
                    Log.i("TAG", "RESTAURANT_LIST: " + response);
                    Reader reader = new StringReader(response);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    RestaurantListPojo data = gson.fromJson(reader, RestaurantListPojo.class);


                    if (data.status == 1) {

                        if (data.responsedata.size() == 1) {
                            storeUserData.setString(Constants.NEAR_RES_ID, ""+data.responsedata.get(0).id);
                            startActivity(new Intent(activity, MenuActivity.class)
                                    .putExtra("ADDRESSID", "" + addressId)
                                    .putExtra("ORDERTYPE", "delivery")
                            );
                        } else {
                            startActivity(new Intent(activity, StoreListActivity.class)
                                    .putExtra("MESSAGE", data.message)
                                    .putExtra("SELECTED_ADDRESS", ""+addressId)
                                    .putExtra("ORDER_TYPE", "delivery")
                            );
                        }
                        finish();


                    } else {
                        startActivity(new Intent(activity, StoreListActivity.class)
                                .putExtra("MESSAGE_DATA_NOT_FOUND", data.message)
                        );
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

    //TODO : THIS IS THE INTERFACE USE FOR HANDEL ITEM CLICK
    public interface ItemClickListener {
        void onClick(int addressId, String addressLine);
        void deleteAddress(int addressId);
    }

}