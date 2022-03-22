package adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.StoreDetailActivity;
import com.angaihouse.databinding.RowRestaurantBinding;
import com.angaihouse.databinding.RowRestaurantListBinding;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import pojo.NotificationPojo;

public class getRestaurantListAdapter extends RecyclerView.Adapter<getRestaurantListAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<NotificationPojo.Responsedata> arrayList;

    public getRestaurantListAdapter(AppCompatActivity activity, ArrayList<NotificationPojo.Responsedata> responsedata) {
        this.activity = activity;
        this.arrayList = responsedata;
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from( parent.getContext() )
                .inflate( R.layout.row_restaurant, parent, false );
        return new ViewHolder( itemView );
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final NotificationPojo.Responsedata pojo = arrayList.get( position );
//        holder.binding.tvTitle.setText( pojo.store_name);
//        holder.binding.tvContent.setText( Html.fromHtml(pojo.description));
//        Date serverDate = null;
//        SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        DateFormat dateFormat=new SimpleDateFormat("MMM dd, yyyy hh:mm a");
//        try {
//            serverDate = sdfInput.parse(pojo.cat);
//        } catch (Exception e) {
//            Log.e("formattedDateFromString", "Exception in formateDateFromstring(): " + e.getMessage());
//        }
//        holder.binding.tvDate.setText( dateFormat.format( serverDate ) );
        Glide.with( activity ).load( pojo.image ).into( holder.binding.foodImage );
        holder.binding.name.setText( pojo.name );
        holder.binding.tvAddress.setText( pojo.address );
        holder.binding.tvContactNumber.setText( pojo.mobile_no );

        holder.itemView.setOnClickListener( v -> {
            activity.startActivity( new Intent( activity, StoreDetailActivity.class ).putExtra( "Store_Id", "" + pojo.id ) );
            activity.finish();
        } );
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowRestaurantBinding binding;

        public ViewHolder(View view) {
            super( view );
            binding = DataBindingUtil.bind( itemView );
        }
    }
}