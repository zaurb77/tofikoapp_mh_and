package com.angaihouse.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityPrivacyPolicyBinding;
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

public class PrivacyPolicyActivity extends AppCompatActivity {


    ActivityPrivacyPolicyBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("LOGIN_ACTIVITY")){
            setTheme(R.style.SplashScreenTheme);
        }
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);

        binding = DataBindingUtil.setContentView(activity,R.layout.activity_privacy_policy);
        binding.back.setOnClickListener(view -> finish());
        binding.back1.setOnClickListener(view -> finish());
        binding.title.setText(Constants.PRIVACY_POLICY);
        binding.title1.setText(Constants.PRIVACY_POLICY);

        if(!storeUserData.getString(Constants.USER_ID).isEmpty()) {
            whiteTheme();
            binding.textTv.setTextColor(Color.parseColor("#000000"));
        }else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("LOGIN_ACTIVITY")){
            blackTheme();
            binding.textTv.setTextColor(Color.parseColor("#D3AB5B"));
        }else  if (storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")){
            whiteTheme();
            binding.textTv.setTextColor(Color.parseColor("#000000"));
        }
        getPageApi(getString(R.string.policy));

    }


    public void blackTheme(){
        binding.backImage.setVisibility(View.VISIBLE);
        binding.top1.setVisibility(View.VISIBLE);
        binding.top2.setVisibility(View.GONE);
    }

    public void whiteTheme(){
        binding.backImage.setVisibility(View.GONE);
        binding.top1.setVisibility(View.GONE);
        binding.top2.setVisibility(View.VISIBLE);
    }


    //TODO : GET POLICY FROM WEB
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
                    String response = body.body().string();
                    Log.i("TAG", "onSuccess: " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1){
                        JSONObject jsonObject1 = jsonObject.getJSONObject("responsedata");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding.textTv.setText(Html.fromHtml(jsonObject1.getString("content"), Html.FROM_HTML_MODE_COMPACT));
                        }else {
                            binding.textTv.setText(Html.fromHtml(jsonObject1.getString("content")));
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
}
