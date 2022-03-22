package com.angaihouse.activity.newmodel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.activity.BrowserActivity;
import com.angaihouse.activity.EditProfileActivity;
import com.angaihouse.activity.LoginActivity;
import com.angaihouse.activity.RestaurantListActivity;
import com.angaihouse.activity.SelectRestaurantActivity;
import com.angaihouse.activity.TableBookingActivity;
import com.angaihouse.databinding.ActivityHomeNewBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import adapter.CustomPagerAdapter;
import okhttp3.ResponseBody;
import pojo.GeneralPojo;
import retrofit2.Call;
import retrofit2.Response;

public class Home_New extends AppCompatActivity {

    final ArrayList<GeneralPojo.Banners> imageList = new ArrayList<>();
    ActivityHomeNewBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;
    AppUpdateManager appUpdateManager;

    CustomPagerAdapter sliderPagerAdapter;
    int page = 0, numberOfPages;
    GeneralPojo generalData;
    private Handler handlerSlider = new Handler();

    ItemClickListener itemClickListener;

    Runnable runnableSlider = new Runnable() {
        @Override
        public void run() {
            handlerSlider.postDelayed(runnableSlider, 5000);
            if (page > numberOfPages) {
                binding.viewPager.setCurrentItem(0);
                page = 0;
            } else {
                binding.viewPager.setCurrentItem(page++);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_home__new);
        Utils.getDay(activity,Calendar.getInstance().get(Calendar.DAY_OF_WEEK));



        Constants.BANNER_DELIVERY_TYPE = "";
        Constants.BANNER_ADDRESS_ID = "";

        //TODO : RUN TIME APP UPDATE
        appUpdateManager = AppUpdateManagerFactory.create(activity);
        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.
            }
        });


        sliderPagerAdapter = new CustomPagerAdapter(activity, imageList, itemClickListener);
        binding.viewPager.setAdapter(sliderPagerAdapter);

        binding.orderFromMenu.setOnClickListener(view -> {
            if (storeUserData.getString(Constants.CART_ID).length() == 0) {
                if (storeUserData.getString(Constants.ORDER_TYPE).length() == 0) {
                    startActivity(new Intent(activity, OrderMenuActivity.class));
                } else {
                    if (storeUserData.getString(Constants.ORDER_TYPE).equalsIgnoreCase("pickup")) {
                        startActivity(new Intent(activity, MapActivity.class));
                    } else {
                        startActivity(new Intent(activity, MenuActivity.class)
                                .putExtra("ORDERTYPE", "delivery")
                                .putExtra("RES_ID", storeUserData.getString(Constants.NEAR_RES_ID))
                        );
                    }
                }
            }else {
                startActivity(new Intent(activity, MenuActivity.class)
                        .putExtra("ORDERTYPE", storeUserData.getString(Constants.ORDER_TYPE))
                        .putExtra("RES_ID", storeUserData.getString(Constants.NEAR_RES_ID))
                );
            }
        });

        binding.imgPoweredBy.setOnClickListener(v -> startActivity(new Intent(activity, BrowserActivity.class).putExtra("url", storeUserData.getString(Constants.tofiko_url))));

        binding.llWhereWeAre.setOnClickListener(view -> startActivity(new Intent(activity, MapActivity.class).putExtra("WHEREWEARE", "WHEREWEARE")));

        binding.sideMenu.setOnClickListener(view -> startActivity(SideMenuActivity.class));

        binding.llOrderHistory.setOnClickListener(v -> {
            if (storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")) {
                Utils.guestLogin(activity);
            }else {
                startActivity(OrderHistoryActivity.class);
            }
        });

        binding.llMyOffer.setOnClickListener(v -> {
            if (storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")) {
                Utils.guestLogin(activity);
            }else {
                startActivity(MyOfferActivity.class);
            }
        });

        binding.llInviteFriends.setOnClickListener(v -> {
            if (storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")) {
                Utils.guestLogin(activity);
            }else {
                startActivity(ShareAndEarnActivity.class);
            }
        });

