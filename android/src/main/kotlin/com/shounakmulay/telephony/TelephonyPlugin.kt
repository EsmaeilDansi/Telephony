package com.shounakmulay.telephony

import android.content.Context
import androidx.annotation.NonNull
import com.shounakmulay.telephony.sms.IncomingSmsHandler
import com.shounakmulay.telephony.utils.Constants.CHANNEL_SMS
import com.shounakmulay.telephony.sms.IncomingSmsReceiver
import com.shounakmulay.telephony.sms.SmsController
import com.shounakmulay.telephony.sms.SmsMethodCallHandler
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.*
import com.shounakmulay.telephony.sms.PhoneStateHandler
import com.shounakmulay.telephony.sms.ConnectionStateHandler
import com.shounakmulay.telephony.sms.ConnectivityReceiver
import android.util.Log
import android.content.Intent
import android.net.ConnectivityManager
import android.content.IntentFilter

class TelephonyPlugin : FlutterPlugin, ActivityAware {

    private lateinit var smsChannel: MethodChannel

    private lateinit var smsMethodCallHandler: SmsMethodCallHandler

    private lateinit var smsController: SmsController

    private lateinit var binaryMessenger: BinaryMessenger

    private lateinit var permissionsController: PermissionsController

    private lateinit var phoneStateHandler: PhoneStateHandler

    private lateinit var connectionHandler: ConnectionStateHandler

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        if (!this::binaryMessenger.isInitialized) {
            binaryMessenger = flutterPluginBinding.binaryMessenger
        }

        setupPlugin(flutterPluginBinding.applicationContext, binaryMessenger)
        phoneStateHandler = PhoneStateHandler(flutterPluginBinding)
        connectionHandler = ConnectionStateHandler(flutterPluginBinding)
//        flutterPluginBinding
//                .applicationContext
//                .registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        tearDownPlugin()
    }

    override fun onDetachedFromActivity() {
        tearDownPlugin()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        IncomingSmsReceiver.foregroundSmsChannel = smsChannel
        smsMethodCallHandler.setActivity(binding.activity)
        binding.addRequestPermissionsResultListener(smsMethodCallHandler)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    private fun setupPlugin(context: Context, messenger: BinaryMessenger) {
        smsController = SmsController(context)
        permissionsController = PermissionsController(context)
        smsMethodCallHandler = SmsMethodCallHandler(context, smsController, permissionsController)

        smsChannel = MethodChannel(messenger, CHANNEL_SMS)
        smsChannel.setMethodCallHandler(smsMethodCallHandler)
        smsMethodCallHandler.setForegroundChannel(smsChannel)
    }

    private fun tearDownPlugin() {
        IncomingSmsReceiver.foregroundSmsChannel = null
        smsChannel.setMethodCallHandler(null)
        phoneStateHandler.dispose()
        connectionHandler.dispose()
    }

}
