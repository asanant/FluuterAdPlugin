package sport583.ad_sport_plugin.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.platform.PlatformView;

import static sport583.ad_sport_plugin.AdSportPlugin.CHANNELTAG;

public class TTExpressFeedView implements PlatformView, MethodChannel.MethodCallHandler {
    final int viewID;
    final Context mContext;
    final MethodChannel methodChannel;
    final PluginRegistry.Registrar mRegistrar;
    private Map<String, Object> params;
    private TTNativeExpressAd mTTAd;
    private FrameLayout mExpressContainer;
    private long startTime;

    private AdSlot adSlot;

    public TTExpressFeedView(Context context, PluginRegistry.Registrar registrar, int id, Map<String, Object> args) {
        this.mContext = context;
        this.mRegistrar = registrar;
        this.viewID = id;
        this.params = args;
        Resources resources = registrar.activity().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        methodChannel = new MethodChannel(registrar.messenger(), CHANNELTAG+"/ttview_express_feed_" + id);
        mExpressContainer = new FrameLayout(mContext);
        mExpressContainer.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        adSlot = new AdSlot.Builder()
                .setCodeId((String) params.get("slotID")) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(dm.widthPixels, 0) //期望个性化模板广告view的size,单位dp
                 .setImageAcceptedSize(640, 320) //这个参数设置即可，不影响个性化模板广告的size
                .build();
        TTAdNative mTTAdNative = TTAdSdk.getAdManager().createAdNative(context);
        startTime = System.currentTimeMillis();
        mTTAdNative.loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String msg) {
                HashMap<String, Object> rets = new HashMap<>();
                rets.put("message", msg);
                rets.put("code", code);
                methodChannel.invokeMethod("onError", rets);

                Log.e("onError",msg);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    return;
                }
                mTTAd = ads.get(0);
                bindAdListener(mTTAd);
                mTTAd.render();//调用render开始渲染广告
                methodChannel.invokeMethod("onNativeExpressAdLoad", "");
            }
        });
    }

    private boolean mHasShowDownloadActive = false;

    private void bindAdListener(TTNativeExpressAd ad) {

        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {

            @Override
            public void onAdClicked(View view, int type) {
              //  TToast.show(mContext, "广告被点击");
                methodChannel.invokeMethod("onAdClicked", "");
            }

            @Override
            public void onAdShow(View view, int type) {
                Log.i("TAG", "onAdShow: ");
               // TToast.show(mContext, "广告展示");
                methodChannel.invokeMethod("onAdShow", "");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView", "render fail:" + (System.currentTimeMillis() - startTime));
              //  TToast.show(mContext, msg + " code:" + code);
                HashMap<String, Object> rets = new HashMap<>();
                rets.put("message", msg);
                rets.put("code", code);
                methodChannel.invokeMethod("onRenderFail", rets);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                //返回view的宽高 单位 dp
                Log.i("TAG", "onRenderSuccess: " + view.getHeight());
             //   TToast.show(mContext, "渲染成功");
                //在渲染成功回调时展示广告，提升体验
                mExpressContainer.removeAllViews();
                view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
                        mExpressContainer.measure(View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels,
                                View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(0,
                                        View.MeasureSpec.UNSPECIFIED));

                        final int targetWidth = mExpressContainer.getMeasuredWidth();
                        final int targetHeight = mExpressContainer.getMeasuredHeight();
                        HashMap<String, Object> rets = new HashMap<>();

                        rets.put("width", targetWidth);
                        rets.put("height", targetHeight);
                        methodChannel.invokeMethod("onRenderSuccess", rets);
                    }
                });
                mExpressContainer.addView(view);
//                HashMap<String, Object> rets = new HashMap<>();
//                rets.put("width", width);
//                rets.put("height", height);
//                methodChannel.invokeMethod("onRenderSuccess", rets);
            }
        });
        //dislike设置
        bindDislike(ad, false);
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
            return;
        }
        //可选，下载监听设置
      //  ad.setDownloadListener(new AppDownloadListener(mContext, methodChannel));
    }

    /**
     * 设置广告的不喜欢，开发者可自定义样式
     *
     * @param ad
     * @param customStyle 是否自定义样式，true:样式自定义
     */
    private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
        //使用默认个性化模板中默认dislike弹出样式


        ad.setDislikeCallback(mRegistrar.activity(), new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onSelected(int position, String value) {
                //用户选择不喜欢原因后，移除广告展示
                mExpressContainer.removeAllViews();
                methodChannel.invokeMethod("onClose", "");
            }

            @Override
            public void onCancel() {
            }
        });
    }


    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {

    }

    @Override
    public View getView() {
        return mExpressContainer;
    }

    @Override
    public void dispose() {
        if (mTTAd != null) {
            mTTAd.destroy();
        }
        adSlot = null;
        startTime = 0;
        mExpressContainer.removeAllViews();
        params = null;
    }
}
