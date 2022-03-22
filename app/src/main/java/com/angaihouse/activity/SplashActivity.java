package com.angaihouse.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.angaihouse.R;
import com.angaihouse.activity.newmodel.CartNewActivity;
import com.angaihouse.activity.newmodel.Home_New;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.GPSTracker;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import pojo.LanguageStringPojo;
import retrofit2.Call;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    Activity activity;
    StoreUserData storeUserData;
    boolean askOnceAgain = false;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;


    private Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (Constants.latitude != -99.99 && Constants.latitude != -99.99) {
                storeUserData.setDouble( Constants.LAT_STRING, Constants.latitude );
                storeUserData.setDouble( Constants.LNG_STRING, Constants.longitude );
            }
            handler.postDelayed( runnable, 5000 );
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        FirebaseApp.initializeApp( this );
        activity = this;
        setContentView( R.layout.activity_splash );
        storeUserData = new StoreUserData( activity );
        handler.postDelayed( runnable, 2000 );

        if (storeUserData.getString( Constants.APP_LANGUAGE ).equalsIgnoreCase( "" ) || storeUserData.getString( Constants.APP_LANGUAGE ).equalsIgnoreCase( "0" )) {
            storeUserData.setString( Constants.APP_LANGUAGE, "2" );
        }

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setConfigSettingsAsync( new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds( 3600L ).build() );

        getHashKey();

        Uri uri = getIntent().getData();

        if (uri != null) {

            if (getIntent().getData().getLastPathSegment().equalsIgnoreCase( "paypal_fail" )) {
                new Handler().postDelayed( () -> {
                    startActivity( new Intent( activity, CartNewActivity.class ) );
                    finish();
                }, 2000 );
            } else {
                new Handler().postDelayed( () -> {
                    startActivity( new Intent( activity, ThankyouActivity.class ) );
                    finish();
                }, 2000 );
            }
        } else {
            checkPermissions( false );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = getIntent().getData();
        if (uri != null) {
            new Handler().postDelayed( () -> {
                startActivity( new Intent( activity, ThankyouActivity.class ) );
                finish();
            }, 2000 );
        } else {
            if (askOnceAgain) {
                askOnceAgain = false;
                checkPermissions( false );
            }
        }

        handler.postDelayed( runnable, 500 );
        String provider = Settings.Secure.getString( activity.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED );

        if (!(provider.contains( "network" ) || provider.contains( "gps" ))) {
            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder( activity );
            alertDialogBuilder.setMessage( "To continue it is necessary to turn on geolocation" );
            alertDialogBuilder.setPositiveButton( R.string.ok,
                    (arg0, arg1) -> startActivity( new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS ) ) );
            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable( false );
            alertDialog.show();
        } else {
            GPSTracker gpsTracker = new GPSTracker( activity );
            gpsTracker.getLocation();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 99) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (permissions[i].equalsIgnoreCase( "android.permission.ACCESS_FINE_LOCATION" )) {
                        Constants.DONT_ALLOW_PERMISSION = 1;
                    } else {
                        Constants.DONT_ALLOW_PERMISSION = 0;
                    }
                }
            }
            getLanguageText();

        } else {
            super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        }
    }


    private void checkPermissions(boolean check) {

        int hasLocationPermission = ContextCompat.checkSelfPermission( activity, Manifest.permission.ACCESS_FINE_LOCATION );
        List<String> permissions = new ArrayList<>();

        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add( Manifest.permission.ACCESS_FINE_LOCATION );
        }

        if (!permissions.isEmpty()) {
            if (!check) {
                ActivityCompat.requestPermissions( activity, permissions.toArray( new String[permissions.size()] ), 99 );
            }
        } else {
            getLanguageText();
        }
    }


    public void startActivityMethod() {
        new Handler().postDelayed( () -> {
            String provider = Settings.Secure.getString( activity.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED );
            if (!(provider.contains( "network" ) || provider.contains( "gps" ))) {
                androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder( activity );
                alertDialogBuilder.setMessage( "To continue it is necessary to turn on geolocation" );
                alertDialogBuilder.setPositiveButton( R.string.ok,
                        (arg0, arg1) -> startActivity( new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS ) ) );
                androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCancelable( false );
                alertDialog.show();
            } else if (!storeUserData.getString( Constants.USER_ID ).isEmpty()) {
                updatePushCounterApi();
            } else {
                startActivity( new Intent( activity, LoginActivity.class ) );
                finish();
            }
        }, 3000 );
    }


    private void updatePushCounterApi() {
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().updatePushCounter(
                storeUserData.getString( Constants.USER_ID ),
                storeUserData.getString( Constants.TOKEN ),
                storeUserData.getString( Constants.USER_FCM ),
                Constants.DEVICE_TYPE
        );

        retrofitHelper.callApi( activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                try {
                    if (body.code() != 200) {
                        Utils.serverError( activity, body.code() );
                        return;
                    }
                    String response = body.body().string();
                    Log.i( "updatePushCounterApi", "" + response );
                    startActivity( new Intent( activity, Home_New.class ) );
                    finish();
                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
                Log.e( "ERROR", error );
            }
        } );
    }

    private void getLanguageText() {
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().getLangKeyword( storeUserData.getString( Constants.APP_LANGUAGE ) );
        Log.i( "LANGUAGE_ID", storeUserData.getString( Constants.APP_LANGUAGE ) );
        retrofitHelper.callApi( activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                try {
                    if (body.code() != 200) {
                        Utils.serverError( activity, body.code() );
                        return;
                    }
                    String response = body.body().string();
                    Log.i( "LANGUAGE", "LANGUAGE_STRING" + response );


                    Reader reader = new StringReader( response );
                    Utils.dismissProgress();
                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers( Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC )
                            .serializeNulls()
                            .create();

                    LanguageStringPojo pojo = gson.fromJson( reader, LanguageStringPojo.class );

                    if (pojo.status == 1) {
                        Constants.BOOK_A_TABLE =                   pojo.responsedata.BOOK_A_TABLE;
                        Constants.ADD_A_NEW_RESERVATION =          pojo.responsedata.ADD_A_NEW_RESERVATION;
                        Constants.APPROVED =                       pojo.responsedata.APPROVED;
                        Constants.REJECT =                         pojo.responsedata.REJECT;
                        Constants.CANCEL_BOOKING =                 pojo.responsedata.CANCEL_BOOKING;
                        Constants.SEARCH =                         pojo.responsedata.SEARCH;
                        Constants.ABOUT =                          pojo.responsedata.ABOUT;
                        Constants.CHOOSE_YOUR_TIME =               pojo.responsedata.CHOOSE_YOUR_TIME;
                        Constants.NUMBER_OF_GUEST =                pojo.responsedata.NUMBER_OF_GUEST;
                        Constants.SET_BOOKING =                    pojo.responsedata.SET_BOOKING;
                        Constants.BOOK_TABLE =                     pojo.responsedata.BOOK_TABLE;
                        Constants.TABLE_BOOKING =                  pojo.responsedata.TABLE_BOOKING;
                        Constants.SPACE_AVAILABLE =                pojo.responsedata.SPACE_AVAILABLE;
                        Constants.REJECT_TABLE_RESERVATION =       pojo.responsedata.REJECT_TABLE_RESERVATION;
                        Constants.PLEASE_WRITE_REJECTION_MESSAGE = pojo.responsedata.PLEASE_WRITE_REJECTION_MESSAGE;
                        Constants.WRITE_YOUR_QUERY_HERE =          pojo.responsedata.WRITE_YOUR_QUERY_HERE;
                        Constants.DINNER =                         pojo.responsedata.DINNER;
                        Constants.LUNCH =                          pojo.responsedata.LUNCH;
                        Constants.CANCEL_RESERVATION =              pojo.responsedata.CANCEL_RESERVATION;
                        Constants.ARRIVED =                        pojo.responsedata.ARRIVED;
                        Constants.LEFT =                           pojo.responsedata.LEFT;
                        Constants.SPECIAL_REQ =                    pojo.responsedata.SPECIAL_REQ;
                        Constants.WRITE_SPECIAL_REQ =              pojo.responsedata.WRITE_SPECIAL_REQ;
                        Constants.SEATS_LEFT =                     pojo.responsedata.SEATS_LEFT;
                        Constants.TODAY_TABLE_BOOKING =            pojo.responsedata.TODAY_TABLE_BOOKING;
                        Constants.NEW =                            pojo.responsedata.NEW;
                        Constants.ORDERS =                         pojo.responsedata.ORDERS;
                        Constants.ONGOING =                        pojo.responsedata.ONGOING;
                        Constants.TABLE_NOT_FOUND =                pojo.responsedata.TABLE_NOT_FOUND;
                        Constants.STATUS =                         pojo.responsedata.STATUS;
                        Constants.CURRENT =                        pojo.responsedata.CURRENT;
                        Constants.BOOKING_TYPE =                   pojo.responsedata.BOOKING_TYPE;
                        Constants.ALL_SERVICE =                    pojo.responsedata.ALL_SERVICE;
                        Constants.SELECT_SERVICE =                 pojo.responsedata.SELECT_SERVICE;
                        Constants.RESERVED =                       pojo.responsedata.RESERVED;
                        Constants.CANCELLATION_NOTE =              pojo.responsedata.CANCELLATION_NOTE;
                        Constants.NEW_BOOKING_RECEIVED =           pojo.responsedata.NEW_BOOKING_RECEIVED;
                        Constants.BOOKING_HAS_BEEN_ACCEPTED =      pojo.responsedata.BOOKING_HAS_BEEN_ACCEPTED;
                        Constants.CUSTOMER_HAS_BEEN_ARRIVED =      pojo.responsedata.CUSTOMER_HAS_BEEN_ARRIVED;
                        Constants.CUSTOMER_HAS_BEEN_LEFT =         pojo.responsedata.CUSTOMER_HAS_BEEN_LEFT;
                        Constants.BOOKING_HAS_BEEN_REJECTED =      pojo.responsedata.BOOKING_HAS_BEEN_REJECTED;


                        Constants.COLLECT_HERE = pojo.responsedata.COLLECT_HERE;
                        Constants.ADDRESS_LINE_2 = pojo.responsedata.ADDRESS_LINE_2;
                        Constants.NOTIFICATION_DETAIL = pojo.responsedata.NOTIFICATION_DETAIL;
                        Constants.NO_NOTIFICATIONS_AVAILABLE = pojo.responsedata.NO_NOTIFICATIONS_AVAILABLE;
                        Constants.SELECT_RATING_TYPE = pojo.responsedata.SELECT_RATING_TYPE;
                        Constants.ENTER_COMMENT = pojo.responsedata.ENTER_COMMENT;
                        Constants.LOGIN = pojo.responsedata.LOGIN;
                        Constants.PAY_WITH_MANGAL = pojo.responsedata.PAY_WITH_MANGAL;
                        Constants.PROVIDE_NAME = pojo.responsedata.PROVIDE_NAME;
                        Constants.APPLY_CHANGES = pojo.responsedata.APPLY_CHANGES;
                        Constants.LANGUAGE = pojo.responsedata.LANGUAGE;
                        Constants.GUEST_ERROR = pojo.responsedata.GUEST_ERROR;
                        Constants.ORDER_SUCCESS = pojo.responsedata.ORDER_SUCCESS;
                        Constants.PROVIDE_TERMS_CONDITION = pojo.responsedata.PROVIDE_TERMS_CONDITION;
                        Constants.RES_NUMBER = pojo.responsedata.RES_NUMBER;
                        Constants.CASH = pojo.responsedata.CASH;
                        Constants.ORDER = pojo.responsedata.ORDER;
                        Constants.ADD_INVOICE = pojo.responsedata.ADD_INVOICE;
                        Constants.ENTER_COMPANY_INFO = pojo.responsedata.ENTER_COMPANY_INFO;
                        Constants.SELECT_TIME_ERROR = pojo.responsedata.SELECT_TIME_ERROR;
                        Constants.SET_PASSWORD = pojo.responsedata.SET_PASSWORD;
                        Constants.CHOOSE_TEST = pojo.responsedata.CHOOSE_TEST;
                        Constants.COOKING_LEVEL = pojo.responsedata.COOKING_LEVEL;
                        Constants.ADD_EXTRA = pojo.responsedata.ADD_EXTRA;
                        Constants.WANT_TO_REMOVE = pojo.responsedata.WANT_TO_REMOVE;
                        Constants.ORDER_SUCCESS_DESC = pojo.responsedata.ORDER_SUCCESS_DESC;
                        Constants.CONTINUE_SHOPPING = pojo.responsedata.CONTINUE_SHOPPING;
                        Constants.PHONE_NUMBER = pojo.responsedata.PHONE_NUMBER;
                        Constants.REFERRAL_CODE_EXIST = pojo.responsedata.REFERRAL_CODE_EXIST;
                        Constants.LOGIN_FLOW = pojo.responsedata.LOGIN_FLOW;
                        Constants.I_AGREE = pojo.responsedata.I_AGREE;
                        Constants.INGREDIENTS = pojo.responsedata.INGREDIENTS;
                        Constants.ITEM_TOTAL = pojo.responsedata.ITEM_TOTAL;
                        Constants.COOKIES = pojo.responsedata.COOKIES;
                        Constants.WANT_TO_REORDER1 = pojo.responsedata.WANT_TO_REORDER1;
                        Constants.WANT_TO_REORDER2 = pojo.responsedata.WANT_TO_REORDER2;
                        Constants.RES_ADDRESS = pojo.responsedata.RES_ADDRESS;
                        Constants.ADD_CUTLERY = pojo.responsedata.ADD_CUTLERY;
                        Constants.DELIVERY = pojo.responsedata.DELIVERY;
                        Constants.SEARCH_PRODUCT = pojo.responsedata.SEARCH_PRODUCT;
                        Constants.SEE_MORE = pojo.responsedata.SEE_MORE;
                        Constants.FAQ = pojo.responsedata.FAQ;
                        Constants.MY_MANGAL_POINT = pojo.responsedata.MY_MANGAL_POINT;
                        Constants.TAKEAWAY_FROM_STORE = pojo.responsedata.TAKEAWAY_FROM_STORE;
                        Constants.HOW_WOULD_LIKE = pojo.responsedata.HOW_WOULD_LIKE;
                        Constants.RECEIVE_ORDER = pojo.responsedata.RECEIVE_ORDER;
                        Constants.LIKE_MANGAL = pojo.responsedata.LIKE_MANGAL;
                        Constants.SHARE_WITH_FRIENDS = pojo.responsedata.SHARE_WITH_FRIENDS;
                        Constants.INVITE_DESCRIPTION = pojo.responsedata.INVITE_DESCRIPTION;
                        Constants.STILL_QUESTIONS = pojo.responsedata.STILL_QUESTIONS;
                        Constants.ADD = pojo.responsedata.ADD;
                        Constants.TAKEAWAY = pojo.responsedata.TAKEAWAY;
                        Constants.SIGN_UP = pojo.responsedata.SIGN_UP;
                        Constants.ORDER_TYPE_K = pojo.responsedata.ORDER_TYPE;
                        Constants.MY_ACCOUNT = pojo.responsedata.MY_ACCOUNT;
                        Constants.YOUR_CART_EMPTY = pojo.responsedata.YOUR_CART_EMPTY;
                        Constants.BACK_MAIN_MENU = pojo.responsedata.BACK_MAIN_MENU;
                        Constants.LOGOUT_CONFIRM = pojo.responsedata.LOGOUT_CONFIRM;
                        Constants.ORDER_FROM_MENU = pojo.responsedata.ORDER_FROM_MENU;
                        Constants.MY_ORDER_HISTORY = pojo.responsedata.MY_ORDER_HISTORY;
                        Constants.MY_CART = pojo.responsedata.MY_CART;
                        Constants.CUSTOMER_SERVICE = pojo.responsedata.CUSTOMER_SERVICE;
                        Constants.WHERE_WE_ARE = pojo.responsedata.WHERE_WE_ARE;
                        Constants.SPECIAL_OFFERS = pojo.responsedata.SPECIAL_OFFERS;
                        Constants.EMAIL_ADDRESS = pojo.responsedata.EMAIL_ADDRESS;
                        Constants.PASSWORD = pojo.responsedata.PASSWORD;
                        Constants.FORGOT_PASS = pojo.responsedata.FORGOT_PASS;
                        Constants.AND = pojo.responsedata.AND;
                        Constants.CREATE_AC = pojo.responsedata.CREATE_AC;
                        Constants.BY_LOGIN_TERMS_CONDITION_LABEL = pojo.responsedata.BY_LOGIN_TERMS_CONDITION_LABEL;
                        Constants.SEND_PASS = pojo.responsedata.SEND_PASS;
                        Constants.WITHOUT_LOGIN = pojo.responsedata.WITHOUT_LOGIN;
                        Constants.GENDER = pojo.responsedata.GENDER;
                        Constants.MALE = pojo.responsedata.MALE;
                        Constants.SIGN_AGREEMENT = pojo.responsedata.SIGN_AGREEMENT;
                        Constants.FEMALE = pojo.responsedata.FEMALE;
                        Constants.FNAME = pojo.responsedata.FNAME;
                        Constants.LNAME = pojo.responsedata.LNAME;
                        Constants.DOB = pojo.responsedata.DOB;
                        Constants.PH_NUMBER = pojo.responsedata.PH_NUMBER;
                        Constants.CONFIRM_PASS = pojo.responsedata.CONFIRM_PASS;
                        Constants.ALREADY_AC_LABEL = pojo.responsedata.ALREADY_AC_LABEL;
                        Constants.NEXT = pojo.responsedata.NEXT;
                        Constants.DELIVERY_INFO = pojo.responsedata.DELIVERY_INFO;
                        Constants.BACK = pojo.responsedata.BACK;
                        Constants.TYPE = pojo.responsedata.TYPE;
                        Constants.PRIVATE = pojo.responsedata.PRIVATE;
                        Constants.COMPANY = pojo.responsedata.COMPANY;
                        Constants.ADDRESS = pojo.responsedata.ADDRESS;
                        Constants.PROVINCE = pojo.responsedata.PROVINCE;
                        Constants.CITY = pojo.responsedata.CITY;
                        Constants.COUNTRY = pojo.responsedata.COUNTRY;
                        Constants.ZIPCODE = pojo.responsedata.ZIPCODE;
                        Constants.COMPANY_INFO = pojo.responsedata.COMPANY_INFO;
                        Constants.COMPANY_NAME = pojo.responsedata.COMPANY_NAME;
                        Constants.VAT_ID = pojo.responsedata.VAT_ID;
                        Constants.LEGAL_EMAIL = pojo.responsedata.LEGAL_EMAIL;
                        Constants.INVOICE_CODE = pojo.responsedata.INVOICE_CODE;
                        Constants.TERMS_CONDITION_LABEL = pojo.responsedata.TERMS_CONDITION_LABEL;
                        Constants.REGISTER = pojo.responsedata.REGISTER;
                        Constants.INVITE_FRIENDS_DESC = pojo.responsedata.INVITE_FRIENDS_DESC;
                        Constants.SHARE_CODE = pojo.responsedata.SHARE_CODE;
                        Constants.INVITE_FRIENDS_LABEL = pojo.responsedata.INVITE_FRIENDS_LABEL;
                        Constants.SHARE_FB_INSTA_DESC = pojo.responsedata.SHARE_FB_INSTA_DESC;
                        Constants.CONTACT_NO = pojo.responsedata.CONTACT_NO;
                        Constants.MESSAGE = pojo.responsedata.MESSAGE;
                        Constants.SHARE = pojo.responsedata.SHARE;
                        Constants.CONTACT_US = pojo.responsedata.CONTACT_US;
                        Constants.SUBMIT = pojo.responsedata.SUBMIT;
                        Constants.DELIVERY_ADDRESS = pojo.responsedata.DELIVERY_ADDRESS;
                        Constants.DOOR_NO = pojo.responsedata.DOOR_NO;
                        Constants.ADDRESS_LINE = pojo.responsedata.ADDRESS_LINE;
                        Constants.HOME = pojo.responsedata.HOME;
                        Constants.CONTINUE = pojo.responsedata.CONTINUE;
                        Constants.WORK = pojo.responsedata.WORK;
                        Constants.OTHER = pojo.responsedata.OTHER;
                        Constants.SAVE = pojo.responsedata.SAVE;
                        Constants.RES_NAME = pojo.responsedata.RES_NAME;
                        Constants.PAYMENT = pojo.responsedata.PAYMENT;
                        Constants.NAME_OF_PERSON = pojo.responsedata.NAME_OF_PERSON;
                        Constants.MOBILE_NO = pojo.responsedata.MOBILE_NO;
                        Constants.TOTAL_PAYABLE = pojo.responsedata.TOTAL_PAYABLE;
                        Constants.QTY = pojo.responsedata.QTY;
                        Constants.DELIVERY_CHARGE = pojo.responsedata.DELIVERY_CHARGE;
                        Constants.TOTAL = pojo.responsedata.TOTAL;
                        Constants.CHOOSE_DELIVERY_TIME = pojo.responsedata.CHOOSE_DELIVERY_TIME;
                        Constants.CREDIT_DEBIT_CARD = pojo.responsedata.CREDIT_DEBIT_CARD;
                        Constants.LATER = pojo.responsedata.LATER;
                        Constants.ADD_NEW_CARD = pojo.responsedata.ADD_NEW_CARD;
                        Constants.COD = pojo.responsedata.COD;
                        Constants.KEEP_CASH_ON_HAND = pojo.responsedata.KEEP_CASH_ON_HAND;
                        Constants.PAY_ON_DELIVERY = pojo.responsedata.PAY_ON_DELIVERY;
                        Constants.PAYPAL = pojo.responsedata.PAYPAL;
                        Constants.PAY_VIA_PAYPAL = pojo.responsedata.PAY_VIA_PAYPAL;
                        Constants.ORDER_PLACED = pojo.responsedata.ORDER_PLACED;
                        Constants.THANK_YOU = pojo.responsedata.THANK_YOU;
                        Constants.ORDER_PLACED_SUCCESS = pojo.responsedata.ORDER_PLACED_SUCCESS;
                        Constants.GO_TO_HOME = pojo.responsedata.GO_TO_HOME;
                        Constants.ADD_CARD = pojo.responsedata.ADD_CARD;
                        Constants.CARD_TYPE = pojo.responsedata.CARD_TYPE;
                        Constants.CARD_NUMBER = pojo.responsedata.CARD_NUMBER;
                        Constants.EXPIRY_DATE = pojo.responsedata.EXPIRY_DATE;
                        Constants.CVV_CODE = pojo.responsedata.CVV_CODE;
                        Constants.SELECT_CARD_TYPE = pojo.responsedata.SELECT_CARD_TYPE;
                        Constants.ENTER_CARD_NUMBER = pojo.responsedata.ENTER_CARD_NUMBER;
                        Constants.MONTH = pojo.responsedata.MONTH;
                        Constants.YEAR = pojo.responsedata.YEAR;
                        Constants.CONTACT_SUPPORT = pojo.responsedata.CONTACT_SUPPORT;
                        Constants.ASKED_QUE = pojo.responsedata.ASKED_QUE;
                        Constants.ORDER_HISTORY = pojo.responsedata.ORDER_HISTORY;
                        Constants.PENDING = pojo.responsedata.PENDING;
                        Constants.IN_PREPARE = pojo.responsedata.IN_PREPARE;
                        Constants.COMPLETED = pojo.responsedata.COMPLETED;
                        Constants.TRANSACTION_ID = pojo.responsedata.TRANSACTION_ID;
                        Constants.REPEAT_ORDER = pojo.responsedata.REPEAT_ORDER;
                        Constants.ORDER_DATE = pojo.responsedata.ORDER_DATE;
                        Constants.ORDER_AMOUNT = pojo.responsedata.ORDER_AMOUNT;
                        Constants.FEEDBACK = pojo.responsedata.FEEDBACK;
                        Constants.RATING = pojo.responsedata.RATING;
                        Constants.ADD_COMMENT = pojo.responsedata.ADD_COMMENT;
                        Constants.WOOPS = pojo.responsedata.WOOPS;
                        Constants.CART_EMPTY = pojo.responsedata.CART_EMPTY;
                        Constants.CUSTOMIZABLE = pojo.responsedata.CUSTOMIZABLE;
                        Constants.CART = pojo.responsedata.CART;
                        Constants.SPECIAL_REQUEST = pojo.responsedata.SPECIAL_REQUEST;
                        Constants.CHECKOUT = pojo.responsedata.CHECKOUT;
                        Constants.PUSH_NOTIFICATION = pojo.responsedata.PUSH_NOTIFICATION;
                        Constants.ORDER_DETAIL = pojo.responsedata.ORDER_DETAIL;
                        Constants.N = pojo.responsedata.N;
                        Constants.A = pojo.responsedata.A;
                        Constants.M = pojo.responsedata.M;
                        Constants.TYPE_OF_PAYMENT = pojo.responsedata.TYPE_OF_PAYMENT;
                        Constants.ABOUT_US = pojo.responsedata.ABOUT_US;
                        Constants.SETTING = pojo.responsedata.SETTING;
                        Constants.VERSION = pojo.responsedata.VERSION;
                        Constants.EDIT_PROFILE = pojo.responsedata.EDIT_PROFILE;
                        Constants.UPDATE = pojo.responsedata.UPDATE;
                        Constants.PRIVACY_POLICY = pojo.responsedata.PRIVACY_POLICY;
                        Constants.MY_MANGALS = pojo.responsedata.MY_MANGALS;
                        Constants.AVAILABLE_OFFERS = pojo.responsedata.AVAILABLE_OFFERS;
                        Constants.RES_DETAIL = pojo.responsedata.RES_DETAIL;
                        Constants.INFO = pojo.responsedata.INFO;
                        Constants.GALLERY = pojo.responsedata.GALLERY;
                        Constants.REVIEW = pojo.responsedata.REVIEW;
                        Constants.ADD_TO_CART = pojo.responsedata.ADD_TO_CART;
                        Constants.ADD_TO_FAVOURITE = pojo.responsedata.ADD_TO_FAVOURITE;
                        Constants.NOT_AVAILABLE_FOR_ORDER = pojo.responsedata.NOT_AVAILABLE_FOR_ORDER;
                        Constants.FAVOURITE = pojo.responsedata.FAVOURITE;
                        Constants.LOCATE_ON_MAP = pojo.responsedata.LOCATE_ON_MAP;
                        Constants.LIST_OF_ALLERGENS = pojo.responsedata.LIST_OF_ALLERGENS;
                        Constants.RES_REVIEW = pojo.responsedata.RES_REVIEW;
                        Constants.RES_INFO = pojo.responsedata.RES_INFO;
                        Constants.SHARE_AND_EARN = pojo.responsedata.SHARE_AND_EARN;
                        Constants.LOGOUT = pojo.responsedata.LOGOUT;
                        Constants.VAILD_EMAIL = pojo.responsedata.VAILD_EMAIL;
                        Constants.PROVIDE_PASS = pojo.responsedata.PROVIDE_PASS;
                        Constants.PROVIDE_EMAIL = pojo.responsedata.PROVIDE_EMAIL;
                        Constants.CHECK_INTERNET = pojo.responsedata.CHECK_INTERNET;
                        Constants.PROVIDE_PROFILE_IMG = pojo.responsedata.PROVIDE_PROFILE_IMG;
                        Constants.PROVIDE_FNAME = pojo.responsedata.PROVIDE_FNAME;
                        Constants.PROVIDE_LNAME = pojo.responsedata.PROVIDE_LNAME;
                        Constants.PROVIDE_DOB = pojo.responsedata.PROVIDE_DOB;
                        Constants.PROVIDE_PH_NO = pojo.responsedata.PROVIDE_PH_NO;
                        Constants.PROVIDE_VALID_PH_NO = pojo.responsedata.PROVIDE_VALID_PH_NO;
                        Constants.PROVIDE_CONFIRM_PASS = pojo.responsedata.PROVIDE_CONFIRM_PASS;
                        Constants.CONFIRM_PASS_NOT_MATCHED = pojo.responsedata.CONFIRM_PASS_NOT_MATCHED;
                        Constants.PROVIDE_ADDRESS = pojo.responsedata.PROVIDE_ADDRESS;
                        Constants.PROVIDE_PROVINCE = pojo.responsedata.PROVIDE_PROVINCE;
                        Constants.PROVIDE_CITY = pojo.responsedata.PROVIDE_CITY;
                        Constants.PROVIDE_COUNTRY = pojo.responsedata.PROVIDE_COUNTRY;
                        Constants.PROVIDE_VALID_ZIPCODE = pojo.responsedata.PROVIDE_VALID_ZIPCODE;
                        Constants.PROVIDE_COM_NAME = pojo.responsedata.PROVIDE_COM_NAME;
                        Constants.PROVIDE_VAT_ID = pojo.responsedata.PROVIDE_VAT_ID;
                        Constants.PROVIDE_LEGAL_MAIL = pojo.responsedata.PROVIDE_LEGAL_MAIL;
                        Constants.PROVIDE_INVOICING_CODE = pojo.responsedata.PROVIDE_INVOICING_CODE;
                        Constants.REPLACE_CART = pojo.responsedata.REPLACE_CART;
                        Constants.RES_CLOSED_PRE_ORDER_LABEL = pojo.responsedata.RES_CLOSED_PRE_ORDER_LABEL;
                        Constants.RES_CLOSED_OPEN_AFETR_LABEL = pojo.responsedata.RES_CLOSED_OPEN_AFETR_LABEL;
                        Constants.RES_CLOSED_LABEL = pojo.responsedata.RES_CLOSED_LABEL;
                        Constants.PREORDER_ACCEPTED = pojo.responsedata.PREORDER_ACCEPTED;
                        Constants.OPEN_NOW = pojo.responsedata.OPEN_NOW;
                        Constants.FREE_CUSOMIZATION = pojo.responsedata.FREE_CUSOMIZATION;
                        Constants.PAID_CUSTOMIZATION = pojo.responsedata.PAID_CUSTOMIZATION;
                        Constants.ORDER_FROM = pojo.responsedata.ORDER_FROM;
                        Constants.CUSTOMIZE = pojo.responsedata.CUSTOMIZE;
                        Constants.PROVIDE_DOOR_NO = pojo.responsedata.PROVIDE_DOOR_NO;
                        Constants.PROVIDE_STREET_ADD1 = pojo.responsedata.PROVIDE_STREET_ADD1;
                        Constants.PROVIDE_CITY_NAME = pojo.responsedata.PROVIDE_CITY_NAME;
                        Constants.PROVIDE_ZIP_CODE = pojo.responsedata.PROVIDE_ZIP_CODE;
                        Constants.SELECT_COUNTRY_NAME = pojo.responsedata.SELECT_COUNTRY_NAME;
                        Constants.PROVIDE_PROPER_CITY = pojo.responsedata.PROVIDE_PROPER_CITY;
                        Constants.PROVIDE_PROPER_ZIP = pojo.responsedata.PROVIDE_PROPER_ZIP;
                        Constants.GEO_LOCATION_UNABLE = pojo.responsedata.GEO_LOCATION_UNABLE;
                        Constants.UNABLE_TO_FIND_ADD = pojo.responsedata.UNABLE_TO_FIND_ADD;
                        Constants.ADDRESS_NOT_FOUNDED = pojo.responsedata.ADDRESS_NOT_FOUNDED;
                        Constants.MONDAY = pojo.responsedata.MONDAY;
                        Constants.TUESDAY = pojo.responsedata.TUESDAY;
                        Constants.WEDNESDAY = pojo.responsedata.WEDNESDAY;
                        Constants.THURSDAY = pojo.responsedata.THURSDAY;
                        Constants.FRIDAY = pojo.responsedata.FRIDAY;
                        Constants.SATURDAY = pojo.responsedata.SATURDAY;
                        Constants.SUNDAY = pojo.responsedata.SUNDAY;
                        Constants.PROVIDE_DEVLIERY_ADD = pojo.responsedata.PROVIDE_DEVLIERY_ADD;
                        Constants.REPLACE_CART_ITEM = pojo.responsedata.REPLACE_CART_ITEM;
                        Constants.WANT_TO_REORDER = pojo.responsedata.WANT_TO_REORDER;
                        Constants.YES_LABEL = pojo.responsedata.YES_LABEL;
                        Constants.NO_LABEL = pojo.responsedata.NO_LABEL;
                        Constants.OFFLINE = pojo.responsedata.OFFLINE;
                        Constants.ONLINE = pojo.responsedata.ONLINE;
                        Constants.REMOVE_FROM_FAV = pojo.responsedata.REMOVE_FROM_FAV;
                        Constants.NO_IN_FAV = pojo.responsedata.NO_IN_FAV;
                        Constants.TERMS_AND_CONDITIONS = pojo.responsedata.TERMS_AND_CONDITIONS;
                        Constants.COOKIES_POLICY = pojo.responsedata.COOKIES_POLICY;
                        Constants.PROVIDE_VAILD_NUMBER = pojo.responsedata.PROVIDE_VAILD_NUMBER;
                        Constants.PROVIDE_VAILD_NAME = pojo.responsedata.PROVIDE_VAILD_NAME;
                        Constants.PROVIDE_MSG = pojo.responsedata.PROVIDE_MSG;
                        Constants.PROVIDE_CONTACT_NO = pojo.responsedata.PROVIDE_CONTACT_NO;
                        Constants.CONFIRM = pojo.responsedata.CONFIRM;
                        Constants.SUBMIT_DETAIL = pojo.responsedata.SUBMIT_DETAIL;
                        Constants.ORDER_NOTIFICATION = pojo.responsedata.ORDER_NOTIFICATION;
                        Constants.NEWS_AND_OFFERS = pojo.responsedata.NEWS_AND_OFFERS;
                        Constants.EMAIL_NOTIFICATION = pojo.responsedata.EMAIL_NOTIFICATION;
                        Constants.WENT_WRONG = pojo.responsedata.WENT_WRONG;
                        Constants.TOTAL_PAYABLE_AMT = pojo.responsedata.TOTAL_PAYABLE_AMT;
                        Constants.DELIVERY_TIME = pojo.responsedata.DELIVERY_TIME;
                        Constants.RES_CLOSED_DELIVERY_TIME = pojo.responsedata.RES_CLOSED_DELIVERY_TIME;
                        Constants.PROVIDE_PAYMENT_TYPE = pojo.responsedata.PROVIDE_PAYMENT_TYPE;
                        Constants.PROVIDE_CARD = pojo.responsedata.PROVIDE_CARD;
                        Constants.PROVIDE_DELIVERY_TIME = pojo.responsedata.PROVIDE_DELIVERY_TIME;
                        Constants.PROVIDE_CARD_TYPE = pojo.responsedata.PROVIDE_CARD_TYPE;
                        Constants.PROVIDE_CVV_CODE = pojo.responsedata.PROVIDE_CVV_CODE;
                        Constants.PROVIDE_EXPIRY_YEAR = pojo.responsedata.PROVIDE_EXPIRY_YEAR;
                        Constants.PROVIDE_EXPIRY_MONTH = pojo.responsedata.PROVIDE_EXPIRY_MONTH;
                        Constants.PROVIDE_CARD_NO = pojo.responsedata.PROVIDE_CARD_NO;
                        Constants.SELECT_EXP_MONTH = pojo.responsedata.SELECT_EXP_MONTH;
                        Constants.SELECT_EXP_YEAR = pojo.responsedata.SELECT_EXP_YEAR;
                        Constants.INSTALL_INSTA_APP = pojo.responsedata.INSTALL_INSTA_APP;
                        Constants.MENU = pojo.responsedata.MENU;
                        Constants.PRODUCT_LIST = pojo.responsedata.PRODUCT_LIST;
                        Constants.ALL_ENABLE_DISABLE = pojo.responsedata.ALL_ENABLE_DISABLE;
                        Constants.ITEMS = pojo.responsedata.ITEMS;
                        Constants.ITEM_DETAIL = pojo.responsedata.ITEM_DETAIL;
                        Constants.TYPE_OF_CUSINE = pojo.responsedata.TYPE_OF_CUSINE;
                        Constants.SHOW_CUST = pojo.responsedata.SHOW_CUST;
                        Constants.CUSTOMIZATION = pojo.responsedata.CUSTOMIZATION;
                        Constants.PAST_ORDERS = pojo.responsedata.PAST_ORDERS;
                        Constants.CANCEL_NOTE = pojo.responsedata.CANCEL_NOTE;
                        Constants.DASHBOARD = pojo.responsedata.DASHBOARD;
                        Constants.UPCOMING = pojo.responsedata.UPCOMING;
                        Constants.DECLINE = pojo.responsedata.DECLINE;
                        Constants.DELIVER = pojo.responsedata.DELIVER;
                        Constants.SCHEDULE = pojo.responsedata.SCHEDULE;
                        Constants.HOLIDAY = pojo.responsedata.HOLIDAY;
                        Constants.OPENING_TIME = pojo.responsedata.OPENING_TIME;
                        Constants.CLOSING_TIME = pojo.responsedata.CLOSING_TIME;
                        Constants.REPLY = pojo.responsedata.REPLY;
                        Constants.PAYMENT_TYPE = pojo.responsedata.PAYMENT_TYPE;
                        Constants.SPE_NOTE = pojo.responsedata.SPE_NOTE;
                        Constants.ADD_ONS = pojo.responsedata.ADD_ONS;
                        Constants.REMOVE = pojo.responsedata.REMOVE;
                        Constants.TOTAL_AMT = pojo.responsedata.TOTAL_AMT;
                        Constants.SCANNER = pojo.responsedata.SCANNER;
                        Constants.SCAN_BARCODE = pojo.responsedata.SCAN_BARCODE;
                        Constants.ADD_POINT = pojo.responsedata.ADD_POINT;
                        Constants.CUST_INFO = pojo.responsedata.CUST_INFO;
                        Constants.ENTER_MANGALS = pojo.responsedata.ENTER_MANGALS;
                        Constants.NOTIFICATION = pojo.responsedata.NOTIFICATION;
                        Constants.SUPPORT = pojo.responsedata.SUPPORT;
                        Constants.SEND_QUERY_TEAM = pojo.responsedata.SEND_QUERY_TEAM;
                        Constants.SEND = pojo.responsedata.SEND;
                        Constants.WRITE_QUERY_HERE = pojo.responsedata.WRITE_QUERY_HERE;
                        Constants.PROFILE = pojo.responsedata.PROFILE;
                        Constants.CHANGE_PASS = pojo.responsedata.CHANGE_PASS;
                        Constants.OLD_PASS = pojo.responsedata.OLD_PASS;
                        Constants.NEW_PASS = pojo.responsedata.NEW_PASS;
                        Constants.DELIVERY_TYPE_LATER = pojo.responsedata.DELIVERY_TYPE_LATER;
                        Constants.DELIVERY_TYPE_NOW = pojo.responsedata.DELIVERY_TYPE_NOW;
                        Constants.CASH_ON_DELIVERY = pojo.responsedata.CASH_ON_DELIVERY;
                        Constants.ACCEPTED = pojo.responsedata.ACCEPTED;
                        Constants.CANCELLED = pojo.responsedata.CANCELLED;
                        Constants.PROVIDE_OLD_PASS = pojo.responsedata.PROVIDE_OLD_PASS;
                        Constants.PROVIDE_NEW_PASS = pojo.responsedata.PROVIDE_NEW_PASS;
                        Constants.PASS_NOT_MATCH = pojo.responsedata.PASS_NOT_MATCH;
                        Constants.SCANNING = pojo.responsedata.SCANNING;
                        Constants.PROVIDE_MANGALS = pojo.responsedata.PROVIDE_MANGALS;
                        Constants.OUR_MENU = pojo.responsedata.OUR_MENU;
                        Constants.CART_CONTAIN = pojo.responsedata.CART_CONTAIN;
                        Constants.CART_DISCARD = pojo.responsedata.CART_DISCARD;
                        Constants.ITEM_CUST = pojo.responsedata.ITEM_CUST;
                        Constants.ADD_CUST = pojo.responsedata.ADD_CUST;
                        Constants.ANOTHER_ITEM = pojo.responsedata.ANOTHER_ITEM;
                        Constants.RED_ADD = pojo.responsedata.RED_ADD;
                        Constants.OPEN_CLOSE = pojo.responsedata.OPEN_CLOSE;
                        Constants.FAV = pojo.responsedata.FAV;
                        Constants.NO_ITEM_FND = pojo.responsedata.NO_ITEM_FND;
                        Constants.CHANGE_COMPANY = pojo.responsedata.CHANGE_COMPANY;
                        Constants.CHANGE_ADDRESS = pojo.responsedata.CHANGE_ADDRESS;
                        Constants.CHOOSE_OPT = pojo.responsedata.CHOOSE_OPT;
                        Constants.CAMERA = pojo.responsedata.CAMERA;
                        Constants.CANCEL = pojo.responsedata.CANCEL;
                        Constants.CLOSE = pojo.responsedata.CLOSE;
                        Constants.SELECT_LANG = pojo.responsedata.SELECT_LANGUAGE;
                        Constants.RATE_NOW = pojo.responsedata.RATE_NOW;
                        Constants.PROVIDE_COMMENT = pojo.responsedata.PROVIDE_COMMENT;
                        Constants.NOW = pojo.responsedata.NOW;
                        Constants.NAME = pojo.responsedata.NAME;
                        Constants.AVAILABLE_MANGAL = pojo.responsedata.AVAILABLE_MANGAL;
                        Constants.REDEEM_MANGAL = pojo.responsedata.REDEEM_MANGAL;
                        Constants.PAY_CARD = pojo.responsedata.PAY_CARD;
                        Constants.ORDER_NOT_FND = pojo.responsedata.ORDER_NOT_FND;
                        Constants.ACCEPT = pojo.responsedata.ACCEPT;
                        Constants.REASON = pojo.responsedata.REASON;
                        Constants.PROD_NOT_AVAIL = pojo.responsedata.PROD_NOT_AVAIL;
                        Constants.RES_CLOSED = pojo.responsedata.RES_CLOSED;
                        Constants.INGREDIENT = pojo.responsedata.INGREDIENT;
                        Constants.TOTAL_COMP_ORDER = pojo.responsedata.TOTAL_COMP_ORDER;
                        Constants.TOTAL_COMP_ORDER_PRICE = pojo.responsedata.TOTAL_COMP_ORDER_PRICE;
                        Constants.TOTAL_DECLINE_ORDER = pojo.responsedata.TOTAL_DECLINE_ORDER;
                        Constants.TOTAL_DECLINE_ORDER_PRICE = pojo.responsedata.TOTAL_DECLINE_ORDER_PRICE;
                        Constants.SELECT = pojo.responsedata.SELECT;
                        Constants.STREET = pojo.responsedata.STREET;
                        Constants.BUILDING_NO = pojo.responsedata.BUILDING_NO;
                        Constants.RES_WEBSITE = pojo.responsedata.RES_WEBSITE;
                        Constants.PROVIDE_CONF_PASS = pojo.responsedata.PROVIDE_CONF_PASS;
                        Constants.ALLERGENS = pojo.responsedata.ALLERGENS;
                        Constants.WRONG_PASS_MSG = pojo.responsedata.WRONG_PASS_MSG;
                        Constants.WRONG_EMAIL_MSG = pojo.responsedata.WRONG_EMAIL_MSG;
                        Constants.ENTER_REQUIRED_FIELD_MSG = pojo.responsedata.ENTER_REQUIRED_FIELD_MSG;
                        Constants.LANG_FOUNDED_MSG = pojo.responsedata.LANG_FOUNDED_MSG;
                        Constants.UNAUTH_ACCESS_MSG = pojo.responsedata.UNAUTH_ACCESS_MSG;
                        Constants.LANG_NOT_FOUNDED_MSG = pojo.responsedata.LANG_NOT_FOUNDED_MSG;
                        Constants.LOCATION_PERMISSION = pojo.responsedata.LOCATION_PERMISSION;
                        Constants.LANG_SET_MSG = pojo.responsedata.LANG_SET_MSG;
                        Constants.CART_UPDATED_MSG = pojo.responsedata.CART_UPDATED_MSG;
                        Constants.SOMETHING_WRONG_MSG = pojo.responsedata.SOMETHING_WRONG_MSG;
                        Constants.ITEM_REMOVE_FROM_CART_MSG = pojo.responsedata.ITEM_REMOVE_FROM_CART_MSG;
                        Constants.OFFER_FOUNDED_MSG = pojo.responsedata.OFFER_FOUNDED_MSG;
                        Constants.OFFER_NOT_FOUNDED_MSG = pojo.responsedata.OFFER_NOT_FOUNDED_MSG;
                        Constants.RES_NOT_NEAR_YOU_MSG = pojo.responsedata.RES_NOT_NEAR_YOU_MSG;
                        Constants.ITEM_ADD_FAV_MSG = pojo.responsedata.ITEM_ADD_FAV_MSG;
                        Constants.ALREADY_FAV_MSG = pojo.responsedata.ALREADY_FAV_MSG;
                        Constants.ITEM_REMOVE_FAV_MSG = pojo.responsedata.ITEM_REMOVE_FAV_MSG;
                        Constants.ITEM_NOT_IN_FAV_MSG = pojo.responsedata.ITEM_NOT_IN_FAV_MSG;
                        Constants.ITEM_NOT_FOUNDED_MSG = pojo.responsedata.ITEM_NOT_FOUNDED_MSG;
                        Constants.APPLY_SUCCESS_MSG = pojo.responsedata.APPLY_SUCCESS_MSG;
                        Constants.NOT_ENOUGH_MANGALS_MSG = pojo.responsedata.NOT_ENOUGH_MANGALS_MSG;
                        Constants.ITEM_FOUNDED_MSG = pojo.responsedata.ITEM_FOUNDED_MSG;
                        Constants.CART_EMPTY_MSG = pojo.responsedata.CART_EMPTY_MSG;
                        Constants.SUCCESS_MSG = pojo.responsedata.SUCCESS_MSG;
                        Constants.PAYMENT_METHOD_INVALID_MSG = pojo.responsedata.PAYMENT_METHOD_INVALID_MSG;
                        Constants.CART_NOT_EXIST_MSG = pojo.responsedata.CART_NOT_EXIST_MSG;
                        Constants.ADDED_IN_CART_MSG = pojo.responsedata.ADDED_IN_CART_MSG;
                        Constants.SOMETHING_NOT_ADDED_IN_CART_MSG = pojo.responsedata.SOMETHING_NOT_ADDED_IN_CART_MSG;
                        Constants.NOT_ADDED_IN_CART_MSG = pojo.responsedata.NOT_ADDED_IN_CART_MSG;
                        Constants.CART_NOT_FOUND_MSG = pojo.responsedata.CART_NOT_FOUND_MSG;
                        Constants.ITEM_NO_LONGER_AVA_MSG = pojo.responsedata.ITEM_NO_LONGER_AVA_MSG;
                        Constants.ITEM_MISMATCH_RES_MSG = pojo.responsedata.ITEM_MISMATCH_RES_MSG;
                        Constants.RES_CLOSE_MSG = pojo.responsedata.RES_CLOSE_MSG;
                        Constants.RES_FOUNDED_MSG = pojo.responsedata.RES_FOUNDED_MSG;
                        Constants.RES_NOT_FOUNDED_MSG = pojo.responsedata.RES_NOT_FOUNDED_MSG;
                        Constants.COM_NOT_CREATE_MSG = pojo.responsedata.COM_NOT_CREATE_MSG;
                        Constants.USER_NOT_REG_MSG = pojo.responsedata.USER_NOT_REG_MSG;
                        Constants.USER_ALREADY_EXIST_MSG = pojo.responsedata.USER_ALREADY_EXIST_MSG;
                        Constants.PRO_UPDATED_MSG = pojo.responsedata.PRO_UPDATED_MSG;
                        Constants.ITEM_NOT_AVA_MSG = pojo.responsedata.ITEM_NOT_AVA_MSG;
                        Constants.NOT_ANY_FAV_MSG = pojo.responsedata.NOT_ANY_FAV_MSG;
                        Constants.ORDER_FOUNDED_MSG = pojo.responsedata.ORDER_FOUNDED_MSG;
                        Constants.ORDER_NOT_FOUNDED_MSG = pojo.responsedata.ORDER_NOT_FOUNDED_MSG;
                        Constants.ALREADY_SHARE_MSG = pojo.responsedata.ALREADY_SHARE_MSG;
                        Constants.INVALID_SHARE_MSG = pojo.responsedata.INVALID_SHARE_MSG;
                        Constants.CART_FOUNDED_MSG = pojo.responsedata.CART_FOUNDED_MSG;
                        Constants.CART_NOT_FOUNDED = pojo.responsedata.CART_NOT_FOUNDED;
                        Constants.CARD_ADDED_MSG = pojo.responsedata.CARD_ADDED_MSG;
                        Constants.CARD_NOT_ADDED_MSG = pojo.responsedata.CARD_NOT_ADDED_MSG;
                        Constants.CARD_SET_MSG = pojo.responsedata.CARD_SET_MSG;
                        Constants.CARD_NOT_FOUNDED_MSG = pojo.responsedata.CARD_NOT_FOUNDED_MSG;
                        Constants.LOGOUT_MSG = pojo.responsedata.LOGOUT_MSG;
                        Constants.RECORD_NOT_FOUNDED_MSG = pojo.responsedata.RECORD_NOT_FOUNDED_MSG;
                        Constants.RATING_ADDED_MSG = pojo.responsedata.RATING_ADDED_MSG;
                        Constants.CONTACT_SUB_MSG = pojo.responsedata.CONTACT_SUB_MSG;
                        Constants.QUE_ALREADY_MSG = pojo.responsedata.QUE_ALREADY_MSG;
                        Constants.DEV_TOKEN_ADDED_MSG = pojo.responsedata.DEV_TOKEN_ADDED_MSG;
                        Constants.QUE_NOT_FOUNDED = pojo.responsedata.QUE_NOT_FOUNDED;
                        Constants.QUE_FOUNDED = pojo.responsedata.QUE_FOUNDED;
                        Constants.MAIL_SENT_MSG = pojo.responsedata.MAIL_SENT_MSG;
                        Constants.USER_NOT_EXIST_EMAIL_MSG = pojo.responsedata.USER_NOT_EXIST_EMAIL_MSG;
                        Constants.PAGE_FOUNDED_MSG = pojo.responsedata.PAGE_FOUNDED_MSG;
                        Constants.PAGE_NOT_FOUNDED = pojo.responsedata.PAGE_NOT_FOUNDED;
                        Constants.STATUS_CHANGED_MSG = pojo.responsedata.STATUS_CHANGED_MSG;
                        Constants.INVALID_NOTI_MSG = pojo.responsedata.INVALID_NOTI_MSG;
                        Constants.ADD_UPDATED_MSG = pojo.responsedata.ADD_UPDATED_MSG;
                        Constants.COM_DETAIL_UPDATED_MSG = pojo.responsedata.COM_DETAIL_UPDATED_MSG;
                        Constants.IMG_FOUNDED_MSG = pojo.responsedata.IMG_FOUNDED_MSG;
                        Constants.IMG_NOT_FOUNDED_MSG = pojo.responsedata.IMG_NOT_FOUNDED_MSG;
                        Constants.ADD_SAVED_MSG = pojo.responsedata.ADD_SAVED_MSG;
                        Constants.ADD_FOUNDED_MSG = pojo.responsedata.ADD_FOUNDED_MSG;
                        Constants.ADD_NOT_FOUNDED_MSG = pojo.responsedata.ADD_NOT_FOUNDED_MSG;
                        Constants.ADD_SET_MSG = pojo.responsedata.ADD_SET_MSG;
                        Constants.THANK_FOR_FEEDBACK_MSG = pojo.responsedata.THANK_FOR_FEEDBACK_MSG;
                        Constants.ALREADY_REVIEW_MSG = pojo.responsedata.ALREADY_REVIEW_MSG;
                        Constants.REVIEW_FOUNDED_MSG = pojo.responsedata.REVIEW_FOUNDED_MSG;
                        Constants.REVIEW_NOT_FOUNDED_MSG = pojo.responsedata.REVIEW_NOT_FOUNDED_MSG;
                        Constants.ITEM_FOUNDED_IN_CAT_MSG = pojo.responsedata.ITEM_FOUNDED_IN_CAT_MSG;
                        Constants.ORDER_STATUS_CHANGED_MSG = pojo.responsedata.ORDER_STATUS_CHANGED_MSG;
                        Constants.ORDER_ACCEPTED_MSG = pojo.responsedata.ORDER_ACCEPTED_MSG;
                        Constants.ORDER_DECLINED_MSG = pojo.responsedata.ORDER_DECLINED_MSG;
                        Constants.ORDER_PREAPRED_MSG = pojo.responsedata.ORDER_PREAPRED_MSG;
                        Constants.ORDER_DELIVERED_MSG = pojo.responsedata.ORDER_DELIVERED_MSG;
                        Constants.POINTS_ADDED_MSG = pojo.responsedata.POINTS_ADDED_MSG;
                        Constants.USER_NOT_FOUNDED = pojo.responsedata.USER_NOT_FOUNDED;
                        Constants.CAT_FOUNDED = pojo.responsedata.CAT_FOUNDED;
                        Constants.CAT_NOT_FOUNDED_MSG = pojo.responsedata.CAT_NOT_FOUNDED_MSG;
                        Constants.USER_FOUNDED_MSG = pojo.responsedata.USER_FOUNDED_MSG;
                        Constants.REPLY_ADDED_MSG = pojo.responsedata.REPLY_ADDED_MSG;
                        Constants.ITEM_STATUS_CHANGED = pojo.responsedata.ITEM_STATUS_CHANGED;
                        Constants.INVALID_STATUS = pojo.responsedata.INVALID_STATUS;
                        Constants.TIME_CHANGED_MSG = pojo.responsedata.TIME_CHANGED_MSG;
                        Constants.UNAUTH_ORDER_ACCESS_MSG = pojo.responsedata.UNAUTH_ORDER_ACCESS_MSG;
                        Constants.LOGIN_SUCCESS_MSG = pojo.responsedata.LOGIN_SUCCESS_MSG;
                        Constants.HOLIDAY_FOUNDED_MSG = pojo.responsedata.HOLIDAY_FOUNDED_MSG;
                        Constants.HOLIDAY_NOT_FOUNDED_MSG = pojo.responsedata.HOLIDAY_NOT_FOUNDED_MSG;
                        Constants.TICKET_GEN_MSG = pojo.responsedata.TICKET_GEN_MSG;
                        Constants.ITEM_CUST_FOUNDED_MSG = pojo.responsedata.ITEM_CUST_FOUNDED_MSG;
                        Constants.ITEM_CUST_NOT_FOUNDED_MSG = pojo.responsedata.ITEM_CUST_NOT_FOUNDED_MSG;
                        Constants.PASS_CHANGED__MSG = pojo.responsedata.PASS_CHANGED__MSG;
                        Constants.HOLIDAY_ADDED_MSG = pojo.responsedata.HOLIDAY_ADDED_MSG;
                        startActivityMethod();
                    }


                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
                Log.e( "orderList Error", error );
            }
        } );
    }

    public void getHashKey() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo( getPackageName(), PackageManager.GET_SIGNATURES );
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance( "SHA" );
                md.update( signature.toByteArray() );
                String something = new String( Base64.encode( md.digest(), 0 ) );
                Log.i( "HASH_KEY", something );
            }
        } catch (Exception e) {
            Log.i( "HashKey_Exception", e.toString() );
        }
    }
}