//        binding.llAccount.setOnClickListener(v -> {
//            if (storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")) {
//                Utils.guestLogin(activity);
//            }else {
//                startActivity(EditProfileActivity.class);
//            }
//        });

        binding.llAccount.setOnClickListener(v -> {
            if (storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")) {
                activity.startActivity(new Intent(activity, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }else {
                startActivity(EditProfileActivity.class);
            }
        });

        binding.llAboutUs.setOnClickListener(view -> startActivity(AboutUs_NewActivity.class));
        binding.llBookTable.setOnClickListener(view -> startActivity( SelectRestaurantActivity.class));

        binding.cart.setOnClickListener(view -> startActivity(CartNewActivity.class));

        binding.cart.setOnClickListener(v -> {
            if (storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")){
                startActivity(new Intent(activity, LoginActivity.class));
                finish();
            }else {
                startActivity(CartNewActivity.class);
            }
        });

        binding.viewPager.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem());
                handlerSlider.postDelayed(runnableSlider, 5000);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                handlerSlider.removeCallbacks(runnableSlider);
            }
            return false;
        });

        itemClickListener = pojo -> {
            if(!storeUserData.getString(Constants.ORDER_TYPE).equalsIgnoreCase("")) {
                addToCart(pojo.res_id, pojo.item_id);
            }else {
                activity.startActivity(new Intent(activity, OrderMenuActivity.class)
                        .putExtra("BANNER_ITEM_COMBO", "BANNER_ITEM_COMBO")
                        .putExtra("ITEM_ID", pojo.item_id)
                        .putExtra("BANNER_RESTAURANT_ID", pojo.res_id)
                );
            }
        };
    }

    public void startActivity(Class activityName){
        startActivity(new Intent(activity, activityName));
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.tvOrderFromMenu.setText(Constants.ORDER_FROM_MENU.toUpperCase());
        binding.tvInviteFriends.setText(Constants.INVITE_FRIENDS_LABEL.toUpperCase());
        binding.tvOrderHistory.setText(Constants.MY_ORDER_HISTORY.toUpperCase());
        binding.tvSpecialOffer.setText(Constants.SPECIAL_OFFERS.toUpperCase());
        binding.tvWhereWeAre.setText(Constants.WHERE_WE_ARE.toUpperCase());
        binding.tvAboutAs.setText(Constants.ABOUT_US.toUpperCase());
        binding.tvBookTable.setText(Constants.BOOK_A_TABLE.toUpperCase());
        if (storeUserData.getString( Constants.GUEST_LOGIN ).equalsIgnoreCase( "1" )){
            binding.tvMyAccount.setText(Constants.LOGIN.toUpperCase());
        }else {
            binding.tvMyAccount.setText(Constants.MY_ACCOUNT.toUpperCase());
        }

        handlerSlider.postDelayed(runnableSlider, 5000);

        if (storeUserData.getString(Constants.LAT_STRING).length() > 0 && storeUserData.getString(Constants.LNG_STRING).length() > 0) {
            getSettings(storeUserData.getString(Constants.LAT_STRING), storeUserData.getString(Constants.LNG_STRING));
        } else {
            getSettings("99.99", "99.99");
        }
    }

    private void getSettings(String lat, String lng) {
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().getAppSettings(
                lat,
                lng,
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                storeUserData.getString(Constants.CART_ID)
        );

        retrofitHelper.callApi(activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                try {
                    if (body.code() != 200) {
                        Utils.serverError(activity, body.code());
                        return;
                    }
                    String response = Objects.requireNonNull(body.body()).string();
                    Log.i("TAG", "GETSettings: " + response);
                    Reader reader = new StringReader(response);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    generalData = gson.fromJson(reader, GeneralPojo.class);

                    if (generalData.status == 1) {
                        storeUserData.setString(Constants.terms, generalData.responsedata.settings.terms);
                        storeUserData.setString(Constants.CURRENCY, generalData.responsedata.currency);
                        storeUserData.setString(Constants.policy, generalData.responsedata.settings.policy);
                        storeUserData.setString(Constants.cookie, generalData.responsedata.settings.cookie);
                        storeUserData.setString(Constants.offer_banner, generalData.responsedata.settings.offer_banner);
                        storeUserData.setString(Constants.youtube_channel, generalData.responsedata.settings.youtube_channel);
                        storeUserData.setString(Constants.mangal_link, generalData.responsedata.settings.mangal_link);
                        storeUserData.setString(Constants.facebook_channel, generalData.responsedata.settings.facebook_channel);
                        storeUserData.setString(Constants.instagram_channel, generalData.responsedata.settings.instagram_channel);
                        storeUserData.setString(Constants.telegram_channel, generalData.responsedata.settings.telegram_channel);
                        storeUserData.setString(Constants.facebook_share, generalData.responsedata.settings.facebook_share);
                        storeUserData.setString(Constants.instagram_share, generalData.responsedata.settings.instagram_share);
                        storeUserData.setString(Constants.linkedin_share, generalData.responsedata.settings.linkedin_share);
                        storeUserData.setString(Constants.whatsapp_share, generalData.responsedata.settings.whatsapp_share);
                        storeUserData.setString(Constants.telegram_share, generalData.responsedata.settings.telegram_share);
                        storeUserData.setString(Constants.where_we_are, generalData.responsedata.settings.where_we_are);
                        storeUserData.setString(Constants.about_us, generalData.responsedata.settings.about_us);
                        storeUserData.setString(Constants.invite_friends, generalData.responsedata.settings.invite_friends);
                        storeUserData.setString(Constants.NEAR_RES_ID, generalData.responsedata.restaurant_id);
                        storeUserData.setString(Constants.MENU_SEARCH, generalData.responsedata.settings.menu_search);
                        storeUserData.setString(Constants.ITEM_GRID, generalData.responsedata.settings.grid);

                        if (generalData.responsedata.cart_items > 0) {
                            binding.llqty.setVisibility(View.VISIBLE);
                            binding.llqty.setText("" + generalData.responsedata.cart_items);
                        } else {
                            binding.llqty.setVisibility(View.GONE);
                        }

                        storeUserData.setString(Constants.ORDER_TYPE, generalData.responsedata.order_type);

                        if (generalData.responsedata.order_status.equalsIgnoreCase("1")) {
                            storeUserData.setString(Constants.CART_ID, generalData.responsedata.cart_id);
                        } else {
                            storeUserData.setString(Constants.CART_ID, "");
                            //storeUserData.setString(Constants.SELECTED_DELIVERY_ADDRESS, "");
                        }


                        if (storeUserData.getString(Constants.offer_banner).equalsIgnoreCase("1")) {
                            binding.llBanner.setVisibility(View.VISIBLE);
                            imageList.clear();
                            imageList.addAll(generalData.responsedata.banners);
                            numberOfPages = imageList.size();

                            if (imageList.size() > 0) {
                                binding.indicator.setVisibility(View.INVISIBLE);
                            }
                            sliderPagerAdapter.notifyDataSetChanged();
                            binding.indicator.setViewPager(binding.viewPager);
                        } else {
                            binding.llBanner.setVisibility(View.GONE);
                        }

                        if (storeUserData.getString(Constants.about_us).equalsIgnoreCase("1")) {
                            binding.llAboutUs.setVisibility(View.VISIBLE);
                        }else {
                            binding.llAboutUs.setVisibility(View.GONE);
                        }

                        if (storeUserData.getString(Constants.where_we_are).equalsIgnoreCase("1")) {
                            binding.llWhereWeAre.setVisibility(View.VISIBLE);
                        }else {
                            binding.llWhereWeAre.setVisibility(View.GONE);
                        }

                        binding.orderFromMenu.setVisibility(View.VISIBLE);
                        binding.llInviteFriends.setVisibility(View.VISIBLE);
                        binding.llOrderHistory.setVisibility(View.VISIBLE);
                        binding.llMyOffer.setVisibility(View.VISIBLE);
                        binding.llAccount.setVisibility(View.VISIBLE);

                        storeUserData.setString(Constants.FB_URL,generalData.responsedata.social_links.fb_url);
                        storeUserData.setString(Constants.INSTA_URL,generalData.responsedata.social_links.insta_url);
                        storeUserData.setString(Constants.MANGAL_URL,generalData.responsedata.social_links.mangal_url);
                        storeUserData.setString(Constants.INSTA_IMAGE,generalData.responsedata.social_links.insta_img);
                        storeUserData.setString(Constants.YOUTUBE_URL,generalData.responsedata.social_links.youtube_url);
                        storeUserData.setString(Constants.TELEGRAM_URL,generalData.responsedata.social_links.telegram_url);
                        storeUserData.setString(Constants.referral_msg,generalData.responsedata.social_links.referral_msg);
                        storeUserData.setString(Constants.tofiko_url,generalData.responsedata.social_links.tofiko_url);


                    }

                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
                Log.e("ERROR_GET_SETTING", error);
            }
        });
    }

    private void addToCart(String resID, String itemId) {

        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().addToCart(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                resID,
                itemId,
                "1",
                "",
                "",
                "",
                "",
                storeUserData.getString(Constants.CART_ID),
                "",
                "pickup"
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
                    Log.i("TAG", "addToCartItem: " + response);
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("status") == 0) {
                        Utils.dismissProgress();
                        Utils.showTopMessageError(activity, jsonObject.getString("message"));
                    } else {
                        JSONObject object = jsonObject.getJSONObject("responsedata");
                        if (object.has("isdiff_res")) {
                            Utils.dismissProgress();
                            if (object.getInt("isdiff_res") == 1) {
                                new AlertDialog.Builder(activity)
                                        .setTitle("Replace Cart Item ?")
                                        .setMessage("Your cart contains dishes from.Do you want to discard selection and add dishes from.")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", (dialog, which) -> {
                                            new StoreUserData(activity).setString(Constants.CART_ID, "");
                                            addToCart(resID, itemId);
                                            return;
                                        })
                                        .setNegativeButton("No", null)
                                        .setIcon(R.mipmap.ic_launcher)
                                        .show();
                            }
                        } else {
                            if (object.has("cart_id")) {
                                storeUserData.setString(Constants.CART_ID, object.getString("cart_id"));
                            }
                            Utils.showTopMessageSuccess(activity, jsonObject.getString("message"));
                            startActivity(new Intent(activity, CartNewActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
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

    public interface ItemClickListener {
        void onClick(GeneralPojo.Banners pojo);
    }
}
