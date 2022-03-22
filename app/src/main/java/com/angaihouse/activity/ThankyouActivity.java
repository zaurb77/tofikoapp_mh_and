package com.angaihouse.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.activity.newmodel.Home_New;
import com.angaihouse.databinding.ActivityThankyouBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.StoreUserData;

public class ThankyouActivity extends AppCompatActivity {


    private ActivityThankyouBinding binding;
    private AppCompatActivity activity;
    private StoreUserData storeUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity,R.layout.activity_thankyou);
        binding.back.setOnClickListener(view -> startActivity(new Intent(activity, Home_New.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)));
        binding.backMainMenu.setOnClickListener(view -> startActivity(new Intent(activity, Home_New.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)));

        binding.tvOrderSuccess.setText(Constants.ORDER_SUCCESS);
        binding.tvErrorMessage.setText(Constants.ORDER_SUCCESS_DESC);
        binding.backMainMenu.setText(Constants.CONTINUE_SHOPPING);

        storeUserData.setString(Constants.CART_ID, "");
        storeUserData.setString(Constants.ORDER_TYPE, "");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(activity,Home_New.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
