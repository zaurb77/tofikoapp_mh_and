package com.angaihouse.activity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityAddAddressTypeBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.google.gson.JsonSyntaxException;
import com.seatgeek.placesautocomplete.DetailsCallback;
import com.seatgeek.placesautocomplete.model.AddressComponent;
import com.seatgeek.placesautocomplete.model.AddressComponentType;
import com.seatgeek.placesautocomplete.model.PlaceDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class AddAddressTypeActivity extends AppCompatActivity {

    public String selectedType = "home";
    AppCompatActivity activity;
    ActivityAddAddressTypeBinding binding;
    StoreUserData storeUserData;
    String zip;
    Geocoder geocoder;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_add_address_type);
        geocoder = new Geocoder(activity, Locale.getDefault());

        binding.tvHome.setText(Constants.HOME);
        binding.tvWork.setText(Constants.WORK);
        binding.tvOther.setText(Constants.OTHER);
        binding.saveAddress.setText(Constants.SAVE);

        binding.doorNumber.setHint(Constants.DOOR_NO);
        binding.atvPlaces.setHint(Constants.ADDRESS);
        binding.address2.setHint(Constants.ADDRESS_LINE_2);
        binding.city.setHint(Constants.CITY);
        binding.country.setHint(Constants.COUNTRY);
        binding.ZipCode.setHint(Constants.ZIPCODE);



        binding.atvPlaces.setOnPlaceSelectedListener(
                place -> {
                    binding.atvPlaces.getDetailsFor(place, new DetailsCallback() {
                        @Override
                        public void onSuccess(final PlaceDetails details) {
                            binding.atvPlaces.setText(details.name);
                            for (AddressComponent component : details.address_components) {
                                for (AddressComponentType type : component.types) {
                                    switch (type) {
                                        case LOCALITY:
                                            binding.city.setText(component.long_name);
                                            Log.d("test-city", component.long_name);
                                            break;
                                        case COUNTRY:
                                            binding.country.setText(component.long_name);
                                            break;
                                        case POSTAL_CODE:
                                            binding.ZipCode.setText(component.long_name);
                                            break;
                                    }
                                }
                            }

                            binding.country.requestFocus();
                            latitude = details.geometry.location.lat;
                            longitude = details.geometry.location.lng;
                            if (Utils.isEmpty(binding.ZipCode)) {
                                getAddress(details.geometry.location.lat, details.geometry.location.lng);
                            }
                        }

                        @Override
                        public void onFailure(final Throwable failure) {
                            Log.d("test", "failure " + failure);
                        }
                    });
                }
        );


        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("EditAddress")) {
            binding.doorNumber.setText(getIntent().getStringExtra("ADDRESS_DOOR_NUMNER"));
            binding.atvPlaces.setText(getIntent().getStringExtra("ADDRESS"));

            if (getIntent().getStringExtra("ADDRESS_TYPE").equalsIgnoreCase("home")) {
                clearBg(binding.homeSelect);
                selectedType = "home";
            } else if (getIntent().getStringExtra("ADDRESS_TYPE").equalsIgnoreCase("work")) {
                clearBg(binding.workSelect);
                selectedType = "work";
            } else if (getIntent().getStringExtra("ADDRESS_TYPE").equalsIgnoreCase("other")) {
                clearBg(binding.otherSelect);
                selectedType = "other";
            }

            binding.city.setEnabled(true);
            binding.country.setEnabled(true);
            binding.ZipCode.setEnabled(true);

            binding.city.setText(getIntent().getStringExtra("ADDRESS_CITY"));
            binding.country.setText(getIntent().getStringExtra("ADDRESS_COUNTRY"));
            binding.ZipCode.setText(getIntent().getStringExtra("ADDRESS_ZIP"));

            latitude = Double.parseDouble(getIntent().getStringExtra("ADDRESS_LATITUDE"));
            longitude = Double.parseDouble(getIntent().getStringExtra("ADDRESS_LONGITUDE"));

        }

        binding.back.setOnClickListener(view -> finish());

        binding.homeSelect.setOnClickListener(view -> {
            selectedType = "home";
            clearBg(binding.homeSelect);
        });
        binding.workSelect.setOnClickListener(view -> {
            selectedType = "work";
            clearBg(binding.workSelect);
        });
        binding.otherSelect.setOnClickListener(view -> {
            selectedType = "other";
            clearBg(binding.otherSelect);
        });

        binding.tvHome.setOnClickListener(view -> {
            selectedType = "home";
            clearBg(binding.homeSelect);
        });
        binding.tvWork.setOnClickListener(view -> {
            selectedType = "work";
            clearBg(binding.workSelect);
        });
        binding.tvOther.setOnClickListener(view -> {
            selectedType = "other";
            clearBg(binding.otherSelect);
        });


        //TODO : SAVE USER ADDRESS WITH FULL VALIDATION
        binding.saveAddress.setOnClickListener(view -> {


            if (Utils.isEmpty(binding.doorNumber)) {
                Utils.showTopMessageError(activity, "Please enter your door number.");
            } else if (Utils.isEmpty(binding.atvPlaces)) {
                Utils.showTopMessageError(activity, "Please enter your address.");
            } else if (Utils.isEmpty(binding.city)) {
                Utils.showTopMessageError(activity, "Please enter your city.");
            } else if (Utils.isEmpty(binding.ZipCode)) {
                Utils.showTopMessageError(activity, "Please enter your zip code.");
            } else if (Utils.isEmpty(binding.country)) {
                Utils.showTopMessageError(activity, "Please enter your country.");
            } else {
                if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("EditAddress")) {
                    updateAddress();
                } else {
                    addAddress();
                }
            }
        });
    }


    //TODO : THIS USE FOR CHANGE BG FOR SELECT ITEM
    public void clearBg(ImageView imageView) {
        binding.homeSelect.setImageResource(R.drawable.radio_blank);
        binding.workSelect.setImageResource(R.drawable.radio_blank);
        binding.otherSelect.setImageResource(R.drawable.radio_blank);
        imageView.setImageResource(R.drawable.radio_on);
    }


    // TODO : GET ADDRESS FROM USING LAT LNG GOOGLE
    private void getAddress(Double lat, Double lng) {
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.isEmpty())
                return;

            zip = addresses.get(0).getPostalCode();

            if (zip.length() > 0) {
                binding.ZipCode.setText("" + zip);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //TODO : ADD ADDRESS USING THIS METHOD FROM WEB
    private void addAddress() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;
        call = retrofitHelper.api().address(
                "add",
                "",
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                selectedType,
                binding.atvPlaces.getText().toString().trim(),
                binding.address2.getText().toString().trim(),
                binding.doorNumber.getText().toString().trim(),
                binding.city.getText().toString(),
                binding.ZipCode.getText().toString(),
                binding.country.getText().toString(),
                "" + latitude,
                "" + longitude

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
                    Log.i("TAG", "addAddress: " + response);
                    JSONObject object = new JSONObject(response);

                    if (object.getInt("status") == 1) {

                        Utils.showTopMessageSuccess(activity, object.getString("message"));
                        new Handler().postDelayed(() -> finish(),2000);

                    } else {

                        Utils.showTopMessageError(activity, object.getString("message"));
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    Utils.dismissProgress();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
            }
        });
    }


    private void updateAddress() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;
        call = retrofitHelper.api().address(
                "edit",
                getIntent().getStringExtra("ADDRESS_ID"),
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                selectedType,
                binding.atvPlaces.getText().toString().trim(),
                binding.address2.getText().toString().trim(),
                binding.doorNumber.getText().toString().trim(),
                binding.city.getText().toString(),
                binding.ZipCode.getText().toString(),
                binding.country.getText().toString(),
                ""+latitude,
                ""+longitude
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
                    Log.i("TAG", "UpdateAddAddress: " + response);
                    JSONObject object = new JSONObject(response);

                    if (object.getInt("status") == 1) {
                        Utils.showTopMessageSuccess(activity, object.getString("message"));
                        new Handler().postDelayed(() -> finish(),2000);
                    } else {
                        Utils.showTopMessageError(activity, object.getString("message"));
                    }

                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    Utils.dismissProgress();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
            }
        });
    }

}
