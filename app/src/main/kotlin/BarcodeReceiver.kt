package com.zebra.arcore.psspoc.helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
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

fun dwSetPrefixPostfix(context: Context): Bundle {

    val bBDFpluginConfig = Bundle()
    bBDFpluginConfig.putString("PLUGIN_NAME", "BDF")
    bBDFpluginConfig.putString("RESET_CONFIG", "false")
    bBDFpluginConfig.putString("OUTPUT_PLUGIN_NAME", "INTENT")

    // param_list bundle properties
    val bParams = Bundle()
    bParams.putString("bdf_enabled", "true")
    bParams.putString("bdf_prefix", BuildConfig.PREFIX)
    //bParams.putString("bdf_send_enter", "true")

    bBDFpluginConfig.putBundle("PARAM_LIST", bParams)

    return bBDFpluginConfig
}

fun createDataWedgeProfile(context: Context, barcodeReceiver: BroadcastReceiver) {

    val bMainProfile = Bundle()
    val bOutputPluginConfig = Bundle()
    val bParams = Bundle()
    val bundleApp1 = Bundle()
    val appName = context.packageName
    bParams.putString("scanner_selection", "auto")
    bParams.putString("intent_output_enabled", "true")
    //bParams.putString("intent_action", "com.ndzl.DW")
    bParams.putString("intent_action", appName)
    bParams.putString("intent_category", "android.intent.category.DEFAULT")
    bParams.putString("intent_delivery", "2")
    bParams.putString("keystroke_output_enabled", "false")

    //configBundle.putString(PROFILE_NAME, "DW-WEBVIEW")
    bMainProfile.putString(PROFILE_NAME, appName)
    bMainProfile.putString(PROFILE_STATUS, "true")
    bMainProfile.putString(CONFIG_MODE, CONFIG_MODE_CREATE)

    bundleApp1.putString("PACKAGE_NAME", appName)
    val activityName = arrayOf("*")
    bundleApp1.putStringArray("ACTIVITY_LIST", activityName)

    bMainProfile.putParcelableArray("APP_LIST", arrayOf(bundleApp1))

    bOutputPluginConfig.putString("PLUGIN_NAME", "INTENT")
    bOutputPluginConfig.putString("RESET_CONFIG", "false")
    bOutputPluginConfig.putBundle("PARAM_LIST", bParams)

    //bMainProfile.putBundle("PLUGIN_CONFIG", bOutputPluginConfig)

    val bundleAllPluginsConfig = ArrayList<Bundle>()
    bundleAllPluginsConfig.add(bOutputPluginConfig)
    bundleAllPluginsConfig.add(dwSetPrefixPostfix(context))
    bMainProfile.putParcelableArrayList("PLUGIN_CONFIG", bundleAllPluginsConfig)

    val i = Intent()
    i.action = ACTION
    i.putExtra(SET_CONFIG, bMainProfile)



    /*
        val bNotification = Bundle()
        bNotification.putString("com.symbol.datawedge.api.APPLICATION_NAME", appName)
        bNotification.putString("com.symbol.datawedge.api.NOTIFICATION_TYPE", "SCANNER_STATUS")
        i.action = "com.symbol.datawedge.api.ACTION"
        i.putExtra("com.symbol.datawedge.api.REGISTER_FOR_NOTIFICATION", bNotification)

     */

    context.sendBroadcast(i)

   // i.putExtra(SET_CONFIG, dwSetPrefixPostfix(context))
   // context.sendBroadcast(i)



    val filter = IntentFilter()
    filter.addCategory(Intent.CATEGORY_DEFAULT)

    val Activity_Intent_Filter = context.packageName

    filter.addAction(Activity_Intent_Filter)
    //filter.addAction(NOTIFICATION_ACTION)
    context.registerReceiver(barcodeReceiver, filter, RECEIVER_EXPORTED)//register broadcast receiver - https://stackoverflow.com/questions/77235063/one-of-receiver-exported-or-receiver-not-exported-should-be-specified-when-a-rec
}


