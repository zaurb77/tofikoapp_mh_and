package com.angaihouse.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityPaymentInfoScreenBinding;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import adapter.CardListAdapter;
import okhttp3.ResponseBody;
import pojo.CardListPojo;
import retrofit2.Call;
import retrofit2.Response;

public class PaymentInfoScreen extends AppCompatActivity {

    ActivityPaymentInfoScreenBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;
    String paymentType = "",deliveryType = "Now",SELECTED_CARD_ID = "";
    int isInvoice = 0;
    private ItemClickListener itemClickListener;
    CardListPojo pojo;
    CardListAdapter cardListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_payment_info_screen);

        binding.tvAddInvoice.setText(Constants.ADD_INVOICE);
        binding.resName.setText(Constants.RES_NAME);
        binding.name.setText(Constants.NAME);
        binding.address.setText(Constants.ADDRESS);
        binding.mobileNo.setText(Constants.MOBILE_NO);
        binding.restaurantMobileNo.setText(Constants.RES_NUMBER);
        binding.tvChooseDeliveryTime.setText(Constants.CHOOSE_DELIVERY_TIME);
        binding.creditAndDebitCard.setText(Constants.CREDIT_DEBIT_CARD);
        binding.tvAddNewCard.setText(Constants.ADD_NEW_CARD);
        binding.cashOnDelivery.setText(Constants.CASH_ON_DELIVERY);
        binding.tvCash.setText(Constants.CASH);
        binding.tvKeepCash.setText(Constants.KEEP_CASH_ON_HAND);
        binding.tvPayOnDelivery.setText(Constants.PAY_ON_DELIVERY);
        binding.tvOrder.setText(Constants.ORDER);
        binding.tvPaypal.setText(Constants.PAYPAL);
        binding.tvItemTotal.setText(Constants.ITEM_TOTAL);
        binding.tvDeliveryCharge.setText(Constants.DELIVERY_CHARGE);
        binding.tvTotalPayable.setText(Constants.TOTAL_PAYABLE_AMT);

        binding.resNameTV.setText(getIntent().getStringExtra("RES_NAME"));
        binding.nameTv.setText(getIntent().getStringExtra("USER_NAME"));
        binding.mobileNoTV.setText(getIntent().getStringExtra("MOBILE_NO"));
        binding.addressTv.setText(getIntent().getStringExtra("ADDRESS"));
        binding.restaurantMobileNoTV.setText(getIntent().getStringExtra("RESTAURANT_MOBILE_NO"));

        if (Objects.requireNonNull(getIntent().getStringExtra("is_mangal_cart")).equalsIgnoreCase("1")){
            binding.llMangalPrice.setVisibility(View.VISIBLE);
            binding.mangalsPrice.setText(getIntent().getStringExtra("MANGAL_PRICE"));
        }else {
            binding.llMangalPrice.setVisibility(View.GONE);
        }

        if (Objects.requireNonNull(getIntent().getStringExtra("res_is_open")).equalsIgnoreCase("2")) {
            binding.llNow.setVisibility(View.GONE);
        } else {
            binding.llNow.setVisibility(View.VISIBLE);
        }

        binding.llLater.setVisibility(View.VISIBLE);

        binding.invoice.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                isInvoice = 1;
                if (!TextUtils.isEmpty(storeUserData.getString(Constants.company_id)) && storeUserData.getString(Constants.company_id).equalsIgnoreCase("0")){
                    binding.addCompany.setVisibility(View.VISIBLE);
                    binding.addCompany.setText("ADD");

                }else {
                    binding.addCompany.setText("CHANGE");
                    binding.addCompany.setVisibility(View.VISIBLE);
                }
            }else {
                isInvoice = 0;
                binding.addCompany.setVisibility(View.INVISIBLE);
            }
        });

        binding.addCompany.setOnClickListener(view ->
                startActivity(new Intent(activity,CompanyInformation.class)
                        .putExtra("updateInfo","updateInfo")
                )
        );

        if (Objects.requireNonNull(getIntent().getStringExtra("SELECTED_TYPE")).equalsIgnoreCase("0")) {
            binding.llAddress.setVisibility(View.GONE);
            storeUserData.setString(Constants.ORDER_TYPE, "pickup");
        } else {
            storeUserData.setString(Constants.ORDER_TYPE, "delivery");
        }

        binding.back.setOnClickListener(view -> finish());
        binding.amount.setText(storeUserData.getString( Constants.CURRENCY )+" " + getIntent().getStringExtra("totalAmount"));
        binding.deliverycharge.setText(storeUserData.getString( Constants.CURRENCY )+" " + getIntent().getStringExtra("deliveryCharges"));

        String totalPayble = getIntent().getStringExtra("totalPayAmount");

        binding.totalPayable.setText(storeUserData.getString( Constants.CURRENCY )+" " + totalPayble);

        itemClickListener = Id -> {
            paymentType = Id;
            SELECTED_CARD_ID = Id;
            selectPaymentType(-1);
        };


        binding.llAddCard.setOnClickListener(view -> startActivity(new Intent(activity, AddNewCardActivity.class)));
        binding.llNow.setOnClickListener(view -> selectItem(0));
        binding.llLater.setOnClickListener(view -> selectItem(1));

        binding.llCod.setOnClickListener(view -> selectPaymentType(0));

        binding.llPaypal.setOnClickListener(view -> selectPaymentType(1));

        binding.llBancomat.setOnClickListener(v -> selectPaymentType(2));

        binding.llSatisPay.setOnClickListener(v -> selectPaymentType(3));

        binding.rvCard.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvCard.setNestedScrollingEnabled(false);
        binding.rvCard.setHasFixedSize(true);

        binding.submit.setOnClickListener(view -> {
            //TODO : IN PROGRESS
            if (!TextUtils.isEmpty(storeUserData.getString(Constants.company_id)) && storeUserData.getString(Constants.company_id).equalsIgnoreCase("0") && isInvoice == 1){
                Utils.showTopMessageError(activity,Constants.ENTER_COMPANY_INFO);
            }else {
                if (paymentType.isEmpty()) {
                    Utils.showTopMessageError(activity, Constants.PAYMENT_TYPE);
                } else if (deliveryType.equalsIgnoreCase("Empty")) {
                    Utils.showTopMessageError(activity, Constants.SELECT_TIME_ERROR);
                } else {

                    String type = null;

                    if (paymentType.equalsIgnoreCase("100")) {
                        type = "cod";
                    } else if (paymentType.equalsIgnoreCase("101")) {
                        type = "paypal";
                    } else if (paymentType.equalsIgnoreCase("102")) {
                        type = "bancomat";
                    } else if (paymentType.equalsIgnoreCase("103")) {
                        type = "satispay";
                    } else {
                        type = "stripe";
                    }

                    checkoutApi(type);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.addCompany.setVisibility(View.INVISIBLE);
        cardList();
    }


    private void cardList() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().cardList(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                storeUserData.getString(Constants.CUST_ID)
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
                    Log.i("TAG", "getCardLIST: " + response);
                    Reader reader = new StringReader(response);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                     pojo = gson.fromJson(reader, CardListPojo.class);

                    for (int i=0; i<pojo.responsedata.size();i++){
                        pojo.responsedata.get(i).selectedCard = -1;
                    }

                    if (pojo.status == 1) {

                        cardListAdapter = new CardListAdapter(activity,pojo.responsedata,itemClickListener);
                        binding.rvCard.setAdapter(cardListAdapter);
                    }

                    binding.llMain.setVisibility(View.VISIBLE);
                    binding.submit.setVisibility(View.VISIBLE);

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

    public void selectItem(int selectType)  {
        binding.imgNow.setImageResource(R.drawable.on);
        binding.imgLater.setImageResource(R.drawable.on);
        if (selectType == 0) {

            binding.imgNow.setImageResource(R.drawable.off);
            binding.selectTime.setVisibility(View.INVISIBLE);
            binding.selectTime.setText("");
            deliveryType = "Now";

        } else if (selectType == 1) {

            binding.imgLater.setImageResource(R.drawable.off);
            binding.selectTime.setVisibility(View.VISIBLE);
            deliveryType = "Later";
            String time = getIntent().getStringExtra("time");

            if (Objects.requireNonNull(getIntent().getStringExtra("res_is_open")).equalsIgnoreCase("2")) {

                ArrayList<String> arrayList = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();

                Calendar calendar1 = Calendar.getInstance();
                SimpleDateFormat formatter1 = new SimpleDateFormat("dd/M/yyyy hh:mm");
                String currentDate = formatter1.format(calendar1.getTime());

                String[] arrCurrentDate = currentDate.split(" ");
                String dtStart = arrCurrentDate[0] + " " + time + "";

                Date openDate;
                int all = 0;
                Long t = Long.valueOf(0);
                try {
                    Date curDate = formatter1.parse(currentDate);
                    openDate = formatter1.parse(dtStart);
                    if(curDate.compareTo(openDate)>=0) {
                        long duration  = curDate.getTime() - openDate.getTime();
                        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                        Log.i("diffInMin", diffInMinutes + "");
                        if(diffInMinutes >= 40) {
                            all = 0;
                            t = calendar.getTimeInMillis();
                        }else {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(openDate);
                            cal.add(Calendar.MINUTE, 40);
                            t = cal.getTimeInMillis();
                        }
                    }else {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(openDate);
                        cal.add(Calendar.MINUTE, 40);
                        t = cal.getTimeInMillis();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat dt = new SimpleDateFormat("hh:mm");
                Date dt2 = null;
                try {
                    dt2 = dt.parse(Objects.requireNonNull(time));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                calendar.setTimeInMillis(Objects.requireNonNull(dt2).getTime());

                long timeInMin = TimeUnit.MILLISECONDS.toMinutes(t);
                for(int i = 0; i< 5; i++) {
                    if(timeInMin % 5 == 0) {
                        break;
                    }
                    timeInMin += 1;
                }
                long t1 = TimeUnit.MINUTES.toMillis(timeInMin);

                for (int i = 0; i < 12; i++) {
                    Date afterAddingTenMins = new Date(t1 + all * 60000);
                    SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
                    String time1 = sdf.format(afterAddingTenMins);
                    arrayList.add(time1);
                    all += 5;
                }

                Utils.showListDialog(activity, binding.selectTime, arrayList);

            } else if (Objects.requireNonNull(getIntent().getStringExtra("res_is_open")).equalsIgnoreCase("1")) {

                ArrayList<String> arrayList = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();
                int first = 40;
                int all = 5;

                long t = calendar.getTimeInMillis();
                long timeInMin = TimeUnit.MILLISECONDS.toMinutes(t);

                for(int i = 0; i< 5; i++) {
                    if(timeInMin % 5 == 0) {
                        break;
                    }
                    timeInMin += 1;
                }
                long t1 = TimeUnit.MINUTES.toMillis(timeInMin);

                for (int i = 0; i < 12; i++) {
                    Date afterAddingTenMins = new Date(t1 + all * 60000);
                    SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
                    String time1 = sdf.format(afterAddingTenMins);
                    arrayList.add(time1);
                    all += 5;
                }
                Utils.showListDialog(activity, binding.selectTime, arrayList);
            }
        }
    }

    public void selectPaymentType(int selectType) {

        binding.imgCod.setImageResource(R.drawable.radio_blank);
        binding.imgPaypal.setImageResource(R.drawable.radio_blank);
        binding.imgBancomat.setImageResource(R.drawable.radio_blank);
        binding.imgSatispay.setImageResource(R.drawable.radio_blank);

        if (selectType == 0) {

            binding.imgCod.setImageResource(R.drawable.select_item);
            for (int i=0; i<pojo.responsedata.size();i++){
                if (pojo.responsedata.get(i).selectedCard != -1){
                    pojo.responsedata.get(i).selectedCard = -1;
                    cardListAdapter.notifyDataSetChanged();
                }
            }

            paymentType = "100";

        } else if (selectType == 1) {

            binding.imgPaypal.setImageResource(R.drawable.select_item);
            for (int i=0; i<pojo.responsedata.size();i++){
                if (pojo.responsedata.get(i).selectedCard != -1){
                    pojo.responsedata.get(i).selectedCard = -1;
                    cardListAdapter.notifyDataSetChanged();
                }
            }
            paymentType = "101";

        }

        else if (selectType == 2) {

            binding.imgBancomat.setImageResource(R.drawable.select_item);
            for (int i=0; i<pojo.responsedata.size();i++){
                if (pojo.responsedata.get(i).selectedCard != -1){
                    pojo.responsedata.get(i).selectedCard = -1;
                    cardListAdapter.notifyDataSetChanged();
                }
            }
            paymentType = "102";

        }


        else if (selectType == 3) {

            binding.imgSatispay.setImageResource(R.drawable.select_item);
            for (int i=0; i<pojo.responsedata.size();i++){
                if (pojo.responsedata.get(i).selectedCard != -1){
                    pojo.responsedata.get(i).selectedCard = -1;
                    cardListAdapter.notifyDataSetChanged();
                }
            }
            paymentType = "103";
        }

        else {
            binding.imgCod.setImageResource(R.drawable.radio_blank);
            binding.imgPaypal.setImageResource(R.drawable.radio_blank);
            binding.imgBancomat.setImageResource(R.drawable.radio_blank);
            binding.imgSatispay.setImageResource(R.drawable.radio_blank);

        }
    }

    private void checkoutApi(String pType) {

        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;
        String delId;

        if (storeUserData.getString(Constants.ORDER_TYPE).equalsIgnoreCase("delivery")) {
            delId = getIntent().getStringExtra("ADDRESS_ID");
        } else {
            delId = "";
        }

        if (deliveryType.equalsIgnoreCase("Later")) {

            call = retrofitHelper.api().checkout(
                    storeUserData.getString(Constants.USER_ID),
                    storeUserData.getString(Constants.TOKEN),
                    pType,
                    storeUserData.getString(Constants.CART_ID),
                    deliveryType,
                    getIntent().getStringExtra("note"),
                    binding.selectTime.getText().toString(),
                    storeUserData.getString(Constants.ORDER_TYPE),
                    ""+isInvoice,
                    getIntent().getStringExtra("IS_CUTLERY"),
                    delId,
                    getIntent().getStringExtra("is_mangal_cart"),
                    SELECTED_CARD_ID
            );
        } else {

            call = retrofitHelper.api().checkout(
                    storeUserData.getString(Constants.USER_ID),
                    storeUserData.getString(Constants.TOKEN),
                    pType,
                    storeUserData.getString(Constants.CART_ID),
                    deliveryType,
                    getIntent().getStringExtra("note"),
                    storeUserData.getString(Constants.ORDER_TYPE),
                    ""+isInvoice,
                    getIntent().getStringExtra("IS_CUTLERY"),
                    delId,
                    getIntent().getStringExtra("is_mangal_cart"),
                    SELECTED_CARD_ID
            );
        }

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
                    Log.i("TAG", "checkOut: " + response);
                    JSONObject object = new JSONObject(response);

                    if (object.getInt("status") == 1) {

                        if (pType.equalsIgnoreCase("stripe") || pType.equalsIgnoreCase("cod") || pType.equalsIgnoreCase("bancomat")) {
                            startActivity(new Intent(activity, ThankyouActivity.class));
                        } else {
                            JSONObject jsonObject = object.getJSONObject("responsedata");
                            if (jsonObject.getString("url").length() > 0) {
                                openWebPage(jsonObject.getString("url"));
                                finish();
                            } else {
                                startActivity(new Intent(activity, ThankyouActivity.class));
                            }
                        }
                    } else {
                        Utils.showTopMessageError(activity, object.getString("message"));
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    Utils.dismissProgress();
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
            }
        });
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public interface ItemClickListener {
        void setCardClick(String addressId);
    }
}