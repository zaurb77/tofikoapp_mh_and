package com.angaihouse.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityCompanyInformationBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class CompanyInformation extends AppCompatActivity {

    ActivityCompanyInformationBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_company_information);

        binding.title.setText(Constants.COMPANY_INFO);
        binding.backTv.setText(Constants.BACK);
        binding.next.setText("Next");


        binding.back1.setOnClickListener(view -> finish());


        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("updateInfo")) {

            binding.next.setText("Save");
            binding.companyName.setText(storeUserData.getString(Constants.company_name));
            binding.vatId.setText(storeUserData.getString(Constants.vat_id));
            binding.legalEmail.setText(storeUserData.getString(Constants.company_legal_email));
            binding.uniqueCode.setText(storeUserData.getString(Constants.unique_invoicing_code));
            binding.imgBg.setVisibility(View.INVISIBLE);
            binding.llMain1.setVisibility(View.GONE);

        }
        else {
            binding.llMail.setVisibility(View.GONE);


            binding.companyName.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    changeBg();
                    binding.llCompanyName.setBackgroundResource(R.drawable.round_light_yeallow_border);
                }
            });


            binding.vatId.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    changeBg();
                    binding.llVatId.setBackgroundResource(R.drawable.round_light_yeallow_border);
                }
            });

            binding.legalEmail.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    changeBg();
                    binding.llLegalEmail.setBackgroundResource(R.drawable.round_light_yeallow_border);
                }
            });


            binding.uniqueCode.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    changeBg();
                    binding.llUniqueCode.setBackgroundResource(R.drawable.round_light_yeallow_border);
                }
            });
        }

        binding.back.setOnClickListener(view -> finish());
        binding.next.setOnClickListener(view -> {

            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("updateInfo")) {

                if (Utils.isEmpty(binding.companyName)) {
                    Utils.showTopMessageError(activity, "Please enter your company name.");
                } else if (Utils.isEmpty(binding.vatId)) {
                    Utils.showTopMessageError(activity, "Please enter your vatId.");
                } else if (Utils.isEmpty(binding.legalEmail)) {
                    Utils.showTopMessageError(activity, "Please enter your email address.");
                } else if (Utils.isEmpty(binding.uniqueCode)) {
                    Utils.showTopMessageError(activity, "Please enter your invoice code.");
                } else {
                    if (!Utils.isValidEmail(binding.legalEmail)) {
                        Utils.showTopMessageError(activity, "Please enter a valid email address.");
                    } else {
                        updateCompany();
                    }
                }
            } else {
                if (Utils.isEmpty(binding.companyName)) {
                    Utils.showTopMessageError(activity, "Please enter your company name.");
                } else if (Utils.isEmpty(binding.vatId)) {
                    Utils.showTopMessageError(activity, "Please enter your vatId.");
                } else if (Utils.isEmpty(binding.legalEmail)) {
                    Utils.showTopMessageError(activity, "Please enter your email address.");
                } else if (Utils.isEmpty(binding.uniqueCode)) {
                    Utils.showTopMessageError(activity, "Please enter your invoice code.");
                } else {
                    updateCompany();
                }
            }
        });
    }


    // TODO : UPDATE COMPANY DATA
    private void updateCompany() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;
        call = retrofitHelper.api().updateUserCompany(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                Objects.requireNonNull(binding.legalEmail.getText()).toString(),
                Objects.requireNonNull(binding.uniqueCode.getText()).toString(),
                Objects.requireNonNull(binding.companyName.getText()).toString(),
                Objects.requireNonNull(binding.vatId.getText()).toString(),
                storeUserData.getString(Constants.company_id)
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
                    Log.i("TAG", "UpdateCompany: " + response);
                    JSONObject object = new JSONObject(response);


                    if (object.getInt("status") == 1) {

                        Utils.showTopMessageSuccess(activity, object.getString("message"));
                        JSONObject responseData = object.getJSONObject("responsedata");
                        storeUserData.setString(Constants.company_name, responseData.getString("company_name"));
                        storeUserData.setString(Constants.company_legal_email, responseData.getString("company_legal_email"));
                        storeUserData.setString(Constants.company_id, responseData.getString("company_id"));
                        storeUserData.setString(Constants.unique_invoicing_code, responseData.getString("unique_invoicing_code"));
                        storeUserData.setString(Constants.vat_id, responseData.getString("vat_id"));

                        new Handler().postDelayed(() -> finish(),2000);

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


    public void changeBg() {
        binding.llCompanyName.setBackgroundResource(R.drawable.round_green_border);
        binding.llVatId.setBackgroundResource(R.drawable.round_green_border);
        binding.llLegalEmail.setBackgroundResource(R.drawable.round_green_border);
        binding.llUniqueCode.setBackgroundResource(R.drawable.round_green_border);
    }

}
