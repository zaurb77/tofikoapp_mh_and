package adapter;

import android.app.Activity;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.PaymentInfoScreen;
import com.angaihouse.databinding.QuesAnsLayoutBinding;
import com.angaihouse.databinding.RowCardListBinding;

import java.util.ArrayList;

import pojo.CardListPojo;
import pojo.QuestionAnsPojo;

public class QuestionAnsAdapter extends RecyclerView.Adapter<QuestionAnsAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<QuestionAnsPojo.ResponseData> arrayList;

    int pos = -1;

    public QuestionAnsAdapter(Activity activity, ArrayList<QuestionAnsPojo.ResponseData> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ques_ans_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final QuestionAnsPojo.ResponseData pojo = arrayList.get(position);

        holder.binding.ans.setText(pojo.answer);
        holder.binding.question.setText((position +1)+" . "+pojo.question);


        if (pos == position) {
            if (holder.binding.ans.getVisibility() == View.VISIBLE) {
                holder.binding.img.setImageResource(R.drawable.plus);
                holder.binding.ans.setVisibility(View.GONE);
                holder.binding.llItem.setBackgroundResource(R.drawable.bottom_grey_line);

            } else {
                holder.binding.img.setImageResource(R.drawable.minus);
                holder.binding.ans.setVisibility(View.VISIBLE);
                holder.binding.llItem.setBackground(null);
            }
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos = position;
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        QuesAnsLayoutBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}