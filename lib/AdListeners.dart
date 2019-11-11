

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class AdListeners{
  MethodChannel mChannel;
  VoidCallback onVideoComplete;
  VoidCallback onAdClose;
  AdListeners({this.onAdClose,this.onVideoComplete});

  void bindMethodChannel(MethodChannel channel){
    this.mChannel=channel;
    this.mChannel.setMethodCallHandler(methodCallHandler);
  }


  // ignore: missing_return
  Future methodCallHandler(MethodCall call) {
    switch (call.method) {
      case "onVideoComplete":
        this.onVideoComplete();

        break;
      case "onAdClose":
        this.onAdClose();
        break;
    }
  }
}