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
import com.angaihouse.activity.newmodel.CartNewActivity;
import com.angaihouse.databinding.RowCartBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.StoreUserData;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import pojo.CartListPojo;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<CartListPojo.RestaurantData.Items> arrayList;
    private CartNewActivity.ItemClickListener itemClickListener;
    StoreUserData storeUserData;


    public CartListAdapter(Activity activity, ArrayList<CartListPojo.RestaurantData.Items> arrayList, CartNewActivity.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.itemClickListener = itemClickListener;
        storeUserData = new StoreUserData( activity );
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_cart, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CartListPojo.RestaurantData.Items pojo = arrayList.get(position);

        if (pojo.image_enable == 1){
            holder.binding.catImage.setVisibility(View.VISIBLE);
        }else {
            holder.binding.catImage.setVisibility(View.GONE);
        }


        if (pojo.is_offered == 1){
            if(pojo.mangal_remain_price.equalsIgnoreCase("0") && !pojo.need_mangals.equalsIgnoreCase("0")){
                holder.binding.price.setText("" + pojo.need_mangals);
                holder.binding.offerImage.setVisibility(View.VISIBLE);

            }else if(pojo.mangal_remain_price.equalsIgnoreCase("0")){
                holder.binding.price.setText(storeUserData.getString( Constants.CURRENCY )+" " + pojo.main_price);
                holder.binding.offerImage.setVisibility(View.GONE);

            }else {

                holder.binding.offerImage.setVisibility(View.VISIBLE);
                holder.binding.price.setText(storeUserData.getString( Constants.CURRENCY )+" "+pojo.mangal_remain_price+" + "+ pojo.need_mangals);
            }

        }else {
            holder.binding.offerImage.setVisibility(View.GONE);
            holder.binding.price.setText(storeUserData.getString( Constants.CURRENCY )+" " + pojo.main_price);
        }


        Glide.with(activity)
                .load(pojo.item_image)
                .into(holder.binding.catImage);

        holder.binding.name.setText(pojo.item_name);
        holder.binding.taste.setText("Taste : "+pojo.taste_customization);
        holder.binding.cooking.setText("Cooking Level : "+pojo.cooking_customization);
        holder.binding.qty.setText("" + pojo.quantity);

        if (pojo.category.length()>0){
            holder.binding.category.setText(pojo.category);
        }else {
            holder.binding.category.setVisibility(View.GONE);
        }

        if (pojo.taste_customization.length() > 0){
            holder.binding.taste.setVisibility(View.VISIBLE);
        }else {
            holder.binding.taste.setVisibility(View.GONE);
        }

        if (pojo.cooking_customization.length() > 0){
            holder.binding.cooking.setVisibility(View.VISIBLE);
        }else {
            holder.binding.cooking.setVisibility(View.GONE);
        }

        if (pojo.paid_customization.length()<=0 && pojo.free_customization.length()<=0){
            holder.binding.description.setVisibility(View.INVISIBLE);
        }else {
            holder.binding.description.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(pojo.paid_customization)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.binding.addOn.setText(Html.fromHtml(   Constants.ADD_ONS+" : "+pojo.paid_customization, Html.FROM_HTML_MODE_COMPACT));
            }else {
                holder.binding.addOn.setText(Html.fromHtml(Constants.ADD_ONS+" : "+pojo.paid_customization));
            }
            holder.binding.addOn.setVisibility(View.VISIBLE);
        }else {
            holder.binding.addOn.setVisibility(View.GONE);
        }


        if (!TextUtils.isEmpty(pojo.free_customization)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.binding.description.setText(Html.fromHtml(Constants.REMOVE+" : "+ pojo.free_customization, Html.FROM_HTML_MODE_COMPACT));
            }else {
                holder.binding.description.setText(Html.fromHtml(Constants.REMOVE+" : "+ pojo.free_customization));
            }
            holder.binding.description.setVisibility(View.VISIBLE);
        }else {
            holder.binding.description.setVisibility(View.GONE);
        }


        holder.binding.add.setOnClickListener(view -> {
            if (pojo.quantity > 0) {
                    pojo.quantity++;
                    itemClickListener.onClick(pojo.cart_item_id, pojo.quantity);
            }
        });

        holder.binding.remove.setOnClickListener(view -> {
            if (pojo.quantity != 0) {
                pojo.quantity--;
                itemClickListener.onClick(pojo.cart_item_id, pojo.quantity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowCartBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}