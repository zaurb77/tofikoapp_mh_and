package adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.newmodel.MenuActivity;
import com.angaihouse.databinding.RowCategoryItemBinding;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import pojo.CategoryPojo;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<CategoryPojo.ResponseData> arrayList;
    private int selectItem = -1;
    private MenuActivity.ItemClickListener itemClickListener;

    public CategoryAdapter(Activity activity, ArrayList<CategoryPojo.ResponseData> arrayList, MenuActivity.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CategoryPojo.ResponseData pojo = arrayList.get(position);


        if (selectItem == -1) {
            selectItem = 0;
            itemClickListener.onClick("" + pojo.id, -1, pojo.name);
        }


        if (pojo.selectedItem) {
            holder.binding.imgSelect.setVisibility(View.VISIBLE);
            holder.binding.llFrame.setAlpha(0.9f);
            Typeface face = Typeface.createFromAsset(activity.getAssets(), "ralewaybold.ttf");
            holder.binding.catName.setTypeface(face);
        } else {
            holder.binding.imgSelect.setVisibility(View.INVISIBLE);
            holder.binding.llFrame.setAlpha(0.4f);
            Typeface face = Typeface.createFromAsset(activity.getAssets(), "ralewayitalic.ttf");
            holder.binding.catName.setTypeface(face);
        }


        if (pojo.image.length() > 0) {
            Glide.with(activity)
                    .load(pojo.image)
                    .placeholder(R.drawable.notfound)
                    .circleCrop()
                    .into(holder.binding.catImage);
        } else {
            Glide.with(activity)
                    .load(R.drawable.notfound)
                    .circleCrop()
                    .into(holder.binding.catImage);
        }

        holder.binding.catName.setText(pojo.name);

        holder.itemView.setOnClickListener(view -> {
            itemClickListener.onClick("" + pojo.id, position, pojo.name);
            selectItem = position;
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowCategoryItemBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }


}