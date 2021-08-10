package com.example.notification_manager

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


/** NotificationManagerPlugin */
class NotificationManagerPlugin: FlutterPlugin, MethodCallHandler {
  private val STRING_RES_TYPE: String = "string"
  private val RAW_RES_TYPE: String = "raw"

  private lateinit var channel: MethodChannel
  private lateinit var context: Context
  private lateinit var notificationManager: NotificationManager

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    context = flutterPluginBinding.applicationContext
    notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "notification_manager")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "createNotificationChannel" -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          val id = call.argument<String>("id") ?: getResource(call.argument<String>("resource")!!, STRING_RES_TYPE)
          val name = call.argument<String>("name")
          val importance = call.argument<Int>("importance") ?: NotificationManager.IMPORTANCE_DEFAULT
          val description = call.argument<String>("description")
          val lockscreenVisibility = call.argument<Int>("lockscreenVisibility")
          val enableLights = call.argument<Boolean>("enableLights")
          var lightColor: Int? = null
          val lightColorAlpha = call.argument<Int>("lightColorAlpha")
          val lightColorRed = call.argument<Int>("lightColorRed")
          val lightColorGreen = call.argument<Int>("lightColorGreen")
          val lightColorBlue = call.argument<Int>("lightColorBlue")
          var sound: Uri? = null
          val soundResource = call.argument<String>("soundResource")
          val enableVibration = call.argument<Boolean>("enableVibration")
          val vibrationPattern = call.argument<LongArray>("vibrationPattern")
          val showBadge = call.argument<Boolean>("showBadge")
          val groupId = call.argument<String>("groupId")
          val groupName = call.argument<String>("groupName")

          if (lightColorAlpha != null && lightColorRed != null && lightColorGreen != null && lightColorBlue != null) {
            lightColor = Color.argb(lightColorAlpha, lightColorRed, lightColorGreen, lightColorBlue)
          }
          if (soundResource != null) {
            sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    context.packageName + "/" +
                    getResource(soundResource, RAW_RES_TYPE))
          }

          createNotificationChannel(id, name!!, importance, description, lockscreenVisibility, enableLights!!,
                  lightColor, sound, enableVibration!!, vibrationPattern, showBadge!!, groupId, groupName)
        }
        result.success(null)
      }
      "deleteNotificationChannel" -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          val id = call.argument<String>("id") ?: getResource(call.argument<String>("resource")!!, STRING_RES_TYPE)
          val groupId = call.argument<String>("groupId")

          deleteNotificationChannel(id, groupId)
        }
        result.success(null)
      }
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun createNotificationChannel(id: String, name: String, importance: Int, description: String?,
                                        lockscreenVisibility: Int?, enableLights: Boolean, lightColor: Int?,
                                        sound: Uri?, enableVibration: Boolean, vibrationPattern: LongArray?,
                                        showBadge: Boolean, groupId: String?, groupName: String?) {
    val channel = NotificationChannel(id, name, importance)
    channel.description = description
    if (lockscreenVisibility != null) {
      channel.lockscreenVisibility = lockscreenVisibility
    }
    channel.enableLights(enableLights)
    if (enableLights && lightColor != null) {
      channel.lightColor = lightColor
    }
    if (sound != null) {
      val audioAttributes = AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_NOTIFICATION)
              .build()
      channel.setSound(sound, audioAttributes)
    }
    channel.enableVibration(enableVibration)
    channel.vibrationPattern = vibrationPattern
    if (channel.canShowBadge()) {
      channel.setShowBadge(showBadge)
    }

    if (groupId != null && groupName != null) {
      notificationManager.createNotificationChannelGroup(NotificationChannelGroup(groupId, groupName))
      channel.group = groupId
    }
    notificationManager.createNotificationChannel(channel)
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun deleteNotificationChannel(id: String, groupId: String?) {
    notificationManager.deleteNotificationChannel(id)
    if (groupId != null) {
      notificationManager.deleteNotificationChannelGroup(groupId)
    }
  }

  private fun getResource(resource: String, type: String): String {
    val identifier = context.resources.getIdentifier(resource, type, context.packageName)
    if (identifier == 0) {
      throw IllegalArgumentException("The 'R.$type.$resource' value it's not defined in your project's resources file.")
    }
    return context.getString(identifier)
  }
}
