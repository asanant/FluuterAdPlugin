import 'dart:async';

import 'package:ad_sport_plugin/AdListeners.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';



class TTInitConfig {
  String appid;
  String appname;
  bool debug;

  TTInitConfig({
    @required this.appid,
    @required this.appname,
    this.debug = true
  });

  TTInitConfig.fromJson(Map<String, dynamic> json) {
    appid = json['appid'];
    appname = json['appname'];
    debug = json['debug'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['appid'] = this.appid;
    data['appname'] = this.appname;
    data['debug'] = this.debug;

    return data;
  }
}

class AdSportPlugin {
  static const MethodChannel _channel = const MethodChannel('ad_sport_plugin');

  static Future<dynamic> init(TTInitConfig config) {
    return _channel.invokeMethod("init", config.toJson());
  }
  static Future<dynamic> openRewardVideo(String codeId,int orientation,{VoidCallback onAdClose,VoidCallback onVideoComplete} ) {
     AdListeners(onAdClose:onAdClose,onVideoComplete: onVideoComplete).bindMethodChannel(new MethodChannel("ad_sport_plugin/$codeId"));
    return _channel.invokeMethod("openRewardVideo",{"codeId":codeId,"orientation":orientation});
  }



}
