package adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.databinding.RowTimingBinding;
import com.angaihouse.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;

import pojo.RestaurantDetailPojo;

public class TimingAdapter extends RecyclerView.Adapter<TimingAdapter.ViewHolder> {

    Activity activity;
    String weekDay = "";
    private ArrayList<RestaurantDetailPojo.ResponseData.OpenCloseTime> arrayList;


    public TimingAdapter(Activity activity, ArrayList<RestaurantDetailPojo.ResponseData.OpenCloseTime> arrayList/*, HomeFragment.ItemClickListener itemClickListener*/) {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_timing, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final RestaurantDetailPojo.ResponseData.OpenCloseTime pojo = arrayList.get(position);
        holder.binding.day.setText(getDay(position + 1));
        holder.binding.openTime.setText(pojo.open_time + " AM to 11:00 PM");
        holder.binding.closeTime.setText(pojo.close_time + " PM to 12:00 PM");

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public String getDay(int dayOfWeek) {
        if (Calendar.MONDAY == dayOfWeek) {
            weekDay = Constants.TUESDAY;
        } else if (Calendar.TUESDAY == dayOfWeek) {
            weekDay = Constants.WEDNESDAY;
        } else if (Calendar.WEDNESDAY == dayOfWeek) {
            weekDay = Constants.TUESDAY;
        } else if (Calendar.THURSDAY == dayOfWeek) {
            weekDay = Constants.FRIDAY;
        } else if (Calendar.FRIDAY == dayOfWeek) {
            weekDay = Constants.SATURDAY;
        } else if (Calendar.SATURDAY == dayOfWeek) {
            weekDay = Constants.SUNDAY;
        } else if (Calendar.SUNDAY == dayOfWeek) {
            weekDay = Constants.MONDAY;
        }

        return weekDay;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowTimingBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);

        }
    }
}