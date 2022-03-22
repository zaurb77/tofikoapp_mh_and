package com.angaihouse.activity.newmodel;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.LoginActivity;
import com.angaihouse.databinding.ActivityMenuBinding;
import com.angaihouse.databinding.DialogCartCustomizationItemsBinding;
import com.angaihouse.databinding.RowCustomizeItemBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import adapter.CategoryAdapter;
import adapter.CategoryItemAdapter;
import adapter.CategoryItemNewAdapter;
import adapter.CookingGradeCustomizationMenuAdapter;
import adapter.CuatomizationAdapter;
import adapter.FreeCustomizationMenuAdapter;
import adapter.PaidCustomizationMenuAdapter;
import adapter.TasteCustomizationMenuAdapter;
import adapter.TestCustomizationMenuAdapter;
import okhttp3.ResponseBody;
import pojo.CategoryItemPojo;
import pojo.CategoryPojo;
import pojo.CustomizationPojo;
import retrofit2.Call;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    public ArrayList<CategoryPojo.ResponseData> categoryData = new ArrayList<>();
    ActivityMenuBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;
    BottomSheetDialog dialog;
    RowCustomizeItemBinding rowCustomizeItemBinding;
    DialogCartCustomizationItemsBinding dialogCartCustomizationItemsBinding;
    FrameLayout bottomSheet;
    FrameLayout bottomSheet1;
    ArrayList<String> freeCustomizationArray = new ArrayList<>();
    String freeCustomizationItemString;
    ArrayList<String> paidCustomizationArray = new ArrayList<>();
    String paidCustomizationItemString, cookingGradesCustomizationItemString, tasteCustomizationItemString;
    PaidCustomizationMenuAdapter paidCustomizationMenuAdapter;
    FreeCustomizationMenuAdapter freeCustomizationMenuAdapter;
    TestCustomizationMenuAdapter testCustomizationMenuAdapter;
    CookingGradeCustomizationMenuAdapter cookingGradeCustomizationMenuAdapter;
    TasteCustomizationMenuAdapter tasteCustomizationMenuAdapter;
    LinearLayoutManager horizontalLayout;
    RecyclerView rvCust;
    BottomSheetDialog custItemDialog;
    CategoryAdapter categoryAdapter;
    private ItemClickListener itemClickListener;
    private String catID;
    CategoryPojo categoryDataItem;
    private int cartCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_menu);

        binding.edtSearch.setHint(Constants.SEARCH_PRODUCT);
        binding.tvNext.setText(Constants.NEXT);

        Constants.CATEGORY_ID = "";

        if (storeUserData.getString(Constants.MENU_SEARCH).equalsIgnoreCase("1")) {
            binding.llSearch.setVisibility(View.VISIBLE);
        } else {
            binding.llSearch.setVisibility(View.GONE);
        }

        itemClickListener = new ItemClickListener() {
            @Override
            public void onClick(String id, int position, String catName) {

                for (int i = 0; i < categoryData.size(); i++) {
                    if (categoryData.get(i).id == Integer.parseInt(id)) {
                        categoryData.get(i).selectedItem = true;
                    } else {
                        categoryData.get(i).selectedItem = false;
                    }
                }
                catID = id;
                Constants.CATEGORY_ID = catID;
                if (position == -1) {
                    if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                        categoryItem(false, getIntent().getStringExtra("RES_ID"));
                    } else {
                        categoryItem(false, storeUserData.getString(Constants.NEAR_RES_ID));
                    }
                } else {
                    if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                        categoryItem(true, getIntent().getStringExtra("RES_ID"));
                    } else {
                        categoryItem(true, storeUserData.getString(Constants.NEAR_RES_ID));
                    }
                }
            }


            @Override
            public void addToCartClick(String id, String qty) {
                if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                    addToCart(getIntent().getStringExtra("RES_ID"), id, qty);
                } else {
                    addToCart(storeUserData.getString(Constants.NEAR_RES_ID), id, qty);
                }
            }


            @Override
            public void changeQtyCust(String id, String itemId, String qty) {
                changeCartQtyApi(id, itemId, qty);
            }

            @Override
            public void openDetailScreen(String itemId) {

                String addressId = null;
                if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("ADDRESSID")) {
                    addressId = getIntent().getStringExtra("ADDRESSID");
                }

                String RESTAURANT_ID;
                if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                    RESTAURANT_ID = getIntent().getStringExtra("RES_ID");
                } else {
                    RESTAURANT_ID = storeUserData.getString(Constants.NEAR_RES_ID);
                }

                activity.startActivity(new Intent(activity, DetailActivity.class)
                        .putExtra("MENU_ACTIVITY", "MENU_ACTIVITY")
                        .putExtra("ITEM_ID", itemId)
                        .putExtra("ADDRESS_ID", addressId)
                        .putExtra("RESTAURANT_ID", RESTAURANT_ID)
                        .putExtra("BANNER_DELIVERY_TYPE", getIntent().getStringExtra("ORDERTYPE"))
                );
            }

            @Override
            public void changeQty(int cartItems, int removeItem, String iteID, String id, String qty, int customization, CategoryItemPojo.ResponseData items, int position, int testCustomization) {

                if (removeItem == 1) {

                    if (cartItems > 1) {

                        custItemDialog = new BottomSheetDialog(activity, R.style.CustomBottomSheetDialogTheme);
                        View deliveryTimeSheetView = getLayoutInflater().inflate(R.layout.dialog_cart_customization_items, null);
                        custItemDialog.setContentView(deliveryTimeSheetView);
                        custItemDialog.setCancelable(false);
                        rvCust = custItemDialog.findViewById(R.id.rvCustomization);
                        dialogCartCustomizationItemsBinding = DataBindingUtil.bind(deliveryTimeSheetView);
                        bottomSheet1 = custItemDialog.findViewById(R.id.design_bottom_sheet);
                        if (rvCust != null) {
                            Objects.requireNonNull(rvCust).setLayoutManager(new LinearLayoutManager(activity));
                        }
                        Objects.requireNonNull(rvCust).setNestedScrollingEnabled(false);
                        rvCust.setHasFixedSize(true);

                        if (bottomSheet1 != null) {
                            BottomSheetBehavior.from(bottomSheet1).setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                        if (bottomSheet1 != null) {
                            BottomSheetBehavior.from(bottomSheet1).setSkipCollapsed(true);
                        }
                        if (bottomSheet1 != null) {
                            BottomSheetBehavior.from(bottomSheet1).setHideable(true);
                        }
                        custItemDialog.show();

                        BottomSheetBehavior behaviour = BottomSheetBehavior.from(bottomSheet1);
                        behaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                        behaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                            @Override
                            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                                    // Bottom Sheet was dismissed by user! But this is only fired, if dialog is swiped down! Not if touch outside dismissed the dialog or the back button
                                    custItemDialog.dismiss();
                                    if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                                        checkCart(true, getIntent().getStringExtra("RES_ID"));
                                    } else {
                                        checkCart(true, storeUserData.getString(Constants.NEAR_RES_ID));
                                    }
                                }
                            }

                            @Override
                            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                            }
                        });

                        cartCustItem(true, iteID);

                    } else {
                        changeQtyApi(id, qty);
                    }

                } else {

                    Log.i( "FSfsafsdfsd",""+customization );

                    if (customization == 1) {

                        dialog = new BottomSheetDialog(activity, R.style.CustomBottomSheetDialogTheme);
                        View deliveryTimeSheetView = getLayoutInflater().inflate(R.layout.row_customize_item, null);
                        dialog.setContentView(deliveryTimeSheetView);
                        dialog.setCancelable(false);
                        rowCustomizeItemBinding = DataBindingUtil.bind(deliveryTimeSheetView);
                        bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);

                        if (bottomSheet != null) {
                            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                        if (bottomSheet != null) {
                            BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
                        }
                        if (bottomSheet != null) {
                            BottomSheetBehavior.from(bottomSheet).setHideable(true);
                        }

                        customizationButtonClick();
                        dialog.show();
                        BottomSheetBehavior behaviour = BottomSheetBehavior.from(bottomSheet);
                        behaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                        behaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                            @Override
                            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                                    // Bottom Sheet was dismissed by user! But this is only fired, if dialog is swiped down! Not if touch outside dismissed the dialog or the back button
                                    Log.i("Dialog_Close", "Dialog Close");

                                    paidCustomizationArray.clear();
                                    freeCustomizationArray.clear();
                                    paidCustomizationItemString = "";
                                    freeCustomizationItemString = "";
                                    tasteCustomizationItemString = "";
                                    cookingGradesCustomizationItemString = "";

                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                            }
                        });

                        TextView tvTaste1 = dialog.findViewById(R.id.tvTaste);
                        tvTaste1.setText(Constants.CHOOSE_TEST);

                        TextView tvCookingGrade1 = dialog.findViewById(R.id.tvCookingGrade);
                        tvCookingGrade1.setText(Constants.COOKING_LEVEL);

                        TextView tvPaidCustomization1 = dialog.findViewById(R.id.tvPaidCustomization);
                        tvPaidCustomization1.setText(Constants.ADD_EXTRA);

                        TextView tvFreeModification1 = dialog.findViewById(R.id.tvFreeModification);
                        tvFreeModification1.setText(Constants.WANT_TO_REMOVE + "?");

                        TextView addToCartCustomize1 = dialog.findViewById(R.id.addToCartCustomize);
                        addToCartCustomize1.setText(Constants.APPLY_CHANGES);

                        if(items.cooking_grades.size() == 0 && items.taste.size() == 0){
                            Log.i( "adfmlkasfklasdf","dsfgdsgfdsgsd" );

                            addToCartCustomize1.setEnabled(true);
                            addToCartCustomize1.setAlpha( 0.9f );
                        }
                        //TODO : REMOVE CUSTOMIZATIONS
                        TextView tvFreeCost = dialog.findViewById(R.id.tvFreeModification);
                        if (items.remove_customization.size() > 0) {
                            tvFreeCost.setVisibility(View.VISIBLE);
                        } else {
                            Objects.requireNonNull(tvFreeCost).setVisibility(View.GONE);
                        }

                        RecyclerView rvRemove;
                        rvRemove = dialog.findViewById(R.id.rvFreeCustomization);
                        rvRemove.setLayoutManager(new LinearLayoutManager(activity));
                        rvRemove.setNestedScrollingEnabled(false);
                        rvRemove.setHasFixedSize(true);
                        freeCustomizationMenuAdapter = new FreeCustomizationMenuAdapter(activity, items.remove_customization, itemClickListener);
                        rvRemove.setAdapter(freeCustomizationMenuAdapter);
                        freeCustomizationMenuAdapter.notifyDataSetChanged();


                        //TODO : CUSTOMIZATIONS
                        TextView tvPaidCost = dialog.findViewById(R.id.tvPaidCustomization);
                        if (items.customization.size() > 0) {
                            Objects.requireNonNull(tvPaidCost).setVisibility(View.VISIBLE);
                        } else {
                            Objects.requireNonNull(tvPaidCost).setVisibility(View.GONE);
                        }

                        RecyclerView rvCustomization;
                        rvCustomization = dialog.findViewById(R.id.rvPaidCustomization);
                        Objects.requireNonNull(rvCustomization).setLayoutManager(new LinearLayoutManager(activity));
                        rvCustomization.setNestedScrollingEnabled(false);
                        rvCustomization.setHasFixedSize(true);
                        paidCustomizationMenuAdapter = new PaidCustomizationMenuAdapter(activity, items.customization, itemClickListener);
                        rvCustomization.setAdapter(paidCustomizationMenuAdapter);
                        paidCustomizationMenuAdapter.notifyDataSetChanged();


                        //TODO : REMOVE COOKING GRADE CUSTOMIZATIONS
                        TextView tvCookingGrade = dialog.findViewById(R.id.tvCookingGrade);
                        if (items.cooking_grades.size() > 0) {
                            Objects.requireNonNull(tvCookingGrade).setVisibility(View.VISIBLE);
                        } else {
                            Objects.requireNonNull(tvCookingGrade).setVisibility(View.GONE);
                        }

                        RecyclerView rvCookingGrade;
                        rvCookingGrade = dialog.findViewById(R.id.rvCookingGrade);
                        rvCookingGrade.setLayoutManager(new LinearLayoutManager(activity));
                        rvCookingGrade.setNestedScrollingEnabled(false);
                        rvCookingGrade.setHasFixedSize(true);
                        cookingGradeCustomizationMenuAdapter = new CookingGradeCustomizationMenuAdapter(activity, items.cooking_grades, itemClickListener);
                        rvCookingGrade.setAdapter(cookingGradeCustomizationMenuAdapter);
                        cookingGradeCustomizationMenuAdapter.notifyDataSetChanged();


                        //TODO : TASTE CUSTOMIZATIONS
                        TextView tvTaste = dialog.findViewById(R.id.tvTaste);
                        if (items.taste.size() > 0) {
                            Objects.requireNonNull(tvTaste).setVisibility(View.VISIBLE);
                        } else {
                            Objects.requireNonNull(tvTaste).setVisibility(View.GONE);
                        }

                        RecyclerView rvTaste;
                        rvTaste = dialog.findViewById(R.id.rvTaste);
                        rvTaste.setLayoutManager(new LinearLayoutManager(activity));
                        rvTaste.setNestedScrollingEnabled(false);
                        rvTaste.setHasFixedSize(true);
                        tasteCustomizationMenuAdapter = new TasteCustomizationMenuAdapter(activity, items.taste, itemClickListener);
                        rvTaste.setAdapter(tasteCustomizationMenuAdapter);
                        tasteCustomizationMenuAdapter.notifyDataSetChanged();

                        ImageView close = dialog.findViewById(R.id.close);
                        TextView addToCartCustomize = dialog.findViewById(R.id.addToCartCustomize);
                        LinearLayout llSkip = dialog.findViewById(R.id.llSkip);

                        Objects.requireNonNull(addToCartCustomize).setOnClickListener(view -> {

                            if (!TextUtils.isEmpty(freeCustomizationItemString) || !TextUtils.isEmpty(paidCustomizationItemString) || !TextUtils.isEmpty(tasteCustomizationItemString) || !TextUtils.isEmpty(cookingGradesCustomizationItemString)) {
                                if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                                    addToCart(getIntent().getStringExtra("RES_ID"), "" + items.id, "1");
                                } else {
                                    addToCart(storeUserData.getString(Constants.NEAR_RES_ID), "" + items.id, "1");
                                }
                                dialog.dismiss();
                            } else {
                                Utils.showTopMessageError(activity, "Please Select Your Customizations.");
                            }
                        });

                        assert llSkip != null;
                        llSkip.setOnClickListener(view -> {

                            paidCustomizationArray.clear();
                            freeCustomizationArray.clear();

                            paidCustomizationItemString = "";
                            freeCustomizationItemString = "";
                            tasteCustomizationItemString = "";
                            cookingGradesCustomizationItemString = "";

                            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                                addToCart(getIntent().getStringExtra("RES_ID"), "" + items.id, "1");
                            } else {
                                addToCart(storeUserData.getString(Constants.NEAR_RES_ID), "" + items.id, "1");
                            }
                            dialog.dismiss();
                        });

                    } else {
                        changeQtyApi(id, qty);
                    }
                }
            }

            @Override
            public void freeCustomizeClick(String item) {
                freeCustomizationArray.add(item);
                freeCustomizationItemString = android.text.TextUtils.join(",", freeCustomizationArray);
                customizationButtonClick();
            }

            @Override
            public void paidCustomizeClick(String item) {
                paidCustomizationArray.add(item);
                paidCustomizationItemString = android.text.TextUtils.join(",", paidCustomizationArray);
                customizationButtonClick();
            }

            @Override
            public void paidCustomizeTestClick(String item) {
                paidCustomizationItemString = item;
                customizationButtonClick();
            }

            @Override
            public void cookingGradesCustomizeTestClick(String item) {
                cookingGradesCustomizationItemString = item;
                customizationButtonClick();
            }

            @Override
            public void tasteCustomizeTestClick(String item) {
                tasteCustomizationItemString = item;
                customizationButtonClick();
            }

            @Override
            public void freeCustomizationRemove(String item) {
                for (int i = 0; i < freeCustomizationArray.size(); i++) {
                    if (freeCustomizationArray.get(i).equalsIgnoreCase(item)) {
                        freeCustomizationArray.remove(i);
                    }
                }
                freeCustomizationItemString = android.text.TextUtils.join(",", freeCustomizationArray);
                customizationButtonClick();
            }
            @Override
            public void paidCustomizeRemove(String item) {
                for (int i = 0; i < paidCustomizationArray.size(); i++) {
                    if (paidCustomizationArray.get(i).equalsIgnoreCase(item)) {
                        paidCustomizationArray.remove(i);
                    }
                }
                paidCustomizationItemString = android.text.TextUtils.join(",", paidCustomizationArray);
                customizationButtonClick();
            }

            @Override
            public void onDialog(CategoryItemPojo.ResponseData items, int position) {

                dialog = new BottomSheetDialog(activity, R.style.CustomBottomSheetDialogTheme);
                View deliveryTimeSheetView = getLayoutInflater().inflate(R.layout.row_customize_item, null);
                dialog.setContentView(deliveryTimeSheetView);
                dialog.setCancelable(false);
                rowCustomizeItemBinding = DataBindingUtil.bind(deliveryTimeSheetView);
                bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);

                if (bottomSheet != null) {
                    BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                if (bottomSheet != null) {
                    BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
                }
                if (bottomSheet != null) {
                    BottomSheetBehavior.from(bottomSheet).setHideable(true);
                }

                customizationButtonClick();
                dialog.show();

                BottomSheetBehavior behaviour = BottomSheetBehavior.from(bottomSheet);
                behaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                behaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                            // Bottom Sheet was dismissed by user! But this is only fired, if dialog is swiped down! Not if touch outside dismissed the dialog or the back button
                            Log.i("Dialog_Close", "Dialog Close");

                            paidCustomizationArray.clear();
                            freeCustomizationArray.clear();
                            paidCustomizationItemString = "";
                            freeCustomizationItemString = "";
                            tasteCustomizationItemString = "";
                            cookingGradesCustomizationItemString = "";

                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });

                TextView tvTaste1 = dialog.findViewById(R.id.tvTaste);
                tvTaste1.setText(Constants.CHOOSE_TEST);

                TextView tvCookingGrade1 = dialog.findViewById(R.id.tvCookingGrade);
                tvCookingGrade1.setText(Constants.COOKING_LEVEL);

                TextView tvPaidCustomization1 = dialog.findViewById(R.id.tvPaidCustomization);
                tvPaidCustomization1.setText(Constants.ADD_EXTRA);

                TextView tvFreeModification1 = dialog.findViewById(R.id.tvFreeModification);
                tvFreeModification1.setText(Constants.WANT_TO_REMOVE + "?");

                TextView addToCartCustomize1 = dialog.findViewById(R.id.addToCartCustomize);
                addToCartCustomize1.setText(Constants.APPLY_CHANGES);

                if (items.cooking_grades.size() == 0 && items.taste.size() == 0){
                    addToCartCustomize1.setEnabled( true );
                    addToCartCustomize1.setAlpha( 0.9f );
                }

                //TODO : REMOVE CUSTOMIZATIONS
                TextView tvFreeCost = dialog.findViewById(R.id.tvFreeModification);
                if (items.remove_customization.size() > 0) {
                    tvFreeCost.setVisibility(View.VISIBLE);
                } else {
                    Objects.requireNonNull(tvFreeCost).setVisibility(View.GONE);
                }

                RecyclerView rvRemove;
                rvRemove = dialog.findViewById(R.id.rvFreeCustomization);
                rvRemove.setLayoutManager(new LinearLayoutManager(activity));
                rvRemove.setNestedScrollingEnabled(false);
                rvRemove.setHasFixedSize(true);
                freeCustomizationMenuAdapter = new FreeCustomizationMenuAdapter(activity, items.remove_customization, itemClickListener);
                rvRemove.setAdapter(freeCustomizationMenuAdapter);
                freeCustomizationMenuAdapter.notifyDataSetChanged();


                //TODO : CUSTOMIZATIONS
                TextView tvPaidCost = dialog.findViewById(R.id.tvPaidCustomization);
                if (items.customization.size() > 0) {
                    Objects.requireNonNull(tvPaidCost).setVisibility(View.VISIBLE);
                } else {
                    Objects.requireNonNull(tvPaidCost).setVisibility(View.GONE);
                }

                RecyclerView rvCustomization;
                rvCustomization = dialog.findViewById(R.id.rvPaidCustomization);
                Objects.requireNonNull(rvCustomization).setLayoutManager(new LinearLayoutManager(activity));
                rvCustomization.setNestedScrollingEnabled(false);
                rvCustomization.setHasFixedSize(true);
                paidCustomizationMenuAdapter = new PaidCustomizationMenuAdapter(activity, items.customization, itemClickListener);
                rvCustomization.setAdapter(paidCustomizationMenuAdapter);
                paidCustomizationMenuAdapter.notifyDataSetChanged();


                //TODO : REMOVE COOKING GRADE CUSTOMIZATIONS
                TextView tvCookingGrade = dialog.findViewById(R.id.tvCookingGrade);
                if (items.cooking_grades.size() > 0) {
                    Objects.requireNonNull(tvCookingGrade).setVisibility(View.VISIBLE);
                } else {
                    Objects.requireNonNull(tvCookingGrade).setVisibility(View.GONE);
                }

                RecyclerView rvCookingGrade;
                rvCookingGrade = dialog.findViewById(R.id.rvCookingGrade);
                rvCookingGrade.setLayoutManager(new LinearLayoutManager(activity));
                rvCookingGrade.setNestedScrollingEnabled(false);
                rvCookingGrade.setHasFixedSize(true);
                cookingGradeCustomizationMenuAdapter = new CookingGradeCustomizationMenuAdapter(activity, items.cooking_grades, itemClickListener);
                rvCookingGrade.setAdapter(cookingGradeCustomizationMenuAdapter);
                cookingGradeCustomizationMenuAdapter.notifyDataSetChanged();


                //TODO : TASTE CUSTOMIZATIONS
                TextView tvTaste = dialog.findViewById(R.id.tvTaste);
                if (items.taste.size() > 0) {
                    Objects.requireNonNull(tvTaste).setVisibility(View.VISIBLE);
                } else {
                    Objects.requireNonNull(tvTaste).setVisibility(View.GONE);
                }

                RecyclerView rvTaste;
                rvTaste = dialog.findViewById(R.id.rvTaste);
                rvTaste.setLayoutManager(new LinearLayoutManager(activity));
                rvTaste.setNestedScrollingEnabled(false);
                rvTaste.setHasFixedSize(true);
                tasteCustomizationMenuAdapter = new TasteCustomizationMenuAdapter(activity, items.taste, itemClickListener);
                rvTaste.setAdapter(tasteCustomizationMenuAdapter);
                tasteCustomizationMenuAdapter.notifyDataSetChanged();

                ImageView close = dialog.findViewById(R.id.close);
                TextView addToCartCustomize = dialog.findViewById(R.id.addToCartCustomize);
                LinearLayout llSkip = dialog.findViewById(R.id.llSkip);


                Objects.requireNonNull(addToCartCustomize).setOnClickListener(view -> {
                    if (!TextUtils.isEmpty(freeCustomizationItemString) || !TextUtils.isEmpty(paidCustomizationItemString) || !TextUtils.isEmpty(tasteCustomizationItemString) || !TextUtils.isEmpty(cookingGradesCustomizationItemString)) {
                        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                            addToCart(getIntent().getStringExtra("RES_ID"), "" + items.id, "1");
                        } else {
                            addToCart(storeUserData.getString(Constants.NEAR_RES_ID), "" + items.id, "1");
                        }
                        dialog.dismiss();
                    } else {
                        if (items.cooking_grades.size()==0 && items.taste.size() == 0){
                            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                                addToCart(getIntent().getStringExtra("RES_ID"), "" + items.id, "1");
                            } else {
                                addToCart(storeUserData.getString(Constants.NEAR_RES_ID), "" + items.id, "1");
                            }
                            dialog.dismiss();
                        }else {
                            Utils.showTopMessageError(activity, "Please Select Your Customizations.");
                        }
                    }
                });

                assert llSkip != null;
                llSkip.setOnClickListener(view -> {

                    paidCustomizationArray.clear();
                    freeCustomizationArray.clear();

                    paidCustomizationItemString = "";
                    freeCustomizationItemString = "";
                    tasteCustomizationItemString = "";
                    cookingGradesCustomizationItemString = "";

                    if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                        addToCart(getIntent().getStringExtra("RES_ID"), "" + items.id, "1");
                    } else {
                        addToCart(storeUserData.getString(Constants.NEAR_RES_ID), "" + items.id, "1");
                    }
                    dialog.dismiss();
                });
            }

            @Override
            public void onDialogTest(CategoryItemPojo.ResponseData items, int position) {

                dialog = new BottomSheetDialog(activity, R.style.CustomBottomSheetDialogTheme);
                View deliveryTimeSheetView = getLayoutInflater().inflate(R.layout.row_customize_item, null);
                dialog.setContentView(deliveryTimeSheetView);
                dialog.setCancelable(false);
                rowCustomizeItemBinding = DataBindingUtil.bind(deliveryTimeSheetView);
                bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);

                if (bottomSheet != null) {
                    BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                }

                if (bottomSheet != null) {
                    BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
                }

                if (bottomSheet != null) {
                    BottomSheetBehavior.from(bottomSheet).setHideable(true);
                }

                customizationButtonClick();

                dialog.show();

                TextView titleCust = dialog.findViewById(R.id.titleCust);
                titleCust.setText("CHOOSE YOUR TEST ADD ON");

                TextView tvPaidCost = dialog.findViewById(R.id.tvPaidCustomization);
                tvPaidCost.setVisibility(View.GONE);

                RecyclerView paidCustomization;
                paidCustomization = dialog.findViewById(R.id.rvPaidCustomization);
                Objects.requireNonNull(paidCustomization).setLayoutManager(new LinearLayoutManager(activity));
                paidCustomization.setNestedScrollingEnabled(false);
                paidCustomization.setHasFixedSize(true);

                testCustomizationMenuAdapter = new TestCustomizationMenuAdapter(activity, items.customization, itemClickListener);
                paidCustomization.setAdapter(testCustomizationMenuAdapter);
                testCustomizationMenuAdapter.notifyDataSetChanged();

                ImageView close = dialog.findViewById(R.id.close);
                TextView addToCartCustomize = dialog.findViewById(R.id.addToCartCustomize);
                LinearLayout llSkip = dialog.findViewById(R.id.llSkip);

                Objects.requireNonNull(addToCartCustomize).setOnClickListener(view -> {
                    if (!TextUtils.isEmpty(paidCustomizationItemString) || !TextUtils.isEmpty(freeCustomizationItemString)) {
                        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                            addToCart(getIntent().getStringExtra("RES_ID"), "" + items.id, "1");
                        } else {
                            addToCart(storeUserData.getString(Constants.NEAR_RES_ID), "" + items.id, "1");
                        }
                        dialog.dismiss();
                    } else {
                        Utils.showTopMessageError(activity, "Please Select Your Customizations.");
                    }
                });

                assert llSkip != null;
                llSkip.setOnClickListener(view -> {

                    paidCustomizationItemString = "";

                    if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                        addToCart(getIntent().getStringExtra("RES_ID"), "" + items.id, "1");
                    } else {
                        addToCart(storeUserData.getString(Constants.NEAR_RES_ID), "" + items.id, "1");
                    }
                    dialog.dismiss();
                });

                assert close != null;
                close.setOnClickListener(view -> {
                    paidCustomizationItemString = "";
                    dialog.dismiss();
                });

            }
        };

        binding.back.setOnClickListener(view -> finish());

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartCounter == 1){
                    startActivity(new Intent(activity,Home_New.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                }else {
                    finish();
                }
            }
        });

        binding.cart.setOnClickListener(v -> {
            if (storeUserData.getString(Constants.GUEST_LOGIN).equalsIgnoreCase("1")) {
                Utils.guestLogin(activity);
            } else {
                startActivity(new Intent(activity, CartNewActivity.class));
            }
        });

        binding.llTotalAmount.setOnClickListener(view -> {

            int ct = 0;
            int selectPos = 0;
            for (int i = 0; i < categoryData.size(); i++) {
                ct++;
                if (categoryData.get(i).selectedItem && categoryData.size() - 1 >= i + 1) {
                    selectPos = i;
                    categoryData.get(i + 1).selectedItem = true;
                    catID = "" + categoryData.get(i + 1).id;
                    categoryData.get(i).selectedItem = false;
                    break;
                } else {
                    categoryData.get(i).selectedItem = false;
                }
            }
            if (categoryData.size() == ct) {
                Constants.CATEGORY_ID = "";
                startActivity(new Intent(activity, CartNewActivity.class));
            } else {
                if (horizontalLayout.findLastCompletelyVisibleItemPosition() <= selectPos) {
                    binding.rvCategory.smoothScrollToPosition(horizontalLayout.findLastCompletelyVisibleItemPosition() + 1);
                }
                categoryAdapter.notifyDataSetChanged();
                Log.d("DID", "" + catID);

                if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                    categoryItem(true, getIntent().getStringExtra("RES_ID"));
                } else {
                    categoryItem(true, storeUserData.getString(Constants.NEAR_RES_ID));
                }
            }
        });

        binding.list.setOnClickListener(view -> {
            binding.box.setVisibility(View.VISIBLE);
            binding.list.setVisibility(View.GONE);
            binding.rvCategoryItemNew.setVisibility(View.GONE);
            binding.rvCategoryItem.setVisibility(View.VISIBLE);
        });

        binding.box.setOnClickListener(view -> {
            binding.box.setVisibility(View.GONE);
            binding.list.setVisibility(View.VISIBLE);
            binding.rvCategoryItemNew.setVisibility(View.VISIBLE);
            binding.rvCategoryItem.setVisibility(View.GONE);
        });

        horizontalLayout = new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false);
        binding.rvCategory.setLayoutManager(horizontalLayout);
        binding.rvCategory.setNestedScrollingEnabled(false);
        binding.rvCategory.setHasFixedSize(true);

        binding.rvCategoryItem.setLayoutManager(new GridLayoutManager(activity, 2));
        binding.rvCategoryItem.setNestedScrollingEnabled(false);
        binding.rvCategoryItem.setHasFixedSize(true);

        binding.rvCategoryItemNew.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvCategoryItemNew.setNestedScrollingEnabled(false);
        binding.rvCategoryItemNew.setHasFixedSize(true);

        binding.prevImg.setOnClickListener(view -> {
            if (horizontalLayout.findFirstVisibleItemPosition() > 0) {
                binding.rvCategory.smoothScrollToPosition(horizontalLayout.findFirstVisibleItemPosition() - 1);
            } else {
                binding.rvCategory.smoothScrollToPosition(0);
            }
        });

        binding.nextImg.setOnClickListener(view -> {
            Log.i("FDSF", "" + (horizontalLayout.findLastVisibleItemPosition() + 1));
            binding.rvCategory.smoothScrollToPosition(horizontalLayout.findLastVisibleItemPosition() + 1);
        });
    }

    @Override
    public void onBackPressed() {
        if (cartCounter == 1){
            startActivity(new Intent(activity,Home_New.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        }else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
            checkCart(true, getIntent().getStringExtra("RES_ID"));
        } else {
            checkCart(true, storeUserData.getString(Constants.NEAR_RES_ID));
        }
    }

    private void getCategory(boolean progress, String id) {

        if (progress) {
            Utils.showProgress(activity);
        }

        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;
        call = retrofitHelper.api().restaurantCategory(
                new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()),
                storeUserData.getString(Constants.DAY),
                id,
                storeUserData.getString(Constants.APP_LANGUAGE)
        );
        retrofitHelper.callApi(activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                if (progress) {
                    Utils.dismissProgress();
                }
                try {
                    if (body.code() != 200) {
                        Utils.serverError(activity, body.code());
                        return;
                    }
                    String response = body.body().string();
                    Log.i("TAG", "CATEGORY: " + response);
                    Reader reader = new StringReader(response);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    categoryDataItem = gson.fromJson(reader, CategoryPojo.class);

                    if (categoryDataItem.status == 1 && categoryDataItem.responsedata.size() > 0) {

                        if (TextUtils.isEmpty(categoryDataItem.res_open_error)) {
                            binding.llOrderInfo.setVisibility(View.GONE);
                        } else if (categoryDataItem.res_open_error.equalsIgnoreCase("1")) {
                            binding.view1.setBackgroundResource(R.drawable.circle_green_solid);
                            binding.view2.setBackgroundResource(R.drawable.circle_green_solid);
                        } else if (categoryDataItem.res_open_error.equalsIgnoreCase("0")) {
                            binding.view1.setBackgroundResource(R.drawable.circle_red_solid);
                            binding.view2.setBackgroundResource(R.drawable.circle_red_solid);
                        } else {
                            binding.llOrderInfo.setVisibility(View.VISIBLE);
                            binding.orderText.setText(categoryDataItem.res_open_error);
                        }

                        categoryData = categoryDataItem.responsedata;
                        categoryData.get(0).selectedItem = true;
                        binding.llMain.setVisibility(View.VISIBLE);
                        binding.llEmptyCart1.setVisibility(View.GONE);
                        categoryAdapter = new CategoryAdapter(activity, categoryData, itemClickListener);
                        binding.rvCategory.setAdapter(categoryAdapter);
                        binding.llCategory.setVisibility(View.VISIBLE);

                    } else {
                        binding.llMain.setVisibility(View.GONE);
                        binding.llEmptyCart1.setVisibility(View.VISIBLE);
                        binding.list.setVisibility(View.GONE);
                        binding.box.setVisibility(View.GONE);
                        binding.llCategory.setVisibility(View.GONE);
                        Utils.dismissProgress();
                    }

                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.e("ERROR", error);
            }
        });
    }

    private void categoryItem(boolean progress, String id) {
        if (progress) {
            Utils.showProgress(activity);
        }
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().restaurantMenuItems(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                id,
                catID,
                storeUserData.getString(Constants.CART_ID)
        );


        retrofitHelper.callApi(activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                Utils.dismissProgress();
                try {
                    if (body.code() != 200) {
                        Utils.serverError(activity, body.code());
                        return;
                    }

                    String response = body.body().string();
                    Log.i("TAG", "CATEGORY_ITEM: " + response);
                    Reader reader = new StringReader(response);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CategoryItemPojo data = gson.fromJson(reader, CategoryItemPojo.class);

                    if (data.status.equalsIgnoreCase("1")) {

                        if (storeUserData.getString(Constants.ITEM_GRID).equalsIgnoreCase("0")) {
                            binding.box.performClick();
                            binding.list.setVisibility(View.GONE);
                            binding.box.setVisibility(View.GONE);
                            binding.demoBox.setVisibility(View.GONE);

                        } else {
                            if (binding.box.getVisibility() == View.VISIBLE) {
                                binding.list.performClick();
                            } else if (binding.list.getVisibility() == View.VISIBLE) {
                                binding.box.performClick();
                            } else {
                                binding.list.performClick();
                            }
                        }

                        binding.llEmptyCart.setVisibility(View.GONE);
                        binding.rvCategoryItem.setAdapter(new CategoryItemAdapter(activity, categoryDataItem.pre_order_accept, data.responsedata, itemClickListener));
                        binding.rvCategoryItemNew.setAdapter(new CategoryItemNewAdapter(activity, categoryDataItem.pre_order_accept, data.responsedata, itemClickListener));

                    } else {
                        binding.box.setVisibility(View.GONE);
                        binding.list.setVisibility(View.GONE);
                        binding.llqty.setVisibility(View.GONE);
                        binding.rvCategoryItem.setVisibility(View.INVISIBLE);
                        binding.rvCategoryItemNew.setVisibility(View.INVISIBLE);
                        binding.llEmptyCart.setVisibility(View.VISIBLE);
                    }

                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.e("ERROR", error);
            }
        });
    }

    private void addToCart(String resID, String id, String qty) {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        String ADDID = "";
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("ADDRESSID")) {
            ADDID = getIntent().getStringExtra("ADDRESSID");
        }

        call = retrofitHelper.api().addToCart(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                resID,
                id,
                qty,
                paidCustomizationItemString,
                freeCustomizationItemString,
                cookingGradesCustomizationItemString,
                tasteCustomizationItemString,
                storeUserData.getString(Constants.CART_ID),
                ADDID,
                getIntent().getStringExtra("ORDERTYPE")
        );
        retrofitHelper.callApi(activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                try {
                    if (body.code() != 200) {
                        Utils.serverError(activity, body.code());
                        return;
                    }

                    String response = body.body().string();
                    Log.i("TAG", "addToCartItem: " + response);
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("status") == 0) {
                        Utils.dismissProgress();
                        Utils.showTopMessageError(activity, jsonObject.getString("message"));
                    } else {

                        JSONObject object = jsonObject.getJSONObject("responsedata");

                        if (object.has("isdiff_res")) {

                            Utils.dismissProgress();

                            if (object.getInt("isdiff_res") == 1) {

                                new AlertDialog.Builder(activity)
                                        .setTitle("Replace Cart Item ?")
                                        .setMessage("Your cart contains dishes from.Do you want to discard selection and add dishes from.")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", (dialog, which) -> {
                                            new StoreUserData(activity).setString(Constants.CART_ID, "");
                                            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                                                addToCart(getIntent().getStringExtra("RES_ID"), id, "1");
                                            } else {
                                                addToCart(storeUserData.getString(Constants.NEAR_RES_ID), id, "1");
                                            }
                                            freeCustomizationArray.clear();
                                            paidCustomizationArray.clear();

                                            paidCustomizationItemString = "";
                                            freeCustomizationItemString = "";
                                            cookingGradesCustomizationItemString = "";
                                            tasteCustomizationItemString = "";

                                            return;
                                        })
                                        .setNegativeButton("No", null)
                                        .setIcon(R.mipmap.ic_launcher)
                                        .show();
                            }
                        } else {

                            freeCustomizationArray.clear();
                            paidCustomizationArray.clear();

                            paidCustomizationItemString = "";
                            freeCustomizationItemString = "";
                            cookingGradesCustomizationItemString = "";
                            tasteCustomizationItemString = "";

                            if (storeUserData.getString(Constants.CART_ID).equalsIgnoreCase("")) {
                                storeUserData.setString(Constants.CART_ID, object.getString("cart_id"));
                            }

                            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                                checkCart(false, getIntent().getStringExtra("RES_ID"));
                            } else {
                                checkCart(false, storeUserData.getString(Constants.NEAR_RES_ID));
                            }
                        }
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();

                    freeCustomizationArray.clear();
                    paidCustomizationArray.clear();

                    paidCustomizationItemString = "";
                    freeCustomizationItemString = "";
                    cookingGradesCustomizationItemString = "";
                    tasteCustomizationItemString = "";


                    if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                        checkCart(false, getIntent().getStringExtra("RES_ID"));
                    } else {
                        checkCart(false, storeUserData.getString(Constants.NEAR_RES_ID));
                    }
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.e("ERROR", error);
                if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                    checkCart(false, getIntent().getStringExtra("RES_ID"));
                } else {
                    checkCart(false, storeUserData.getString(Constants.NEAR_RES_ID));
                }
            }
        });
    }

    private void checkCart(boolean progress, String id) {

        if (progress) {
            Utils.showProgress(activity);
        }

        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().checkCartAvailability(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                storeUserData.getString(Constants.CART_ID),
                id
        );

        retrofitHelper.callApi(activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                try {
                    if (body.code() != 200) {
                        Utils.serverError(activity, body.code());
                        return;
                    }

                    String response = body.body().string();
                    Log.i("TAG", "checkCartAvailability: " + response);
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("status") == 1) {

                        Log.i("CART", "FULL");


                        JSONObject jsonObject1 = jsonObject.getJSONObject("responsedata");
                        if (jsonObject1.getInt("cart_items") > 0) {
                            binding.llqty.setVisibility(View.VISIBLE);
                            binding.llqty.setText(jsonObject1.getString("cart_items"));
                            cartCounter = 1;
                        } else {
                            binding.llqty.setVisibility(View.INVISIBLE);
                            cartCounter = 0;
                        }




                        if (jsonObject1.getString("final_total").equalsIgnoreCase("0.00")) {
                            binding.amount.setVisibility(View.INVISIBLE);
                        } else {
                            binding.amount.setVisibility(View.VISIBLE);
                            binding.amount.setText(Constants.TOTAL + " |  "+storeUserData.getString( Constants.CURRENCY )+" " + jsonObject1.getString("final_total"));
                        }

                        callApi(progress);

                    } else {
                        Log.i("CART", "EMPTY");
                        binding.llqty.setVisibility(View.INVISIBLE);
                        binding.amount.setVisibility(View.INVISIBLE);

                        callApi(progress);
                    }


                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }


            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
                Log.e("ERROR", error);
            }
        });
    }

    public void callApi(boolean progress) {


        if (progress) {

            Log.i("CART", "PROGRESS_OPEN");

            if (!TextUtils.isEmpty(Constants.CATEGORY_ID)) {

                Log.i("CART", "CATEGORY_ITEM_CATEGORY_ID");

                if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                    categoryItem(false, getIntent().getStringExtra("RES_ID"));
                } else {
                    categoryItem(false, storeUserData.getString(Constants.NEAR_RES_ID));
                }

            } else {

                Log.i("CART", "CATEGORY_API");

                if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                    getCategory(false, getIntent().getStringExtra("RES_ID"));
                } else {
                    getCategory(false, storeUserData.getString(Constants.NEAR_RES_ID));
                }
            }

        } else {
            Log.i("CART", "PROGRESS_CLOSE");
            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                categoryItem(false, getIntent().getStringExtra("RES_ID"));
            } else {
                categoryItem(false, storeUserData.getString(Constants.NEAR_RES_ID));
            }
        }

    }

    private void changeQtyApi(String cartItemId, String qty) {

        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().changeQuantity(
                new StoreUserData(activity).getString(Constants.USER_ID),
                new StoreUserData(activity).getString(Constants.TOKEN),
                "" + cartItemId,
                storeUserData.getString(Constants.CART_ID),
                "" + qty
        );

        retrofitHelper.callApi(activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                // Utils.dismissProgress();
                try {
                    if (body.code() != 200) {
                        Utils.serverError(activity, body.code());
                        return;
                    }
                    String response = body.body().string();
                    Log.i("TAG", "changeQty: " + response);

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {

                        JSONObject jsonObject1 = jsonObject.getJSONObject("responsedata");

                        if (jsonObject1.getString("order_total").equalsIgnoreCase("0")) {
                            storeUserData.setString(Constants.CART_ID, "");
                        }

                        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                            checkCart(false, getIntent().getStringExtra("RES_ID"));
                        } else {
                            checkCart(false, storeUserData.getString(Constants.NEAR_RES_ID));
                        }

                    } else {
                        Utils.dismissProgress();
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.e("ERROR", error);
                Utils.dismissProgress();
            }
        });
    }

    private void changeCartQtyApi(String i, String cartItemId, String qty) {

        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().changeQuantity(
                new StoreUserData(activity).getString(Constants.USER_ID),
                new StoreUserData(activity).getString(Constants.TOKEN),
                "" + cartItemId,
                storeUserData.getString(Constants.CART_ID),
                "" + qty
        );

        retrofitHelper.callApi(activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                // Utils.dismissProgress();
                try {
                    if (body.code() != 200) {
                        Utils.serverError(activity, body.code());
                        return;
                    }
                    String response = body.body().string();
                    Log.i("TAG", "ChangeCartQty: " + response);

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        cartCustItem(false, i);
                    } else {
                        Utils.dismissProgress();
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.e("ERROR", error);
                Utils.dismissProgress();
            }
        });
    }

    private void cartCustItem(boolean progress, String itemId) {

        if (progress) {
            Utils.showProgress(activity);
        }

        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().cartCustomizationItems(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                itemId,
                storeUserData.getString(Constants.CART_ID)
        );


        retrofitHelper.callApi(activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                Utils.dismissProgress();
                try {
                    if (body.code() != 200) {
                        Utils.serverError(activity, body.code());
                        return;
                    }
                    String response = body.body().string();
                    Log.i("TAG", "cart_customization_items" + response);
                    Reader reader = new StringReader(response);

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                            .serializeNulls()
                            .create();

                    CustomizationPojo data = gson.fromJson(reader, CustomizationPojo.class);

                    if (data.status == 1) {
                        rvCust.setAdapter(new CuatomizationAdapter(activity, data.responsedata, itemClickListener));
                    } else {
                        custItemDialog.dismiss();
                        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("RES_ID")) {
                            checkCart(true, getIntent().getStringExtra("RES_ID"));
                        } else {
                            checkCart(true, storeUserData.getString(Constants.NEAR_RES_ID));
                        }
                    }

                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.e("ERROR", error);
            }
        });
    }

    public void customizationButtonClick() {
        TextView tv = dialog.findViewById(R.id.addToCartCustomize);
        if (!TextUtils.isEmpty(freeCustomizationItemString) || !TextUtils.isEmpty(paidCustomizationItemString) || !TextUtils.isEmpty(tasteCustomizationItemString) || !TextUtils.isEmpty(cookingGradesCustomizationItemString)) {
            tv.setAlpha((float) 0.9);
            tv.setEnabled(true);
        } else {
            tv.setAlpha((float) 0.5);
            tv.setEnabled(false);
        }
    }

    public interface ItemClickListener {

        void onClick(String id, int pos, String categoryName);

        void addToCartClick(String id, String qty);

        void freeCustomizeClick(String item);

        void paidCustomizeClick(String item);

        void paidCustomizeTestClick(String item);

        void cookingGradesCustomizeTestClick(String item);

        void tasteCustomizeTestClick(String item);

        void freeCustomizationRemove(String item);

        void paidCustomizeRemove(String item);

        void onDialog(CategoryItemPojo.ResponseData items, int position);

        void onDialogTest(CategoryItemPojo.ResponseData items, int position);

        void changeQtyCust(String id, String itemId, String qty);

        void openDetailScreen(String itemId);

        void changeQty(int cartItems, int removeItem, String iteId, String id, String qty, int isCustomization, CategoryItemPojo.ResponseData items, int position, int testCustomization);
    }
}
