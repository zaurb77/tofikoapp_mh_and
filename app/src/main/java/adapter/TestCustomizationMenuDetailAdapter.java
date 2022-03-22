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
import com.angaihouse.databinding.RowItemCustomTestBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.StoreUserData;

import java.util.ArrayList;

import pojo.DetailPojo;

public class TestCustomizationMenuDetailAdapter extends RecyclerView.Adapter<TestCustomizationMenuDetailAdapter.ViewHolder> {

    Activity activity;
    int select = -1;
    private ArrayList<DetailPojo.Responsedata.PaidCustomization> arrayList;
    private DetailActivity.ItemClickListener itemClickListener;
    StoreUserData storeUserData;

    public TestCustomizationMenuDetailAdapter(Activity activity, ArrayList<DetailPojo.Responsedata.PaidCustomization> arrayList, DetailActivity.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.itemClickListener = itemClickListener;
        storeUserData = new StoreUserData( activity );
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_custom_test, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final DetailPojo.Responsedata.PaidCustomization pojo = arrayList.get(position);


        if (select == position) {
            holder.binding.select.setImageResource(R.drawable.fill);
            itemClickListener.paidCustomizeTestClick(pojo.name+"="+pojo.price);
        } else {
            holder.binding.select.setImageResource(R.drawable.empty);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.binding.itemName.setText(Html.fromHtml("Add on "+pojo.name, Html.FROM_HTML_MODE_COMPACT));
        }else {
            holder.binding.itemName.setText("Add on "+pojo.name);
        }


        if (!TextUtils.isEmpty(pojo.price)) {
            holder.binding.price.setText(storeUserData.getString( Constants.CURRENCY )+" " + pojo.price);
        }else {
            holder.binding.price.setText("");
        }

        holder.binding.llAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select = position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowItemCustomTestBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}