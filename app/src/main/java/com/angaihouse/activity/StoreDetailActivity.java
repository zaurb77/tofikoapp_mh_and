package com.angaihouse.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityStoreDetailBinding;
import com.angaihouse.databinding.RestaurantBookingPopupBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import adapter.DinnerTimeAdapter;
import adapter.LunchTimeAdapter;
import adapter.SelectMemberAdapter;
import okhttp3.ResponseBody;
import pojo.GetTimePojo;
import pojo.StoreDetailPojo;
import retrofit2.Call;
import retrofit2.Response;

public class StoreDetailActivity extends AppCompatActivity {

    AppCompatActivity activity;
    ActivityStoreDetailBinding binding;
    StoreUserData storeUserData;
    Dialog bookingDialog;
    RestaurantBookingPopupBinding popupBinding;
    String selectedDate = "", selectedTime = "", selectedMember = "";
    GetTimePojo data;
    ItemClickListener itemClickListener;
    public ArrayList<String> openDates = new ArrayList<>();
    public ArrayList<GetTimePojo.Morning> arrLunch = new ArrayList<>();
    public ArrayList<GetTimePojo.Evening> arrDinner = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        activity = this;
        binding = DataBindingUtil.setContentView( activity, R.layout.activity_store_detail );
        storeUserData = new StoreUserData( activity );
        binding.tvDetail.setText(Constants.ABOUT );
        binding.tvNext.setText(Constants.NEXT );
        binding.back.setOnClickListener( v -> finish() );

        Calendar c = Calendar.getInstance();
        c.setTime(c.getTime());
        c.add(Calendar.DATE, 30);
        Date expDate = c.getTime();
        Log.i( "FUTURE_DATE" , ""+expDate );

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd" );
        selectedDate = formatter.format( today );

        bookingDialog = new Dialog( activity );
        View dialogBinding = getLayoutInflater().inflate( R.layout.restaurant_booking_popup, null );
        bookingDialog.setContentView( dialogBinding );
        bookingDialog.setCancelable( true );
        Objects.requireNonNull( bookingDialog.getWindow() ).setLayout( LinearLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT );
        bookingDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        popupBinding = DataBindingUtil.bind( dialogBinding );
        changeLayout( 1 );
        Calendar firstDay = Calendar.getInstance();
        firstDay.set( Calendar.DAY_OF_MONTH, 1 );

        popupBinding.calendarViewSingle.state().edit()
                .setFirstDayOfWeek( Calendar.MONDAY )
                .setMinimumDate( firstDay.getTime() )
                .setMaximumDate( expDate
                )
                .setCalendarDisplayMode( CalendarMode.MONTHS )
                .commit();

//        Calendar c = Calendar.getInstance();
//        c.setTime(startDate);
//        c.add(Calendar.DATE, 30);
//        Date expDate = c.getTime();

        Utils.disablePastDates( activity, Utils.getDatesNew( Utils.getFirstDateOfMonth(), Utils.getCurrentDateOfMonth() ), popupBinding.calendarViewSingle );

        popupBinding.calendarViewSingle.setSelectionMode( MaterialCalendarView.SELECTION_MODE_SINGLE );
        popupBinding.calendarViewSingle.setSelectedDate( Calendar.getInstance().getTime() );

