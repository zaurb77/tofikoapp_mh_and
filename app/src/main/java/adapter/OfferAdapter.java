package adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.newmodel.MyOfferActivity;
import com.angaihouse.databinding.RowMymangalBinding;
import com.angaihouse.utils.StoreUserData;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import pojo.OfferListPojo;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {

    Activity activity;
    private MyOfferActivity.ItemClickListener itemClickListener;
    private ArrayList<OfferListPojo.ResponseData.OfferItems> arrayList;
    StoreUserData storeUserData;

    public OfferAdapter(Activity activity, ArrayList<OfferListPojo.ResponseData.OfferItems> arrayList, MyOfferActivity.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.itemClickListener = itemClickListener;
        storeUserData = new StoreUserData(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_mymangal, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final OfferListPojo.ResponseData.OfferItems pojo = arrayList.get(position);


        if (pojo.is_offer_active.equalsIgnoreCase("1")){
            holder.binding.overlay.setVisibility(View.GONE);
            holder.itemView.setEnabled(true);
        }else {
            holder.binding.overlay.setVisibility(View.VISIBLE);
            holder.itemView.setEnabled(false);
        }


        Glide.with(activity)
                .load(pojo.image)
                .into(holder.binding.image);

        holder.binding.itemName.setText(pojo.offer_name);
        holder.binding.itemNameResName.setText("FROM : "+pojo.restaurant_name);
        holder.binding.mangles.setText(pojo.offered_price);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                itemClickListener.onClick(pojo);


            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowMymangalBinding binding;
        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}