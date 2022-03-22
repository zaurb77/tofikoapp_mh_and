package com.angaihouse.utils;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.net.ParseException;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class RetrofitHelper {

    private API gsonAPI;
    private ConnectionCallBack connectionCallBack;

    public RetrofitHelper() {
        OkHttpClient okHttpClient = new OkHttpClient();
        int TIMEOUT = 2 * 60 * 1000;
        Retrofit gsonretrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL)
                .client(okHttpClient.newBuilder().connectTimeout(TIMEOUT, TimeUnit.SECONDS).readTimeout(TIMEOUT, TimeUnit.SECONDS).writeTimeout(TIMEOUT, TimeUnit.SECONDS).build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        gsonAPI = gsonretrofit.create(API.class);


    }


    public RetrofitHelper(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        int TIMEOUT = 2 * 60 * 1000;
        Retrofit gsonretrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient.newBuilder().connectTimeout(TIMEOUT, TimeUnit.SECONDS).readTimeout(TIMEOUT, TimeUnit.SECONDS).writeTimeout(TIMEOUT, TimeUnit.SECONDS).build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        gsonAPI = gsonretrofit.create(API.class);


    }

    public static RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                MultipartBody.FORM, descriptionString);
    }


    public static MultipartBody.Part prepareFilePart(String partName, String filePath) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"),
                new File(filePath));
        return MultipartBody.Part.createFormData(partName, new File(filePath).getName(), requestFile);
    }

    public API api() {
        return gsonAPI;
    }

    public void callApi(Activity activity, Call<ResponseBody> call, ConnectionCallBack callBack) {
        if (!Utils.isOnline(activity)) {
            Utils.dismissProgress();
            Utils.internetAlert(activity);
            return;
        }
        connectionCallBack = callBack;
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (connectionCallBack != null)
                        connectionCallBack.onSuccess(response);
                } else {
                    try {
                        String res = response.body().string();
                        if (connectionCallBack != null)
                            connectionCallBack.onError(response.code(), res);
                    } catch (IOException | NullPointerException e) {
                        Log.i("TAG", "onResponse: " + call.request().url());
                        e.printStackTrace();
                        if (connectionCallBack != null)
                            connectionCallBack.onError(response.code(), e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable error) {
                String message = null;
                if (error instanceof NetworkErrorException) {
                    message = "Please check your internet connection";
                } else if (error instanceof ParseException) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof TimeoutException) {
                    message = "Connection TimeOut! Please check your internet connection.";
                } else if (error instanceof UnknownHostException) {
                    message = "Please check your internet connection and try later";
                } else if (error instanceof Exception) {
                    message = error.getMessage();
                }
                if (connectionCallBack != null)
                    connectionCallBack.onError(-1, message);
            }
        });
    }

    public void callApi(Call<ResponseBody> call, ConnectionCallBack callBack) {
        connectionCallBack = callBack;
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    if (connectionCallBack != null)
                        connectionCallBack.onSuccess(response);
                } else {
                    try {
                        String res = response.body().string();
                        if (connectionCallBack != null)
                            connectionCallBack.onError(response.code(), res);
                    } catch (IOException | NullPointerException e) {
                        Log.i("TAG", "onResponse: " + call.request().url());
                        e.printStackTrace();
                        if (connectionCallBack != null)
                            connectionCallBack.onError(response.code(), e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable error) {
                String message = null;
                if (error instanceof NetworkErrorException) {
                    message = "Please check your internet connection";
                } else if (error instanceof ParseException) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof TimeoutException) {
                    message = "Connection TimeOut! Please check your internet connection.";
                } else if (error instanceof UnknownHostException) {
                    message = "Please check your internet connection and try later";
                } else if (error instanceof Exception) {
                    message = error.getMessage();
                }
                if (connectionCallBack != null)
                    connectionCallBack.onError(-1, message);
            }
        });
    }

    public interface ConnectionCallBack {
        public void onSuccess(Response<ResponseBody> body);

        public void onError(int code, String error);
    }
}

/******************USAGE**********************

 private void getAPIData() {
 Utils.showProgress(activity);
 RetrofitHelper retrofitHelper = new RetrofitHelper();
 Call<ResponseBody> call;
 call = retrofitHelper.api().login();
 retrofitHelper.callApi(activity,call, new RetrofitHelper.ConnectionCallBack() {
@Override public void onSuccess(Response<ResponseBody> body) {
Utils.dismissProgress();
try {
if (body.code() != 200) {
Utils.serverError(activity, body.code());
return;
}
String response = body.body().string();
Log.i("TAG", "onSuccess: " + response);
Reader reader = new StringReader(response);
Gson gson = new GsonBuilder()
.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
.serializeNulls()
.create();
LiveMatch liveMatch = gson.fromJson(reader, LiveMatch.class);
} catch (IOException | NullPointerException | JsonSyntaxException | JSONException e) {
e.printStackTrace();
}
}

@Override public void onError(int code, String error) {
Utils.dismissProgress();
}
});

 }
 */