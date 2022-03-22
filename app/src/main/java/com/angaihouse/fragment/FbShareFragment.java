package com.angaihouse.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.angaihouse.R;
import com.angaihouse.databinding.FbShareBinding;
import com.angaihouse.utils.StoreUserData;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

public class FbShareFragment extends Fragment {

    FbShareBinding binding;
    FragmentActivity activity;
    StoreUserData storeUserData;
    private static String TAG = FbShareFragment.class.getName();
    private CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fb_share, container, false);
        activity = getActivity();
        storeUserData = new StoreUserData(activity);

        // Initialize facebook SDK.
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(activity);

        setLinkShare();

        return binding.getRoot();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setLinkShare() {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle("Tutorialwing - Free programming tutorials")
                .setImageUrl(Uri.parse("https://scontent-sin6-1.xx.fbcdn.net/t31.0-8/13403381_247495578953089_8113745370016563192_o.png"))
                .setContentDescription("Tutorialwing is an online platform for free programming tutorials. These tutorials are designed for beginners as well as experienced programmers.")
                .setContentUrl(Uri.parse("https://www.facebook.com/tutorialwing/"))
                .setShareHashtag(new ShareHashtag.Builder()
                        .setHashtag("#Tutorialwing")
                        .build())
                .setQuote("Learn and share your knowledge")
                .build();
        binding.fbShareButton.setShareContent(content);
    }

    private FacebookCallback<Sharer.Result> callback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {
            Log.v(TAG, "Successfully posted");
            // Write some code to do some operations when you shared content successfully.
        }

        @Override
        public void onCancel() {
            Log.v(TAG, "Sharing cancelled");
            // Write some code to do some operations when you cancel sharing content.
        }

        @Override
        public void onError(FacebookException error) {
            Log.v(TAG, error.getMessage());
            // Write some code to do some operations when some error occurs while sharing content.
        }
    };
}
