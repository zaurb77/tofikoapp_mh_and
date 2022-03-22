package adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.newmodel.MenuActivity;
import com.angaihouse.databinding.RowCartCustomizationItemsBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.StoreUserData;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import pojo.CustomizationPojo;

public class CuatomizationAdapter extends RecyclerView.Adapter<CuatomizationAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<CustomizationPojo.ResponseData> arrayList;
    private MenuActivity.ItemClickListener itemClickListener;
    StoreUserData storeUserData;


    public CuatomizationAdapter(Activity activity, ArrayList<CustomizationPojo.ResponseData> arrayList, MenuActivity.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.itemClickListener = itemClickListener;
        storeUserData = new StoreUserData( activity );
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_cart_customization_items, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CustomizationPojo.ResponseData pojo = arrayList.get(position);


        holder.binding.itemName.setText(pojo.item_name);
        holder.binding.price.setText(storeUserData.getString( Constants.CURRENCY )+" " + pojo.price);
        holder.binding.category.setText(pojo.category);
        holder.binding.qty.setText(""+pojo.quantity);


        if (pojo.free_customization.length()>0 || pojo.paid_customization.length()>0){
           holder.binding.description.setVisibility(View.VISIBLE);
        }else {
            holder.binding.description.setVisibility(View.GONE);
        }

        holder.binding.description.setText(pojo.paid_customization+","+pojo.free_customization);

        Glide.with(activity)
                .load(pojo.item_image)
                .placeholder(R.drawable.notfound)
                .into(holder.binding.foodImage);

        holder.binding.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pojo.quantity > 0) {
                    pojo.quantity--;
                    itemClickListener.changeQtyCust(""+pojo.item_id,""+pojo.cart_item_id,""+pojo.quantity);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowCartCustomizationItemsBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}