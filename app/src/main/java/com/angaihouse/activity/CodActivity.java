package com.angaihouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityCodBinding;
import com.angaihouse.utils.Utils;

import java.util.Objects;

public class CodActivity extends AppCompatActivity {

    ActivityCodBinding binding;
    AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = DataBindingUtil.setContentView(activity,R.layout.activity_cod);

        binding.emailCODE.setText(getIntent().getStringExtra("EMAIL_CODE"));

        binding.backSecurityCode.setOnClickListener(view -> finish());

        binding.otp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    binding.otp.setBackgroundResource(R.drawable.round_light_yeallow_border);
                }
            }
        });



        binding.nextSecurityCode.setOnClickListener(view -> {
            if (Objects.requireNonNull(getIntent().getStringExtra("CODE")).equalsIgnoreCase(Objects.requireNonNull(binding.otp.getText()).toString())){
                startActivity(new Intent(activity,SetPasswordActivity.class)
                .putExtra("EMAIL_CODE",getIntent().getStringExtra("EMAIL_CODE")));
            }else{
                Utils.showTopMessageError(activity,"Please enter a valid OTP.");
            }
        });
    }
}
