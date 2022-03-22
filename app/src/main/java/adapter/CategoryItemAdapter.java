package adapter;

import android.app.Activity;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.newmodel.MenuActivity;
import com.angaihouse.databinding.RowMenuItemBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import pojo.CategoryItemPojo;

public class CategoryItemAdapter extends RecyclerView.Adapter<CategoryItemAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<CategoryItemPojo.ResponseData> arrayList;
    private MenuActivity.ItemClickListener itemClickListener;
    String resOpen;
    StoreUserData storeUserData;

    public CategoryItemAdapter(Activity activity, String resOpen, ArrayList<CategoryItemPojo.ResponseData> arrayList, MenuActivity.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.resOpen = resOpen;
        this.arrayList = arrayList;
        this.itemClickListener = itemClickListener;
        storeUserData = new StoreUserData( activity );
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from( parent.getContext() )
                .inflate( R.layout.row_menu_item, parent, false );
        return new ViewHolder( itemView );
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CategoryItemPojo.ResponseData pojo = arrayList.get( position );

        holder.binding.catPrice.setText( storeUserData.getString( Constants.CURRENCY )+" " + pojo.price );
        holder.binding.catName.setText( pojo.name );
        holder.binding.tvAdd.setText( Constants.ADD );
        holder.binding.more.setText( Constants.SEE_MORE );
        holder.binding.customizable.setText( Constants.CUSTOMIZABLE );

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

        if (pojo.is_veg == 1) {
            holder.binding.imgVeg.setImageResource( R.drawable.veg_ic );
            holder.binding.imgVeg.setVisibility( View.VISIBLE );
        } else {
            holder.binding.imgVeg.setVisibility( View.GONE );
        }

        if (pojo.is_spicy == 1) {
            holder.binding.imgSpicy.setImageResource( R.drawable.spicy );
            holder.binding.imgSpicy.setVisibility( View.VISIBLE );
        } else {
            holder.binding.imgSpicy.setVisibility( View.GONE );
        }

        if (pojo.is_show == 0) {

            holder.binding.tvNotAvailable.setVisibility( View.VISIBLE );
            holder.itemView.setEnabled( false );
            holder.binding.llQty.setVisibility( View.GONE );
            holder.binding.llAddQty.setVisibility( View.GONE );

        } else {

            holder.binding.tvNotAvailable.setVisibility( View.GONE );
            holder.itemView.setEnabled( true );

            if (TextUtils.isEmpty( pojo.quantity ) || pojo.quantity.equalsIgnoreCase( "0" )) {
                holder.binding.llQty.setVisibility( View.GONE );
                holder.binding.llAddQty.setVisibility( View.VISIBLE );
                pojo.qty = 0;
            } else {
                holder.binding.llQty.setVisibility( View.VISIBLE );
                holder.binding.llAddQty.setVisibility( View.GONE );
                holder.binding.qty.setText( pojo.quantity );
                pojo.qty = Integer.parseInt( pojo.quantity );
            }
        }


        if (pojo.image_enable == 1) {

            Glide.with( activity )
                    .load( pojo.image )
                    .placeholder( R.drawable.notfound )
                    .into( holder.binding.catImage );

        } else {

            Glide.with( activity )
                    .load( R.drawable.notfound )
                    .placeholder( R.drawable.notfound )
                    .into( holder.binding.catImage );
        }


        if (TextUtils.isEmpty( pojo.ingredients )) {
            holder.binding.catInfo.setVisibility( View.GONE );
            holder.binding.more.setVisibility( View.GONE );
        } else {
            holder.binding.catInfo.setVisibility( View.VISIBLE );
            holder.binding.more.setVisibility( View.VISIBLE );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.binding.catInfo.setText( Html.fromHtml( pojo.ingredients, Html.FROM_HTML_MODE_COMPACT ) );
            } else {
                holder.binding.catInfo.setText( Html.fromHtml( pojo.ingredients ) );
            }
        }

        if (pojo.remove_customization.size() == 0 && pojo.customization.size() == 0) {
            holder.binding.customizable.setVisibility( View.GONE );
        } else {
            if (pojo.is_show == 0) {
                holder.binding.customizable.setVisibility( View.GONE );
            } else {
                holder.binding.customizable.setVisibility( View.VISIBLE );
            }
        }

        holder.binding.addItem.setOnClickListener( view -> {
            if (pojo.qty > 0) {
                pojo.qty++;
                pojo.quantity = String.valueOf( pojo.qty );
                itemClickListener.changeQty( pojo.cart_items.size(), 0, "" + pojo.id, "" + pojo.cart_item_id, "" + pojo.quantity, Integer.parseInt( pojo.prev_customization ), pojo, position, pojo.is_taste );
            }
        } );

        holder.binding.removeItem.setOnClickListener( view -> {
            if (pojo.qty > 0) {
                pojo.qty--;
                pojo.quantity = String.valueOf( pojo.qty );
                itemClickListener.changeQty( pojo.cart_items.size(), 1, "" + pojo.id, "" + pojo.cart_item_id, "" + pojo.quantity, Integer.parseInt( pojo.prev_customization ), pojo, position, pojo.is_taste );
            } else {
                pojo.quantity = "";
            }
        } );

        holder.binding.llAddQty.setOnClickListener( view -> {
            if (new StoreUserData( activity ).getString( Constants.GUEST_LOGIN ).equalsIgnoreCase( "1" )) {
                Utils.guestLogin( activity );
            } else {
                if (pojo.remove_customization.size() == 0 && pojo.customization.size() == 0 && pojo.cooking_grades.size() == 0 && pojo.taste.size() == 0) {
                    itemClickListener.addToCartClick( "" + pojo.id, "" + 1 );
                } else {
                    itemClickListener.onDialog( pojo, position );
                }
            }
        } );
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowMenuItemBinding binding;

        public ViewHolder(View view) {
            super( view );
            binding = DataBindingUtil.bind( itemView );
        }
    }
}