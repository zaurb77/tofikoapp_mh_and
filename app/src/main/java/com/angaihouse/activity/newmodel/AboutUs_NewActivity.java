package com.angaihouse.activity.newmodel;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityAboutUsNewBinding;

public class AboutUs_NewActivity extends AppCompatActivity {

    ActivityAboutUsNewBinding binding;
    AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_about_us__new);
        binding.back.setOnClickListener(view -> finish());
    }
}
