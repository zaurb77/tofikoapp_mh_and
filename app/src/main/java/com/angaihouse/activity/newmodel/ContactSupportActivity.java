package com.angaihouse.activity.newmodel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.angaihouse.R;
import com.angaihouse.activity.ContactUsActivity;
import com.angaihouse.databinding.ActivityContactSupportBinding;
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

import adapter.QuestionAnsAdapter;
import okhttp3.ResponseBody;
import pojo.QuestionAnsPojo;
import retrofit2.Call;
import retrofit2.Response;

public class ContactSupportActivity extends AppCompatActivity {

    StoreUserData storeUserData;
    AppCompatActivity activity;
    ActivityContactSupportBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity,R.layout.activity_contact_support);

        binding.tvContactSupport.setText(Constants.CONTACT_SUPPORT);
        binding.frequentlyQuestion.setText(Constants.FAQ);
        binding.tvStillHaveQuestion.setText(Constants.STILL_QUESTIONS.toUpperCase());


        binding.llContactUS.setOnClickListener(view -> startActivity(new Intent(activity, ContactUsActivity.class)));

        binding.back.setOnClickListener(view -> finish());

        binding.rvQuestion.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvQuestion.setNestedScrollingEnabled(false);
        binding.rvQuestion.setHasFixedSize(true);

        getQuestions();

    }


    private void getQuestions() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().questionList(
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
                    Log.i("TAG", "getQuestion:" + response);
                    Reader reader = new StringReader(response);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("status") == 1){
                        QuestionAnsPojo list = gson.fromJson(reader, QuestionAnsPojo.class);
                        binding.rvQuestion.setAdapter(new QuestionAnsAdapter(activity,list.responsedata));
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
}
