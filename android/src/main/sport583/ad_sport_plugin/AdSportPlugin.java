package sport583.ad_sport_plugin;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdSdk;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;
import sport583.ad_sport_plugin.Factory.*;

public class AdSportPlugin implements MethodChannel.MethodCallHandler {

    public static final String CHANNELTAG="ad_sport_plugin";
    public static MethodChannel methodChannel;
    public static PluginRegistry.Registrar mRegistar;

    /** Plugin registration. */
    public static void registerWith(PluginRegistry.Registrar registrar) {
        mRegistar=registrar;
        methodChannel = new MethodChannel(registrar.messenger(), CHANNELTAG);
        methodChannel.setMethodCallHandler(new AdSportPlugin());
        //穿山甲注册信息流广告视图
        registrar.platformViewRegistry().registerViewFactory(CHANNELTAG+"/ttview_express_feed", new TTExpressFeedFactory(registrar));
        registrar.platformViewRegistry().registerViewFactory(CHANNELTAG+"/ttview_express_banner",new TTExpressBannerFactory(registrar));
    }

    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
        if ( methodCall.method.equals("init")) {
            initTt(methodCall,result);
        }else if (methodCall.method.equals("openRewardVideo")){
             openRewardVideo(methodCall,result);

        }

    }


    /**
     * 初始化Sdk 建议放在flutter第一次启动时候初始化
     */
    private void initTt(MethodCall methodCall, MethodChannel.Result result) {
        if (!methodCall.hasArgument("appid") || !methodCall.hasArgument("appname")) {
            result.error("101", "SDK 初始化必须传入appid, appname", "");
            return ;
        }

        TTAdSdk.init(mRegistar.context().getApplicationContext(),
                new TTAdConfig.Builder()
                        .appId((String)methodCall.argument("appid"))
                        .useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                        .appName((String)methodCall.argument("appname"))
                        .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_NO_TITLE_BAR)
                        .allowShowNotify(true) //是否允许sdk展示通知栏提示
                        .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                        .debug((boolean)methodCall.argument("debug")) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                        .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G) //允许直接下载的网络状态集合
                        .supportMultiProcess(false) //是否支持多进程，true支持
                        //.httpStack(new MyOkStack3())//自定义网络库，demo中给出了okhttp3版本的样例，其余请自行开发或者咨询工作人员。
                        .build());
        // 申请权限
        TTAdSdk.getAdManager().requestPermissionIfNecessary(mRegistar.context().getApplicationContext());
    }


    private void openRewardVideo(MethodCall methodCall, MethodChannel.Result result){

        if (!methodCall.hasArgument("codeId") || !methodCall.hasArgument("orientation")) {
            result.error("102", "参数缺失 codeId, orientation", "");
            return ;
        }
        new RewardVideo((String)methodCall.argument("codeId"),
                (int)methodCall.argument("orientation")==0 ? TTAdConstant.VERTICAL:TTAdConstant.HORIZONTAL,result);
    }

}
