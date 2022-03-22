package com.angaihouse.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class BrowserActivity extends AppCompatActivity {

    AppCompatActivity activity;
    private WebView mWebview ;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        mWebview  = new WebView(this);

        mWebview.getSettings().setJavaScriptEnabled(true);

        final Activity activity = this;

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Error:" + description, Toast.LENGTH_SHORT).show();

            }
        });

        mWebview.loadUrl( getIntent().getStringExtra("url"));
        setContentView(mWebview );
    }
}
