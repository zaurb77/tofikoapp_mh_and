package com.angaihouse.activity.newmodel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityMapBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import pojo.RestaurantListPojo;
import retrofit2.Call;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    StoreUserData storeUserData;
    AppCompatActivity activity;
    ActivityMapBinding binding;
    RestaurantListPojo data;
    MarkerOptions markerOptions;
    LatLng snowqualmie;
    GoogleMap.InfoWindowAdapter infoadapte;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(MapActivity.this);

        binding.back.setOnClickListener(view -> finish());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        String lat, lng;
        if (storeUserData.getString(Constants.LAT_STRING).length() > 0) {
            lat = storeUserData.getString(Constants.LAT_STRING);
        } else {
            lat = "0.0";
        }

        if (storeUserData.getString(Constants.LNG_STRING).length() > 0) {
            lng = storeUserData.getString(Constants.LNG_STRING);
        } else {
            lng = "0.0";
        }

        call = retrofitHelper.api().getRestaurantList(
                lat,
                lng,
                new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()),
                storeUserData.getString(Constants.DAY),
                "",
                "pickup",
                Constants.COMPANY_ID,
                storeUserData.getString(Constants.APP_LANGUAGE)
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
                    Log.i("TAG", "RESTAURANT_LIST: " + response);
                    Reader reader = new StringReader(response);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    data = gson.fromJson(reader, RestaurantListPojo.class);

                    mMap = googleMap;


                    if (data.status == 1) {

                        if (data.responsedata.size() > 0) {
                            for (int i = 0; i < data.responsedata.size(); i++) {
                                createMarker(i);
                            }
                        } else {

                            LatLng snowqualmie = new LatLng(Double.parseDouble(storeUserData.getString(Constants.LAT_STRING)), Double.parseDouble(storeUserData.getString(Constants.LNG_STRING)));
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(snowqualmie, 16));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(snowqualmie));
                            mMap.getUiSettings().setZoomControlsEnabled(true);
                        }

                    } else {
                        Toast.makeText(activity, ""+data.message, Toast.LENGTH_LONG).show();
                        finish();
                    }

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

    public void createMarker(int position) {
        snowqualmie = new LatLng(Double.parseDouble(data.responsedata.get(position).latitude), Double.parseDouble(data.responsedata.get(position).longitude));
        markerOptions = new MarkerOptions();
        markerOptions.position(snowqualmie).icon(BitmapDescriptorFactory.fromResource(R.drawable.green_pin));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(snowqualmie));

        mMap.setOnMarkerClickListener(marker -> {

            for (int J = 0; J < data.responsedata.size(); J++) {
                if (data.responsedata.get(J).latitude.equalsIgnoreCase("" + marker.getPosition().latitude) && data.responsedata.get(J).longitude.equalsIgnoreCase("" + marker.getPosition().longitude)) {
                    int finalJ = J;
                    GoogleMap.InfoWindowAdapter infoadapte1 = (new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {

                            View view = getLayoutInflater().inflate(R.layout.info_window, null);
                            TextView infoTitle = view.findViewById(R.id.infoTitle);
                            TextView address = view.findViewById(R.id.address);
                            TextView mobileNumber = view.findViewById(R.id.mobileNumber);
                            TextView startTime = view.findViewById(R.id.startTime);
                            TextView endTIme = view.findViewById(R.id.endTIme);
                            TextView description = view.findViewById(R.id.descriptionMapInfo);

                            infoTitle.setText(data.responsedata.get(finalJ).name);
                            description.setText(data.responsedata.get(finalJ).description);
                            address.setText(data.responsedata.get(finalJ).address);
                            mobileNumber.setText(data.responsedata.get(finalJ).mobile_no);
                            startTime.setText(data.responsedata.get(finalJ).open_close_time1);
                            endTIme.setText(data.responsedata.get(finalJ).open_close_time2);

                            return view;
                        }
                    });
                    mMap.setInfoWindowAdapter(infoadapte1);
                    marker.showInfoWindow();
                }
            }
            return false;
        });

        infoadapte = (new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View view = getLayoutInflater().inflate(R.layout.info_window, null);
                TextView infoTitle = view.findViewById(R.id.infoTitle);
                TextView address = view.findViewById(R.id.address);
                TextView mobileNumber = view.findViewById(R.id.mobileNumber);
                TextView startTime = view.findViewById(R.id.startTime);
                TextView endTIme = view.findViewById(R.id.endTIme);
                TextView description = view.findViewById(R.id.descriptionMapInfo);
                TextView openBtn = view.findViewById(R.id.btnOpen);
                TextView tvDat = view.findViewById(R.id.tvDat);
                TextView tvCelluar = view.findViewById(R.id.tvCelluar);

                tvDat.setText(Constants.MONDAY + "-"+ Constants.SUNDAY);
                openBtn.setText(Constants.COLLECT_HERE );
                tvCelluar.setText(Constants.PHONE_NUMBER );

                infoTitle.setText(data.responsedata.get(position).name);
                description.setText(data.responsedata.get(position).description);
                address.setText(data.responsedata.get(position).address);
                mobileNumber.setText(data.responsedata.get(position).mobile_no);
                startTime.setText(data.responsedata.get(position).open_close_time1);
                endTIme.setText(data.responsedata.get(position).open_close_time2);

                if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("WHEREWEARE")) {
                    openBtn.setVisibility(View.GONE);
                }else {
                    openBtn.setVisibility(View.VISIBLE);
                }

                return view;
            }
        });

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("WHEREWEARE")) {

        }else {
            mMap.setOnInfoWindowClickListener(marker -> {
                for (int J = 0; J < data.responsedata.size(); J++) {
                    if (data.responsedata.get(J).latitude.equalsIgnoreCase("" + marker.getPosition().latitude) && data.responsedata.get(J).longitude.equalsIgnoreCase("" + marker.getPosition().longitude)) {
                        startActivity(new Intent(activity, MenuActivity.class)
                                .putExtra("RES_ID", "" + data.responsedata.get(J).id)
                                .putExtra("ORDERTYPE", "pickup")
                        );
                        break;
                    }
                }
            });
        }

        mMap.setInfoWindowAdapter(infoadapte);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(snowqualmie, 14));
        mMap.addMarker(markerOptions).showInfoWindow();
    }
}
