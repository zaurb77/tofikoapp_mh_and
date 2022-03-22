package com.angaihouse.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.angaihouse.R;

public class CEditText extends AppCompatEditText {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public CEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context, attrs, 0);
    }

    public CEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomFont(context, attrs, defStyle);
    }

    private void applyCustomFont(Context context, AttributeSet attrs, int defStyle) {
        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CTextView, defStyle, 0);
        boolean isRequired = typedArray.getBoolean(R.styleable.CTextView_is_required, false);
        if (isRequired) {
            SpannableString string = new SpannableString(getHint().toString() + "*");
            string.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorRed)), getHint().toString().length(), getHint().toString().length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setHint(string);
        }
        setTypeface(selectTypeface(context, textStyle));
        if (getGravity() == 49 || getGravity() == Gravity.CENTER || getGravity() == Gravity.CENTER_HORIZONTAL || getGravity() == Gravity.CENTER_VERTICAL) {

        } else {
            setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
            setTextDirection(TEXT_DIRECTION_LOCALE);
        }
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
