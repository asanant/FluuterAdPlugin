import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:ad_sport_plugin/ad_sport_plugin.dart';
import 'package:ad_sport_plugin/ttFeedView.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initAd();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initAd() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
      platformVersion = await AdSportPlugin.init(TTInitConfig(appid: "5034670",appname: "583体育"));

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: TTFeed(androidSlotID: "934670501"),
      ),
    );
  }
}

