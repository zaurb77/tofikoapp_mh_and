package com.angaihouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityForgotPasswordBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.Utils;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;
    AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_forgot_password);
        binding.back.setOnClickListener(view -> finish());
        binding.title.setText(Constants.FORGOT_PASS);

        binding.forgotBtn.setText(Constants.SUBMIT);
        binding.email.setText(Constants.EMAIL_ADDRESS);

        binding.forgotBtn.setOnClickListener(view -> {
            if (Utils.isEmpty(binding.email)) {
                Utils.showTopMessageError(activity, Constants.PROVIDE_EMAIL);
            } else {
                if (!Utils.isValidEmail(binding.email)) {
                    Utils.showTopMessageError(activity, Constants.VAILD_EMAIL);
                } else {
                    forgotPassword();
                }
            }
        });

        binding.email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    binding.llEmail.setBackgroundResource(R.drawable.round_light_yeallow_border);
                }
            }
        });

    }


    //TODO : FORGOT PASSWORD
    private void forgotPassword() {

        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().forgotPassword(
                binding.email.getText().toString().trim()
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
                    Log.i("FORGOT_PASS_RESPONSE", "onSuccess: " + response);
                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.getInt("status") == 1) {

                        JSONObject jsonObject1 = jsonObject.getJSONObject("responsedata");
                        startActivity(new Intent(activity,CodActivity.class)
                        .putExtra("CODE",jsonObject1.getString("otp"))
                        .putExtra("EMAIL_CODE",binding.email.getText().toString()));

                    }

                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
            }
        });
    }
}
