package com.angaihouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivitySetPasswordBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.Utils;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SetPasswordActivity extends AppCompatActivity {

    ActivitySetPasswordBinding binding;
    AppCompatActivity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_set_password);

        binding.setPassword.setText(Constants.SET_PASSWORD);
        binding.newPasswordChange.setText(Constants.NEW_PASS);
        binding.confirmPasswordChange.setText(Constants.CONFIRM_PASS);
        binding.saveSetPassword.setText(Constants.SAVE);


        binding.backSetPassword.setOnClickListener(view -> finish());


        binding.newPasswordChange.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.newPasswordChange.setBackgroundResource(R.drawable.round_light_yeallow_border);
                binding.confirmPasswordChange.setBackgroundResource(R.drawable.round_green_border);
            }
        });


        binding.confirmPasswordChange.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.confirmPasswordChange.setBackgroundResource(R.drawable.round_light_yeallow_border);
                binding.newPasswordChange.setBackgroundResource(R.drawable.round_green_border);
            }
        });



        binding.saveSetPassword.setOnClickListener(view -> {
            if (Utils.isEmpty(binding.newPasswordChange)) {
                Utils.showTopMessageError(activity, "Please enter your new password.");
            } else if (Utils.isEmpty(binding.confirmPasswordChange)) {
                Utils.showTopMessageError(activity, "Please enter your confirm password.");
            } else {
                if (Objects.requireNonNull(binding.newPasswordChange.getText()).toString().equalsIgnoreCase(Objects.requireNonNull(binding.confirmPasswordChange.getText()).toString())) {
                    setPassword();
                } else {
                    Utils.showTopMessageError(activity, "your password not matched please enter a valid password.");
                }
            }
        });
    }


    private void setPassword() {

        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().resetPassword(
                getIntent().getStringExtra("EMAIL_CODE"),
                binding.confirmPasswordChange.getText().toString().trim()
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
                    Log.i("SET_PASSWORD", "onSuccess: " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        startActivity(new Intent(activity,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
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
