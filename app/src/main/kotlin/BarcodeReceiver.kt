package com.zebra.arcore.psspoc.helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.core.content.ContextCompat.registerReceiver
import com.zebra.dw_webview.BuildConfig


//constants
const val Intent_Key_Data = "com.symbol.datawedge.data_string"

const val NOTIFICATION_ACTION = "com.symbol.datawedge.api.NOTIFICATION_ACTION"
const val ACTION = "com.symbol.datawedge.api.ACTION"
const val PROFILE_NAME = "PROFILE_NAME"
const val PROFILE_STATUS = "PROFILE_ENABLED"
const val CONFIG_MODE = "CONFIG_MODE"
const val CONFIG_MODE_CREATE = "CREATE_IF_NOT_EXIST"
const val SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG"
val TAG: String = "DW-WEBVIEW-KT"

fun dwStartScan(context: Context) {
    val softScanTrigger = "com.symbol.datawedge.api.ACTION"
    val extraData = "com.symbol.datawedge.api.SOFT_SCAN_TRIGGER"

    val i = Intent()
    i.action = softScanTrigger
    i.putExtra(extraData, "START_SCANNING")
    context.sendBroadcast(i)
}
/*
private val barcodeReceiver =
    object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == "com.ndzl.DW") {
                val decoded =
                    intent.getStringExtra("com.symbol.datawedge.data_string") ?: "none"
            }
        }
    }*/

fun dwSetPrefixPostfix(context: Context, prefix_postfix: String): Bundle {

    val bBDFpluginConfig = Bundle()
    bBDFpluginConfig.putString("PLUGIN_NAME", "BDF")
    bBDFpluginConfig.putString("RESET_CONFIG", "true")
    bBDFpluginConfig.putString("OUTPUT_PLUGIN_NAME", "INTENT")

    // param_list bundle properties
    val bParams = Bundle()
    bParams.putString("bdf_enabled", "true")
    bParams.putString("bdf_prefix", prefix_postfix)
    bParams.putString("bdf_suffix", prefix_postfix)
    bParams.putString("bdf_send_enter", "false")

    bBDFpluginConfig.putBundle("PARAM_LIST", bParams)

    return bBDFpluginConfig
}

fun dwSwitchOffKeystrokeOutput(): Bundle {

    val bKSOpluginConfig = Bundle()
    bKSOpluginConfig.putString("PLUGIN_NAME", "KEYSTROKE")
    bKSOpluginConfig.putString("RESET_CONFIG", "false")

    // param_list bundle properties
    val bParams = Bundle()
    bParams.putString("keystroke_output_enabled", "false")

    bKSOpluginConfig.putBundle("PARAM_LIST", bParams)

    return bKSOpluginConfig

}

private fun dwSetAssociatedApps(appName: String): Array<Bundle> {
    val bParams = Bundle()
    bParams.putString("PACKAGE_NAME", appName)
    bParams.putStringArray("ACTIVITY_LIST",  arrayOf("*"))

    return arrayOf(bParams)
}

fun dwConfigBarcodeInput(): Bundle {

    val barcodeInputPlugin = Bundle()
    barcodeInputPlugin.putString("PLUGIN_NAME", "BARCODE")
    barcodeInputPlugin.putString("RESET_CONFIG", "true")

    val barCodeProps = Bundle()
    //barCodeProps.putString("scanner_selection", "auto")  //0 for internal camera - auto for internal imager
    barCodeProps.putString("scanner_selection_by_identifier", "INTERNAL_IMAGER");
    barCodeProps.putString("scanner_input_enabled", "true")
    barCodeProps.putString("decoder_qrcode", "false")


    barcodeInputPlugin.putBundle("PARAM_LIST", barCodeProps)

    return barcodeInputPlugin

}

fun dwEnumScanners(context: Context) {

    val i = Intent()
    i.setAction("com.symbol.datawedge.api.ACTION")
    i.putExtra("com.symbol.datawedge.api.ENUMERATE_SCANNERS", "")
    context.sendBroadcast(i);
}








fun createDataWedgeProfile(context: Context, profileName: String, prefix_postfix: String) {

    val bMainProfile = Bundle()
    val bOutputPluginConfig = Bundle()
    val bParams = Bundle()
    val bundleApp1 = Bundle()
    val appName = profileName



    bMainProfile.putString(PROFILE_NAME, appName)
    bMainProfile.putString(PROFILE_STATUS, "true")
    bMainProfile.putString(CONFIG_MODE, CONFIG_MODE_CREATE)

    val bundleAllPluginsConfig = ArrayList<Bundle>()



    bundleAllPluginsConfig.add(dwSetOutputPlugin(context.packageName))

    bundleAllPluginsConfig.add(dwSwitchOffKeystrokeOutput())

    bundleAllPluginsConfig.add(dwConfigBarcodeInput())

    bundleAllPluginsConfig.add(dwSetPrefixPostfix(context, prefix_postfix))

    bMainProfile.putParcelableArrayList("PLUGIN_CONFIG", bundleAllPluginsConfig)

    //bMainProfile.putParcelableArray("APP_LIST", dwSetAssociatedApps(context.packageName)) //removed on Feb 2025 to exercise profile switching

    val i = Intent()
    i.action = ACTION
    i.putExtra(SET_CONFIG, bMainProfile)

    context.sendBroadcast(i)

}

fun registerReceiverKt(context: Context, barcodeReceiver: BroadcastReceiver){
    val filter = IntentFilter()
    filter.addCategory(Intent.CATEGORY_DEFAULT)

    val Activity_Intent_Filter = context.packageName
    filter.addAction(Activity_Intent_Filter)
    //filter.addAction(NOTIFICATION_ACTION)
    filter.addAction("com.symbol.datawedge.api.RESULT_ACTION")
    context.registerReceiver(barcodeReceiver, filter, RECEIVER_EXPORTED)
}

private fun dwSetOutputPlugin(

    appName: String


):Bundle {
    val bParams = Bundle()
    bParams.putString("intent_output_enabled", "true")
    //bParams.putString("intent_action", "com.ndzl.DW")
    bParams.putString("intent_action", appName)
    bParams.putString("intent_category", "android.intent.category.DEFAULT")
    bParams.putString("intent_delivery", "2")

    val bOutputPluginConfig = Bundle()
    bOutputPluginConfig.putString("PLUGIN_NAME", "INTENT")
    bOutputPluginConfig.putString("RESET_CONFIG", "false")
    bOutputPluginConfig.putBundle("PARAM_LIST", bParams)

    return bOutputPluginConfig
}


