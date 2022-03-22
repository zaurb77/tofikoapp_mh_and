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
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.angaihouse.R;
import com.angaihouse.databinding.ActivityEditProfileBinding;
import com.angaihouse.utils.Constants;
import com.angaihouse.utils.ImageFilePath;
import com.angaihouse.utils.RetrofitHelper;
import com.angaihouse.utils.StoreUserData;
import com.angaihouse.utils.Utility;
import com.angaihouse.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonSyntaxException;
import com.ybs.countrypicker.CountryPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    ActivityEditProfileBinding binding;
    AppCompatActivity activity;
    StoreUserData storeUserData;
    CountryPicker picker;
    int selectMaleFemale = -1;
    String image1;
    int hasStoragePermission, hasCameraPermission;
    String imagePath;
    File destination;
    private Bitmap bm;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        storeUserData = new StoreUserData(activity);
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_edit_profile);

        binding.tvGender.setText(Constants.GENDER);
        binding.tvMale.setText(Constants.MALE);
        binding.tvFemale.setText(Constants.FEMALE);
        binding.update.setText(Constants.UPDATE);

        binding.llFemale.setEnabled(false);
        binding.llMale.setEnabled(false);
        binding.countryFlg.setVisibility(View.VISIBLE);

        binding.dateOfBirth.setOnClickListener(view -> Utils.selectDate(activity, binding.dateOfBirth));

        if (storeUserData.getString(Constants.gender).equalsIgnoreCase("1")) {
            selectItem(1, storeUserData.getString(Constants.image));
        } else if (storeUserData.getString(Constants.gender).equalsIgnoreCase("0")) {
            selectItem(0, storeUserData.getString(Constants.image));
        }

        binding.fName.setText(storeUserData.getString(Constants.USER_FNAME));
        binding.lName.setText(storeUserData.getString(Constants.USER_LNAME));
        binding.dateOfBirth.setText(storeUserData.getString(Constants.USER_DOB));
        binding.countryCode.setText(storeUserData.getString(Constants.COUNTRY_CODE));
        binding.phoneNumber.setText(storeUserData.getString(Constants.mobile_no));
        binding.emailAddress.setText(storeUserData.getString(Constants.email));
        binding.password.setText(storeUserData.getString(Constants.password));
        binding.confirmPassword.setText(storeUserData.getString(Constants.password));

        binding.back.setOnClickListener(view -> finish());

        binding.imgProfile.setOnClickListener(view -> selectImage());

        binding.llMale.setOnClickListener(view -> selectItem(1, ""));


        binding.llFemale.setOnClickListener(view -> selectItem(0, ""));

        binding.countryCode.setOnClickListener(view -> openPicker(binding.countryCode));

        picker = CountryPicker.newInstance("Select Country");  // dialog title
        picker.setListener((name, code, dialCode, flagDrawableResID) -> {
            binding.countryCode.setText(dialCode);
            binding.countryFlg.setImageResource(flagDrawableResID);
            picker.dismiss();
        });

        binding.changeAddress.setOnClickListener(view ->
                startActivity(new Intent(activity, DeliveryInformationActivity.class)
                        .putExtra("updateAddress", "updateAddress")
                ));

        binding.changeCompanyInfo.setOnClickListener(view ->
                startActivity(new Intent(activity, CompanyInformation.class)
                        .putExtra("updateInfo", "updateInfo")
                ));


        binding.update.setOnClickListener(view -> {

            if (selectMaleFemale == -1) {
                Utils.showTopMessageError(activity, "Please select your gender.");
            } else if (Utils.isEmpty(binding.fName)) {
                Utils.showTopMessageError(activity, "Please enter your first name.");
            } else if (Utils.isEmpty(binding.lName)) {
                Utils.showTopMessageError(activity, "Please enter your last name.");
            } else if (Utils.isEmpty(binding.dateOfBirth)) {
                Utils.showTopMessageError(activity, "Please enter your date of birth.");
            } else if (Utils.isEmpty(binding.countryCode)) {
                Utils.showTopMessageError(activity, "Please select your country code.");
            } else if (Utils.isEmpty(binding.phoneNumber)) {
                Utils.showTopMessageError(activity, "Please enter your phone number.");
            } else if (Utils.isEmpty(binding.emailAddress)) {
                Utils.showTopMessageError(activity, "Please enter your email address number.");
            } else if (Utils.isEmpty(binding.password)) {
                Utils.showTopMessageError(activity, "Please enter your password.");
            } else {
                if (!Utils.isValidEmail(binding.emailAddress)) {
                    Utils.showTopMessageError(activity, "Please enter valid email address.");
                } else {
                    if (Objects.requireNonNull(binding.password.getText()).toString().equalsIgnoreCase(Objects.requireNonNull(binding.confirmPassword.getText()).toString())) {
                        updateProfile();
                    } else {
                        Utils.showTopMessageError(activity, "Please a valid password your password not matched.");
                    }
                }
            }
        });
    }

    //TODO : SELECT IMAGE FROM CAMERA OR GALLERY
    //Camera and Gallery==>
    private void selectImage() {
        hasStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11);
        } else {
            final CharSequence[] items = {"Take Photo", "Choose from Gallery"};
            android.app.AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
    //Camera and Gallery==>

    public void openPicker(View view) {
        picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
    }

    public void selectItem(int selectGender, String imageUrl) {
        binding.imgFemale.setImageResource(R.drawable.radio_blank);
        binding.imgMale.setImageResource(R.drawable.radio_blank);

        if (selectGender == 0) {
            selectMaleFemale = 0;

            binding.imgFemale.setImageResource(R.drawable.radio_on);

            if (imageUrl.length() > 0) {

                Glide.with(activity)
                        .load(imageUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imgProfile);
            } else {
                Glide.with(activity)
                        .load(R.drawable.girl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imgProfile);
            }


        } else if (selectGender == 1) {
            selectMaleFemale = 1;
            binding.imgMale.setImageResource(R.drawable.radio_on);

            if (imageUrl.length() > 0) {

                Glide.with(activity)
                        .load(imageUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imgProfile);

            } else {

                Glide.with(activity)
                        .load(R.drawable.boy)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imgProfile);
            }

        }
    }

    //TODO : UPDATE PROFILE
    private void updateProfile() {
        Utils.showProgress(activity);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<ResponseBody> call;
        call = retrofitHelper.api().updateProfile(
                storeUserData.getString(Constants.USER_ID),
                storeUserData.getString(Constants.TOKEN),
                binding.fName.getText().toString(),
                binding.lName.getText().toString(),
                binding.dateOfBirth.getText().toString(),
                binding.countryCode.getText().toString(),
                binding.phoneNumber.getText().toString(),
                binding.emailAddress.getText().toString(),
                binding.password.getText().toString(),
                "" + selectMaleFemale,
                image1
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
                    Log.i("TAG", "UpdateProfile: " + response);
                    JSONObject object = new JSONObject(response);

                    if (object.getInt("status") == 1) {

                        Utils.showTopMessageSuccess(activity, object.getString("message"));
                        JSONObject responseData = object.getJSONObject("responsedata");

                        storeUserData.setString(Constants.USER_FNAME, responseData.getString("fname"));
                        storeUserData.setString(Constants.USER_LNAME, responseData.getString("lname"));
                        storeUserData.setString(Constants.USER_DOB, responseData.getString("dob"));
                        storeUserData.setString(Constants.COUNTRY_CODE, responseData.getString("country_code"));
                        storeUserData.setString(Constants.mobile_no, responseData.getString("mobile_no"));
                        storeUserData.setString(Constants.email, responseData.getString("email"));
                        storeUserData.setString(Constants.password, responseData.getString("password"));
                        storeUserData.setString(Constants.gender, responseData.getString("gender"));
                        storeUserData.setString(Constants.image, responseData.getString("image"));

                    } else {
                        Utils.showTopMessageError(activity, object.getString("message"));
                    }
                } catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
                    Utils.dismissProgress();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String error) {
                Utils.dismissProgress();
            }
        });
    }


    public String convertImage64_1(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
}
