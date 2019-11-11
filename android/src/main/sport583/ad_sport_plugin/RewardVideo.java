package sport583.ad_sport_plugin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;

import io.flutter.plugin.common.MethodChannel;

public class RewardVideo {
  private TTAdNative mTTAdNative;
  private TTRewardVideoAd mttRewardVideoAd;
  private MethodChannel.Result result;
  private MethodChannel methodChannel;
    public RewardVideo( String codeId, int orientation,MethodChannel.Result result) {
        this.result=result;
        //step1:初始化sdk
        TTAdManager ttAdManager = TTAdSdk.getAdManager();
        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        ttAdManager.requestPermissionIfNecessary(AdSportPlugin.mRegistar.activity().getApplicationContext());
        //step3:创建TTAdNative对象,用于调用广告请求接口
        mTTAdNative = ttAdManager.createAdNative(AdSportPlugin.mRegistar.activity().getApplicationContext());
        methodChannel = new MethodChannel(AdSportPlugin.mRegistar.messenger(), "ad_sport_plugin/" + codeId);

        loadAd(codeId,orientation);
    }

    private void loadAd(String codeId, int orientation) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(3)  //奖励的数量
                .setUserID("user123")//用户id,必传参数
                .setMediaExtra("media_extra") //附加参数，可选
                .setOrientation(orientation) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        //step5:请求广告
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                result.error("LoadonError",String.format("code:%d message:%s",code,message),null);
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                mttRewardVideoAd = ad;
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        methodChannel.invokeMethod("onAdShow","");
                        Log.e("yian","onAdShow");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        methodChannel.invokeMethod("onAdVideoBarClick","");

                        Log.e("yian","onAdVideoBarClick");

                    }

                    @Override
                    public void onAdClose() {
                        methodChannel.invokeMethod("onAdClose","");
                        Log.e("yian","onAdClose");

                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                        methodChannel.invokeMethod("onVideoComplete","");

                        Log.e("yian","onVideoComplete");

                    }

                    @Override
                    public void onVideoError() {
                        Log.e("yian","onVideoError");
                        methodChannel.invokeMethod("onVideoError","");

                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                        result.success("onRewardVerify");
                        methodChannel.invokeMethod("onRewardVerify","");

                        Log.e("yian","onRewardVerify");

                    }

                    @Override
                    public void onSkippedVideo() {
                        methodChannel.invokeMethod("onSkippedVideo","");

                    }
                });

                showVideo();

            }
        });
    }


  private void   showVideo()
    {
        if (mttRewardVideoAd != null) {
            //step6:在获取到广告后展示
            //该方法直接展示广告
//                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this);

            //展示广告，并传入广告展示的场景
            mttRewardVideoAd.showRewardVideoAd(AdSportPlugin.mRegistar.activity(), TTAdConstant.RitScenes.CUSTOMIZE_SCENES,"scenes_test");
            mttRewardVideoAd = null;
        } else {
        }
    }
}