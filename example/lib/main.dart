import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:notification_manager/notification_manager.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  late bool _active;
  late bool _fromResourceActive;
  late final String _channelId;
  late final String _groupId;
  late final String _channelIdResource;

  @override
  void initState() {
    super.initState();

    _active = false;
    _fromResourceActive = false;
    _channelId = 'channelId';
    _groupId = 'groupId';
    _channelIdResource = 'notification_channel_id';

    WidgetsBinding.instance?.addPostFrameCallback((_) async {
      await NotificationManager.createNotificationChannel(
        _channelId,
        details: NotificationChannelDetails(
          name: 'name',
          importance: Importance.high,
          description: 'description',
          lockscreenVisibility: LockscreenVisibility.public,
          enableLights: true,
          lightColor: Colors.blue,
          enableVibration: true,
          vibrationPattern: [1000, 500],
          showBadge: false,
          soundResource: 'sample',
        ),
        groupDetails: NotificationChannelGroupDetails(id: _groupId, name: 'groupName'),
      );
      setState(() => _active = true);
      await NotificationManager.createNotificationChannelFromResource(
        _channelIdResource,
        details: NotificationChannelDetails(name: 'nameFromResource'),
      );
      setState(() => _fromResourceActive = true);
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Notificaion Manager Example App',
      home: Scaffold(
        body: Center(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 48.0, vertical: 96.0),
            child: Column(
              children: [
                Text(
                  'Channel with id {$_channelId}\nactive: $_active',
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 16.0),
                Text(
                  'Channel with id from resource {$_channelIdResource}\nactive: $_fromResourceActive',
                  textAlign: TextAlign.center,
                ),
                const Spacer(),
                TextButton(
                  child: Text('Send notification'),
                  onPressed: () async {
                    const _channel = const MethodChannel('notification');
                    await _channel.invokeMethod('sendNotification', _channelId);
                    await _channel.invokeMethod('sendNotificationFromResource', _channelIdResource);
                  },
                ),
                const Spacer(),
                TextButton(
                  child: Text('Create channel'),
                  onPressed: () async {
                    await NotificationManager.createNotificationChannel(
                      _channelId,
                      details: NotificationChannelDetails(
                        name: 'name',
                        description: 'description',
                        importance: Importance.high,
                        lightColor: Colors.red,
                      ),
                      groupDetails: NotificationChannelGroupDetails(
                        id: _groupId,
                        name: 'groupName',
                      ),
                    );
                    setState(() => _active = true);
                    await NotificationManager.createNotificationChannelFromResource(
                      _channelIdResource,
                      details: NotificationChannelDetails(name: 'nameFromResource'),
                    );
                    setState(() => _fromResourceActive = true);
                  },
                ),
                const SizedBox(height: 16.0),
                TextButton(
                  child: Text('Delete channel'),
                  onPressed: () async {
                    await NotificationManager.deleteNotificationChannel(
                      _channelId,
                      groupId: _groupId,
                    );
                    setState(() => _active = false);
                    await NotificationManager.deleteNotificationChannelFromResource(
                      _channelIdResource,
                      groupId: _groupId,
                    );
                    setState(() => _fromResourceActive = false);
                  },
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
