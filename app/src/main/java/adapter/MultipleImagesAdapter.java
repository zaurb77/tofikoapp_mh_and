package adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.newmodel.DetailActivity;
import com.angaihouse.databinding.MultipleImagesBinding;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MultipleImagesAdapter extends RecyclerView.Adapter<MultipleImagesAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<String> arrayList;
    DetailActivity.ItemClickListener itemClickListener;

    public MultipleImagesAdapter(Activity activity, ArrayList<String> arrayList, DetailActivity.ItemClickListener itemClickListener) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.multiple_images, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String imageData = arrayList.get(position);

        Glide.with(activity)
                .load(imageData)
                .into(holder.binding.img);

        holder.itemView.setOnClickListener(v -> itemClickListener.imageClick(imageData));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MultipleImagesBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}