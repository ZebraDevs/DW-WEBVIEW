package com.zebra.dw_webview;

import static android.widget.Toast.makeText;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.zebra.arcore.psspoc.helpers.BarcodeReceiverKt;

import java.util.Objects;

//chrome://inspect/#devices for webview debugging

public class HDLauncherActivity extends AppCompatActivity {

    Intent starterIntent;
    WebView webView;
    //@SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        starterIntent = getIntent();

        setContentView(R.layout.activity_hdlauncher);

        webView = findViewById(R.id.web_interface);

        webView.loadUrl("file:///android_asset/asset_code_quantity.html");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.addJavascriptInterface(this, "DATAWEDGE");

        BarcodeReceiverKt.createDataWedgeProfile(this, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String acquiredbarcode = Objects.requireNonNull(intent.getStringExtra("com.symbol.datawedge.data_string"));
                Log.i("onReceive", "barcode="+ acquiredbarcode);
                webView.loadUrl("javascript:formFill('"+acquiredbarcode+"')");
            }
        });
    }

    @JavascriptInterface
    public String getWebviewVersion() {
        PackageInfo info = WebView.getCurrentWebViewPackage();
        return "WEBVIEW version " + info.versionName+"\n"+ Build.FINGERPRINT;
    }

    @JavascriptInterface
    public void showMsg(String _bc, String _qty) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HDLauncherActivity.this);
        builder.setTitle("Confirmation").setMessage("Barcode:\t" + _bc + "\nQuantity:\t" + _qty)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), " Data Saved Locally", Toast.LENGTH_SHORT).show();
                        // You can use shared preference or db here to store The Data
                    }
                })
        ;
        builder.create().show();
    }

    public void onClickbtn_SCAN(View v) {
        try {
            String softScanTrigger = "com.symbol.datawedge.api.ACTION";
            String extraData = "com.symbol.datawedge.api.SOFT_SCAN_TRIGGER";

// create the intent
            Intent i = new Intent();

// set the action to perform
            i.setAction(softScanTrigger);

// add additional info
            i.putExtra(extraData, "START_SCANNING");

// send the intent to DataWedge
            this.sendBroadcast(i);

        } catch (Exception e) {
            Log.e("TAG", "onClickbtn_SCAN "+e.getMessage());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}