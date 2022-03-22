package com.angaihouse.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.angaihouse.R;
import com.angaihouse.activity.LoginActivity;
import com.angaihouse.activity.newmodel.NoInternetActivity;
import com.angaihouse.controls.CustomProgressDialog;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.aviran.cookiebar2.CookieBar;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    static CustomProgressDialog dialog;

    public static void showProgress(Activity activity) {
        dialog = new CustomProgressDialog(activity);
        dialog.show();
    }

    public static void showTopMessageError(Activity activity, String message) {

        CookieBar.build(activity)
                .setTitle("Error")
                .setTitleColor(R.color.white)
                .setIcon( R.drawable.close_cookie_bar)
                .setMessage(message)
                .setBackgroundColor(R.color.colorLightGreen)
                .setDuration(2000)
                .show();
        new Handler().postDelayed(() -> {
            CookieBar.dismiss(activity);
        }, 2000);
    }

    public static void selectFutureDate(AppCompatActivity activity, final TextView btnDate) {
        //Disable All Past Date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get( Calendar.YEAR );
        int month = calendar.get( Calendar.MONTH );
        int day = calendar.get( Calendar.DAY_OF_MONTH );


        DecimalFormat mFormat = new DecimalFormat( "00" );
        mFormat.format( Double.valueOf( year ) );

        DatePickerDialog datePickerDialog = new DatePickerDialog( activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String sDate = year + "-" + (month + 1) + "-" + dayOfMonth;//2021-4-21
                String Dates = mFormat.format( Double.valueOf( year ) ) + "-" + mFormat.format( Double.valueOf( month + 1 ) ) + "-" + mFormat.format( Double.valueOf( dayOfMonth ) );//221-04-21
                SimpleDateFormat format1 = new SimpleDateFormat( "yyyy-MM-dd" );
                DateFormat format2 = new SimpleDateFormat( "MMM dd, yy" );
                try {
                    btnDate.setText( (format2.format( format1.parse( Dates ) )) );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, year, month, day
        );
        datePickerDialog.getDatePicker().setMinDate( System.currentTimeMillis() - 1000 );
        datePickerDialog.show();
    }

    public static ArrayList<String> getDatesNew(String dateString1, String dateString2) {
        ArrayList<String> dates = new ArrayList<String>();
        DateFormat df1 = new SimpleDateFormat( "yyyy-MM-dd" );

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse( dateString1 );
            date2 = df1.parse( dateString2 );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat server = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
        DateFormat week =new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime( date1 );


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime( date2 );

        while (!cal1.after( cal2 )) {

            try {
                dates.add( week.format( server.parse( String.valueOf( cal1.getTime() ) ) )     );
            } catch (Exception e) {
                e.printStackTrace();
            }


            cal1.add( Calendar.DATE, 1 );
        }
        return dates;
    }

    public static void disablePastDates(AppCompatActivity activity, ArrayList<String> arrDateList, MaterialCalendarView calendarViewSingle) {
        calendarViewSingle.addDecorator( new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                String month = (day.getMonth() + 1) + "";
                if (month.length() == 1) { month = "0" + month; }
                String date = (day.getDay()) + "";
                if (date.length() == 1) { date = "0" + date; }
                String curDate = day.getYear() + "-" + month + "-" + date;
                return arrDateList.contains( curDate );
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setDaysDisabled( true );
                view.setSelectionDrawable( activity.getResources().getDrawable( R.drawable.background_disable_date_gray ) );
            }
        } );
    }

    public static String getFirstDateOfMonth(){
        String first;
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        Calendar firstDay = Calendar.getInstance();
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
        first = s.format(firstDay.getTime());
        return first+" ";
    }

    public static String getCurrentDateOfMonth(){
        String  current;
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        Calendar firstDay = Calendar.getInstance();
        Date currentDat = Calendar.getInstance().getTime();
        firstDay.setTime( currentDat );
        firstDay.add(Calendar.DATE, -1);
        current = s.format(firstDay.getTime());

        return current;
    }

    public static void selectFutureTime(AppCompatActivity activity, TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int cHour = calendar.get( Calendar.HOUR_OF_DAY );
        int cMinute = calendar.get( Calendar.MINUTE );

        TimePickerDialog timePickerFragment = new TimePickerDialog( activity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar datetime = Calendar.getInstance();
                Calendar c = Calendar.getInstance();
                datetime.set( Calendar.HOUR_OF_DAY, hourOfDay );
                datetime.set( Calendar.MINUTE, minute );
                    int hour = hourOfDay % 12;
                    textView.setText( String.format( "%02d:%02d %s", hour == 0 ? 12 : hour,
                            minute, hourOfDay < 12 ? "am" : "pm" ) );
            }
        }, cHour, cMinute, false );
        timePickerFragment.show();
    }


    public static void showTopMessageSuccess(Activity activity, String message) {

        CookieBar.build(activity)
                .setTitle("Success")
                .setTitleColor(R.color.white)
                .setIcon(R.drawable.success_cookie_bar)
                .setMessage(message)
                .setBackgroundColor(R.color.colorLightGreen)
                .setDuration(2000) // 5 seconds
                .show();
        new Handler().postDelayed(() -> CookieBar.dismiss( activity ),2000 );

    }

    public static void dismissProgress() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        else
            Log.i("Dialog", "already dismissed");
    }


    public static void serverError(Activity activity, int code) {
        dismissProgress();
        String message = "";
        switch (code) {
            case 400:
                message = "400 - Bad Request";
                break;
            case 401:
                message = "401 - Unauthorized";
                break;
            case 404:
                message = "404 - Not Found";
                break;
            case 500:
                message = "500 - Internal Server Error";
                break;
            default:
                message = "Server error";
        }
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }


    public static void internetAlert(Activity activity) {


        activity.startActivity(new Intent(activity, NoInternetActivity.class));

    }


    public static void guestLogin(Activity activity) {

        new AlertDialog.Builder(activity)
                .setTitle("Mangal House")
                .setMessage(Constants.GUEST_ERROR)
                .setNegativeButton(Constants.NO_LABEL,null)
                .setPositiveButton(Constants.YES_LABEL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.startActivity(new Intent(activity, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                })
                .create()
                .show();
    }


    public static boolean isOnline(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public static boolean isValidEmail(EditText email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches();
    }

    public static void selectDate(Activity activity, final TextView btnDate) {
        new DatePickerDialog(activity, (view, year, monthOfYear, dayOfMonth) -> {
            btnDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DATE, dayOfMonth);
            //calendar.getTimeInMillis() + "";
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show();
    }


    public static boolean isEmpty(View view) {
        if (view instanceof EditText) {
            if (((EditText) view).getText().toString().length() == 0) {
                return true;
            }
        } else if (view instanceof Button) {
            if (((Button) view).getText().toString().length() == 0) {
                return true;
            }
        }else if (view instanceof TextView) {
            if (((TextView) view).getText().toString().length() == 0) {
                return true;
            }
        }
        return false;
    }

    public static void showListDialog(Activity activity, final TextView btnShow, final ArrayList<String> list) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_dropdown_item_1line, list);
        //pass custom layout with single textview to customize list
        builder.setAdapter(dataAdapter, (dialog, which) -> btnShow.setText(list.get(which)));
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public static void hideKB(Activity activity, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public static void getDay(Activity activity,int dayOfWeek) {
        String weekDay = "";
        if (Calendar.MONDAY == dayOfWeek) {
            weekDay = "monday";
        } else if (Calendar.TUESDAY == dayOfWeek) {
            weekDay = "tuesday";
        } else if (Calendar.WEDNESDAY == dayOfWeek) {
            weekDay = "wednesday";
        } else if (Calendar.THURSDAY == dayOfWeek) {
            weekDay = "thursday";
        } else if (Calendar.FRIDAY == dayOfWeek) {
            weekDay = "friday";
        } else if (Calendar.SATURDAY == dayOfWeek) {
            weekDay = "saturday";
        } else if (Calendar.SUNDAY == dayOfWeek) {
            weekDay = "sunday";
        }
        new StoreUserData(activity).setString(Constants.DAY, weekDay);
    }

}