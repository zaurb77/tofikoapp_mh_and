package com.angaihouse.activity.newmodel;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityNoInternetBinding;

public class NoInternetActivity extends AppCompatActivity {

    AppCompatActivity activity;
    ActivityNoInternetBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = DataBindingUtil.setContentView(activity,R.layout.activity_no_internet);
        binding.back.setOnClickListener(v -> activity.finishAffinity());

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        activity.finishAffinity();
    }
}