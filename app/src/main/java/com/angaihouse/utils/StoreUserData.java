package com.angaihouse.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class StoreUserData {

    public static String APP_KEY;
    private SharedPreferences pref = null;
    private Context parentActivity;

    public StoreUserData(Context context) {
        parentActivity = context;
        APP_KEY = context.getPackageName().replaceAll("\\.", "_").toLowerCase();
    }


    public void setString(String key, String value) {
        pref = parentActivity.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        pref = parentActivity.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }

    public void setDouble(String key, double value) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value + "");
        editor.apply();
    }

    public Double getDouble(String key) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        if (pref.getString(key, "").length() > 0) {
            return Double.parseDouble(pref.getString(key, ""));
        } else {
            return -99.99;
        }
    }

    public void setBoolean(String key, boolean value) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }


    public void setInt(String key, int value) {
        pref = parentActivity.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        return pref.getInt(key, -1);
    }

    public void setLong(String key, long value) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public long getLong(String key) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        return pref.getLong(key, -1);
    }

    public boolean is_exist(String key) {
        pref = parentActivity.getSharedPreferences(APP_KEY,
                Context.MODE_PRIVATE);
        return pref.contains(key);
    }

    public void clearData(Context context) {
        SharedPreferences settings = context.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE);
        settings.edit().clear().apply();
    }

}
