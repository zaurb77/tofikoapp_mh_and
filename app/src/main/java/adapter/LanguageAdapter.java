package adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.newmodel.SettingActivity;
import com.angaihouse.databinding.RowLanguageBinding;

import java.util.ArrayList;

import pojo.LanguagePojo;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<LanguagePojo.ResponseData> arrayList;
    private SettingActivity.ItemClickListener itemClick;

    int pos = -1;

    public LanguageAdapter(Activity activity, ArrayList<LanguagePojo.ResponseData> arrayList, SettingActivity.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        itemClick=  itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_language, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LanguagePojo.ResponseData pojo = arrayList.get(position);



        if (pos == -1) {
            if (pojo.is_selected == 1) {
                holder.binding.checkBox.setImageResource(R.drawable.checked);
            } else {
                holder.binding.checkBox.setImageResource(R.drawable.unchecked);
            }
        } else {
            if (pos == position) {
                holder.binding.checkBox.setImageResource(R.drawable.checked);
            } else {
                holder.binding.checkBox.setImageResource(R.drawable.unchecked);
            }
        }



        holder.binding.tvLanguage.setText(pojo.full_name);
        holder.binding.checkBox.setOnClickListener(view -> {
            pos = position;
            notifyDataSetChanged();
            itemClick.onClick(""+pojo.id);
        });


        holder.itemView.setOnClickListener(view -> {
            pos = position;
            notifyDataSetChanged();
            itemClick.onClick(""+pojo.id);
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowLanguageBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}