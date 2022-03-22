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
import com.angaihouse.activity.newmodel.MenuActivity;
import com.angaihouse.databinding.RowItemCustomTestBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.StoreUserData;

import java.util.ArrayList;

import pojo.CategoryItemPojo;

public class TasteCustomizationMenuAdapter extends RecyclerView.Adapter<TasteCustomizationMenuAdapter.ViewHolder> {

    Activity activity;
    int select = -1;
    private ArrayList<CategoryItemPojo.ResponseData.Taste> arrayList;
    private MenuActivity.ItemClickListener itemClickListener;
    StoreUserData storeUserData;


    public TasteCustomizationMenuAdapter(Activity activity, ArrayList<CategoryItemPojo.ResponseData.Taste> arrayList, MenuActivity.ItemClickListener itemClickListener) {
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
        final CategoryItemPojo.ResponseData.Taste pojo = arrayList.get(position);

        if (select == position) {
            holder.binding.select.setImageResource(R.drawable.fill);
            if (pojo.price.length() > 0){
                itemClickListener.tasteCustomizeTestClick(pojo.name+"="+pojo.price);
            }else {
                itemClickListener.tasteCustomizeTestClick(pojo.name);
            }
        } else {
            holder.binding.select.setImageResource(R.drawable.empty);
        }


        if (select == -1 && pojo.is_default == 1){
            holder.binding.select.setImageResource(R.drawable.fill);
            if (pojo.price.length() > 0){
                itemClickListener.tasteCustomizeTestClick(pojo.name+"="+pojo.price);
            }else {
                itemClickListener.tasteCustomizeTestClick(pojo.name);
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.binding.itemName.setText(Html.fromHtml(pojo.name, Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.binding.itemName.setText(Html.fromHtml(pojo.name));
        }

        if (!TextUtils.isEmpty(pojo.price)) {
            holder.binding.price.setText(storeUserData.getString( Constants.CURRENCY )+" " + pojo.price);
        } else {
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