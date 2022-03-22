package com.angaihouse.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityAddNewCardBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.google.gson.JsonSyntaxException;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;


public class AddNewCardActivity extends AppCompatActivity {

    ActivityAddNewCardBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;
    Stripe stripe;
    Card card;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_add_new_card);
        binding.back.setOnClickListener(view -> finish());
        stripe = new Stripe(getApplicationContext(), Constants.STRIPE_KEY);

        binding.cardNumberTv.setText(Constants.CARD_NUMBER);
        binding.expiryDateTv.setText(Constants.EXPIRY_DATE);
        binding.cvvTv.setText(Constants.CVV_CODE);
        binding.addCard.setText(Constants.ADD);

        binding.cardNumber.setHint(Constants.ENTER_CARD_NUMBER);
        binding.month.setHint(Constants.MONTH);
        binding.year.setHint(Constants.YEAR);

        binding.year.setOnClickListener(v -> {
            Utils.hideKB(activity,binding.year);
            MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(activity, (selectedMonth, selectedYear) -> binding.year.setText(Integer.toString(selectedYear)), Calendar.getInstance().get(Calendar.YEAR), 0);
            builder.showYearOnly()
                    .setYearRange(1990, 2090)
                    .build()
                    .show();
        });

        binding.month.setOnClickListener(v -> {
            Utils.hideKB(activity,binding.year);
            MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(activity, (selectedMonth, selectedYear) -> binding.month.setText(""+(selectedMonth+1)), /* activated number in year */ 3, Calendar.MONTH - 1);
            builder.showMonthOnly()
                    .build()
                    .show();
        });


        binding.cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (count <= binding.cardNumber.getText().toString().length()
                        && (binding.cardNumber.getText().toString().length() == 4
                        || binding.cardNumber.getText().toString().length() == 9
                        || binding.cardNumber.getText().toString().length() == 14)) {
                    binding.cardNumber.setText(binding.cardNumber.getText().toString() + " ");
                    int pos = binding.cardNumber.getText().length();
                    binding.cardNumber.setSelection(pos);
                } else if (count >= binding.cardNumber.getText().toString().length()
                        && (binding.cardNumber.getText().toString().length() == 4
                        || binding.cardNumber.getText().toString().length() == 9
                        || binding.cardNumber.getText().toString().length() == 14)) {
                    binding.cardNumber.setText(binding.cardNumber.getText().toString().substring(0, binding.cardNumber.getText().toString().length() - 1));
                    int pos = binding.cardNumber.getText().length();
                    binding.cardNumber.setSelection(pos);
                }
                count = binding.cardNumber.getText().toString().length();

            }
        });




        binding.addCard.setOnClickListener(view -> {

            Utils.showProgress(activity);

            if (Utils.isEmpty(binding.cardNumber)) {
                Utils.showTopMessageError(activity, "Please Enter your card number");
            } else if (Utils.isEmpty(binding.month)) {
                Utils.showTopMessageError(activity, "Please Enter your card expiry month");
            } else if (Utils.isEmpty(binding.year)) {
                Utils.showTopMessageError(activity, "Please Enter your card expiry year");
            } else {

                card = new Card(Objects.requireNonNull(binding.cardNumber.getText()).toString(),
                        Integer.valueOf(binding.month.getText().toString()),
                        Integer.valueOf(Objects.requireNonNull(binding.year.getText()).toString()),
                        Objects.requireNonNull(binding.cvv.getText()).toString()
                );

                stripe.createToken(card, Constants.STRIPE_KEY, new TokenCallback() {
                    @Override
                    public void onError(Exception error) {
                        Log.d("Stripe", error.getLocalizedMessage());
                        Utils.showTopMessageError(activity,error.getLocalizedMessage());
                        Utils.dismissProgress();
                    }

                    @Override
                    public void onSuccess(Token token) {
                        Log.d("stripe_token", token.getId());
                        addNewCard(token.getId());
                    }
                });
            }
        });
    }

    //TODO : ADD NEW CARD FROM WEB
    private void addNewCard(String token) {
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().addCard(
                new StoreUserData(activity).getString(Constants.USER_ID),
                new StoreUserData(activity).getString(Constants.TOKEN),
                new StoreUserData(activity).getString(Constants.CUST_ID),
                token
        );

        retrofitHelper.callApi(activity, call, new RetrofitHelper.ConnectionCallBack() {

            @Override
            public void onSuccess(retrofit2.Response<ResponseBody> body) {
                try {
                    if (body.code() != 200) {
                        Utils.serverError(activity, body.code());
                        return;
                    }
                    String response = Objects.requireNonNull(body.body()).string();
                    Log.i("TAG", "AddNewCard: " + response);
                    Utils.dismissProgress();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("responsedata");

                    if (jsonObject.getInt("status") == 1) {

                        storeUserData.setString(Constants.CUST_ID, jsonObject1.getString("cust_id"));

                        binding.cardNumber.setText("");
                        binding.month.setText("");
                        binding.cvv.setText("");
                        binding.year.setText("");
                        binding.year.setText("");
                        finish();

                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.e("ERROR", error);
                Utils.dismissProgress();
            }
        });
    }
}
