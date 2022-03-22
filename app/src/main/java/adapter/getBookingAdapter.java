package adapter;

import android.app.Activity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.PaymentInfoScreen;
import com.angaihouse.activity.SelectRestaurantActivity;
import com.angaihouse.databinding.NotificationRowBinding;
import com.angaihouse.databinding.RowRestaurantListBinding;
import com.angaihouse.utils.Constants;
import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pojo.NotificationPojo;

public class getBookingAdapter extends RecyclerView.Adapter<getBookingAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<NotificationPojo.Responsedata> arrayList;
    SelectRestaurantActivity.ItemClickListener itemClickListener;

    public getBookingAdapter(AppCompatActivity activity, ArrayList<NotificationPojo.Responsedata> responsedata, SelectRestaurantActivity.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = responsedata;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_restaurant_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final NotificationPojo.Responsedata pojo = arrayList.get(position);

        Glide.with( activity ).load( pojo.store_image ).into( holder.binding.foodImage );
        holder.binding.name.setText( pojo.store_name );
        holder.binding.tvAddress.setText( pojo.address );
        holder.binding.tvMember.setText( pojo.guests );
        holder.binding.tvTime.setText( pojo.dt + "  " + pojo.tm );
        holder.binding.tvTotalAmount.setText( pojo.status);

        holder.binding.tvSpecialRequestTitle.setText( Constants.SPECIAL_REQUEST );
        holder.binding.tvReplayTitle.setText( Constants.REPLY  );

        if (pojo.status.equalsIgnoreCase( "approved" )){
            holder.binding.tvSearch.setVisibility( View.VISIBLE );
        }else if (pojo.status.equalsIgnoreCase( "pending" )){
            holder.binding.tvSearch.setVisibility( View.VISIBLE );
        }else{
            holder.binding.tvSearch.setVisibility( View.GONE );
        }

        if (pojo.reason.length() > 0){
            holder.binding.tvSpecialRequestTitle.setVisibility( View.GONE );
            holder.binding.tvSpecialRequest.setVisibility( View.GONE );
            holder.binding.tvReplayTitle.setVisibility( View.GONE );
            holder.binding.tvReplay.setVisibility( View.GONE );
            holder.binding.tvReason.setVisibility( View.VISIBLE );
            holder.binding.tvReason.setText( pojo.reason );
        }else if (pojo.notes.length() > 0) {
            holder.binding.tvReason.setVisibility( View.GONE );

            if (pojo.notes.length() > 0){
                holder.binding.tvSpecialRequestTitle.setVisibility( View.VISIBLE );
                holder.binding.tvSpecialRequest.setVisibility( View.VISIBLE );
                holder.binding.tvSpecialRequest.setText( "~ "+pojo.notes );
            }

            if (pojo.notes_manager.length() > 0){
            holder.binding.tvReplayTitle.setVisibility( View.VISIBLE );
            holder.binding.tvReplay.setVisibility( View.VISIBLE );
            holder.binding.tvReplay.setText( "~ "+pojo.notes_manager );
            }
        }

        holder.binding.tvSearch.setText( Constants.CANCEL_BOOKING );

        holder.binding.tvSearch.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.cancelBooking(pojo);
            }
        } );
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowRestaurantListBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}