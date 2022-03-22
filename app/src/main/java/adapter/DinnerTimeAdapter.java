package adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.StoreDetailActivity;
import com.angaihouse.databinding.RowBookingTimeBinding;

import java.util.ArrayList;

import pojo.GetTimePojo;

public class DinnerTimeAdapter extends RecyclerView.Adapter<DinnerTimeAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<GetTimePojo.Evening> arrayList;
    StoreDetailActivity.ItemClickListener itemClickListener;

    public DinnerTimeAdapter(AppCompatActivity activity, ArrayList<GetTimePojo.Evening> responsedata, StoreDetailActivity.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = responsedata;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_booking_time, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final GetTimePojo.Evening pojo = arrayList.get(position);
        holder.binding.tvTime.setText( pojo.time );
        holder.binding.tvSeat.setText( pojo.available_capacity + " Seat Left" );
        if (pojo.discount.length() > 0){
            holder.binding.tvDiscount.setVisibility( View.VISIBLE );
            holder.binding.tvDiscount.setText( pojo.discount );
        }else {
            holder.binding.tvDiscount.setVisibility( View.GONE );
        }

        holder.itemView.setOnClickListener( v -> itemClickListener.time(pojo.time ,Integer.parseInt( pojo.total_capacity ) ) );
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowBookingTimeBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}