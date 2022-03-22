package adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.AddAddressActivity;
import com.angaihouse.activity.AddAddressTypeActivity;
import com.angaihouse.databinding.RowAddressListBinding;

import java.util.ArrayList;

import pojo.AddressListPojo;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<AddressListPojo.ResponseData> arrayList;
    private AddAddressActivity.ItemClickListener itemClickListener;
    int pos = -1;

    public AddressListAdapter(Activity activity, ArrayList<AddressListPojo.ResponseData> arrayList, AddAddressActivity.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_address_list, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AddressListPojo.ResponseData pojo = arrayList.get(position);

        String upperString = pojo.address_type.substring(0, 1).toUpperCase() + pojo.address_type.substring(1).toLowerCase();
        holder.binding.addressType.setText(upperString);

        holder.binding.address.setText(pojo.address_line);

        if (pos == position) {
            holder.binding.selectAddress.setImageResource(R.drawable.fill);
        } else {
            holder.binding.selectAddress.setImageResource(R.drawable.empty);
        }


        holder.binding.editAddress.setOnClickListener(view -> activity.startActivity(new Intent(activity, AddAddressTypeActivity.class)
                .putExtra("EditAddress", "EditAddress")
                .putExtra("ADDRESS_TYPE", pojo.address_type)
                .putExtra("ADDRESS", pojo.address)
                .putExtra("ADDRESS_ID", "" + pojo.address_id)
                .putExtra("ADDRESS_ZIP", pojo.zip_code)
                .putExtra("ADDRESS_COUNTRY", pojo.country)
                .putExtra("ADDRESS_CITY", pojo.city)
                .putExtra("ADDRESS_DOOR_NUMNER", pojo.door_no)
                .putExtra("ADDRESS_LATITUDE", pojo.latitude)
                .putExtra("ADDRESS_LONGITUDE", pojo.longitude)
        ));


        holder.binding.delete.setOnClickListener(view -> itemClickListener.deleteAddress(pojo.address_id));


        holder.itemView.setOnClickListener(view -> {
                    pos = position;
                    itemClickListener.onClick(pojo.address_id, pojo.address);
                    notifyDataSetChanged();
                }
        );

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowAddressListBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}