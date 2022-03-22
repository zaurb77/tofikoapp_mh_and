package com.angaihouse.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityRestaurantListBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;

import adapter.getRestaurantListAdapter;
import okhttp3.ResponseBody;
import pojo.NotificationPojo;
import retrofit2.Call;
import retrofit2.Response;

public class RestaurantListActivity extends AppCompatActivity {

    AppCompatActivity activity;
    ActivityRestaurantListBinding binding;
    StoreUserData storeUserData;
    String dayName, currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        activity = this;
        binding = DataBindingUtil.setContentView( activity, R.layout.activity_restaurant_list );
        storeUserData = new StoreUserData( activity );
        binding.back.setOnClickListener( v -> finish() );
        binding.etSearch.setHint(Constants.SEARCH);

        SimpleDateFormat timeFormat = new SimpleDateFormat( "HH:mm" );
        SimpleDateFormat day = new SimpleDateFormat( "EEEE" );
        dayName = day.format( new Date() );
        currentTime = timeFormat.format( new Date() );

        binding.etSearch.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                getBookings(s.toString() , false);
            }
        } );

        Log.i( "CurrentDateTime" , dayName + "  " + currentTime );
        getBookings( "" , true);
    }

    private void getBookings(String search ,boolean loader) {
        if (loader){Utils.showProgress( activity );}
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

//        call = retrofitHelper.api().getStoreList(
//                storeUserData.getString( Constants.APP_LANGUAGE ),
//                currentTime,
//                dayName,
//                "1",
//                search

        if(search.length() > 0){
            
            call = retrofitHelper.api().getStoreList(
                    storeUserData.getString(Constants.APP_LANGUAGE),
                    currentTime,
                    dayName,
                    Constants.COMPANY_ID,
                    search
            );
        }else {
            call = retrofitHelper.api().getStoreList(
                    storeUserData.getString(Constants.APP_LANGUAGE),
                    currentTime,
                    dayName,
                    Constants.COMPANY_ID
            );

        }

        retrofitHelper.callApi( activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                Utils.dismissProgress();
                try {
                    if (body.code() != 200) {
                        Utils.serverError( activity, body.code() );
                        return;
                    }
                    String response = body.body().string();
                    Log.i( "GET_NOTIFICATION", "onSuccess: " + response );
                    JSONObject jsonObject = new JSONObject( response );
                    Reader reader = new StringReader( response );

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers( Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC )
                            .serializeNulls()
                            .create();

                    NotificationPojo data = gson.fromJson( reader, NotificationPojo.class );
                    if (jsonObject.getInt( "status" ) == 1) {
                        if (data.responsedata.size() > 0) {
                            binding.rvList.setVisibility( View.VISIBLE );
                            binding.rvList.setLayoutManager( new LinearLayoutManager( activity ) );
                            binding.rvList.setNestedScrollingEnabled( false );
                            binding.rvList.setHasFixedSize( true );
                            binding.rvList.setAdapter( new getRestaurantListAdapter( activity, data.responsedata ) );
                        }else{
                            binding.rvList.setVisibility( View.GONE );
                        }
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
            }

        } );
    }
}