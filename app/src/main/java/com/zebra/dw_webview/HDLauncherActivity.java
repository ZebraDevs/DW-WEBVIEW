package com.zebra.dw_webview;

import static android.widget.Toast.makeText;


import static com.zebra.arcore.psspoc.helpers.BarcodeReceiverKt.dwEnumScanners;

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

import java.util.ArrayList;
import java.util.Objects;

//chrome://inspect/#devices for webview debugging

public class HDLauncherActivity extends AppCompatActivity {

    Intent starterIntent;
    WebView webView;
    //@SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);
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

                if(intent.getAction().equals("com.symbol.datawedge.api.RESULT_ACTION")) {

                    if (intent.hasExtra("com.symbol.datawedge.api.RESULT_ENUMERATE_SCANNERS")) {
                        ArrayList<Bundle> scannerList = (ArrayList<Bundle>) intent.getSerializableExtra("com.symbol.datawedge.api.RESULT_ENUMERATE_SCANNERS");
                        if ((scannerList != null) && (scannerList.size() > 0)) {
                            for (Bundle bunb : scannerList) {
                                String[] entry = new String[4];
                                entry[0] = bunb.getString("SCANNER_NAME");
                                entry[1] = bunb.getBoolean("SCANNER_CONNECTION_STATE") + "";
                                entry[2] = bunb.getInt("SCANNER_INDEX") + "";

                                entry[3] = bunb.getString("SCANNER_IDENTIFIER");

                                Log.d("scanners", "Scanner:" + entry[0] + " Connection:" + entry[1] + " Index:" + entry[2] + " ID:" + entry[3]);
                            }
                        }
                    }
                }
                else {


                    String acquiredbarcode = Objects.requireNonNull(intent.getStringExtra("com.symbol.datawedge.data_string"));
                    String acquiredtype = Objects.requireNonNull(intent.getStringExtra("com.symbol.datawedge.label_type"));
                    //acquiredbarcode = filterOutNonPrintableCharactersWIthRegex(acquiredbarcode);
                    //acquiredbarcode = filterOutNonAlphaNumericCharactersWIthRegex(acquiredbarcode);
                    //acquiredbarcode = encodeString(acquiredbarcode);
                    Log.i("onReceive", "barcode=" + acquiredbarcode + " type=" + acquiredtype);
                    //printAsciiValues(acquiredbarcode);
                    webView.loadUrl("javascript:formFill('" + acquiredbarcode + "')");
                }
            }
        });

        dwEnumScanners(this);
    }

    public String filterOutNonPrintableCharactersWIthRegex(String input) {
        return input.replaceAll("[^\\x24-\\x7e]", "_");
    }

    //filterout all characters that not numbers or letters with regex
    public String filterOutNonAlphaNumericCharactersWIthRegex(String input) {
        return input.replaceAll("[^a-zA-Z0-9]", "");
    }

    //encode string
    public String encodeString(String input) {
        return Uri.encode(input);
    }

    //print the ascii value of each character in the string
    public void printAsciiValues(String input) {
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            int ascii = (int) c;
            Log.i("printAsciiValues", "ascii10="+ascii);
        }
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