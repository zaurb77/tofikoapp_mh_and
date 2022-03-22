package com.angaihouse.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.activity.newmodel.Home_New;
import com.angaihouse.databinding.ActivityAcceptTermsAndConditionBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class AcceptTermsAndConditionActivity extends AppCompatActivity {

    ActivityAcceptTermsAndConditionBinding binding;

    AppCompatActivity activity;
    boolean check = false;
    StoreUserData storeUserData;
    String deviceId;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_accept_terms_and_condition);
        binding.back.setOnClickListener(view -> finish());
        binding.imgCheck.setImageResource(R.drawable.unchecked);

        binding.backTv.setText(Constants.BACK);
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(activity, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            storeUserData.setString(Constants.USER_FCM, newToken);
            Log.i("FCM_TOKEN", newToken);
        });

        getPageApi(getString(R.string.terms));

        binding.imgCheck.setOnClickListener(view -> {
            if (!check) {
                check = true;
                binding.imgCheck.setImageResource(R.drawable.checked);
                binding.register.setEnabled(true);
            } else {
                check = false;
                binding.imgCheck.setImageResource(R.drawable.unchecked);
                binding.register.setEnabled(false);
            }
        });

        binding.register.setOnClickListener(view -> {
            if (check) {
                registerApi();
            } else {
                Utils.showTopMessageError(activity, "Please select I agree to the continue and condition");
            }
        });
    }


    //TODO : GET ACCEPT TERMS AND CONDITION DATA
    private void getPageApi(String pageName) {
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call = retrofitHelper.api().staticPage(
                pageName,
                storeUserData.getString(Constants.company_id)
        );
        retrofitHelper.callApi(activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                try {
                    if (body.code() != 200) {
                        Utils.serverError(activity, body.code());
                        return;
                    }
                    assert body.body() != null;
                    String response = body.body().string();
                    Log.i("TAG", "onSuccess: " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("responsedata");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding.text.setText(Html.fromHtml(jsonObject1.getString("content"), Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            binding.text.setText(Html.fromHtml(jsonObject1.getString("content")));
                        }
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
                try {
                    Utils.dismissProgress();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //TODO : THIS IS USER REGISTER METHOD
    private void registerApi() {

        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().register(
                getIntent().getStringExtra("firstName"),
                getIntent().getStringExtra("lastName"),
                getIntent().getStringExtra("selectCountryCod"),
                getIntent().getStringExtra("phoneNumber"),
                getIntent().getStringExtra("emailAddress"),
                getIntent().getStringExtra("password"),
                getIntent().getStringExtra("imagePath"),
                storeUserData.getString(Constants.USER_FCM),
                getIntent().getStringExtra("referral_code"),
                Constants.DEVICE_TYPE
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
                    assert body.body() != null;
                    String response = body.body().string();
                    Log.i("TAG", "RegisterResponse: " + response);
                    JSONObject object = new JSONObject(response);

                    if (object.getInt("status") == 1) {

                        JSONObject responseData = object.getJSONObject("responsedata");
                        storeUserData.setString(Constants.USER_ID, responseData.getString("user_id"));
                        storeUserData.setString(Constants.USER_FNAME, responseData.getString("fname"));
                        storeUserData.setString(Constants.USER_LNAME, responseData.getString("lname"));
                        storeUserData.setString(Constants.USER_DOB, responseData.getString("dob"));
                        storeUserData.setString(Constants.COUNTRY_CODE, responseData.getString("country_code"));
                        storeUserData.setString(Constants.mobile_no, responseData.getString("mobile_no"));
                        storeUserData.setString(Constants.email, responseData.getString("email"));
                        storeUserData.setString(Constants.password, responseData.getString("password"));
                        storeUserData.setString(Constants.password, responseData.getString("password"));
                        storeUserData.setString(Constants.gender, responseData.getString("gender"));
                        storeUserData.setString(Constants.REFERRAL_CODE, responseData.getString("referral_code"));
                        storeUserData.setString(Constants.type, responseData.getString("type"));
                        storeUserData.setString(Constants.company_id, responseData.getString("company_id"));
                        storeUserData.setString(Constants.company_name, responseData.getString("company_name"));
                        storeUserData.setString(Constants.company_legal_email, responseData.getString("company_legal_email"));
                        storeUserData.setString(Constants.unique_invoicing_code, responseData.getString("unique_invoicing_code"));
                        storeUserData.setString(Constants.vat_id, responseData.getString("vat_id"));
                        storeUserData.setString(Constants.image, responseData.getString("image"));
                        storeUserData.setInt(Constants.ORDER_PUSH_NOTIFICATION, Integer.parseInt(responseData.getString("order_push_notification")));
                        storeUserData.setInt(Constants.ORDER_EMAIL_NOTIFICATION, Integer.parseInt(responseData.getString("order_email_notification")));
                        storeUserData.setInt(Constants.NEWS_PUSH_NOTIFICATION, Integer.parseInt(responseData.getString("news_push_notification")));
                        storeUserData.setInt(Constants.NEWS_EMAIL_NOTIFICATION, Integer.parseInt(responseData.getString("news_email_notification")));
                        storeUserData.setString(Constants.TOKEN, responseData.getString("token"));
                        storeUserData.setString(Constants.CART_ID, responseData.getString("cart_id"));


                        startActivity(new Intent(activity, Home_New.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));


                    } else {
                        Utils.showTopMessageError(activity, object.getString("message"));
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
}
