package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.angaihouse.R;
import com.angaihouse.activity.newmodel.DetailActivity;
import com.angaihouse.activity.newmodel.Home_New;
import com.angaihouse.activity.newmodel.OrderMenuActivity;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.StoreUserData;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import pojo.GeneralPojo;

public class CustomPagerAdapter extends PagerAdapter {
    LayoutInflater mLayoutInflater;
    AppCompatActivity activity;
    StoreUserData storeUserData;
    private Home_New.ItemClickListener itemClickListener;
    private ArrayList<GeneralPojo.Banners> list;

    public CustomPagerAdapter(AppCompatActivity context, ArrayList<GeneralPojo.Banners> mResources, Home_New.ItemClickListener itemClickListener) {
        this.activity = context;
        mLayoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = mResources;
        this.itemClickListener = itemClickListener;
        storeUserData = new StoreUserData(activity);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final GeneralPojo.Banners pojo = list.get(position);
        View itemView = mLayoutInflater.inflate(R.layout.row_slider_image, container, false);

        ImageView imageView = itemView.findViewById(R.id.sliderImage);
        Glide.with(activity)
                .load(pojo.image)
                .into(imageView);

        TextView tvAddBanner = itemView.findViewById(R.id.tvAddBanner);

        itemView.setOnClickListener(view -> {
            if (!storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")) {
                if (pojo.ad_type == 1) {
                    if(!storeUserData.getString(Constants.ORDER_TYPE).equalsIgnoreCase("")) {
                        activity.startActivity(new Intent(activity, DetailActivity.class)
                                .putExtra("BANNER_ITEM", "BANNER_ITEM")
                                .putExtra("BANNER_DELIVERY_TYPE", storeUserData.getString(Constants.ORDER_TYPE))
                                .putExtra("ITEM_ID", pojo.item_id)
                                .putExtra("BANNER_RESTAURANT_ID", pojo.res_id)
                        );
                    }else {
                        activity.startActivity(new Intent(activity, OrderMenuActivity.class)
                                .putExtra("BANNER_ITEM", "BANNER_ITEM")
                                .putExtra("ITEM_ID", pojo.item_id)
                                .putExtra("BANNER_RESTAURANT_ID", pojo.res_id)
                        );
                    }
                }
            }
        });

        tvAddBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")) {
                    itemClickListener.onClick(pojo);
                }
            }
        });

        tvAddBanner.setOnClickListener(view ->
                activity.startActivity(new Intent(activity, OrderMenuActivity.class)
                .putExtra("BANNER_ITEM_COMBO", "BANNER_ITEM_COMBO")
                .putExtra("ITEM_ID", "" + pojo.item_id)
                .putExtra("BANNER_RESTAURANT_ID", "" + pojo.res_id)
        ));

        LinearLayout linearLayout = itemView.findViewById(R.id.llPriceBanner);
        LinearLayout linearLayoutAddQty = itemView.findViewById(R.id.llAddQtyBanner);

        if (pojo.ad_type == 0) {

            linearLayout.setVisibility(View.GONE);
            linearLayoutAddQty.setVisibility(View.GONE);

        } else if (pojo.ad_type == 2) {

            linearLayout.setVisibility(View.VISIBLE);
            linearLayoutAddQty.setVisibility(View.VISIBLE);
            TextView price = itemView.findViewById(R.id.priceBanner);
            price.setText(storeUserData.getString( Constants.CURRENCY )+" "+ pojo.price);

        } else {

            linearLayoutAddQty.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
            TextView price = itemView.findViewById(R.id.priceBanner);
            price.setText(storeUserData.getString( Constants.CURRENCY )+" " + pojo.price);

        }

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}