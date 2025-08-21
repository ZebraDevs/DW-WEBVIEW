package dwwebview

import android.content.Context
import android.content.Intent
import android.os.Bundle
private const val DATAWEDGE_ACTION = "com.symbol.datawedge.api.ACTION"
private const val NOTIFY_EXTRA = "com.symbol.datawedge.api.notification.NOTIFY"
private const val NOTIFICATION_CONFIG = "NOTIFICATION_CONFIG"
private const val ACTION_RESULT_DATAWEDGE_FROM_6_2 = "com.symbol.datawedge.api.RESULT_ACTION"
private const val DEVICE_IDENTIFIER = "DEVICE_IDENTIFIER"
private const val NOTIFICATION_SETTINGS = "NOTIFICATION_SETTINGS"
private const val EXTRA_SEND_RESULT = "SEND_RESULT"
private const val EXTRA_RESULT = "RESULT"
private const val EXTRA_RESULT_INFO = "RESULT_INFO"
private const val EXTRA_COMMAND = "COMMAND"


    fun btDWNotify(context:Context /*notifyParams: IntArray*/)
    {
       // val editDeviceId = findViewById<EditText>(R.id.editDeviceId)
        //val deviceId: String = editDeviceId.text.toString()
        val i = Intent()
        val bundleNotify = Bundle()
        val bundleNotificationConfig = Bundle()
        i.action = DATAWEDGE_ACTION;

        bundleNotificationConfig.putString(DEVICE_IDENTIFIER, /*deviceId*/"BLUETOOTH_RS5100")//https://techdocs.zebra.com/datawedge/latest/guide/api/notify/ the same device id must be set in the dw profile("scanner_selection_by_identifier", "BLUETOOTH_RS5100")
        // bundleNotificationConfig.putIntArray(NOTIFICATION_SETTINGS, notifyParams)
        bundleNotificationConfig.putIntArray("NOTIFICATION_SETTINGS", intArrayOf(17, 23, 8, 43))
        bundleNotify.putBundle(NOTIFICATION_CONFIG, bundleNotificationConfig)
        i.putExtra(NOTIFY_EXTRA, bundleNotify)
        i.putExtra(EXTRA_SEND_RESULT, "true")
        context.sendOrderedBroadcast(i, null)
    }
