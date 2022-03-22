package adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.LoginActivity;
import com.angaihouse.activity.newmodel.MenuActivity;
import com.angaihouse.databinding.RowNewItemBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import pojo.CategoryItemPojo;

public class CategoryItemNewAdapter extends RecyclerView.Adapter<CategoryItemNewAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<CategoryItemPojo.ResponseData> arrayList;
    private MenuActivity.ItemClickListener itemClickListener;
    String resOpen;
    StoreUserData storeUserData;


    public CategoryItemNewAdapter(Activity activity,String resOpen, ArrayList<CategoryItemPojo.ResponseData> arrayList, MenuActivity.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.resOpen = resOpen;
        this.itemClickListener = itemClickListener;
        storeUserData = new StoreUserData( activity );
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_new_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CategoryItemPojo.ResponseData pojo = arrayList.get(position);

        holder.binding.catPrice.setText(storeUserData.getString( Constants.CURRENCY )+" "+pojo.price);
        holder.binding.catName.setText(pojo.name);
        holder.binding.tvAdd.setText(Constants.ADD);

        if (resOpen.equalsIgnoreCase( "0" )) {
            holder.binding.llAddQty.setEnabled( false );
            holder.binding.tvAdd.setAlpha( 0.8F );

        } else {
            holder.binding.llAddQty.setEnabled( true );
        }

        holder.binding.llMain.setOnClickListener( view -> {

            if (storeUserData.getString( Constants.GUEST_LOGIN ).equalsIgnoreCase( "1" )) {
                Utils.guestLogin( activity );
            } else {
                itemClickListener.openDetailScreen( "" + pojo.id );
            }
        } );
        
        if (pojo.is_veg == 1){
            holder.binding.imgVeg.setImageResource(R.drawable.veg_ic);
            holder.binding.imgVeg.setVisibility(View.VISIBLE);
        }else {
            holder.binding.imgVeg.setVisibility(View.GONE);
        }

        if (pojo.is_spicy == 1){
            holder.binding.imgSpicy.setImageResource(R.drawable.spicy);
            holder.binding.imgSpicy.setVisibility(View.VISIBLE);
        }else {
            holder.binding.imgSpicy.setVisibility(View.GONE);
        }

        if (pojo.is_show == 0) {

            holder.binding.tvNotAvailable.setVisibility(View.VISIBLE);
            holder.itemView.setEnabled(false);
            holder.binding.llQty.setVisibility(View.GONE);
            holder.binding.llAddQty.setVisibility(View.GONE);

        } else {

            holder.binding.tvNotAvailable.setVisibility(View.GONE);
            holder.itemView.setEnabled(true);

            if (TextUtils.isEmpty(pojo.quantity) || pojo.quantity.equalsIgnoreCase("0")) {
                holder.binding.llQty.setVisibility(View.GONE);
                holder.binding.llAddQty.setVisibility(View.VISIBLE);
                pojo.qty = 0;
            } else {
                holder.binding.llQty.setVisibility(View.VISIBLE);
                holder.binding.llAddQty.setVisibility(View.GONE);
                holder.binding.qty.setText(pojo.quantity);
                pojo.qty = Integer.parseInt(pojo.quantity);
            }

        }


        if (pojo.image_enable == 1) {

            holder.binding.catImage.setVisibility(View.VISIBLE);
            Glide.with(activity)
                    .load(pojo.image)
                    .placeholder(R.drawable.notfound)
                    .into(holder.binding.catImage);

        } else {
            holder.binding.catImage.setVisibility(View.GONE);
        }


        if (pojo.ingredients.length()>0){
            holder.binding.catInfo.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.binding.catInfo.setText(Html.fromHtml(pojo.ingredients, Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.binding.catInfo.setText(Html.fromHtml(pojo.ingredients));
            }
        }else {
            holder.binding.catInfo.setVisibility(View.GONE);
        }



        if (pojo.remove_customization.size() == 0 && pojo.customization.size() == 0) {
            holder.binding.customizable.setVisibility(View.GONE);
        } else {
            if (pojo.is_show == 0) {
                holder.binding.customizable.setVisibility(View.GONE);
            }else {
                holder.binding.customizable.setVisibility(View.VISIBLE);
            }
        }

        holder.binding.addItem.setOnClickListener(view -> {

            if (pojo.qty > 0) {
                pojo.qty++;
                pojo.quantity = String.valueOf(pojo.qty);
                itemClickListener.changeQty(pojo.cart_items.size(),0,""+pojo.id,"" + pojo.cart_item_id, "" + pojo.quantity,Integer.parseInt(pojo.prev_customization),pojo,position,pojo.is_taste);
            }

        });


        holder.binding.removeItem.setOnClickListener(view -> {
            if (pojo.qty > 0) {
                pojo.qty--;
                pojo.quantity = String.valueOf(pojo.qty);
                itemClickListener.changeQty(pojo.cart_items.size(),1,""+pojo.id,"" + pojo.cart_item_id, "" + pojo.quantity,Integer.parseInt(pojo.prev_customization),pojo,position,pojo.is_taste);
            } else {
                pojo.quantity = "";
            }
        });



        holder.binding.llAddQty.setOnClickListener(view -> {
            if ( new StoreUserData(activity).getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")){
                activity.startActivity(new Intent(activity, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }else{
                if (pojo.remove_customization.size() == 0 && pojo.customization.size() == 0 && pojo.cooking_grades.size() == 0 && pojo.taste.size() == 0 ) {
                    itemClickListener.addToCartClick("" + pojo.id,""+1);
                } else {

                    itemClickListener.onDialog(pojo, position);
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowNewItemBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}