package com.angaihouse.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.angaihouse.R;
import com.angaihouse.databinding.FragmentShareBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.util.List;

import okhttp3.ResponseBody;
import pojo.PointDetailsPojo;
import retrofit2.Call;
import retrofit2.Response;

public class ShareFragment extends Fragment {

    FragmentShareBinding binding;
    Activity activity;
    StoreUserData storeUserData;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_share, container, false);
        activity = getActivity();
        storeUserData = new StoreUserData(activity);

        binding.inviteFriends.setText(Constants.INVITE_FRIENDS_LABEL);


        binding.referralCode.setText(storeUserData.getString(Constants.REFERRAL_CODE));

        binding.facebookShare.setOnClickListener(view -> shareFacebook(activity, "", ""));

        binding.instagramShare.setOnClickListener(view -> {
            
        });

        binding.inviteFriends.setOnClickListener(view -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "Hi Dear, check out Mangal House. It's a great food delivery app. Share this link with your family and friends. Use referral code to earn the free Mangals. Referral Code :- "+storeUserData.getString(Constants.REFERRAL_CODE)+" App Store Link :- https://itunes.apple.com/us/app/apple-store/id375380948?mt=8 Play Store Link :- "+"https://play.google.com/store/apps/details?id="+activity.getPackageName()+" Thank you.");
            startActivity(Intent.createChooser(sharingIntent, "Invite Friend"));
        });

        getPointDetails();

        return binding.getRoot();
    }


    public void shareFacebook(Activity activity, String text, String url) {

        boolean facebookAppFound = false;
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hi Dear, check out Mangal House. It's a great food delivery app. Share this link with your family and friends. Use referral code to earn the free Mangals. Referral Code :- "+storeUserData.getString(Constants.REFERRAL_CODE)+" App Store Link :- https://itunes.apple.com/us/app/apple-store/id375380948?mt=8 Play Store Link :- "+"https://play.google.com/store/apps/details?id="+activity.getPackageName()+" Thank you.");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);

        for (final ResolveInfo app : activityList) {

            if ((app.activityInfo.packageName).contains("com.facebook.katana")) {

                final ActivityInfo activityInfo = app.activityInfo;
                final ComponentName name = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setComponent(name);
                facebookAppFound = true;
                activity.startActivity(shareIntent);

                sharePoint("fb");

                break;
            }
        }
        if (!facebookAppFound) {
            Toast.makeText(activity, "App not installed.", Toast.LENGTH_SHORT).show();
        }

    }



    private void sharePoint(String type) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().sharePoints(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                type
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
                    Log.i("TAG", "Share point: " + response);

                    activity.finish();

                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }
            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
                Log.e("ERROR", error);
            }
        });
    }



    private void getPointDetails() {

        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().pointDetails(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN)
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
                    Log.i("TAG", "getPointDetails: " + response);
                    Reader reader = new StringReader(response);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    PointDetailsPojo pojo = gson.fromJson(reader, PointDetailsPojo.class);

                    if (pojo.status == 1) {
                        binding.pointDetails.setText("invite your friends to Mangal House and they will get some welcome points by registering account in app. Once they register, you will get " +pojo.responsedata.referral_point.you_got +" Mangals. Your friend will get " +pojo.responsedata.total_points+" Mangals");
                        binding.textDetailOne.setText("Share this app in your fb and get "+pojo.responsedata.fb_share_point+" Mangals and Share this app in your insta will get "+pojo.responsedata.insta_share_point +" Mangals");
                        binding.llCod.setVisibility(View.VISIBLE);
                        binding.inviteFriends.setVisibility(View.VISIBLE);
                        binding.llShare.setVisibility(View.VISIBLE);

                    } else {
                        Utils.showTopMessageError(activity,pojo.message);
                    }

                } catch (IOException | NullPointerException | JsonSyntaxException e) {
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
