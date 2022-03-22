package com.angaihouse.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityTableBookingBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.Utils;

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
import java.util.Date;
import java.util.Locale;

import okhttp3.internal.Util;

public class TableBookingActivity extends AppCompatActivity {

    AppCompatActivity activity;
    ActivityTableBookingBinding binding;
    private AutoCompleteTextView searchLocation;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private double latitude = 0, longitude = 0;
    static ArrayList<String> placesId = new ArrayList<>();

    private ArrayList<String> arrMember = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        activity = this;
        binding = DataBindingUtil.setContentView( activity, R.layout.activity_table_booking );
        binding.back.setOnClickListener( v -> finish() );
        for (int i =1; i <21; i++){ arrMember.add( ""+i ); }


        searchLocation = findViewById( R.id.atv_places );
        //searchLocation.setAdapter( new GooglePlacesAutocompleteAdapter( activity, R.layout.support_simple_spinner_dropdown_item ) );
//        searchLocation.setOnItemClickListener( (parent, view, position, id) -> {
//            // TODO Auto-generated method stub
//            new FocusOnMap( placesId.get( position ) ).execute();
//        } );

        binding.tvDate.setOnClickListener( v -> Utils.selectFutureDate(activity, binding.tvDate ) );

        binding.tvTime.setOnClickListener( v -> Utils.selectFutureTime(activity, binding.tvTime ) );

        binding.tvMember.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder( activity );
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>( activity, R.layout.support_simple_spinner_dropdown_item, arrMember );
                builder.setAdapter( dataAdapter, (dialog, which) -> {
                    binding.tvMember.setText( (CharSequence) arrMember.get( which ) );
                } );
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } );

        binding.tvSearch.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isEmpty( binding.atvPlaces )){
                    Utils.showTopMessageError( activity, Constants.SELECT_ADDRESS );
                }else if (Utils.isEmpty( binding.tvDate )){
                    Utils.showTopMessageError( activity, "Select Date" );
                }else if (Utils.isEmpty( binding.tvTime )){
                    Utils.showTopMessageError( activity, "Select Time" );
                }else if (Utils.isEmpty( binding.tvMember )){
                    Utils.showTopMessageError( activity, "Select Member" );
                }else {
                    startActivity( new Intent(activity, BookTableResultActivity.class)
                    .putExtra( "Date" , binding.tvDate.getText().toString() )
                    .putExtra( "Time" , binding.tvTime.getText().toString() )
                    .putExtra( "Member" , binding.tvMember.getText().toString() ) );
                }
            }
        } );
    }

    public ArrayList autocomplete(String input) {
        ArrayList resultList = null;
        placesId = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder( PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON );
            sb.append( "?key=" + getResources().getString( R.string.places_key ) );
            sb.append( "&input=" + URLEncoder.encode( input, "utf8" ) );
            Log.i( "TAG", "autocomplete: " + sb.toString() );
            URL url = new URL( sb.toString() );
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader( conn.getInputStream() );
            int read;
            char[] buff = new char[1024];
            while ((read = in.read( buff )) != -1) {
                jsonResults.append( buff, 0, read );
            }
        } catch (MalformedURLException e) {
            Log.e( "Places", e.getMessage() );
            return resultList;
        } catch (IOException e) {
            Log.e( "Places", e.getMessage() );
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            JSONObject jsonObj = new JSONObject( jsonResults.toString() );
            JSONArray predsJsonArray = jsonObj.getJSONArray( "predictions" );
            resultList = new ArrayList( predsJsonArray.length() );
            placesId = new ArrayList<String>( predsJsonArray.length() );
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println( predsJsonArray.getJSONObject( i ).getString( "description" ) );
                System.out.println( "============================================================" );
                resultList.add( predsJsonArray.getJSONObject( i ).getString( "description" ) );
                placesId.add( predsJsonArray.getJSONObject( i ).getString( "place_id" ) );
            }
        } catch (JSONException e) {
            Log.e( "JSON results", e.getMessage() );
        }
        return resultList;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super( context, textViewResourceId );
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return (String) resultList.get( index );
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        Log.i( "TAG", "performFiltering: " + constraint.toString() );
                        resultList = autocomplete( constraint.toString() );
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
                StringBuilder sb = new StringBuilder( "https://maps.googleapis.com/maps/api/place/details/json" );
                sb.append( "?placeid=" + URLEncoder.encode( string, "utf8" ) );
                sb.append( "&key=" + URLEncoder.encode( getResources().getString( R.string.places_key ), "utf8" ) );
                URL url = new URL( sb.toString() );
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader( conn.getInputStream() );
                int read;
                char[] buff = new char[1024];
                while ((read = in.read( buff )) != -1) {
                    jsonResults.append( buff, 0, read );
                }
            } catch (MalformedURLException e) {
                Log.e( "Places", e.getMessage() );
                return jsonResults.toString();
            } catch (IOException e) {
                Log.e( "Places", e.getMessage() );
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
            super.onPostExecute( response );
            if (response == null) {
            } else if (response.length() == 0) {
            } else {
                Log.d( "Response", response );
                try {
                    JSONObject jsonObj = new JSONObject( response );
                    JSONObject obj = jsonObj.getJSONObject( "result" );
                    JSONObject objGeo = obj.getJSONObject( "geometry" );
                    JSONObject objLoc = objGeo.getJSONObject( "location" );
                    latitude = objLoc.getDouble( "lat" );
                    longitude = objLoc.getDouble( "lng" );
                    Log.i( "LATLONGADD", "" + objLoc.getDouble( "lat" ) + "        " + latitude );
                    View view = activity.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE );
                        imm.hideSoftInputFromWindow( view.getWindowToken(), 0 );
                    }
                } catch (JSONException e) {
                    Log.e( "SON results", e.getMessage() );
                }
            }
        }
    }

}