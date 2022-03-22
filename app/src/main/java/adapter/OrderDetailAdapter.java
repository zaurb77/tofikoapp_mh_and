package adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.databinding.RowPaymentQtyItemBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.StoreUserData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import pojo.OrderDetailPojo;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<OrderDetailPojo.ResponseData.Items> arrayList;
    StoreUserData storeUserData;

    public OrderDetailAdapter(Activity activity, ArrayList<OrderDetailPojo.ResponseData.Items> arrayList/*, AddAddressActivity.ItemClickListener itemClickListener*/) {
        this.activity = activity;
        this.arrayList = arrayList;
        storeUserData = new StoreUserData( activity );
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_payment_qty_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final OrderDetailPojo.ResponseData.Items pojo = arrayList.get(position);

        holder.binding.llMain.setBackground(null);

        if (pojo.image_enable == 1){
            holder.binding.llCarImg.setVisibility(View.VISIBLE);
            Glide.with(activity)
                    .load(pojo.item_image)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.binding.pb.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.binding.pb.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .placeholder(R.drawable.notfound)
                    .into(holder.binding.foodImage);
        }else {
            holder.binding.llCarImg.setVisibility(View.GONE);
        }

        if(!pojo.paid_customization.equals("")) {
            holder.binding.llAddOns.setVisibility(View.VISIBLE);
            holder.binding.addOn.setText("Add On : " + pojo.paid_customization);

            if(!pojo.add_ons_cust_price.equals("")) {
                holder.binding.addOnPrice.setText(Html.fromHtml(pojo.add_ons_cust_price));
                holder.binding.addOnPrice.setVisibility(View.VISIBLE);
            }else {
                holder.binding.addOnPrice.setVisibility(View.GONE);
            }
        }else {
            holder.binding.llAddOns.setVisibility(View.GONE);
        }



        String description = "";
        if(!pojo.taste_customization.equals("")) {
            if(!description.equals("")) {
                description = description + "\nTaste : " + pojo.taste_customization;
            }else {
                description = "Taste : " + pojo.taste_customization;
            }
        }

        if(!pojo.cooking_customization.equals("")) {
            if(!description.equals("")) {
                description = description + "\nCooking Level : " + pojo.cooking_customization;
            }else {
                description = "Cooking Level : " + pojo.cooking_customization;
            }
        }

        if(!pojo.free_customization.equals("")) {
            if(!description.equals("")) {
                description = description + "\nRemove : " + pojo.free_customization;
            }else {
                description = "Remove : " + pojo.free_customization;
            }
        }
        holder.binding.customization.setText(description);
        holder.binding.itemName.setText(pojo.item_name);

        if (!TextUtils.isEmpty(pojo.category)){
            holder.binding.category.setText(pojo.category);
            holder.binding.category.setVisibility(View.VISIBLE);
        }else {
            holder.binding.category.setVisibility(View.GONE);
        }

        holder.binding.price.setText(storeUserData.getString( Constants.CURRENCY )+" "+pojo.price);
        holder.binding.qty.setText("Quantity : "+pojo.quantity);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowPaymentQtyItemBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}