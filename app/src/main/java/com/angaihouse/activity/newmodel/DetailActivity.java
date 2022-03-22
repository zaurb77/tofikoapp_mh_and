package com.angaihouse.activity.newmodel;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.angaihouse.R;
import com.angaihouse.activity.BrowserActivity;
import com.angaihouse.databinding.ActivityDetailBinding;
import com.angaihouse.databinding.DialogCartCustomizationItemsBinding;
import com.angaihouse.databinding.RowCustomizeItemBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utils;
import com.bumptech.glide.Glide;
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
import java.util.ArrayList;
import java.util.Objects;

import adapter.CookingGradeCustomizationMenuAdapter1;
import adapter.CuatomizationDetailAdapter;
import adapter.FreeCustomizationMenuDetailAdapter;
import adapter.MultipleImagesAdapter;
import adapter.PaidCustomizationMenuDetailAdapter;
import adapter.TasteCustomizationMenuAdapter1;
import okhttp3.ResponseBody;
import pojo.CustomizationPojo;
import pojo.DetailPojo;
import retrofit2.Call;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    String paidCustomizationItemString, cookingCustomizationItemString, testCustomizationItemString, freeCustomizationItemString;

    ActivityDetailBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;

    FrameLayout bottomSheet;
    FrameLayout bottomSheet1;

    BottomSheetDialog dialog;
    RowCustomizeItemBinding rowCustomizeItemBinding;
    DialogCartCustomizationItemsBinding dialogCartCustomizationItemsBinding;

    ArrayList<String> freeCustomizationArray = new ArrayList<>();
    ArrayList<String> paidCustomizationArray = new ArrayList<>();

    CookingGradeCustomizationMenuAdapter1 cookingGradeCustomizationMenuAdapter;
    FreeCustomizationMenuDetailAdapter freeCustomizationMenuAdapter;
    PaidCustomizationMenuDetailAdapter paidCustomizationMenuAdapter;
    TasteCustomizationMenuAdapter1 tasteCustomizationMenuAdapter;

    DetailPojo pojo;
    BottomSheetDialog custItemDialog;
    RecyclerView rvCust;
    LinearLayoutManager horizontalLayout;

    private ItemClickListener itemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        activity = this;
        storeUserData = new StoreUserData( activity );
        binding = DataBindingUtil.setContentView( activity, R.layout.activity_detail );
        // llAddQty

        binding.tvAdd.setText( Constants.ADD );
        binding.tvIngredients.setText( Constants.INGREDIENTS );
        binding.back.setOnClickListener( view -> finish() );

        itemClickListener = new ItemClickListener() {
            @Override
            public void freeCustomizeClick(String item) {
                freeCustomizationArray.add( item );
                freeCustomizationItemString = android.text.TextUtils.join( ",", freeCustomizationArray );
                customizationButtonClick();
            }

            @Override
            public void paidCustomizeClick(String item) {
                paidCustomizationArray.add( item );
                paidCustomizationItemString = android.text.TextUtils.join( ",", paidCustomizationArray );
                customizationButtonClick();
            }

            @Override
            public void paidCustomizeTestClick(String item) {
                paidCustomizationItemString = item;
                customizationButtonClick();
            }

            @Override
            public void freeCustomizationRemove(String item) {
                for (int i = 0; i < freeCustomizationArray.size(); i++) {
                    if (freeCustomizationArray.get( i ).equalsIgnoreCase( item )) {
                        freeCustomizationArray.remove( i );
                    }
                }
                freeCustomizationItemString = android.text.TextUtils.join( ",", freeCustomizationArray );
                customizationButtonClick();
            }

            @Override
            public void paidCustomizeRemove(String item) {

                for (int i = 0; i < paidCustomizationArray.size(); i++) {
                    if (paidCustomizationArray.get( i ).equalsIgnoreCase( item )) {
                        paidCustomizationArray.remove( i );
                    }
                }
                paidCustomizationItemString = android.text.TextUtils.join( ",", paidCustomizationArray );
                customizationButtonClick();
            }

            @Override
            public void tasteCustomizeTestClick(String item) {
                testCustomizationItemString = item;
                customizationButtonClick();
            }

            @Override
            public void cookingGradesCustomizeTestClick(String item) {
                cookingCustomizationItemString = item;
                customizationButtonClick();
            }

            @Override
            public void changeQtyCust(String id, String itemId, String qty) {
                changeCartQtyApi( id, itemId, qty );
            }

            @Override
            public void imageClick(String image) {
                Glide.with( activity )
                        .load( image )
                        .into( binding.itemImage );
            }

        };

        binding.llAddQty.setOnClickListener( view -> {

            if (pojo.responsedata.remove_customization.size() > 0 || pojo.responsedata.customization.size() > 0 || pojo.responsedata.cooking_grades.size() > 0 || pojo.responsedata.taste.size() > 0) {

                dialog = new BottomSheetDialog( activity, R.style.CustomBottomSheetDialogTheme );
                View deliveryTimeSheetView = getLayoutInflater().inflate( R.layout.row_customize_item, null );
                dialog.setContentView( deliveryTimeSheetView );
                dialog.setCancelable( false );
                rowCustomizeItemBinding = DataBindingUtil.bind( deliveryTimeSheetView );
                bottomSheet = dialog.findViewById( R.id.design_bottom_sheet );
                if (bottomSheet != null) {
                    BottomSheetBehavior.from( bottomSheet ).setState( BottomSheetBehavior.STATE_EXPANDED );
                }
                if (bottomSheet != null) {
                    BottomSheetBehavior.from( bottomSheet ).setSkipCollapsed( true );
                }
                if (bottomSheet != null) {
                    BottomSheetBehavior.from( bottomSheet ).setHideable( true );
                }

                customizationButtonClick();
                dialog.show();

                BottomSheetBehavior behaviour = BottomSheetBehavior.from( bottomSheet );
                behaviour.setState( BottomSheetBehavior.STATE_EXPANDED );
                behaviour.setBottomSheetCallback( new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            // Bottom Sheet was dismissed by user! But this is only fired, if dialog is swiped down! Not if touch outside dismissed the dialog or the back button
                            Log.i( "Dialog_Close", "Dialog Close" );

                            paidCustomizationArray.clear();
                            freeCustomizationArray.clear();

                            paidCustomizationItemString = "";
                            freeCustomizationItemString = "";
                            testCustomizationItemString = "";
                            cookingCustomizationItemString = "";

                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                } );

                TextView tvTaste1 = dialog.findViewById( R.id.tvTaste );
                tvTaste1.setText( Constants.CHOOSE_TEST );

                TextView tvCookingGrade1 = dialog.findViewById( R.id.tvCookingGrade );
                tvCookingGrade1.setText( Constants.COOKING_LEVEL );

                TextView tvPaidCustomization1 = dialog.findViewById( R.id.tvPaidCustomization );
                tvPaidCustomization1.setText( Constants.ADD_EXTRA );

                TextView tvFreeModification1 = dialog.findViewById( R.id.tvFreeModification );
                tvFreeModification1.setText( Constants.WANT_TO_REMOVE + "?" );

                TextView addToCartCustomize1 = dialog.findViewById( R.id.addToCartCustomize );
                addToCartCustomize1.setText( Constants.APPLY_CHANGES );

                if (pojo.responsedata.cooking_grades.size() == 0 && pojo.responsedata.taste.size() == 0) {
                    addToCartCustomize1.setEnabled( true );
                    addToCartCustomize1.setAlpha( 0.9f );
                }

                //TODO : REMOVE CUSTOMIZATIONS
                TextView tvFreeCost = dialog.findViewById( R.id.tvFreeModification );
                tvFreeCost.setVisibility( View.VISIBLE );
                if (pojo.responsedata.remove_customization.size() > 0) {
                    Objects.requireNonNull( tvFreeCost ).setVisibility( View.VISIBLE );
                } else {
                    Objects.requireNonNull( tvFreeCost ).setVisibility( View.GONE );
                }

                RecyclerView freeCustomization;
                freeCustomization = dialog.findViewById( R.id.rvFreeCustomization );
                Objects.requireNonNull( freeCustomization ).setLayoutManager( new LinearLayoutManager( activity ) );
                freeCustomization.setNestedScrollingEnabled( false );
                freeCustomization.setHasFixedSize( true );
                freeCustomizationMenuAdapter = new FreeCustomizationMenuDetailAdapter( activity, pojo.responsedata.remove_customization, itemClickListener );
                freeCustomization.setAdapter( freeCustomizationMenuAdapter );
                freeCustomizationMenuAdapter.notifyDataSetChanged();

                //TODO : CUSTOMIZATIONS
                TextView tvPaidCost = dialog.findViewById( R.id.tvPaidCustomization );
                if (pojo.responsedata.customization.size() > 0) {
                    tvPaidCost.setVisibility( View.VISIBLE );
                } else {
                    tvPaidCost.setVisibility( View.GONE );
                }

                RecyclerView paidCustomization;
                paidCustomization = dialog.findViewById( R.id.rvPaidCustomization );
                assert paidCustomization != null;
                paidCustomization.setLayoutManager( new LinearLayoutManager( activity ) );
                paidCustomization.setNestedScrollingEnabled( false );
                paidCustomization.setHasFixedSize( true );
                paidCustomizationMenuAdapter = new PaidCustomizationMenuDetailAdapter( activity, pojo.responsedata.customization, itemClickListener );
                paidCustomization.setAdapter( paidCustomizationMenuAdapter );
                paidCustomizationMenuAdapter.notifyDataSetChanged();


                //TODO : COOKING GRADE CUSTOMIZATIONS
                TextView tvCookingGrade = dialog.findViewById( R.id.tvCookingGrade );
                if (pojo.responsedata.cooking_grades.size() > 0) {
                    Objects.requireNonNull( tvCookingGrade ).setVisibility( View.VISIBLE );
                } else {
                    Objects.requireNonNull( tvCookingGrade ).setVisibility( View.GONE );
                }

                RecyclerView rvCookingGrade;
                rvCookingGrade = dialog.findViewById( R.id.rvCookingGrade );
                rvCookingGrade.setLayoutManager( new LinearLayoutManager( activity ) );
                rvCookingGrade.setNestedScrollingEnabled( false );
                rvCookingGrade.setHasFixedSize( true );
                cookingGradeCustomizationMenuAdapter = new CookingGradeCustomizationMenuAdapter1( activity, pojo.responsedata.cooking_grades, itemClickListener );
                rvCookingGrade.setAdapter( cookingGradeCustomizationMenuAdapter );
                cookingGradeCustomizationMenuAdapter.notifyDataSetChanged();


                //TODO : TASTE CUSTOMIZATIONS
                TextView tvTaste = dialog.findViewById( R.id.tvTaste );
                if (pojo.responsedata.taste.size() > 0) {
                    Objects.requireNonNull( tvTaste ).setVisibility( View.VISIBLE );
                } else {
                    Objects.requireNonNull( tvTaste ).setVisibility( View.GONE );
                }

                RecyclerView rvTaste;
                rvTaste = dialog.findViewById( R.id.rvTaste );
                rvTaste.setLayoutManager( new LinearLayoutManager( activity ) );
                rvTaste.setNestedScrollingEnabled( false );
                rvTaste.setHasFixedSize( true );
                tasteCustomizationMenuAdapter = new TasteCustomizationMenuAdapter1( activity, pojo.responsedata.taste, itemClickListener );
                rvTaste.setAdapter( tasteCustomizationMenuAdapter );
                tasteCustomizationMenuAdapter.notifyDataSetChanged();


                ImageView close = dialog.findViewById( R.id.close );
                TextView addToCartCustomize = dialog.findViewById( R.id.addToCartCustomize );
                LinearLayout llSkip = dialog.findViewById( R.id.llSkip );


                assert addToCartCustomize != null;
                addToCartCustomize.setOnClickListener( view2 -> {
                    if (!TextUtils.isEmpty( paidCustomizationItemString ) || !TextUtils.isEmpty( freeCustomizationItemString ) || !TextUtils.isEmpty( cookingCustomizationItemString ) || !TextUtils.isEmpty( testCustomizationItemString )) {
                        addTpCartApiCondition();
                        dialog.dismiss();
                    } else {
                        if (pojo.responsedata.cooking_grades.size() == 0 && pojo.responsedata.taste.size() == 0) {
                            addTpCartApiCondition();
                            dialog.dismiss();
                        } else {
                            Utils.showTopMessageError( activity, "Please Select Your Customizations." );
                        }
                    }
                } );

                assert llSkip != null;
                llSkip.setOnClickListener( view1 -> {

                    paidCustomizationArray.clear();
                    freeCustomizationArray.clear();

                    paidCustomizationItemString = "";
                    freeCustomizationItemString = "";
                    testCustomizationItemString = "";
                    cookingCustomizationItemString = "";

                    addTpCartApiCondition();

                    dialog.dismiss();
                } );

            } else {
                addTpCartApiCondition();
            }
        } );

        binding.addItem.setOnClickListener( view -> {

            if (pojo.responsedata.prev_customization.equalsIgnoreCase( "1" )) {

                dialog = new BottomSheetDialog( activity, R.style.CustomBottomSheetDialogTheme );
                View deliveryTimeSheetView1 = getLayoutInflater().inflate( R.layout.row_customize_item, null );
                dialog.setContentView( deliveryTimeSheetView1 );
                dialog.setCancelable( false );
                rowCustomizeItemBinding = DataBindingUtil.bind( deliveryTimeSheetView1 );
                bottomSheet = dialog.findViewById( R.id.design_bottom_sheet );
                if (bottomSheet != null) {
                    BottomSheetBehavior.from( bottomSheet ).setState( BottomSheetBehavior.STATE_EXPANDED );
                }
                if (bottomSheet != null) {
                    BottomSheetBehavior.from( bottomSheet ).setSkipCollapsed( true );
                }
                if (bottomSheet != null) {
                    BottomSheetBehavior.from( bottomSheet ).setHideable( true );
                }

                dialog.show();


                BottomSheetBehavior behaviour = BottomSheetBehavior.from( bottomSheet );
                behaviour.setState( BottomSheetBehavior.STATE_EXPANDED );
                behaviour.setBottomSheetCallback( new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            // Bottom Sheet was dismissed by user! But this is only fired, if dialog is swiped down! Not if touch outside dismissed the dialog or the back button
                            Log.i( "Dialog_Close", "Dialog Close" );

                            paidCustomizationArray.clear();
                            freeCustomizationArray.clear();

                            paidCustomizationItemString = "";
                            freeCustomizationItemString = "";
                            testCustomizationItemString = "";
                            cookingCustomizationItemString = "";

                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                } );


                TextView tvTaste1 = dialog.findViewById( R.id.tvTaste );
                tvTaste1.setText( Constants.CHOOSE_TEST );

                TextView tvCookingGrade1 = dialog.findViewById( R.id.tvCookingGrade );
                tvCookingGrade1.setText( Constants.COOKING_LEVEL );

                TextView tvPaidCustomization1 = dialog.findViewById( R.id.tvPaidCustomization );
                tvPaidCustomization1.setText( Constants.ADD_EXTRA );

                TextView tvFreeModification1 = dialog.findViewById( R.id.tvFreeModification );
                tvFreeModification1.setText( Constants.WANT_TO_REMOVE + "?" );

                TextView addToCartCustomize1 = dialog.findViewById( R.id.addToCartCustomize );
                addToCartCustomize1.setText( Constants.APPLY_CHANGES );


                TextView tvFreeCost = dialog.findViewById( R.id.tvFreeModification );
                if (pojo.responsedata.remove_customization.size() > 0) {
                    tvFreeCost.setVisibility( View.VISIBLE );
                } else {
                    tvFreeCost.setVisibility( View.GONE );
                }


                RecyclerView freeCustomization;
                freeCustomization = dialog.findViewById( R.id.rvFreeCustomization );
                freeCustomization.setLayoutManager( new LinearLayoutManager( activity ) );
                freeCustomization.setNestedScrollingEnabled( false );
                freeCustomization.setHasFixedSize( true );
                freeCustomizationMenuAdapter = new FreeCustomizationMenuDetailAdapter( activity, pojo.responsedata.remove_customization, itemClickListener );
                freeCustomization.setAdapter( freeCustomizationMenuAdapter );
                freeCustomizationMenuAdapter.notifyDataSetChanged();


                TextView tvPaidCost = dialog.findViewById( R.id.tvPaidCustomization );
                if (pojo.responsedata.customization.size() > 0) {
                    tvPaidCost.setVisibility( View.VISIBLE );
                } else {
                    tvPaidCost.setVisibility( View.GONE );
                }

                RecyclerView paidCustomization;
                paidCustomization = dialog.findViewById( R.id.rvPaidCustomization );
                paidCustomization.setLayoutManager( new LinearLayoutManager( activity ) );
                paidCustomization.setNestedScrollingEnabled( false );
                paidCustomization.setHasFixedSize( true );
                paidCustomizationMenuAdapter = new PaidCustomizationMenuDetailAdapter( activity, pojo.responsedata.customization, itemClickListener );
                paidCustomization.setAdapter( paidCustomizationMenuAdapter );
                paidCustomizationMenuAdapter.notifyDataSetChanged();


                //TODO : REMOVE COOKING GRADE CUSTOMIZATIONS
                TextView tvCookingGrade = dialog.findViewById( R.id.tvCookingGrade );
                if (pojo.responsedata.cooking_grades.size() > 0) {
                    Objects.requireNonNull( tvCookingGrade ).setVisibility( View.VISIBLE );
                } else {
                    Objects.requireNonNull( tvCookingGrade ).setVisibility( View.GONE );
                }

                RecyclerView rvCookingGrade;
                rvCookingGrade = dialog.findViewById( R.id.rvCookingGrade );
                rvCookingGrade.setLayoutManager( new LinearLayoutManager( activity ) );
                rvCookingGrade.setNestedScrollingEnabled( false );
                rvCookingGrade.setHasFixedSize( true );
                cookingGradeCustomizationMenuAdapter = new CookingGradeCustomizationMenuAdapter1( activity, pojo.responsedata.cooking_grades, itemClickListener );
                rvCookingGrade.setAdapter( cookingGradeCustomizationMenuAdapter );
                cookingGradeCustomizationMenuAdapter.notifyDataSetChanged();


                //TODO : TASTE CUSTOMIZATIONS
                TextView tvTaste = dialog.findViewById( R.id.tvTaste );
                if (pojo.responsedata.taste.size() > 0) {
                    Objects.requireNonNull( tvTaste ).setVisibility( View.VISIBLE );
                } else {
                    Objects.requireNonNull( tvTaste ).setVisibility( View.GONE );
                }

                RecyclerView rvTaste;
                rvTaste = dialog.findViewById( R.id.rvTaste );
                rvTaste.setLayoutManager( new LinearLayoutManager( activity ) );
                rvTaste.setNestedScrollingEnabled( false );
                rvTaste.setHasFixedSize( true );
                tasteCustomizationMenuAdapter = new TasteCustomizationMenuAdapter1( activity, pojo.responsedata.taste, itemClickListener );
                rvTaste.setAdapter( tasteCustomizationMenuAdapter );
                tasteCustomizationMenuAdapter.notifyDataSetChanged();

                ImageView close = dialog.findViewById( R.id.close );
                TextView addToCartCustomize = dialog.findViewById( R.id.addToCartCustomize );
                LinearLayout llSkip = dialog.findViewById( R.id.llSkip );

                assert addToCartCustomize != null;
                addToCartCustomize.setOnClickListener( view1 -> {
                    if (!TextUtils.isEmpty( paidCustomizationItemString ) || !TextUtils.isEmpty( freeCustomizationItemString ) || !TextUtils.isEmpty( cookingCustomizationItemString ) || !TextUtils.isEmpty( testCustomizationItemString )) {
                        addTpCartApiCondition();
                        dialog.dismiss();
                    } else {
                        Utils.showTopMessageError( activity, "Please Select Your Customizations." );
                    }
                } );


                assert llSkip != null;
                llSkip.setOnClickListener( view2 -> {
                    addTpCartApiCondition();
                    dialog.dismiss();
                } );

            } else {
                if (Integer.parseInt( pojo.responsedata.quantity ) > 0) {
                    pojo.responsedata.qty++;
                    pojo.responsedata.quantity = String.valueOf( pojo.responsedata.qty );
                    changeQtyApi( pojo.responsedata.cart_item_id, pojo.responsedata.quantity );
                }
            }
        } );

        binding.removeItem.setOnClickListener( view -> {

            if (pojo.responsedata.cart_items.size() > 1) {

                custItemDialog = new BottomSheetDialog( activity, R.style.CustomBottomSheetDialogTheme );
                View deliveryTimeSheetView = getLayoutInflater().inflate( R.layout.dialog_cart_customization_items, null );
                custItemDialog.setContentView( deliveryTimeSheetView );
                custItemDialog.setCancelable( false );
                rvCust = custItemDialog.findViewById( R.id.rvCustomization );
                dialogCartCustomizationItemsBinding = DataBindingUtil.bind( deliveryTimeSheetView );
                bottomSheet1 = custItemDialog.findViewById( R.id.design_bottom_sheet );

                if (rvCust != null) {
                    Objects.requireNonNull( rvCust ).setLayoutManager( new LinearLayoutManager( activity ) );
                }
                Objects.requireNonNull( rvCust ).setNestedScrollingEnabled( false );
                rvCust.setHasFixedSize( true );

                if (bottomSheet1 != null) {
                    BottomSheetBehavior.from( bottomSheet1 ).setState( BottomSheetBehavior.STATE_EXPANDED );
                }
                if (bottomSheet1 != null) {
                    BottomSheetBehavior.from( bottomSheet1 ).setSkipCollapsed( true );
                }
                if (bottomSheet1 != null) {
                    BottomSheetBehavior.from( bottomSheet1 ).setHideable( true );
                }


                custItemDialog.show();

                BottomSheetBehavior behaviour = BottomSheetBehavior.from( bottomSheet1 );
                behaviour.setState( BottomSheetBehavior.STATE_EXPANDED );
                behaviour.setBottomSheetCallback( new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            // Bottom Sheet was dismissed by user! But this is only fired, if dialog is swiped down! Not if touch outside dismissed the dialog or the back button
                            custItemDialog.dismiss();
                            getDetails( true );
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                } );

                cartCustItem( true, "" + pojo.responsedata.item_id );

            } else {
                if (pojo.responsedata.qty > 0) {
                    pojo.responsedata.qty--;
                    pojo.responsedata.quantity = String.valueOf( pojo.responsedata.qty );
                    changeQtyApi( pojo.responsedata.cart_item_id, pojo.responsedata.quantity );
                } else {
                    pojo.responsedata.quantity = "";
                }
            }
        } );

        binding.infoUrl.setOnClickListener( v -> {
            startActivity( new Intent( activity, BrowserActivity.class ).putExtra( "paymentUrl", binding.infoUrl.getText().toString() ) );
        } );

        horizontalLayout = new LinearLayoutManager( activity, RecyclerView.HORIZONTAL, false );
        binding.rvMultipleImage.setLayoutManager( horizontalLayout );
        binding.rvMultipleImage.setNestedScrollingEnabled( false );
        binding.rvMultipleImage.setHasFixedSize( true );

        binding.prevImg.setOnClickListener( v -> {
            if (horizontalLayout.findFirstVisibleItemPosition() > 0) {
                binding.rvMultipleImage.smoothScrollToPosition( horizontalLayout.findFirstVisibleItemPosition() - 1 );
            } else {
                binding.rvMultipleImage.smoothScrollToPosition( 0 );
            }
        } );

        binding.nextImg.setOnClickListener( v -> {
            binding.rvMultipleImage.smoothScrollToPosition( horizontalLayout.findLastVisibleItemPosition() + 1 );
        } );

        getDetails( true );
    }

    public void addTpCartApiCondition() {
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey( "MENU_ACTIVITY" )) {
            addToCart( getIntent().getStringExtra( "RESTAURANT_ID" ), "" + pojo.responsedata.item_id, "1", getIntent().getStringExtra( "ADDRESS_ID" ) );
        } else {
            addToCart( getIntent().getStringExtra( "BANNER_RESTAURANT_ID" ), "" + getIntent().getStringExtra( "ITEM_ID" ), "1", getIntent().getStringExtra( "BANNER_ADDRESS_ID" ) );
        }
    }

    private void getDetails(boolean progress) {
        if (progress) {
            Utils.showProgress( activity );
        }

        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().itemDetails(
                getIntent().getStringExtra( "ITEM_ID" ),
                storeUserData.getString( Constants.USER_ID ),
                storeUserData.getString( Constants.TOKEN ),
                storeUserData.getString( Constants.CART_ID )
        );

        retrofitHelper.callApi( activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                Utils.dismissProgress();
                try {
                    if (body.code() != 200) {
                        Utils.serverError( activity, body.code() );
                        return;
                    }
                    String response = body.body().string();
                    Log.i( "ItemDetails", response );

                    Reader reader = new StringReader( response );

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers( Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC )
                            .serializeNulls()
                            .create();

                    pojo = gson.fromJson( reader, DetailPojo.class );

                    Glide.with( activity )
                            .load( pojo.responsedata.item_image )
                            .placeholder( R.drawable.notfound )
                            .into( binding.itemImage );

                    Glide.with( activity )
                            .load( pojo.responsedata.barcode )
                            .into( binding.barCodeImage );

                    Glide.with( activity )
                            .load( pojo.responsedata.isbn )
                            .into( binding.isbnImage );

                    if (pojo.responsedata.images.size() > 1) {
                        binding.rvMultipleImage.setAdapter( new MultipleImagesAdapter( activity, pojo.responsedata.images, itemClickListener ) );
                        binding.llMultipleImages.setVisibility( View.VISIBLE );
                    } else {
                        binding.llMultipleImages.setVisibility( View.GONE );
                    }

                    if (pojo.responsedata.barcode.length() > 0) {
                        binding.llBarcode.setVisibility( View.VISIBLE );
                    }

                    if (pojo.responsedata.isbn.length() > 0) {
                        binding.llIsbn.setVisibility( View.VISIBLE );
                    }

                    if (pojo.responsedata.url.length() > 0) {
                        binding.llUrl.setVisibility( View.VISIBLE );
                    }

                    if (pojo.responsedata.manufacturer.length() > 0) {
                        binding.llManufacturer.setVisibility( View.VISIBLE );
                    }

                    if (pojo.responsedata.publisher.length() > 0) {
                        binding.llPublisher.setVisibility( View.VISIBLE );
                    }

                    binding.infoUrl.setText( pojo.responsedata.url );
                    binding.manufacturer.setText( pojo.responsedata.manufacturer );
                    binding.publisher.setText( pojo.responsedata.publisher );

                    if (pojo.responsedata.is_offered.equalsIgnoreCase( "1" )) {
                        binding.offerImage.setVisibility( View.VISIBLE );
                        binding.catPrice.setText( pojo.responsedata.offered_price );
                        binding.offerImage.setVisibility( View.VISIBLE );
                    } else {
                        binding.catPrice.setText( storeUserData.getString( Constants.CURRENCY ) + " " + pojo.responsedata.price );
                        binding.offerImage.setVisibility( View.GONE );
                    }

                    binding.itemName.setText( pojo.responsedata.item_name );

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        binding.descriptionDetail.setText( Html.fromHtml( pojo.responsedata.description, Html.FROM_HTML_MODE_COMPACT ) );
                    } else {
                        binding.descriptionDetail.setText( pojo.responsedata.description );
                    }

                    if (!TextUtils.isEmpty( pojo.responsedata.ingredients )) {
                        binding.llIngredientsDetail.setVisibility( View.VISIBLE );
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding.ingredientsDetail.setText( Html.fromHtml( pojo.responsedata.ingredients, Html.FROM_HTML_MODE_COMPACT ) );
                        } else {
                            binding.ingredientsDetail.setText( pojo.responsedata.ingredients );
                        }
                    } else {
                        binding.llIngredientsDetail.setVisibility( View.GONE );
                    }

                    if (!TextUtils.isEmpty( pojo.responsedata.allergens )) {
                        binding.llAllergens.setVisibility( View.VISIBLE );
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            binding.allergensDetail.setText( Html.fromHtml( pojo.responsedata.allergens, Html.FROM_HTML_MODE_COMPACT ) );
                        } else {
                            binding.allergensDetail.setText( pojo.responsedata.allergens );
                        }
                    } else {
                        binding.llAllergens.setVisibility( View.GONE );
                    }

                    if (pojo.responsedata.is_show == 0) {
                        if (pojo.pre_order_accept.equalsIgnoreCase( "0" )) {
                            binding.llAddQty.setEnabled( false );
                            binding.tvAdd.setAlpha( 0.8F );
                            binding.llQty.setVisibility( View.GONE );
                        } else {
                            binding.llAddQty.setEnabled( true );
                            binding.llQty.setVisibility( View.VISIBLE );
                        }

                    } else {
                        if (!TextUtils.isEmpty( pojo.responsedata.quantity ) && Integer.parseInt( pojo.responsedata.quantity ) > 0) {
                            binding.llQty.setVisibility( View.VISIBLE );
                            binding.llAddQty.setVisibility( View.GONE );
                            binding.qty.setText( pojo.responsedata.quantity );
                        } else {
                            binding.llQty.setVisibility( View.GONE );
                            binding.llAddQty.setVisibility( View.VISIBLE );
                        }

                        if (TextUtils.isEmpty( pojo.responsedata.quantity ) || pojo.responsedata.quantity.equalsIgnoreCase( "0" )) {
                            pojo.responsedata.qty = 0;
                        } else {
                            pojo.responsedata.qty = Integer.parseInt( pojo.responsedata.quantity );
                        }
                    }

                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.e( "ERROR", error );
            }
        } );
    }

    private void addToCart(String resID, String id, String qty, String addressID) {

        Utils.showProgress( activity );
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().addToCart(
                storeUserData.getString( Constants.USER_ID ),
                storeUserData.getString( Constants.TOKEN ),
                resID,
                id,
                qty,
                paidCustomizationItemString,
                freeCustomizationItemString,
                cookingCustomizationItemString,
                testCustomizationItemString,
                storeUserData.getString( Constants.CART_ID ),
                addressID,
                getIntent().getStringExtra( "BANNER_DELIVERY_TYPE" )
        );

        retrofitHelper.callApi( activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                try {
                    if (body.code() != 200) {
                        Utils.serverError( activity, body.code() );
                        return;
                    }

                    String response = body.body().string();
                    Log.i( "TAG", "addToCartItem: " + response );
                    JSONObject jsonObject = new JSONObject( response );

                    if (jsonObject.getInt( "status" ) == 0) {
                        Utils.dismissProgress();
                        Utils.showTopMessageError( activity, jsonObject.getString( "message" ) );
                    } else {

                        JSONObject object = jsonObject.getJSONObject( "responsedata" );

                        if (object.has( "isdiff_res" )) {

                            Utils.dismissProgress();

                            if (object.getInt( "isdiff_res" ) == 1) {

                                new AlertDialog.Builder( activity )
                                        .setTitle( "Replace Cart Item ?" )
                                        .setMessage( "Your cart contains dishes from.Do you want to discard selection and add dishes from." )
                                        .setCancelable( false )
                                        .setPositiveButton( "Yes", (dialog, which) -> {
                                            new StoreUserData( activity ).setString( Constants.CART_ID, "" );

                                            addTpCartApiCondition();
                                            freeCustomizationArray.clear();
                                            paidCustomizationArray.clear();
                                            paidCustomizationItemString = "";
                                            freeCustomizationItemString = "";
                                            testCustomizationItemString = "";
                                            cookingCustomizationItemString = "";


                                            return;
                                        } )
                                        .setNegativeButton( "No", null )
                                        .setIcon( R.mipmap.ic_launcher )
                                        .show();

                            }
                        } else {
                            if ((getIntent().getExtras() != null && getIntent().getExtras().containsKey( "BANNER_ITEM" )) || (getIntent().getExtras() != null && getIntent().getExtras().containsKey( "BANNER_ITEM_COMBO" ))) {
                                storeUserData.setString( Constants.NEAR_RES_ID, "" + getIntent().getStringExtra( "BANNER_RESTAURANT_ID" ) );
                                startActivity( new Intent( activity, MenuActivity.class )
                                        .putExtra( "ADDRESSID", "" + addressID )
                                        .putExtra( "ORDERTYPE", getIntent().getStringExtra( "BANNER_DELIVERY_TYPE" ) )
                                        .putExtra( "RES_ID", getIntent().getStringExtra( "BANNER_RESTAURANT_ID" ) )
                                );
                                finish();
                            } else {
                                freeCustomizationArray.clear();
                                paidCustomizationArray.clear();
                                paidCustomizationItemString = "";
                                freeCustomizationItemString = "";
                                testCustomizationItemString = "";
                                cookingCustomizationItemString = "";

                                if (storeUserData.getString( Constants.CART_ID ).equalsIgnoreCase( "" )) {
                                    storeUserData.setString( Constants.CART_ID, object.getString( "cart_id" ) );
                                }
                                getDetails( false );
                            }
                        }
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.e( "ERROR", error );
            }
        } );
    }

    private void changeQtyApi(String cartItemId, String qty) {

        Utils.showProgress( activity );
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().changeQuantity(
                new StoreUserData( activity ).getString( Constants.USER_ID ),
                new StoreUserData( activity ).getString( Constants.TOKEN ),
                "" + cartItemId,
                storeUserData.getString( Constants.CART_ID ),
                "" + qty
        );

        retrofitHelper.callApi( activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                // Utils.dismissProgress();
                try {
                    if (body.code() != 200) {
                        Utils.serverError( activity, body.code() );
                        return;
                    }
                    String response = body.body().string();
                    Log.i( "TAG", "changeQty: " + response );

                    JSONObject jsonObject = new JSONObject( response );
                    if (jsonObject.getInt( "status" ) == 1) {

                        JSONObject jsonObject1 = jsonObject.getJSONObject( "responsedata" );

                        if (jsonObject1.getString( "order_total" ).equalsIgnoreCase( "0" )) {
                            storeUserData.setString( Constants.CART_ID, "" );
                        }

                        freeCustomizationArray.clear();
                        paidCustomizationArray.clear();
                        paidCustomizationItemString = "";
                        freeCustomizationItemString = "";
                        testCustomizationItemString = "";
                        cookingCustomizationItemString = "";


                        getDetails( false );

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
                Log.e( "ERROR", error );
                Utils.dismissProgress();
            }
        } );
    }

    public void customizationButtonClick() {
        if (!TextUtils.isEmpty( freeCustomizationItemString ) || !TextUtils.isEmpty( paidCustomizationItemString ) || !TextUtils.isEmpty( cookingCustomizationItemString ) || !TextUtils.isEmpty( testCustomizationItemString )) {
            TextView tv = dialog.findViewById( R.id.addToCartCustomize );
            tv.setAlpha( (float) 0.9 );
            tv.setEnabled( true );
        } else {
            TextView tv = dialog.findViewById( R.id.addToCartCustomize );
            tv.setAlpha( (float) 0.5 );
            tv.setEnabled( false );
        }
    }

    public void customizationButtonTestClick() {
        if (paidCustomizationItemString.length() > 0) {
            TextView tv = dialog.findViewById( R.id.addToCartCustomize );
            tv.setAlpha( (float) 0.9 );
            tv.setEnabled( true );
        } else {
            TextView tv = dialog.findViewById( R.id.addToCartCustomize );
            tv.setAlpha( (float) 0.5 );
            tv.setEnabled( false );
        }
    }

    private void cartCustItem(boolean progress, String itemId) {
        if (progress) {
            Utils.showProgress( activity );
        }
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().cartCustomizationItems(
                storeUserData.getString( Constants.USER_ID ),
                storeUserData.getString( Constants.TOKEN ),
                itemId,
                storeUserData.getString( Constants.CART_ID )
        );


        retrofitHelper.callApi( activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                Utils.dismissProgress();
                try {
                    if (body.code() != 200) {
                        Utils.serverError( activity, body.code() );
                        return;
                    }
                    String response = body.body().string();
                    Log.i( "TAG", "cart_customization_items" + response );
                    Reader reader = new StringReader( response );

                    Gson gson = new GsonBuilder()
                            .excludeFieldsWithModifiers( Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC )
                            .serializeNulls()
                            .create();

                    CustomizationPojo data = gson.fromJson( reader, CustomizationPojo.class );

                    if (data.status == 1) {
                        rvCust.setAdapter( new CuatomizationDetailAdapter( activity, data.responsedata, itemClickListener ) );
                    } else {
                        custItemDialog.dismiss();
                        getDetails( false );
                    }

                } catch (IOException | NullPointerException | JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.e( "ERROR", error );
            }
        } );
    }

    private void changeCartQtyApi(String i, String cartItemId, String qty) {

        Utils.showProgress( activity );
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        final Call<ResponseBody> call;

        call = retrofitHelper.api().changeQuantity(
                new StoreUserData( activity ).getString( Constants.USER_ID ),
                new StoreUserData( activity ).getString( Constants.TOKEN ),
                "" + cartItemId,
                storeUserData.getString( Constants.CART_ID ),
                "" + qty
        );

        retrofitHelper.callApi( activity, call, new RetrofitHelper.ConnectionCallBack() {
            @Override
            public void onSuccess(Response<ResponseBody> body) {
                // Utils.dismissProgress();
                try {
                    if (body.code() != 200) {
                        Utils.serverError( activity, body.code() );
                        return;
                    }
                    String response = body.body().string();
                    Log.i( "TAG", "ChangeCartQty: " + response );

                    JSONObject jsonObject = new JSONObject( response );
                    if (jsonObject.getInt( "status" ) == 1) {
                        cartCustItem( false, i );
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
                Log.e( "ERROR", error );
                Utils.dismissProgress();
            }
        } );
    }

    public interface ItemClickListener {

        void freeCustomizeClick(String item);

        void paidCustomizeClick(String item);

        void paidCustomizeTestClick(String item);

        void freeCustomizationRemove(String item);

        void paidCustomizeRemove(String item);

        void tasteCustomizeTestClick(String item);

        void cookingGradesCustomizeTestClick(String item);

        void changeQtyCust(String id, String itemId, String qty);

        void imageClick(String image);

    }
}