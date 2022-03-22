package com.angaihouse.activity.newmodel;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.angaihouse.R;
import com.angaihouse.activity.AddAddressActivity;
import com.angaihouse.activity.PaymentInfoScreen;
import com.angaihouse.databinding.ActivityCartNewBinding;
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

import adapter.CartListAdapter;
import okhttp3.ResponseBody;
import pojo.CartListPojo;
import retrofit2.Call;
import retrofit2.Response;

public class CartNewActivity extends AppCompatActivity {

    AppCompatActivity activity;
    StoreUserData storeUserData;
    ActivityCartNewBinding binding;
    CartListPojo pojo;
    String addressId = "",mangalPay = "0";
    private ItemClickListener itemClickListener;
    int isCutlery = 0,cartSize = 0,selectedTYpe = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_cart_new);

        binding.tvYourCartEmpty.setText(Constants.YOUR_CART_EMPTY);
        binding.tvBackToMainMenu.setText(Constants.BACK_MAIN_MENU);
        binding.tvCheckOut.setText(Constants.CHECKOUT.toUpperCase());
        binding.payWithMangal.setText(Constants.PAY_WITH_MANGAL);
        binding.tvItemTotal.setText(Constants.ITEM_TOTAL);
        binding.tvDeliveryCharge.setText(Constants.DELIVERY_CHARGE);
        binding.tvTotalPayableAmount.setText(Constants.TOTAL_PAYABLE_AMT);
        binding.tvOrderType.setText(Constants.ORDER_TYPE_K);
        binding.tvTakeAway.setText(Constants.TAKEAWAY);
        binding.tvDelivery.setText(Constants.DELIVERY);
        binding.ad.setText(Constants.CHANGE_ADDRESS);
        binding.textMessage.setText(Constants.ADD_CUTLERY);
        binding.specialRequest.setText(Constants.SPECIAL_REQUEST);

        binding.llChangeAddress.setOnClickListener(view -> {
            startActivity(new Intent(activity, AddAddressActivity.class)
                    .putExtra("CHANGE_ADDRESS", "CHANGE_ADDRESS")
            );
            finish();
        });

        binding.llDelivery.setOnClickListener(view -> selectItem(1));

        binding.llPickup.setOnClickListener(view -> selectItem(0));

        binding.mangalSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                mangalPay = "1";
            }else{
                mangalPay = "0";
            }
            getCartList(false,mangalPay);
        });

        binding.llCutlery.setOnClickListener(view -> {
            binding.cutlery.setChecked(!binding.cutlery.isChecked());
        });

        binding.cutlery.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                isCutlery = 1;
            }else {
                isCutlery = 0;
            }
        });


        binding.tvNoteCounter.setText("0/256");
        binding.note.setFilters(new InputFilter[]{new InputFilter.LengthFilter(256)});
        binding.note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tvNoteCounter.setText(s.length()+"/256");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.rvCart.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvCart.setNestedScrollingEnabled(false);
        binding.rvCart.setHasFixedSize(true);
        itemClickListener = this::changeQty;

        binding.llCheckOut.setOnClickListener(view -> {

            if (selectedTYpe != 0 && TextUtils.isEmpty(addressId)){
                Utils.showTopMessageError(activity,"Please Select Your Address.");
            }else {
                startActivity(new Intent(activity, PaymentInfoScreen.class)
                        .putExtra("totalAmount", pojo.responsedata.order_total)
                        .putExtra("totalPayAmount", pojo.responsedata.total_pay_amount)
                        .putExtra("deliveryCharges", pojo.responsedata.delivery_charge)
                        .putExtra("totalPayable", pojo.responsedata.total_pay_amount)
                        .putExtra("isMangals", "" + pojo.responsedata.is_mangals_cart)
                        .putExtra("note", binding.note.getText().toString().trim())
                        .putExtra("RES_NAME", pojo.responsedata.restaurant_name)
                        .putExtra("USER_NAME", storeUserData.getString(Constants.USER_FNAME) + " " + storeUserData.getString(Constants.USER_LNAME))
                        .putExtra("ADDRESS", binding.address.getText().toString())
                        .putExtra("MOBILE_NO", storeUserData.getString(Constants.mobile_no))
                        .putExtra("RESTAURANT_MOBILE_NO", pojo.responsedata.restaurant_phone)
                        .putExtra("res_is_open", "" + pojo.responsedata.res_is_open)
                        .putExtra("time", pojo.responsedata.next_open_time)
                        .putExtra("SELECTED_TYPE", "" + selectedTYpe)
                        .putExtra("IS_CUTLERY", "" + isCutlery)
                        .putExtra("ADDRESS_ID", addressId)
                        .putExtra("MANGAL_PRICE", pojo.responsedata.mangal_all_item_total)
                        .putExtra("is_mangal_cart", mangalPay));
            }
        });

        binding.back.setOnClickListener(view -> {
            if(cartSize == 0) {
                startActivity(new Intent(activity, Home_New.class));
            }else {
                finish();
            }
        });

        binding.backMainMenu.setOnClickListener(v -> binding.back.performClick());

        getCartList(false,mangalPay);

    }


    private void getCartList(boolean progress,String payWithMangalPrice) {
        if (!progress) {
            Utils.showProgress(activity);
        }
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().cartList(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                storeUserData.getString(Constants.CART_ID),
                payWithMangalPrice,
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
                    Log.i("TAG", "GetCartList: " + response);
                    Reader reader = new StringReader(response);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    pojo = gson.fromJson(reader, CartListPojo.class);

                    if (pojo.status == 1) {

                        if(payWithMangalPrice.equalsIgnoreCase("1")){
                            binding.llMangalPrice.setVisibility(View.VISIBLE);
                            binding.mangalsPrice.setText(""+pojo.responsedata.mangal_all_item_total);
                        }else {
                            binding.llMangalPrice.setVisibility(View.GONE);
                            binding.mangalsPrice.setText("0");
                        }

                        if (pojo.responsedata.is_mangals_cart == 1){
                            binding.llMangalSwitch.setVisibility(View.VISIBLE);
                        }else {
                            binding.llMangalSwitch.setVisibility(View.GONE);
                        }

                        cartSize =  pojo.responsedata.items.size();
                        binding.llEmptyCart.setVisibility(View.GONE);
                        binding.llMain.setVisibility(View.VISIBLE);
                        binding.rvCart.setAdapter(new CartListAdapter(activity, pojo.responsedata.items, itemClickListener));
                        binding.totalPrice.setText(storeUserData.getString( Constants.CURRENCY )+" " + pojo.responsedata.total_pay_amount);
                        binding.amount.setText(storeUserData.getString( Constants.CURRENCY )+" " +pojo.responsedata.order_total);
                        binding.deliverycharge.setText(storeUserData.getString( Constants.CURRENCY )+" " +pojo.responsedata.delivery_charge);
                        storeUserData.setString(Constants.ORDER_TYPE, pojo.responsedata.order_type);

                        binding.address.setText(pojo.responsedata.address);
                        if(pojo.responsedata.address_id.equalsIgnoreCase("0")) {
                            addressId = "";
                        }else {
                            addressId = pojo.responsedata.address_id;
                        }

                        if (storeUserData.getString(Constants.ORDER_TYPE).length() > 0) {
                            if (storeUserData.getString(Constants.ORDER_TYPE).equalsIgnoreCase("delivery")) {
                                selectItem(1);
                            } else {
                                selectItem(0);
                                addressId = "";
                            }
                        }

                        if (pojo.responsedata.res_is_open == 0){
                            binding.llCheckOut.setVisibility(View.GONE);
                            binding.ord.setVisibility(View.VISIBLE);
                            binding.ord.setText(pojo.responsedata.res_open_error);
                        }else{
                            binding.llCheckOut.setVisibility(View.VISIBLE);
                            binding.ord.setVisibility(View.GONE);
                        }

                    } else {
                        cartSize = 0;
                        binding.llCheckOut.setVisibility(View.GONE);
                        binding.llEmptyCart.setVisibility(View.VISIBLE);
                        binding.llMain.setVisibility(View.GONE);
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

    private void changeQty(int cartItemId, int qty) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().changeQuantity(
                new StoreUserData(activity).getString(Constants.USER_ID),
                new StoreUserData(activity).getString(Constants.TOKEN),
                "" + cartItemId,
                storeUserData.getString(Constants.CART_ID),
                "" + qty
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
                    Log.i("TAG", "changeQty: " + response);

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("responsedata");
                        if (jsonObject1.getString("order_total").equalsIgnoreCase("0")) {
                            storeUserData.setString(Constants.CART_ID, "");
                        }
                        getCartList(true,mangalPay);
                    }

                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.e("ERROR", error);
                Utils.dismissProgress();
            }
        });
    }

    public void selectItem(int type) {

        binding.imgPickup.setImageResource(R.drawable.radio_blank);
        binding.imgDelivery.setImageResource(R.drawable.radio_blank);

        if (type == 0) {
            binding.imgPickup.setImageResource(R.drawable.radio_on);
            binding.llChangeAddress.setVisibility(View.GONE);
            binding.llAddress.setVisibility(View.GONE);
            selectedTYpe = 0;
        } else if (type == 1) {

            binding.imgDelivery.setImageResource(R.drawable.radio_on);
            binding.llChangeAddress.setVisibility(View.VISIBLE);
            selectedTYpe = 1;

            if (storeUserData.getString(Constants.ORDER_TYPE).equalsIgnoreCase("pickup")){
                binding.llAddress.setVisibility(View.GONE);
                binding.ad.setText(Constants.SELECT_ADDRESS);
            }else {
                binding.ad.setText(Constants.CHANGE_ADDRESS);
                binding.llAddress.setVisibility(View.VISIBLE);
            }
        }
    }

    public interface ItemClickListener {
        void onClick(int cartItemId, int qty);
    }
}
