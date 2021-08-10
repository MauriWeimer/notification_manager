package com.example.notification_manager_example

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val CHANNEL = "notification"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            call, result -> when(call.method) {
            "sendNotification" -> {
                val channelId = call.arguments as String
                sendNotification(channelId)
                result.success(null)
            }
            "sendNotificationFromResource" -> {
                val resource = call.arguments as String
                val channelId = getResource(resource)
                sendNotification(channelId)
                result.success(null)
            }
            else -> result.notImplemented()
            }
        }
    }

    private fun sendNotification(channelId: String) {
        val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Example title")
                .setContentText("Channel: $channelId")
        with(NotificationManagerCompat.from(this)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    private fun getResource(resource: String): String {
        val identifier = context.resources.getIdentifier(resource, "string", context.packageName)
        if (identifier == 0) {
            throw IllegalArgumentException("The 'R.string.$resource' value it's not defined in your project's resources file.")
        }
        return context.getString(identifier)
    }
}