        popupBinding.calendarViewSingle.setOnDateChangedListener( new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                DecimalFormat mFormat = new DecimalFormat( "00" );
                selectedDate = mFormat.format( date.getYear() ) + "-" + mFormat.format( date.getMonth() + 1 ) + "-" + mFormat.format( date.getDay() );
                setTime();
                changeLayout( 2 );
            }
        } );

        popupBinding.ivSelectdate.setOnClickListener( v -> changeLayout( 1 ) );
        popupBinding.ivSelectTime.setOnClickListener( v -> changeLayout( 2 ) );
        popupBinding.ivSelectMember.setOnClickListener( v -> changeLayout( 3 ) );
        popupBinding.submit.setOnClickListener( v -> bookTable() );

        popupBinding.tvChooseTime.setText(Constants.CHOOSE_YOUR_TIME );
        popupBinding.tvLunch.setText(Constants.LUNCH );
        popupBinding.tvDinner.setText(Constants.DINNER );
        popupBinding.tvNumGuest.setText(Constants.NUMBER_OF_GUEST );
        popupBinding.tvSpecialRequest.setText(Constants.SPECIAL_REQ );
        popupBinding.message.setHint(Constants.WRITE_SPECIAL_REQ );
        popupBinding.submit.setText(Constants.SUBMIT );

        binding.llBookTable.setOnClickListener( v -> bookingDialog.show() );

        itemClickListener = new ItemClickListener() {

            @Override
            public void time(String time , int capicity) {
                selectedTime = time;
                changeLayout( 3 );

                popupBinding.rvMember.setVisibility( View.VISIBLE );
                popupBinding.rvMember.setLayoutManager( new GridLayoutManager( activity, 3 ) );
                popupBinding.rvMember.setNestedScrollingEnabled( true );
                popupBinding.rvMember.setHasFixedSize( true );
                popupBinding.rvMember.setAdapter( new SelectMemberAdapter( activity, itemClickListener , capicity ) );
            }

            @Override
            public void member(String i) {
                selectedMember = i;
                changeLayout( 4 );
            }

            @Override
            public void selectedmember(String toString) {
                selectedMember = toString;
                changeLayout( 4 );
            }

        };



        getBookings();
        getTime();
    }

    private void getBookings() {
        Utils.showProgress( activity );
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().getStoreDetail(
                storeUserData.getString(Constants.APP_LANGUAGE),
                getIntent().getStringExtra( "Store_Id" )
        );

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

                    StoreDetailPojo data = gson.fromJson( reader, StoreDetailPojo.class );
                    if (jsonObject.getInt( "status" ) == 1) {
                        Glide.with( activity ).load( data.responsedata.image ).into( binding.ivShop );
                        binding.tvShopName.setText( data.responsedata.name );
                        binding.tvShopDesc.setText( data.responsedata.description );
                        binding.tvAddress.setText( data.responsedata.address );
                        binding.tvContact.setText( data.responsedata.mobile_no );

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

    private void getTime() {
        // Utils.showProgress( activity );
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().getTimeSlot(
                storeUserData.getString(Constants.APP_LANGUAGE),
                getIntent().getStringExtra( "Store_Id" )
        );

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
                    Log.i( "GET_TIME", "onSuccess: " + response );
                    JSONObject jsonObject = new JSONObject( response );
                    Reader reader = new StringReader( response );

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers( Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC )
                            .serializeNulls()
                            .create();

                    data = gson.fromJson( reader, GetTimePojo.class );
                    if (data.status.equalsIgnoreCase( "1" )) {
                        setTime();
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

    public void setTime() {

        for (int i =0; i<data.responsedata.size(); i++){
            if (data.responsedata.get( i ).isOpen.equalsIgnoreCase( "0" )){
                openDates.add( data.responsedata.get( i ).date );
            }
        }
        setCalendar( openDates );

        Utils.disablePastDates( activity, Utils.getDatesNew( Utils.getFirstDateOfMonth(), Utils.getCurrentDateOfMonth() ), popupBinding.calendarViewSingle );

        if (data != null) {
            for (int i = 0; i < data.responsedata.size(); i++) {
                if (selectedDate.equalsIgnoreCase( data.responsedata.get( i ).date )) {
                    arrLunch.clear();
                    arrDinner.clear();
                    arrLunch.addAll( data.responsedata.get( i ).morning );
                    arrDinner.addAll( data.responsedata.get( i ).evening );

                    if (arrLunch.size() > 0) {
                        popupBinding.tvLunch.setVisibility( View.VISIBLE );
                        popupBinding.rvLunch.setVisibility( View.VISIBLE );
                        popupBinding.rvLunch.setLayoutManager( new GridLayoutManager( activity, 3 ) );
                        popupBinding.rvLunch.setNestedScrollingEnabled( true );
                        popupBinding.rvLunch.setHasFixedSize( true );
                        popupBinding.rvLunch.setAdapter( new LunchTimeAdapter( activity, arrLunch, itemClickListener ) );
                    } else {
                        popupBinding.tvLunch.setVisibility( View.GONE );
                        popupBinding.rvLunch.setVisibility( View.GONE );
                    }

                    if (arrDinner.size() > 0) {
                        popupBinding.tvDinner.setVisibility( View.VISIBLE );
                        popupBinding.rvDinner.setVisibility( View.VISIBLE );
                        popupBinding.rvDinner.setLayoutManager( new GridLayoutManager( activity, 3 ) );
                        popupBinding.rvDinner.setNestedScrollingEnabled( true );
                        popupBinding.rvDinner.setHasFixedSize( true );
                        popupBinding.rvDinner.setAdapter( new DinnerTimeAdapter( activity, arrDinner, itemClickListener ) );
                    } else {
                        popupBinding.tvDinner.setVisibility( View.GONE );
                        popupBinding.rvDinner.setVisibility( View.GONE );
                    }

                }
            }
        }
    }
    
    private void bookTable() {
        Utils.showProgress( activity );
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().bookTable(
                getIntent().getStringExtra( "Store_Id" ),
                storeUserData.getString( Constants.APP_LANGUAGE ),
                storeUserData.getString( Constants.USER_ID ),
                storeUserData.getString( Constants.TOKEN ),
                selectedDate,
                selectedTime,
                selectedMember,
                popupBinding.message.getText().toString()
        );

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
                    Log.i( "TABLE_BOOKING_status", "onSuccess: " + response );
                    JSONObject jsonObject = new JSONObject( response );
                    Reader reader = new StringReader( response );

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers( Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC )
                            .serializeNulls()
                            .create();

                    GetTimePojo pojo = gson.fromJson( reader, GetTimePojo.class );

                    if (pojo.status.equalsIgnoreCase( "1" )) {
                       bookingDialog.dismiss();
                       finish();
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

    public void setCalendar(ArrayList<String> arrDateList) {

        popupBinding.calendarViewSingle.setSelectionMode( MaterialCalendarView.SELECTION_MODE_SINGLE );
        popupBinding.calendarViewSingle.addDecorator( new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                String month = (day.getMonth() + 1) + "";
                if (month.length() == 1) {
                    month = "0" + month;
                }

                String date = (day.getDay()) + "";
                if (date.length() == 1) {
                    date = "0" + date;
                }

                String curDate = day.getYear() + "-" + month + "-" + date;
                Log.i( "CURRENT_DATE", curDate );
                return arrDateList.contains( curDate );
            }

            @Override
            public void decorate(DayViewFacade view) {
                try {
//                    view.setSelectionDrawable( activity.getResources().getDrawable( R.drawable.background_enable_date ) );
//                    view.setBackgroundDrawable( activity.getResources().getDrawable( R.drawable.background_enable_date ) );
                    view.setDaysDisabled( false );
                    view.setSelectionDrawable( activity.getResources().getDrawable( R.drawable.background_disable_date_gray) );
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } );
    }

    public void changeLayout(int arg) {
        popupBinding.llSelectDate.setVisibility( View.GONE );
        popupBinding.llChooseTime.setVisibility( View.GONE );
        popupBinding.llSelectMember.setVisibility( View.GONE );
        popupBinding.llSelectSpecialRequest.setVisibility( View.GONE );

        if (arg == 1) {
            popupBinding.llSelectDate.setVisibility( View.VISIBLE );
        } else if (arg == 2) {
            popupBinding.llChooseTime.setVisibility( View.VISIBLE );
        } else if (arg == 3) {
            popupBinding.llSelectMember.setVisibility( View.VISIBLE );
        }else if (arg == 4) {
            popupBinding.llSelectSpecialRequest.setVisibility( View.VISIBLE );
        }
    }

    public interface ItemClickListener {
        void time(String time , int capacity);

        void member(String i);

        void selectedmember(String toString);
    }
}