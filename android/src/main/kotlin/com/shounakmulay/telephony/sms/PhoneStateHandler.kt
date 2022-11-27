package com.shounakmulay.telephony.sms

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import  com.shounakmulay.telephony.sms.PhoneStateReceiver
import  com.shounakmulay.telephony.utils.Constants
import android.util.Log
class PhoneStateHandler(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    private var phoneStateEventChannel: EventChannel = EventChannel(binding.binaryMessenger, "PHONE_STATE_STREAM")

    init {
        Log.e("phonestate", "init phone state ........")
        phoneStateEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            private lateinit var receiver: PhoneStateReceiver

            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                receiver = object : PhoneStateReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        super.onReceive(context, intent)
                        Log.e("phonestate", "incoming call.")
                        events?.success(receiver.status.toString())
                    }
                }

                binding.applicationContext.registerReceiver(
                        receiver,
                        IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
                )
            }

            override fun onCancel(arguments: Any?) {
                binding.applicationContext.unregisterReceiver(receiver)
            }
        })
    }

    fun dispose() {
        phoneStateEventChannel.setStreamHandler(null)
    }
}