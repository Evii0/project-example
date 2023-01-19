package com.dev.goldilocksdatacollection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.dev.goldilocksdatacollection.R;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private Mediator mediator = new Mediator();
    private WebAppInterface w_a_interface;

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            String[] webUrl = mWebView.getUrl().split("/");
            if(webUrl[webUrl.length-1].compareTo("data.html") == 0){
                mWebView.loadUrl("javascript:areYouSure()");
//                w_a_interface.backButton();
            }
            else{
                mWebView.goBack();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("DEBUG", Boolean.toString(getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(this.getResources().getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        mWebView = (WebView) findViewById(R.id.webview);
        w_a_interface = new WebAppInterface(this, getWindow(), mediator, mWebView);
        mWebView.addJavascriptInterface(w_a_interface, "Android");

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        mWebView.loadUrl("file:///android_asset/connect.html");

        if(Build.VERSION.SDK_INT > 30) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e("DEBUG", "Checking for permissions");
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                }, 1000);
            } else {
                w_a_interface.setScanner(new Scanner(this, mWebView, mediator));
            }
        }
        else{
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int i = 0; i < grantResults.length; i++){
            Log.e("DEBUG", Integer.toString(grantResults[i]));
        }

        if(Build.VERSION.SDK_INT > 30) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e("DEBUG", "no luck getting BLUETOOTH_CONNECT permission");
            } else {
                w_a_interface.setScanner(new Scanner(this, mWebView, mediator));
            }
        }
        else{
            w_a_interface.setScanner(new Scanner(this, mWebView, mediator));
        }

    }

}