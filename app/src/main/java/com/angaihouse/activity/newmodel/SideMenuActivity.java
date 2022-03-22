package com.angaihouse.activity.newmodel;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.activity.BrowserActivity;
import com.angaihouse.activity.LoginActivity;
import com.angaihouse.activity.PrivacyPolicyActivity;
import com.angaihouse.activity.TableBookingActivity;
import com.angaihouse.activity.TermsAndConditionActivity;
import com.angaihouse.databinding.ActivitySideMenuBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SideMenuActivity extends AppCompatActivity {

    StoreUserData storeUserData;
    AppCompatActivity activity;
    ActivitySideMenuBinding binding;
    private String LAST_SELECTED_LANGUAGE = "2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_side_menu);

        binding.myCart.setText(Constants.MY_CART.toUpperCase());
        binding.orderHistory.setText(Constants.ORDER_HISTORY.toUpperCase());
        binding.myMangal.setText(Constants.SPECIAL_OFFERS.toUpperCase());
        binding.where.setText(Constants.WHERE_WE_ARE.toUpperCase());
        binding.customerService.setText(Constants.CUSTOMER_SERVICE.toUpperCase());
        binding.setting.setText(Constants.SETTING.toUpperCase());
        binding.shareAndEarn.setText(Constants.SHARE_AND_EARN.toUpperCase());
        binding.notification.setText(Constants.NOTIFICATION.toUpperCase());


        if (storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")) {
            binding.logout.setText(Constants.LOGIN.toUpperCase());
        }else {
            binding.logout.setText(Constants.LOGOUT.toUpperCase());
        }

        binding.termsAndCondition.setText(Constants.TERMS_AND_CONDITIONS.toUpperCase());
        binding.privacyPolicy.setText(Constants.PRIVACY_POLICY.toUpperCase());
        binding.llHome.setText(Constants.HOME.toUpperCase());

        if (storeUserData.getString(Constants.terms).equalsIgnoreCase("1")) {
            binding.termsAndCondition.setVisibility(View.VISIBLE);
        }

        if (storeUserData.getString(Constants.policy).equalsIgnoreCase("1")) {
            binding.privacyPolicy.setVisibility(View.VISIBLE);
        }

        if (storeUserData.getString(Constants.where_we_are).equalsIgnoreCase("1")) {
            binding.where.setVisibility(View.VISIBLE);
        }

        if (storeUserData.getString(Constants.instagram_share).equalsIgnoreCase("1")) {
            binding.instagram.setVisibility(View.VISIBLE);
        }

        if (storeUserData.getString(Constants.facebook_share).equalsIgnoreCase("1")) {
            binding.facebook.setVisibility(View.VISIBLE);
        }

        if (storeUserData.getString(Constants.mangal_link).equalsIgnoreCase("1")) {
            binding.webShare.setVisibility(View.VISIBLE);
        }

        if (storeUserData.getString(Constants.telegram_share).equalsIgnoreCase("1")) {
            binding.generalShare.setVisibility(View.VISIBLE);
        }

        binding.where.setOnClickListener(view -> {
            startActivity(new Intent(activity, MapActivity.class)
                    .putExtra("WHEREWEARE", "WHEREWEARE")
            );
            finish();
        });

        binding.closeActivity.setOnClickListener(view -> finish());

        binding.orderHistory.setOnClickListener(view -> {

            if (storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")) {
                Utils.guestLogin(activity);
            } else {
                startActivity(OrderHistoryActivity.class);
            }

        });

        binding.myCart.setOnClickListener(view -> {
            if (storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")) {
                Utils.guestLogin(activity);
            } else {
                startActivity(CartNewActivity.class);
            }
        });

        binding.customerService.setOnClickListener(view -> {
            if (storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")) {
                Utils.guestLogin(activity);
            } else {
                startActivity(ContactSupportActivity.class);
            }

        });

        binding.setting.setOnClickListener(view -> {
            startActivity(SettingActivity.class);
        });

        binding.shareAndEarn.setOnClickListener(view -> {

            if (storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")) {
                Utils.guestLogin(activity);
            } else {
                startActivity(ShareAndEarnActivity.class);
            }

        });

        binding.privacyPolicy.setOnClickListener(view -> {
            startActivity(new Intent(activity, PrivacyPolicyActivity.class));
        });

        binding.notification.setOnClickListener(view -> {
            startActivity(new Intent(activity, NotificationActivity.class));
        });

        binding.termsAndCondition.setOnClickListener(view -> {
            startActivity(new Intent(activity, TermsAndConditionActivity.class));
        });

        binding.myMangal.setOnClickListener(view -> {
            if (storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")) {
                Utils.guestLogin(activity);
            } else {
                startActivity(MyOfferActivity.class);
            }

        });

        binding.llHome.setOnClickListener(view -> finish());

        binding.instagram.setOnClickListener(view -> {
            openBrowserActivity(BrowserActivity.class, storeUserData.getString(Constants.INSTA_URL));
        });

        binding.facebook.setOnClickListener(view -> {
            openBrowserActivity(BrowserActivity.class, storeUserData.getString(Constants.FB_URL));
        });

        binding.webShare.setOnClickListener(view -> {
            openBrowserActivity(BrowserActivity.class, storeUserData.getString(Constants.MANGAL_URL));
        });

        binding.youtube.setOnClickListener(view -> {
            openBrowserActivity(BrowserActivity.class, storeUserData.getString(Constants.YOUTUBE_URL));
        });

        binding.generalShare.setOnClickListener(view -> {
            openBrowserActivity(BrowserActivity.class, storeUserData.getString(Constants.TELEGRAM_URL));
        });

        binding.logout.setOnClickListener(view -> {

            if (storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")) {
                Utils.guestLogin(activity);
            }else {
                new AlertDialog.Builder(activity)
                        .setMessage(Constants.LOGOUT_CONFIRM)
                        .setPositiveButton(Constants.YES_LABEL, (dialog, which) -> {
                            logout();
                        })
                        .setNegativeButton(Constants.NO_LABEL, null)
                        .show();
            }
        });
    }

    public void startActivity(Class activityName) {
        startActivity(new Intent(activity, activityName));
        finish();
    }


    public void openBrowserActivity(Class activityName, String url) {
        startActivity(new Intent(activity, activityName)
                .putExtra("paymentUrl", url));
        finish();
    }

    private void logout() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().logout(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                storeUserData.getString(Constants.USER_FCM),
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
                    Log.i("TAG", "Logout: " + response);
                    String email = storeUserData.getString(Constants.email);
                    LAST_SELECTED_LANGUAGE = storeUserData.getString(Constants.APP_LANGUAGE);
                    storeUserData.clearData(activity);
                    storeUserData.setString(Constants.email, email);
                    storeUserData.setString(Constants.APP_LANGUAGE,LAST_SELECTED_LANGUAGE);
                    startActivity(new Intent(activity, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));

                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.e("ERROR_LOGOUT", error);
                Utils.dismissProgress();
            }
        });
    }
}
