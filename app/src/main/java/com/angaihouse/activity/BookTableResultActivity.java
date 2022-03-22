package com.angaihouse.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityBookTableResultBinding;

public class BookTableResultActivity extends AppCompatActivity {

    AppCompatActivity activity;
    ActivityBookTableResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        activity = this;
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_book_table_result );
        binding.back.setOnClickListener( v -> finish() );


    }
}