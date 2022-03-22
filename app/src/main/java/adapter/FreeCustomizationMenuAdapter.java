package adapter;

import android.app.Activity;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.newmodel.MenuActivity;
import com.angaihouse.databinding.RowItemCustomBinding;

import java.util.ArrayList;

import pojo.CategoryItemPojo;

public class FreeCustomizationMenuAdapter extends RecyclerView.Adapter<FreeCustomizationMenuAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<CategoryItemPojo.ResponseData.FreeCustomization> arrayList;
    private MenuActivity.ItemClickListener itemClickListener;

    public FreeCustomizationMenuAdapter(Activity activity, ArrayList<CategoryItemPojo.ResponseData.FreeCustomization> arrayList, MenuActivity.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_custom, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CategoryItemPojo.ResponseData.FreeCustomization pojo = arrayList.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.binding.itemName.setText(Html.fromHtml(pojo.name, Html.FROM_HTML_MODE_COMPACT));
        }else {
            holder.binding.itemName.setText(Html.fromHtml(pojo.name));
        }

        if (pojo.freeSelected) {
            holder.binding.select.setVisibility(View.VISIBLE);
        } else {
            holder.binding.select.setVisibility(View.GONE);
        }

        holder.binding.llAddItem.setOnClickListener(view -> {
            if (pojo.freeSelected){
                pojo.freeSelected = false;
                itemClickListener.freeCustomizationRemove(pojo.name);
            }else {
                pojo.freeSelected = true;
                itemClickListener.freeCustomizeClick(pojo.name);
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowItemCustomBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}