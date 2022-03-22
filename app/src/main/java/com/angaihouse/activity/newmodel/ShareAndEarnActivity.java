package com.angaihouse.activity.newmodel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityShareAndEarnBinding;
import com.angaihouse.fragment.FbShareFragment;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ShareAndEarnActivity extends AppCompatActivity {

    StoreUserData storeUserData;
    AppCompatActivity activity;
    ActivityShareAndEarnBinding binding;
    private CallbackManager callbackManager;
    ShareDialog shareDialog;
    private static String TAG = FbShareFragment.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_share_and_earn);

        binding.mangalTitle.setText(Constants.LIKE_MANGAL+"\n"+Constants.SHARE_WITH_FRIENDS);
        binding.tvInviteFriend.setText(Constants.INVITE_DESCRIPTION);
        binding.tvShareYourCode.setText(Constants.SHARE_CODE);

        // Initialize facebook SDK.
        FacebookSdk.sdkInitialize(activity.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(activity);
        shareDialog.registerCallback(callbackManager, callback);

        Glide.with(activity)
                .load(storeUserData.getString(Constants.INSTA_IMAGE))
                .into(binding.imageInstaShare);

        setLinkShare();

        binding.fbShare.setOnClickListener(v -> {
            binding.fbShareButton.performClick();
        });

        if (storeUserData.getString(Constants.REFERRAL_CODE).length() > 0) {
            binding.edtCode.setEnabled(false);
            binding.edtCode.setText(storeUserData.getString(Constants.REFERRAL_CODE));
        } else {
            binding.edtCode.setEnabled(true);
        }

        binding.back.setOnClickListener(view -> finish());

        binding.teli.setOnClickListener(view -> intentMessageTelegram());

        binding.instagram.setOnClickListener(v -> shareImageOnInstagram());

        binding.whatsapp.setOnClickListener(view -> shareWhatsapp());

        binding.linkdin.setOnClickListener(view -> shareLinkdin());

        if (storeUserData.getString(Constants.instagram_share).equalsIgnoreCase("1")) {
            binding.instagram.setVisibility(View.VISIBLE);
        } else {
            binding.instagram.setVisibility(View.GONE);
        }

        if (storeUserData.getString(Constants.facebook_share).equalsIgnoreCase("1")) {
            binding.fbShare.setVisibility(View.VISIBLE);
        } else {
            binding.fbShare.setVisibility(View.GONE);
        }

        if (storeUserData.getString(Constants.whatsapp_share).equalsIgnoreCase("1")) {
            binding.whatsapp.setVisibility(View.VISIBLE);
        } else {
            binding.whatsapp.setVisibility(View.GONE);
        }

        if (storeUserData.getString(Constants.linkedin_share).equalsIgnoreCase("1")) {
            binding.linkdin.setVisibility(View.VISIBLE);
        } else {
            binding.linkdin.setVisibility(View.GONE);
        }

        if (storeUserData.getString(Constants.telegram_share).equalsIgnoreCase("1")) {
            binding.teli.setVisibility(View.VISIBLE);
        } else {
            binding.teli.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Call callbackManager.onActivityResult to pass login result to the LoginManager via callbackManager.
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setLinkShare() {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle("Mangal House")
                .setContentUrl(Uri.parse(storeUserData.getString(Constants.MANGAL_URL)))
                .setShareHashtag(new ShareHashtag.Builder()
                        .setHashtag("#MangalHouse")
                        .build())
                .setQuote(storeUserData.getString(Constants.referral_msg)+"\n\nReferral Code :- " + storeUserData.getString(Constants.REFERRAL_CODE) + "\n\nApp Store Link :- https://apps.apple.com/us/app/mangal-house/id1493326708\n\nPlay Store Link :- " + "https://play.google.com/store/apps/details?id=\n\n" + activity.getPackageName() + "\n\nThank you.")
                .build();
        binding.fbShareButton.setShareContent(content);
    }


    private FacebookCallback<Sharer.Result> callback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {
            Log.v(TAG, "Successfully posted");
            sharePoint("fb");
        }

        @Override
        public void onCancel() {
            Log.v(TAG, "Sharing cancelled");
        }

        @Override
        public void onError(FacebookException error) {
            Log.v(TAG, error.getMessage());
        }
    };

    public void shareWhatsapp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage("com.whatsapp");
        intent.putExtra(Intent.EXTRA_TEXT, storeUserData.getString(Constants.referral_msg)+"\n\nReferral Code :- " + storeUserData.getString(Constants.REFERRAL_CODE) + " App Store Link :- https://itunes.apple.com/us/app/apple-store/id375380948?mt=8 Play Store Link :- " + "https://play.google.com/store/apps/details?id=" + activity.getPackageName() + " Thank you.");
        try {
            activity.startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.whatsapp")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")));
            }
            Toast.makeText(activity, "WhatsApp Not Installed.", Toast.LENGTH_SHORT).show();
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



    public void shareLinkdin() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setClassName("com.linkedin.android", "com.linkedin.android.home.UpdateStatusActivity");
        intent.setType("text/*");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, storeUserData.getString(Constants.referral_msg)+"\n\nReferral Code :- " + storeUserData.getString(Constants.REFERRAL_CODE) + " App Store Link :- https://itunes.apple.com/us/app/apple-store/id375380948?mt=8 Play Store Link :- " + "https://play.google.com/store/apps/details?id=" + activity.getPackageName() + " Thank you.");
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Intent viewWebPage = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com"));
            startActivity(viewWebPage);
            Toast.makeText(activity, "LinkedIn Not Installed.", Toast.LENGTH_SHORT).show();
        }
    }

    void intentMessageTelegram() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setClassName("org.telegram.messenger", "com.telegram.messenger");
        intent.setType("text/*");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, storeUserData.getString(Constants.referral_msg)+"\n\nReferral Code :- " + storeUserData.getString(Constants.REFERRAL_CODE) + " App Store Link :- https://itunes.apple.com/us/app/apple-store/id375380948?mt=8 Play Store Link :- " + "https://play.google.com/store/apps/details?id=" + activity.getPackageName() + " Thank you.");
        try {
            activity.startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=org.telegram.messenger")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=org.telegram.messenger")));
            }
            Toast.makeText(activity, "Telegram Not Installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareImageOnInstagram() {
        Bitmap bm = ((android.graphics.drawable.BitmapDrawable) binding.imageInstaShare.getDrawable()).getBitmap();
        try {
            java.io.File file = new java.io.File( activity.getExternalCacheDir() + "/mangalhouse_menu.jpg" );
            java.io.OutputStream out = new java.io.FileOutputStream( file );
            bm.compress( Bitmap.CompressFormat.JPEG, 100, out );
            out.flush();
            out.close();
            Uri imageUrl = FileProvider.getUriForFile(activity, getApplication().getPackageName()+".provider", file);
            Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
            intent.putExtra("interactive_asset_uri", imageUrl);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setPackage("com.instagram.android");
            grantUriPermission("com.instagram.android", imageUrl, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent chooserIntent = Intent.createChooser(intent, "Share Mangalhouse Menu");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});
            startActivity(chooserIntent);
        } catch (Exception e) {
            Log.i( "Error", "" + e.toString() );
        }
    }
}
