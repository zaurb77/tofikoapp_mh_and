package com.angaihouse.controls;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

public class CButton extends AppCompatButton {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public CButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public CButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context, attrs);

    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);
        setTypeface(selectTypeface(context, textStyle));
        setAllCaps(false);
    }

    private Typeface selectTypeface(Context context, int textStyle) {
        switch (textStyle) {
            case Typeface.BOLD:
                return Typeface.createFromAsset(context.getAssets(), "ralewaybold.ttf");
            case Typeface.ITALIC:
                return Typeface.createFromAsset(context.getAssets(), "ralewayitalic.ttf");// regular
            default:
                return Typeface.createFromAsset(context.getAssets(), "ralewayregular.ttf");// regular
        }
    }
}