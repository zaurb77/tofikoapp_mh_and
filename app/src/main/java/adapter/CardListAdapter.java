package adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.PaymentInfoScreen;
import com.angaihouse.databinding.RowCardListBinding;

import java.util.ArrayList;

import pojo.CardListPojo;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<CardListPojo.Responsedata> arrayList;
    private PaymentInfoScreen.ItemClickListener itemClickListener;
    int selectedCard = -1;

    public CardListAdapter(Activity activity, ArrayList<CardListPojo.Responsedata> arrayList, PaymentInfoScreen.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_card_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CardListPojo.Responsedata pojo = arrayList.get(position);
        holder.binding.cardNumber.setText("**** **** **** "+pojo.card_number);

        if (pojo.selectedCard == position){
            pojo.selectedCard = 1;
            holder.binding.imgSelect.setImageResource(R.drawable.select_item);
        }else {
            pojo.selectedCard = -1;
            holder.binding.imgSelect.setImageResource(R.drawable.radio_blank);
        }

        holder.binding.llMain.setOnClickListener(view -> {
                    pojo.selectedCard = position;
                    itemClickListener.setCardClick(pojo.card_id);
                    notifyDataSetChanged();
                }
        );
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowCardListBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}