package com.angaihouse.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityDeliveryInformationBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class DeliveryInformationActivity extends AppCompatActivity {

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    static ArrayList<String> placesId = new ArrayList<>();
    ActivityDeliveryInformationBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;
    boolean maleSelect = true, femaleSelect;
    int selectedType = 1;
    String zip;
    Geocoder geocoder;
    private double latitude, longitude;
    private AutoCompleteTextView searchLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_delivery_information);
        binding.back.setOnClickListener(view -> finish());
        geocoder = new Geocoder(activity, Locale.getDefault());
        searchLocation = findViewById(R.id.atv_places);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("updateAddress")) {
            binding.llInfo.setVisibility(View.GONE);
            binding.next.setText("Save");
            binding.imgBg.setVisibility(View.INVISIBLE);

            binding.atvPlaces.setText(storeUserData.getString(Constants.address));
            binding.address1.setText(storeUserData.getString(Constants.address1));
            binding.provinceNo.setText(storeUserData.getString(Constants.province));
            binding.country.setText(storeUserData.getString(Constants.country));
            binding.city.setText(storeUserData.getString(Constants.city));
            binding.ZipCode.setText(storeUserData.getString(Constants.zipcode));

            if (storeUserData.getString(Constants.type).equalsIgnoreCase("1")) {
                selectItem(1);
            } else {
                selectItem(0);
            }

            binding.back1.setVisibility(View.GONE);
        } else {

            binding.back1.setVisibility(View.VISIBLE);
            binding.llMain.setVisibility(View.GONE);
            binding.imgTitle.setVisibility(View.INVISIBLE);

        }


        binding.atvPlaces.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    changeBg();
                    binding.llAddress.setBackgroundResource(R.drawable.round_light_yeallow_border);
                }
            }
        });


        binding.address1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    changeBg();
                    binding.llAddress1.setBackgroundResource(R.drawable.round_light_yeallow_border);
                }
            }
        });

        binding.provinceNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    changeBg();
                    binding.llProvinceNo.setBackgroundResource(R.drawable.round_light_yeallow_border);
                }
            }
        });


        binding.city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    changeBg();
                    binding.llCity.setBackgroundResource(R.drawable.round_light_yeallow_border);
                }
            }
        });


        binding.country.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    changeBg();
                    binding.llCountry.setBackgroundResource(R.drawable.round_light_yeallow_border);
                }
            }
        });


        binding.ZipCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    changeBg();
                    binding.llZipCode.setBackgroundResource(R.drawable.round_light_yeallow_border);
                }
            }
        });


        binding.back1.setOnClickListener(view -> finish());

        binding.llType.setOnClickListener(view -> selectItem(1));
        binding.llCompany.setOnClickListener(view -> selectItem(0));

        binding.next.setOnClickListener(view -> {
            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("updateAddress")) {

                if (Utils.isEmpty(binding.atvPlaces)) {
                    Utils.showTopMessageError(activity, "Please enter your address.");
                } else if (Utils.isEmpty(binding.provinceNo)) {
                    Utils.showTopMessageError(activity, "Please enter your province No.");
                } else if (Utils.isEmpty(binding.country)) {
                    Utils.showTopMessageError(activity, "Please enter your country.");
                } else if (Utils.isEmpty(binding.city)) {
                    Utils.showTopMessageError(activity, "Please enter your city.");
                } else if (Utils.isEmpty(binding.ZipCode)) {
                    Utils.showTopMessageError(activity, "Please enter your ZipCode.");
                } else if (selectedType == -1) {
                    Utils.showTopMessageError(activity, "Please select your type.");
                } else {
                    updateAddress();
                }
            } else {
                if (Utils.isEmpty(binding.atvPlaces)) {
                    Utils.showTopMessageError(activity, "Please enter your address.");
                } else if (Utils.isEmpty(binding.provinceNo)) {
                    Utils.showTopMessageError(activity, "Please enter your province No.");
                } else if (Utils.isEmpty(binding.country)) {
                    Utils.showTopMessageError(activity, "Please enter your country.");
                } else if (Utils.isEmpty(binding.city)) {
                    Utils.showTopMessageError(activity, "Please enter your city.");
                } else if (Utils.isEmpty(binding.ZipCode)) {
                    Utils.showTopMessageError(activity, "Please enter your ZipCode.");
                } else {
                    if (selectedType == 1) {
                        startActivity(new Intent(activity, AcceptTermsAndConditionActivity.class)
                                .putExtra("gender", "" + getIntent().getStringExtra("gender"))
                                .putExtra("firstName", getIntent().getStringExtra("firstName"))
                                .putExtra("lastName", getIntent().getStringExtra("lastName"))
                                .putExtra("dateOfBirth", getIntent().getStringExtra("dateOfBirth"))
                                .putExtra("selectCountryCod", getIntent().getStringExtra("selectCountryCod"))
                                .putExtra("emailAddress", getIntent().getStringExtra("emailAddress"))
                                .putExtra("password", getIntent().getStringExtra("password"))
                                .putExtra("confirmPassword", getIntent().getStringExtra("confirmPassword"))
                                .putExtra("phoneNumber", getIntent().getStringExtra("phoneNumber"))
                                .putExtra("imagePath", getIntent().getStringExtra("imagePath"))
                                .putExtra("referralCode", getIntent().getStringExtra("referralCode"))
                                .putExtra("address", binding.atvPlaces.getText().toString().trim())
                                .putExtra("addressLineOne", binding.address1.getText().toString().trim())
                                .putExtra("provinceNo", binding.provinceNo.getText().toString().trim())
                                .putExtra("city", binding.city.getText().toString().trim())
                                .putExtra("zipCode", binding.ZipCode.getText().toString().trim())
                                .putExtra("latitude", "" + latitude)
                                .putExtra("longitude", "" + longitude)
                                .putExtra("country", binding.country.getText().toString().trim())
                                .putExtra("selectedType", "" + selectedType)
                        );
                    } else {
                        startActivity(new Intent(activity, CompanyInformation.class)
                                .putExtra("gender", "" + getIntent().getStringExtra("gender"))
                                .putExtra("firstName", getIntent().getStringExtra("firstName"))
                                .putExtra("lastName", getIntent().getStringExtra("lastName"))
                                .putExtra("dateOfBirth", getIntent().getStringExtra("dateOfBirth"))
                                .putExtra("selectCountryCod", getIntent().getStringExtra("selectCountryCod"))
                                .putExtra("emailAddress", getIntent().getStringExtra("emailAddress"))
                                .putExtra("password", getIntent().getStringExtra("password"))
                                .putExtra("confirmPassword", getIntent().getStringExtra("confirmPassword"))
                                .putExtra("phoneNumber", getIntent().getStringExtra("phoneNumber"))
                                .putExtra("imagePath", getIntent().getStringExtra("imagePath"))
                                .putExtra("referralCode", getIntent().getStringExtra("referralCode"))
                                .putExtra("address", binding.atvPlaces.getText().toString().trim())
                                .putExtra("addressLineOne", binding.address1.getText().toString().trim())
                                .putExtra("provinceNo", binding.provinceNo.getText().toString().trim())
                                .putExtra("city", binding.city.getText().toString().trim())
                                .putExtra("country", binding.country.getText().toString().trim())
                                .putExtra("zipCode", binding.ZipCode.getText().toString().trim())
                                .putExtra("latitude", "" + latitude)
                                .putExtra("longitude", "" + longitude)
                                .putExtra("selectedType", "" + selectedType)
                        );
                    }
                }
            }
        });

        searchLocation.setAdapter(new GooglePlacesAutocompleteAdapter(activity, R.layout.dropdown));
        searchLocation.setOnItemClickListener((parent, view, position, id) -> {
            // TODO Auto-generated method stub
            new FocusOnMap(placesId.get(position)).execute();
        });
    }

    private void getAddress() {
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.isEmpty())
                return;
            String addressLine = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            zip = addresses.get(0).getPostalCode();
            ArrayList<String> lstAddr = new ArrayList();
            lstAddr.add(addressLine);
            lstAddr.add(city);
            lstAddr.add(state);
            lstAddr.add(state);
            lstAddr.add(zip);
            Log.i("Address==>", "City : " + city + ",State : " + state + ",zipCod : " + zip);

            searchLocation.setText(addressLine);
            binding.address1.requestFocus();
            binding.city.setText(city);
            binding.country.setText(country);
            binding.provinceNo.setText(state);

            if (zip.length() > 0) {
                binding.ZipCode.setText("" + zip);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList autocomplete(String input) {
        ArrayList resultList = null;
        placesId = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + getResources().getString(R.string.places_key));
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            Log.i("TAG", "autocomplete: " + sb.toString());
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e("Places", e.getMessage());
            return resultList;
        } catch (IOException e) {
            Log.e("Places", e.getMessage());
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            resultList = new ArrayList(predsJsonArray.length());
            placesId = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                placesId.add(predsJsonArray.getJSONObject(i).getString("place_id"));
            }
        } catch (JSONException e) {
            Log.e("JSON results", e.getMessage());
        }
        return resultList;
    }

    public void selectItem(int selectType) {

        binding.imgCompany.setImageResource(R.drawable.radio_blank);
        binding.imgPrivate.setImageResource(R.drawable.radio_blank);

        if (selectType == 0) {
            selectedType = 0;
            binding.imgCompany.setImageResource(R.drawable.radio_on);
        } else if (selectType == 1) {
            selectedType = 1;
            binding.imgPrivate.setImageResource(R.drawable.radio_on);
        }
    }

    private void updateAddress() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;
        call = retrofitHelper.api().updateUserAddress(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                binding.atvPlaces.getText().toString(),
                binding.address1.getText().toString(),
                binding.provinceNo.getText().toString(),
                binding.city.getText().toString(),
                binding.country.getText().toString(),
                binding.ZipCode.getText().toString(),
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
                    Log.i("TAG", "UpdateAddress: " + response);
                    JSONObject object = new JSONObject(response);


                    if (object.getInt("status") == 1) {

                        Utils.showTopMessageSuccess(activity, object.getString("message"));
                        JSONObject responseData = object.getJSONObject("responsedata");
                        storeUserData.setString(Constants.address, responseData.getString("address"));
                        storeUserData.setString(Constants.address1, responseData.getString("address1"));
                        storeUserData.setString(Constants.province, responseData.getString("province"));
                        storeUserData.setString(Constants.city, responseData.getString("city"));
                        storeUserData.setString(Constants.country, responseData.getString("country"));
                        storeUserData.setString(Constants.email, responseData.getString("email"));
                        storeUserData.setString(Constants.zipcode, responseData.getString("zipcode"));
                        storeUserData.setString(Constants.USER_LNG, responseData.getString("longitude"));
                        storeUserData.setString(Constants.USER_LAT, responseData.getString("latitude"));

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

    public void changeBg() {
        binding.llAddress.setBackgroundResource(R.drawable.round_green_border);
        binding.llAddress1.setBackgroundResource(R.drawable.round_green_border);
        binding.llProvinceNo.setBackgroundResource(R.drawable.round_green_border);
        binding.llCity.setBackgroundResource(R.drawable.round_green_border);
        binding.llCountry.setBackgroundResource(R.drawable.round_green_border);
        binding.llZipCode.setBackgroundResource(R.drawable.round_green_border);
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return (String) resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        Log.i("TAG", "performFiltering: " + constraint.toString());
                        resultList = autocomplete(constraint.toString());
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    public class FocusOnMap extends AsyncTask<String, Void, String> {
        String string;

        public FocusOnMap(String string) {
            this.string = string;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            HttpURLConnection conn = null;
            StringBuilder jsonResults = new StringBuilder();
            try {
                StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json");
                sb.append("?placeid=" + URLEncoder.encode(string, "utf8"));
                sb.append("&key=" + URLEncoder.encode(getResources().getString(R.string.places_key), "utf8"));
                URL url = new URL(sb.toString());
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (MalformedURLException e) {
                Log.e("Places", e.getMessage());
                return jsonResults.toString();
            } catch (IOException e) {
                Log.e("Places", e.getMessage());
                return jsonResults.toString();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return jsonResults.toString();
        }

        @Override
        protected void onPostExecute(String response) {
            // TODO Auto-generated method stub
            super.onPostExecute(response);
            if (response == null) {
            } else if (response.length() == 0) {
            } else {
                Log.d("Response", response);
                Utils.dismissProgress();
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    JSONObject obj = jsonObj.getJSONObject("result");
                    JSONObject objGeo = obj.getJSONObject("geometry");
                    JSONObject objLoc = objGeo.getJSONObject("location");
                    latitude = objLoc.getDouble("lat");
                    longitude = objLoc.getDouble("lng");
                    getAddress();
                    View view = activity.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                } catch (JSONException e) {
                    Log.e("SON results", e.getMessage());
                }
            }
        }
    }
}
