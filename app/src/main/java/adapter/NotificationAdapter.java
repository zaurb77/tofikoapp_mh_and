package adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.AddAddressActivity;
import com.angaihouse.activity.AddAddressTypeActivity;
import com.angaihouse.databinding.NotificationRowBinding;
import com.angaihouse.databinding.RowAddressListBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import pojo.AddressListPojo;
import pojo.NotificationPojo;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<NotificationPojo.Responsedata> arrayList;

    public NotificationAdapter(AppCompatActivity activity, ArrayList<NotificationPojo.Responsedata> responsedata) {
        this.activity = activity;
        this.arrayList = responsedata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_row, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final NotificationPojo.Responsedata pojo = arrayList.get(position);

        holder.binding.tvTitle.setText( pojo.store_name);
        holder.binding.tvContent.setText( Html.fromHtml(pojo.description));

        Date serverDate = null;
        SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dateFormat=new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        try {
            serverDate = sdfInput.parse(pojo.cat);
        } catch (Exception e) {
            Log.e("formattedDateFromString", "Exception in formateDateFromstring(): " + e.getMessage());
        }
        holder.binding.tvDate.setText( dateFormat.format( serverDate ) );


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        NotificationRowBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}