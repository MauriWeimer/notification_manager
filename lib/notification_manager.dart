import 'dart:async';
import 'dart:typed_data';
import 'dart:ui';

import 'package:flutter/services.dart';

class Importance {
  const Importance._(this.value);

  static const Importance unspecified = Importance._(-1000);
  static const Importance none = Importance._(0);
  static const Importance min = Importance._(1);
  static const Importance low = Importance._(2);
  static const Importance high = Importance._(4);

  final int value;
}

class LockscreenVisibility {
  const LockscreenVisibility._(this.value);

  static const LockscreenVisibility secret = LockscreenVisibility._(-1);
  static const LockscreenVisibility private = LockscreenVisibility._(0);
  static const LockscreenVisibility public = LockscreenVisibility._(1);

  final int value;
}

class NotificationChannelDetails {
  const NotificationChannelDetails({
    required this.name,
    this.importance,
    this.description,
    this.lockscreenVisibility,
    this.enableLights = true,
    this.lightColor,
    this.soundResource,
    this.enableVibration = true,
    this.vibrationPattern,
    this.showBadge = true,
  });

  Map<String, dynamic> get toArguments => {
        'name': name,
        'importance': importance?.value,
        'description': description,
        'lockscreenVisibility': lockscreenVisibility?.value,
        'enableLights': enableLights,
        'lightColorAlpha': lightColor?.alpha,
        'lightColorRed': lightColor?.red,
        'lightColorGreen': lightColor?.green,
        'lightColorBlue': lightColor?.blue,
        'enableVibration': enableVibration,
        'soundResource': soundResource,
        if (vibrationPattern != null) 'vibrationPattern': Int64List.fromList(vibrationPattern!),
        'showBadge': showBadge,
      };

  final String name;
  final Importance? importance;
  final String? description;
  final LockscreenVisibility? lockscreenVisibility;
  final bool enableLights;
  final Color? lightColor;
  final String? soundResource;
  final bool enableVibration;
  final List<int>? vibrationPattern;
  final bool showBadge;
}

class NotificationChannelGroupDetails {
  const NotificationChannelGroupDetails({required this.id, required this.name});

  Map<String, dynamic> get toArguments => {'groupId': id, 'groupName': name};

  final String id;
  final String name;
}

class NotificationManager {
  static const MethodChannel _channel = const MethodChannel('notification_manager');

  static Future<void> createNotificationChannel(
    String id, {
    required NotificationChannelDetails details,
    NotificationChannelGroupDetails? groupDetails,
  }) async {
    final arguments = {
      'id': id,
      ...details.toArguments,
      if (groupDetails != null) ...groupDetails.toArguments,
    };
    await _channel.invokeMethod('createNotificationChannel', arguments);
  }

  static Future<void> createNotificationChannelFromResource(
    String resource, {
    required NotificationChannelDetails details,
    NotificationChannelGroupDetails? groupDetails,
  }) async {
    final arguments = {
      'resource': resource,
      ...details.toArguments,
      if (groupDetails != null) ...groupDetails.toArguments,
    };
    await _channel.invokeMethod('createNotificationChannel', arguments);
  }

  static Future<void> deleteNotificationChannel(String id, {String? groupId}) async {
    final arguments = {'id': id, 'groupId': groupId};
    await _channel.invokeMethod('deleteNotificationChannel', arguments);
  }

  static Future<void> deleteNotificationChannelFromResource(
    String resource, {
    String? groupId,
  }) async {
    final arguments = {'resource': resource, 'groupId': groupId};
    await _channel.invokeMethod('deleteNotificationChannel', arguments);
  }
}
