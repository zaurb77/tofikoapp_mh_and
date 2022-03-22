package com.angaihouse.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityCookiesPolicyBinding;
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

public class CookiesPolicy extends AppCompatActivity {

    ActivityCookiesPolicyBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity,R.layout.activity_cookies_policy);
        binding.back.setOnClickListener(view -> {finish();});

        binding.title.setText(Constants.COOKIES);
        binding.textTv.setTextColor(Color.parseColor("#000000"));

        getPageApi(getString(R.string.cookie));
    }

    //TODO : GET COOKIES POLICY FROM WEB
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
