package adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.StoreDetailActivity;
import com.angaihouse.databinding.RowBookingTimeBinding;
import com.angaihouse.databinding.RowSelectedMemberBinding;

import java.util.ArrayList;

import pojo.GetTimePojo;

public class SelectMemberAdapter extends RecyclerView.Adapter<SelectMemberAdapter.ViewHolder> {

    Activity activity;
    StoreDetailActivity.ItemClickListener itemClickListener;
    int pos = -1, member;

    public SelectMemberAdapter(AppCompatActivity activity, StoreDetailActivity.ItemClickListener itemClickListener, int i) {
        this.activity = activity;
        this.member = i;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_selected_member, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int pojo = position+1;
        if (pos == position){
            holder.binding.tvMember.setBackground( activity.getResources().getDrawable( R.drawable.green_corner_bg ) );
        }else {
            holder.binding.tvMember.setBackground( activity.getResources().getDrawable( R.drawable.gray_border ) );
        }


        holder.binding.tvMember.setText( ""+ pojo);

        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos = position;
                itemClickListener.selectedmember(holder.binding.tvMember.getText().toString());
                notifyDataSetChanged();
            }
        } );
    }

    @Override
    public int getItemCount() {
        return member;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowSelectedMemberBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}