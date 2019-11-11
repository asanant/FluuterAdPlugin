import 'dart:async';
import 'dart:ui';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';


abstract class TTADCacheObject  {
  MethodChannel channel;
  bool hasShow = false;

  // 加载数据
  Future load() async {
    assert(channel != null);
    return channel.invokeMethod("load");
  }

  // 展示数据
  Future show() async {
    assert(channel != null);
    if (hasShow == false) {
      return Null;
    }
    hasShow = false;
    return channel.invokeMethod("show");
  }

  // 关闭有的模块不支持
  Future close() async {
    assert(channel != null);
    return channel.invokeMethod("close");
  }

  // 销毁缓存实例
  Future destroy() async {
    assert(channel != null);
    return channel.invokeMethod("destroy");
  }
}

class AdError {
  int code;
  String msg;

  AdError.fromJson(Map<String, dynamic> params) {
    code = params['code'];
    msg = params["msg"];
  }
}






// VIEW（banner，信息流）
class NativeListener  {
  MethodChannel channel;
  BuildContext context;

  StreamController<AdError> _onErrorStreamController = StreamController.broadcast();
  StreamController<String> _onNativeExpressAdLoadStreamController = StreamController.broadcast();
  StreamController<String> _onAdClickedStreamController = StreamController.broadcast();
  StreamController<String> _onAdShowStreamController = StreamController.broadcast();
  StreamController<AdError> _onRenderFailStreamController = StreamController.broadcast();
  StreamController<Size> _onRenderSuccessStreamController = StreamController.broadcast();
  StreamController<Size> _sizeListenner;

  Stream<AdError> get onErrorStream => _onErrorStreamController.stream;

  Stream<String> get onNativeExpressAdLoadStream => _onNativeExpressAdLoadStreamController.stream;

  Stream<String> get onAdClickedStream => _onAdClickedStreamController.stream;

  Stream<String> get onAdShowStream => _onAdShowStreamController.stream;

  Stream<AdError> get onRenderFailStream => _onRenderFailStreamController.stream;

  Stream<Size> get onRenderSuccessStream => _onRenderSuccessStreamController.stream;


  Stream<Size> get size => _sizeListenner.stream;

  NativeListener({this.context}) {
    _sizeListenner = StreamController<Size>.broadcast();
  }

  void bindMethodChannel(MethodChannel channel) {
    this.channel = channel;

    channel.setMethodCallHandler(_handleMessages);
  }



  void onError(AdError err) {}

  void onNativeExpressAdLoad() {}

  void onAdClicked() {}

  void onAdShow() {}

  void onRenderFail(AdError err) {}

  // 渲染成功后自动展示广告
  void onRenderSuccess(double width, double height) {}

  @override
  // ignore: missing_return
  Future _handleMessages(MethodCall call) {
    switch (call.method) {
      case "onError":
        final err = AdError.fromJson(Map<String, dynamic>.from(call.arguments));
        _onErrorStreamController.add(err);
        this.onError(err);
        break;
      case "onNativeExpressAdLoad":
        _onNativeExpressAdLoadStreamController.add("");
        this.onNativeExpressAdLoad();
        break;
      case "onAdClicked":
        _onAdClickedStreamController.add("");
        this.onAdClicked();
        break;
      case "onAdShow":
        _onAdShowStreamController.add("");
        this.onAdShow();
        break;
      case "onRenderFail":
        final err = AdError.fromJson(Map<String, dynamic>.from(call.arguments));
        _onRenderFailStreamController.add(err);
        this.onRenderFail(err);
        break;
      case "onRenderSuccess":
        Size size = Size(double.tryParse(call.arguments["width"].toString()),
                double.tryParse(call.arguments["height"].toString()));
        size = size / window.devicePixelRatio;
        _onRenderSuccessStreamController.add(size);
        this.onRenderSuccess(size.width, size.height);
        break;
    }
  }
}

class DefaultNativeListener extends NativeListener {
  DefaultNativeListener(BuildContext context) : super(context: context);
}
