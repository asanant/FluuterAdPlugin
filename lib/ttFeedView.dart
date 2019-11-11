import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'listeners.dart';

class TTFeed extends StatefulWidget {
  String androidSlotID;
  String iosSlotID;

  NativeListener listener;

  TTFeed({Key key, this.androidSlotID, this.iosSlotID, this.listener}) : super(key: key);

  @override
  _TTFeedState createState() => new _TTFeedState();
}

class _TTFeedState extends State<TTFeed>  with AutomaticKeepAliveClientMixin{
  Size _size;
  NativeListener _listener;

  @override
  void initState() {
    super.initState();
    _size = Size.fromHeight(1);
  }

  @override
  bool get wantKeepAlive => true;

  @override
  Widget build(BuildContext context) {
    super.build(context);
    if (defaultTargetPlatform == TargetPlatform.android) {
      if (widget.androidSlotID == null) {
        return SizedBox();
      }
      return SizedBox(
        height: _size.height,
        child: AndroidView(
          viewType: "ad_sport_plugin/ttview_express_feed",
          creationParams: {
            "slotID": widget.androidSlotID,
          },
          creationParamsCodec: const StandardMessageCodec(),
          onPlatformViewCreated: _onPlatformViewCreated,
          gestureRecognizers: <Factory<OneSequenceGestureRecognizer>>[
            new Factory<OneSequenceGestureRecognizer>(() => new TapGestureRecognizer()),
          ].toSet(),
        ),
      );
    } else if (defaultTargetPlatform == TargetPlatform.iOS) {
      if (widget.iosSlotID == null) {
        return SizedBox();
      }
      return SizedBox(
        height: _size.height,
        child: UiKitView(
          viewType: "ad_sport_plugin/ttview_express_feed",
          creationParams: {
            "slotID": widget.iosSlotID,
          },
          creationParamsCodec: const StandardMessageCodec(),
          onPlatformViewCreated: _onPlatformViewCreated,
          gestureRecognizers: <Factory<OneSequenceGestureRecognizer>>[
            new Factory<OneSequenceGestureRecognizer>(() => new TapGestureRecognizer()),
          ].toSet(),
        ),
      );
    } else {
      return Text('$defaultTargetPlatform 平台暂不支持FlutterTTPlugin插件');
    }
  }

  void onSizeChange(Size size) {
    setState(() {
      _size = size;
    });
  }

  void _onPlatformViewCreated(int id) {
    final channel = MethodChannel('ad_sport_plugin/ttview_express_feed_$id');
    if (widget.listener != null) {
      _listener = widget.listener;
    } else {
      _listener = DefaultNativeListener(context);
    }
    _listener.bindMethodChannel(channel);
    _listener.onRenderSuccessStream.listen(onSizeChange);
  }

  @override
  void dispose() {
    super.dispose();
    
    print("TTFeed:dispose");
    
  }
}
