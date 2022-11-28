package com.shounakmulay.telephony.sms

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import  com.shounakmulay.telephony.sms.ConnectivityReceiver
import  com.shounakmulay.telephony.utils.Constants
import android.net.ConnectivityManager
import android.util.Log


class ConnectionStateHandler(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {

    private var networkStateEventChannel: EventChannel = EventChannel(binding.binaryMessenger, "CONNECTION_STATE")

    init {
        networkStateEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            private lateinit var receiver: ConnectivityReceiver

            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                receiver = object : ConnectivityReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        super.onReceive(context, intent)
                        events?.success(receiver.state.toString())
                    }
                }
                binding.applicationContext.registerReceiver(
                        receiver,
                        IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                )
            }

            override fun onCancel(arguments: Any?) {
                binding.applicationContext.unregisterReceiver(receiver)
            }
        })
    }

    fun dispose() {
        networkStateEventChannel.setStreamHandler(null)
    }


}