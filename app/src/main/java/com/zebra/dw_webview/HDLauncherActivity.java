package com.zebra.dw_webview;

import static android.widget.Toast.makeText;


import static com.zebra.arcore.psspoc.helpers.BarcodeReceiverKt.dwEnumScanners;
import static com.zebra.arcore.psspoc.helpers.BarcodeReceiverKt.registerReceiverKt;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.zebra.arcore.psspoc.helpers.BarcodeReceiverKt;



import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

import dwwebview.DWNotifierKt;

//chrome://inspect/#devices for webview debugging

public class HDLauncherActivity extends AppCompatActivity {

    Intent starterIntent;
    WebView webView;

    BroadcastReceiver broadcastReceiver;

    //@SuppressLint("JavascriptInterface")
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);
        starterIntent = getIntent();

        setContentView(R.layout.activity_hdlauncher);

        //getWindow().setHideOverlayWindows(true);  //true to hide the DW camera preview

        webView = findViewById(R.id.web_interface);

        webView.loadUrl("file:///android_asset/asset_code_quantity.html");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.addJavascriptInterface(this, "DATAWEDGE");

        Button camButton = findViewById(R.id.cam_button);
        camButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                dispatchTakePictureIntent();

                return true;
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
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

                                Log.d("scanners", "#ENUM Scanner:" + entry[0] + " Connection:" + entry[1] + " Index:" + entry[2] + " ID:" + entry[3]);
                            }
                        }
                    }
                    if(intent.hasExtra("RESULT_INFO")) {
                        Bundle resubundle = intent.getBundleExtra("RESULT_INFO");
                        Set<String> keys = resubundle.keySet();
                        String resultInfo = "";
/*                        for (String key : keys) {
                            try {
                                resultInfo += key + ": " + resubundle.getString(key) + "\n";
                            } catch (Exception e) {

                            }
                        }*/
                        resultInfo += resubundle.getString("PROFILE_NAME") + "\n";
                        try {
                            //resultInfo += Objects.requireNonNull(resubundle.getStringArray("RESULT_CODE"))[0] + "\n";
                            resultInfo += Objects.requireNonNull(resubundle.getString("RESULT_CODE")) + "\n";

                        } catch (Exception e) {

                        }
                        Log.d("RESULT_INFO", resultInfo);
                    }
                }
                else if(  intent.getAction().equals("com.symbol.datawedge.api.NOTIFICATION_ACTION") ){
                    Log.d("NOTIFICATION_ACTION", "datawedge.api.NOTIFICATION_ACTION received");
                    if (intent.hasExtra("com.symbol.datawedge.api.NOTIFICATION")) {

                        Bundle b = intent.getBundleExtra("com.symbol.datawedge.api.NOTIFICATION");

                        // Safety check to ensure bundle is not null
                        if (b != null) {
                            String notifType = b.getString("NOTIFICATION_TYPE");
                            if ("PROFILE_SWITCH".equals(notifType)) {
                                Log.d("NOTIFICATION_ACTION", "Received  PROFILE_SWITCH Notification");

                                // Iterate through all keys in the bundle to log the details
                                for (String key : b.keySet()) {
                                    Log.d("NOTIFICATION_ACTION", "Key: " + key + ", Value: " + b.get(key) +", TIME="+System.currentTimeMillis());

                                    //FOLLOW-UP LOGIC TO BE IMPLEMENTED HERE
                                }

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

                    processDWDecodeData(intent);

                    Log.i("onReceive", "barcode=" + acquiredbarcode + " type=" + acquiredtype);
                    //printAsciiValues(acquiredbarcode);
                    webView.loadUrl("javascript:formFill('" + acquiredbarcode + "')");
                }
            }
        };

        BarcodeReceiverKt.createDataWedgeProfile(this,  "dwWebview1", "###");
        BarcodeReceiverKt.createDataWedgeProfile(this,  "dwWebview2", "$$$");
        //since Feb.2025 created profiles are no longer associated with this app => use SWITCH PROFILE button

        registerReceiverKt( this, broadcastReceiver)  ;
        dwEnumScanners(this);

    }

    void processDWDecodeData(Intent initiatingIntent){
        ArrayList<byte[]> rawData = (ArrayList <byte[]>) initiatingIntent.getSerializableExtra("com.symbol.datawedge.decode_data");

        if (rawData != null)
        {
            byte[] rawBytes = rawData.get(0);
            for (int i = 0; i < rawBytes.length; i++)
                Log.d("DW DECODE DATA", i + ": " + rawBytes[i]);
        }
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
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
            this.sendOrderedBroadcast(i, null);

        } catch (Exception e) {
            Log.e("TAG", "onClickbtn_SCAN "+e.getMessage());
        }
    }

    public void onClickbtn_NOTIFY(View v) {
        try {
            //dispatchTakePictureIntent();

            //DW NOTIFICATION EXERCISER
            DWNotifierKt.btDWNotify(this);


        } catch (Exception e) {
            Log.e("TAG", "onClickbtn_CAMERA "+e.getMessage());
        }
    }
    static boolean hideOverlayWindows = false;
    @SuppressLint("NewApi")
    public void onClickbtn_HideNonSysWin(View v) {
        try {
            hideOverlayWindows = !hideOverlayWindows;
            getWindow().setHideOverlayWindows(hideOverlayWindows);

            //WHEN SCANNING WITH DW INTERNAL CAMERA, THE CAMERA PREVIEW IS HIDDEN - BUT THE BARCODES GET DECODED!

        } catch (Exception e) {
            Log.e("TAG", "onClickbtn_CAMERA "+e.getMessage());
        }
    }


    private static int sourceVal=0;
    public void onClickbtn_SWITCH_ONE(View v) {
        try {

            //TESTING https://techdocs.zebra.com/datawedge/latest/guide/api/switchscannerparams/
            //by toggling the scanner source camera/imager

            Intent i = new Intent();
            i.setAction("com.symbol.datawedge.api.ACTION");

            Bundle bScannerParams = new Bundle();

            if(sourceVal++%2==0)
                //bScannerParams.putString("scanner_selection", "0"); //PARAMETER_NOT_SUPPORTED
                bScannerParams.putString("illumination_mode", "torch"); //ok

            else
                //bScannerParams.putString("scanner_selection", "1"); //PARAMETER_NOT_SUPPORTED
                bScannerParams.putString("illumination_mode", "off"); //ok


            i.putExtra("com.symbol.datawedge.api.SWITCH_SCANNER_PARAMS", bScannerParams);

            i.putExtra("SEND_RESULT","true");
            i.putExtra("COMMAND_IDENTIFIER", "123456789"); //returned as it is with the result
            this.sendOrderedBroadcast(i, null);

        } catch (Exception e) {
            Log.e("TAG", "onClickbtn_SWITCH_ONE "+e.getMessage());
        }
    }
    public void onClickbtn_SWITCH_TWO(View v) {
        try {

            Intent i = new Intent();
            i.setAction("com.symbol.datawedge.api.ACTION");

            Bundle bScannerParams = new Bundle();

            if(sourceVal++%2==0)
                i.putExtra("com.symbol.datawedge.api.SWITCH_TO_PROFILE", "dwWebview1");

            else
                i.putExtra("com.symbol.datawedge.api.SWITCH_TO_PROFILE", "dwWebview2");



            i.putExtra("SEND_RESULT","true");
            i.putExtra("COMMAND_IDENTIFIER", "101010"); //returned as it is with the result
            this.sendOrderedBroadcast(i, null);
            Log.d("SWITCH_TO_PROFILE", "SWITCH_TO_PROFILE cmd sent now "+ System.currentTimeMillis());

        } catch (Exception e) {
            Log.e("TAG", "onClickbtn_SWITCH_TWO "+e.getMessage());
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