package adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.angaihouse.R;
import com.angaihouse.utils.StoreUserData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class PopupAdapter implements GoogleMap.InfoWindowAdapter {

    AppCompatActivity activity;
    LayoutInflater inflater;

    public PopupAdapter(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        inflater = (LayoutInflater)
                activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.info_window, null);
        TextView username = v.findViewById(R.id.infoTitle);
        username.setText("Sanjay Damor");


        return v;
    }
}