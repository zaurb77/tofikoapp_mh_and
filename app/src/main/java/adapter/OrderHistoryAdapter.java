package adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.FeedBackActivity;
import com.angaihouse.activity.OrderDetailActivity;
import com.angaihouse.activity.newmodel.OrderHistoryActivity;
import com.angaihouse.databinding.RowOrderHistoryBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.StoreUserData;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import pojo.OrderHistoryPojo;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    Activity activity;
    StoreUserData storeUserData;
    private OrderHistoryActivity.ItemClickListener itemClickListener;
    private ArrayList<OrderHistoryPojo.ResponseData> arrayList;
    String orderType;

    public OrderHistoryAdapter(Activity activity, ArrayList<OrderHistoryPojo.ResponseData> arrayList, OrderHistoryActivity.ItemClickListener itemClickListener,String type) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.itemClickListener = itemClickListener;
        this.orderType = type;
        storeUserData = new StoreUserData(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_history, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final OrderHistoryPojo.ResponseData pojo = arrayList.get(position);

        holder.binding.tvTotalAmount.setText(Constants.TOTAL_AMT.toUpperCase());
        holder.binding.orderDateTv.setText(Constants.ORDER_DATE.toUpperCase());
        holder.binding.transactionIdTv.setText(Constants.TRANSACTION_ID.toUpperCase());
        holder.binding.tvRepeatOrder.setText(Constants.REPEAT_ORDER);
        holder.binding.rateNow.setText(Constants.RATE_NOW);

        Glide.with(activity)
                .load(pojo.image)
                .placeholder(R.drawable.notfound)
                .into(holder.binding.foodImage);

        if (orderType.equalsIgnoreCase("completed")) {

            if (!TextUtils.isEmpty(pojo.rating)) {
                holder.binding.rating.setVisibility(View.VISIBLE);
                holder.binding.llRating.setEnabled(false);
                holder.binding.rating.setRating(Float.parseFloat(pojo.rating));
                holder.binding.llRepeat.setVisibility(View.VISIBLE);
                holder.binding.rateNow.setVisibility(View.GONE);

            } else {
                holder.binding.rateNow.setVisibility(View.VISIBLE);
                holder.binding.rating.setVisibility(View.GONE);
                holder.binding.llRating.setEnabled(true);

            }

            holder.binding.llDelivered.setVisibility(View.VISIBLE);

        } else {

            holder.binding.llBottom.setVisibility(View.GONE);
            holder.binding.viewLine.setVisibility(View.GONE);
        }

        if(pojo.order_status.equalsIgnoreCase("completed")) {
            holder.binding.ivStatus.setImageResource(R.drawable.select);
            holder.binding.status.setText(Constants.COMPLETED);
        }else if(pojo.order_status.equalsIgnoreCase("decline")) {
            holder.binding.ivStatus.setImageResource(R.drawable.ic_wrong);
            holder.binding.status.setText(Constants.CANCELLED);
        }

        holder.binding.rateNow.setOnClickListener(view -> {
            activity.startActivity(new Intent(activity, FeedBackActivity.class)
                    .putExtra("CART_ID", "" + pojo.cart_id)
            );
        });

        holder.binding.llRepeat.setOnClickListener(view -> itemClickListener.onClick(pojo.cart_id, pojo.restaurant_name));

        holder.binding.name.setText(pojo.restaurant_name);
        holder.binding.amount.setText(storeUserData.getString( Constants.CURRENCY )+" " + pojo.order_amount);
        holder.binding.orderDate.setText(pojo.order_date);
        holder.binding.transactionId.setText("#"+pojo.order_number);

        if (pojo.customer_comment.length() > 0) {
            holder.binding.customerComment.setVisibility(View.VISIBLE);
            holder.binding.customerComment.setText("~ "+pojo.customer_comment);
        } else {
            holder.binding.customerComment.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> activity.startActivity(new Intent(activity, OrderDetailActivity.class).putExtra("OrderNumber", pojo.order_number)));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowOrderHistoryBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}