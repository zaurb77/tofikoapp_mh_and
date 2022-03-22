package com.angaihouse.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityContactUsBinding;
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

public class ContactUsActivity extends AppCompatActivity {

    ActivityContactUsBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_contact_us);
        binding.back.setOnClickListener(view -> finish());


        binding.name.setText(storeUserData.getString(Constants.USER_FNAME)+" "+storeUserData.getString(Constants.USER_LNAME));
        binding.emailAddress.setText(storeUserData.getString(Constants.email));
        binding.contactNo.setText(storeUserData.getString(Constants.mobile_no));


        binding.submit.setOnClickListener(view -> {

            if (Utils.isEmpty(binding.name)) {
                Utils.showTopMessageError(activity, Constants.PROVIDE_NAME);
            } else if (Utils.isEmpty(binding.emailAddress)) {
                Utils.showTopMessageError(activity, Constants.PROVIDE_EMAIL);
            } else if (Utils.isEmpty(binding.contactNo)) {
                Utils.showTopMessageError(activity, Constants.PROVIDE_CONTACT_NO);
            } else if (Utils.isEmpty(binding.message)) {
                Utils.showTopMessageError(activity, Constants.PROVIDE_MSG);
            } else {
                sendContactUs();
            }
        });
    }

    private void sendContactUs() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

       call = retrofitHelper.api().contactUs(

                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                binding.message.getText().toString(),
                binding.name.getText().toString(),
                binding.emailAddress.getText().toString(),
                binding.contactNo.getText().toString()

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
                    Log.i("TAG", "ContactUsSend: " + response);
                    JSONObject object = new JSONObject(response);

                    if (object.getInt("status") == 1){
                        Utils.showTopMessageSuccess(activity,object.getString("message"));
                        binding.name.setText("");
                        binding.contactNo.setText("");
                        binding.emailAddress.setText("");
                        binding.message.setText("");
                    }else{
                        Utils.showTopMessageError(activity,object.getString("message"));
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
}
