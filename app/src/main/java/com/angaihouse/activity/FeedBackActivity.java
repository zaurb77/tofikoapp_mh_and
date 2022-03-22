package com.angaihouse.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityFeedBackBinding;
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

public class FeedBackActivity extends AppCompatActivity {

    ActivityFeedBackBinding binding;
    AppCompatActivity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_feed_back);
        binding.back.setOnClickListener(v -> finish());

        binding.submit.setOnClickListener(view -> {
            if (binding.rating.getRating()<0){
                Utils.showTopMessageError(activity,Constants.SELECT_RATING_TYPE);
            }else if (Utils.isEmpty(binding.message)){
                Utils.showTopMessageError(activity,Constants.ENTER_COMMENT);
            }else {
                addReview();
            }
        });
    }


    //TODO : ADD REVIEW
    private void addReview() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().addReview(
                new StoreUserData(activity).getString(Constants.USER_ID),
                new StoreUserData(activity).getString(Constants.TOKEN),
                getIntent().getStringExtra("CART_ID"),
                ""+binding.rating.getRating(),
                binding.message.getText().toString()
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
                    Log.i("AddRating", "" + response);
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("status") == 1) {
                        Utils.showTopMessageSuccess(activity,jsonObject.getString("message"));
                        new Handler().postDelayed(() -> finish(),2000);
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
}
