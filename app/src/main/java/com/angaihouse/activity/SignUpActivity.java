package com.angaihouse.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.activity.newmodel.Home_New;
import com.angaihouse.databinding.ActivitySignUpBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.ImageFilePath;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utility;
import com.angaihouse.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonSyntaxException;
import com.ybs.countrypicker.CountryPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    AppCompatActivity activity;
    CountryPicker picker;
    int hasStoragePermission, hasCameraPermission;
    String imagePath;
    File destination;
    private Bitmap bm;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String image1;
    boolean check = false;
    StoreUserData storeUserData;
    String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_sign_up);

        binding.firstName.setHint(Constants.FNAME);
        binding.lastName.setHint(Constants.LNAME);
        binding.phoneNumber.setHint(Constants.PHONE_NUMBER);
        binding.emailAddress.setHint(Constants.EMAIL_ADDRESS);
        binding.password.setHint(Constants.PASSWORD);
        binding.confirmPassword.setHint(Constants.CONFIRM_PASS);
        binding.referralCode.setHint(Constants.REFERRAL_CODE_EXIST);
        binding.iAgree.setText(Constants.I_AGREE);
        binding.next.setText(Constants.NEXT);
        binding.login.setText(Constants.LOGIN_FLOW);

        binding.back.setOnClickListener(view -> finish());

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(activity, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            storeUserData.setString(Constants.USER_FCM, newToken);
            Log.i("FCM_TOKEN", newToken);
        });

        binding.firstName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                changeBg();
                binding.llFirstName.setBackgroundResource(R.drawable.round_light_yeallow_border);
            }
        });

        binding.lastName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                changeBg();
                binding.llLastName.setBackgroundResource(R.drawable.round_light_yeallow_border);
            }
        });

        binding.phoneNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                changeBg();
                binding.llPhoneNumber.setBackgroundResource(R.drawable.round_light_yeallow_border);
            }
        });

        binding.emailAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                changeBg();
                binding.llEmailAddress.setBackgroundResource(R.drawable.round_light_yeallow_border);
            }
        });

        binding.password.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                changeBg();
                binding.llPassword.setBackgroundResource(R.drawable.round_light_yeallow_border);
            }
        });

        binding.confirmPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                changeBg();
                binding.llConfirmPassword.setBackgroundResource(R.drawable.round_light_yeallow_border);
            }
        });

        binding.referralCode.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                changeBg();
                binding.llReferral.setBackgroundResource(R.drawable.round_light_yeallow_border);
            }
        });

        Glide.with(activity)
                .load(R.drawable.boy)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imgProfile);

        binding.next.setOnClickListener(view -> {

            if (Utils.isEmpty(binding.firstName)) {
                Utils.showTopMessageError(activity, Constants.PROVIDE_FNAME);
            } else if (Utils.isEmpty(binding.lastName)) {
                Utils.showTopMessageError(activity,  Constants.PROVIDE_LNAME);
            } else if (Utils.isEmpty(binding.selectCountryCodTv)) {
                Utils.showTopMessageError(activity, Constants.PROVIDE_COUNTRY);
            }else if (Utils.isEmpty(binding.phoneNumber)) {
                Utils.showTopMessageError(activity, Constants.PROVIDE_PH_NO);
            } else if (Utils.isEmpty(binding.emailAddress)) {
                Utils.showTopMessageError(activity, Constants.PROVIDE_EMAIL );
            } else if (Utils.isEmpty(binding.password)) {
                Utils.showTopMessageError(activity, Constants.PROVIDE_PASS );
            } else if (Utils.isEmpty(binding.confirmPassword)) {
                Utils.showTopMessageError(activity, Constants.PROVIDE_CONFIRM_PASS );
            } else if (!Utils.isValidEmail(binding.emailAddress)) {
                Utils.showTopMessageError(activity, Constants.VAILD_EMAIL);
            } else if (!binding.password.getText().toString().equalsIgnoreCase(binding.confirmPassword.getText().toString())) {
                Utils.showTopMessageError(activity, Constants.CONFIRM_PASS_NOT_MATCHED );
            } else if (!check) {
                Utils.showTopMessageError(activity, Constants.PROVIDE_TERMS_CONDITION );
            }else {
                checkUser();

            }
        });

        binding.llCountryCode.setOnClickListener(view -> {
            changeBg();
            binding.llCountryCode.setBackgroundResource(R.drawable.round_light_yeallow_border);
            openPicker(binding.selectCountryCodTv);
        });

        binding.selectCountryCodTv.setOnClickListener(view -> {
            changeBg();
            binding.llCountryCode.setBackgroundResource(R.drawable.round_light_yeallow_border);
            openPicker(binding.selectCountryCodTv);
        });


        picker = CountryPicker.newInstance("Select Country");  // dialog title

        picker.setListener((name, code, dialCode, flagDrawableResID) -> {
            binding.selectCountryCodTv.setText(dialCode);
            //binding.countryFlg.setImageResource(flagDrawableResID);
            picker.dismiss();
        });

        binding.login.setOnClickListener(view -> {
            startActivity(new Intent(activity, LoginActivity.class));
            finish();
        });

        binding.imgProfile.setOnClickListener(v -> selectImage());

        binding.imgCheck.setOnClickListener(view -> {
            if (!check) {
                check = true;
                binding.imgCheck.setImageResource(R.drawable.checked);
                binding.next.setEnabled(true);
            } else {
                check = false;
                binding.imgCheck.setImageResource(R.drawable.unchecked);
                binding.next.setEnabled(false);
            }
        });

    }


    public String convertImage64_1(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public void openPicker(View view) {
        picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
    }

    //Camera and Gallery==>
    private void selectImage() {
        hasStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11);
        } else {
            final CharSequence[] items = {"Take Photo", "Choose from Gallery"};
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Set a photo");
            builder.setItems(items, (dialog, item) -> {
                boolean result = Utility.checkPermission(activity);
                if (items[item].equals("Take Photo")) {
                    hasCameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
                    if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 12);
                    } else {
                        if (result) {
                            cameraIntent();
                        }
                    }
                } else if (items[item].equals("Choose from Gallery")) {
                    if (result) {
                        galleryIntent();
                    }
                }
            });
            builder.show();
        }
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == SELECT_FILE) {
                if (null == data) {
                    Log.i("data", "null");
                    return;
                }
                String selectedImagePath;
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    selectedImagePath = ImageFilePath.getPath(activity, selectedImageUri);
                    Log.i("Image File Path", "" + selectedImagePath);
                    imagePath = selectedImagePath;
                    destination = new File(imagePath);
                }
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        bm = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        binding.imgProfile.setVisibility(View.VISIBLE);
        Glide.with(activity)
                .load(bm)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imgProfile);

        BitmapDrawable drawable = (BitmapDrawable) binding.imgProfile.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        image1 = convertImage64_1(bitmap);

    }

    private void onSelectFromGalleryResult(Intent data) {
        bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        binding.imgProfile.setVisibility(View.VISIBLE);

        Glide.with(activity)
                .load(bm)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imgProfile);

        BitmapDrawable drawable = (BitmapDrawable) binding.imgProfile.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        image1 = convertImage64_1(bitmap);

    }

    public void changeBg() {
        binding.llFirstName.setBackgroundResource(R.drawable.round_green_border);
        binding.llLastName.setBackgroundResource(R.drawable.round_green_border);
        binding.llPhoneNumber.setBackgroundResource(R.drawable.round_green_border);
        binding.llCountryCode.setBackgroundResource(R.drawable.round_green_border);
        binding.llEmailAddress.setBackgroundResource(R.drawable.round_green_border);
        binding.llPassword.setBackgroundResource(R.drawable.round_green_border);
        binding.llConfirmPassword.setBackgroundResource(R.drawable.round_green_border);
        binding.llReferral.setBackgroundResource(R.drawable.round_green_border);
    }

    private void checkUser() {

        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().checkUserExists(
                binding.phoneNumber.getText().toString().trim(),
                binding.emailAddress.getText().toString().trim(),
                binding.referralCode.getText().toString().trim()
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
                    Log.i("CHECK_USER", "" + response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {

                        registerApi();

                    }else {
                        Utils.showTopMessageError(activity,jsonObject.getString("message"));

                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
            }
        });
    }

    private void registerApi() {

        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;

        call = retrofitHelper.api().register(
                binding.firstName.getText().toString().trim(),
                binding.lastName.getText().toString().trim(),
                binding.selectCountryCodTv.getText().toString().trim(),
                binding.phoneNumber.getText().toString().trim(),
                binding.emailAddress.getText().toString().trim(),
                binding.password.getText().toString().trim(),
                image1,
                storeUserData.getString(Constants.USER_FCM),
                binding.referralCode.getText().toString().trim(),
                Constants.DEVICE_TYPE
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
                    assert body.body() != null;
                    String response = body.body().string();
                    Log.i("TAG", "RegisterResponse: " + response);
                    JSONObject object = new JSONObject(response);

                    if (object.getInt("status") == 1) {

                        JSONObject responseData = object.getJSONObject("responsedata");
                        storeUserData.setString(Constants.USER_ID, responseData.getString("user_id"));
                        storeUserData.setString(Constants.USER_FNAME, responseData.getString("fname"));
                        storeUserData.setString(Constants.USER_LNAME, responseData.getString("lname"));
                        storeUserData.setString(Constants.USER_DOB, responseData.getString("dob"));
                        storeUserData.setString(Constants.COUNTRY_CODE, responseData.getString("country_code"));
                        storeUserData.setString(Constants.mobile_no, responseData.getString("mobile_no"));
                        storeUserData.setString(Constants.email, responseData.getString("email"));
                        storeUserData.setString(Constants.password, responseData.getString("password"));
                        storeUserData.setString(Constants.password, responseData.getString("password"));
                        storeUserData.setString(Constants.gender, responseData.getString("gender"));
                        storeUserData.setString(Constants.REFERRAL_CODE, responseData.getString("referral_code"));
                        storeUserData.setString(Constants.type, responseData.getString("type"));
                        storeUserData.setString(Constants.company_id, responseData.getString("company_id"));
                        storeUserData.setString(Constants.company_name, responseData.getString("company_name"));
                        storeUserData.setString(Constants.company_legal_email, responseData.getString("company_legal_email"));
                        storeUserData.setString(Constants.unique_invoicing_code, responseData.getString("unique_invoicing_code"));
                        storeUserData.setString(Constants.vat_id, responseData.getString("vat_id"));
                        storeUserData.setString(Constants.image, responseData.getString("image"));
                        storeUserData.setInt(Constants.ORDER_PUSH_NOTIFICATION, Integer.parseInt(responseData.getString("order_push_notification")));
                        storeUserData.setInt(Constants.ORDER_EMAIL_NOTIFICATION, Integer.parseInt(responseData.getString("order_email_notification")));
                        storeUserData.setInt(Constants.NEWS_PUSH_NOTIFICATION, Integer.parseInt(responseData.getString("news_push_notification")));
                        storeUserData.setInt(Constants.NEWS_EMAIL_NOTIFICATION, Integer.parseInt(responseData.getString("news_email_notification")));
                        storeUserData.setString(Constants.TOKEN, responseData.getString("token"));
                        storeUserData.setString(Constants.CART_ID, responseData.getString("cart_id"));


                        startActivity(new Intent(activity, Home_New.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));


                    } else {
                        Utils.showTopMessageError(activity, object.getString("message"));
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                    Utils.dismissProgress();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
            }
        });
    }
}





