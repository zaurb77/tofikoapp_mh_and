package adapter;

import android.app.Activity;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.newmodel.DetailActivity;
import com.angaihouse.databinding.RowItemCustomPaidBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.StoreUserData;

import java.util.ArrayList;

import pojo.DetailPojo;

public class PaidCustomizationMenuDetailAdapter extends RecyclerView.Adapter<PaidCustomizationMenuDetailAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<DetailPojo.Responsedata.PaidCustomization> arrayList;
    private DetailActivity.ItemClickListener itemClickListener;
    StoreUserData storeUserData;

    public PaidCustomizationMenuDetailAdapter(Activity activity, ArrayList<DetailPojo.Responsedata.PaidCustomization> arrayList, DetailActivity.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.itemClickListener = itemClickListener;
        storeUserData = new StoreUserData( activity );
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_custom_paid, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final DetailPojo.Responsedata.PaidCustomization pojo = arrayList.get(position);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.binding.itemName.setText(Html.fromHtml(pojo.name, Html.FROM_HTML_MODE_COMPACT));
        }else {
            holder.binding.itemName.setText(pojo.name);
        }


        if (!TextUtils.isEmpty(pojo.price)) {
            holder.binding.price.setText(storeUserData.getString( Constants.CURRENCY )+" " + pojo.price);
        }else {
            holder.binding.price.setText("");
        }

        if (pojo.paidSelected) {
            holder.binding.select.setVisibility(View.VISIBLE);
        } else {
            holder.binding.select.setVisibility(View.GONE);
        }


        holder.binding.llAddItem.setOnClickListener(view -> {
            if (pojo.paidSelected){
                pojo.paidSelected = false;
                itemClickListener.paidCustomizeRemove(pojo.name+"="+pojo.price);
            }else {
                pojo.paidSelected = true;
                itemClickListener.paidCustomizeClick(pojo.name+"="+pojo.price);
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowItemCustomPaidBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}