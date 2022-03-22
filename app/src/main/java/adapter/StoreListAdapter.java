package adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.newmodel.MenuActivity;
import com.angaihouse.databinding.RowStoreListingBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.StoreUserData;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import pojo.RestaurantListPojo;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<RestaurantListPojo.ResponseData> arrayList;
    String selectedAddressId;

    public StoreListAdapter(Activity activity,String selectedAddressId, ArrayList<RestaurantListPojo.ResponseData> arrayList/*, PaymentInfoScreen.ItemClickListener itemClickListener*/) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.selectedAddressId = selectedAddressId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_store_listing, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final RestaurantListPojo.ResponseData pojo = arrayList.get(position);

        holder.binding.storeName.setText(pojo.name);
        holder.binding.storeDetail.setText(pojo.description);
        holder.binding.storePhoneNumber.setText(pojo.mobile_no);
        holder.binding.storeDay.setText(pojo.days);
        holder.binding.closeOpenTime.setText(pojo.open_close_time1+"\n"+pojo.open_close_time2);

        Glide.with(activity)
                .load(pojo.image)
                .placeholder(R.drawable.notfound)
                .into(holder.binding.storeImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new StoreUserData(activity).setString(Constants.NEAR_RES_ID,""+pojo.id);
                activity.startActivity(new Intent(activity, MenuActivity.class)
                        .putExtra("ADDRESSID", "" + selectedAddressId)
                        .putExtra("ORDERTYPE", "delivery")
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowStoreListingBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}