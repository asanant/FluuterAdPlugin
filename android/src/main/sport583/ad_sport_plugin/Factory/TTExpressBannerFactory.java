package sport583.ad_sport_plugin.Factory;

import android.content.Context;

import java.util.Map;

import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;
import sport583.ad_sport_plugin.views.TTExpressBannerView;

public class TTExpressBannerFactory extends PlatformViewFactory {
    PluginRegistry.Registrar registrar;

    public TTExpressBannerFactory(PluginRegistry.Registrar registrar) {
        super(StandardMessageCodec.INSTANCE);
        this.registrar=registrar;
    }

    @Override
    public PlatformView create(Context context, int i, Object args) {
        Map<String, Object> params = (Map<String, Object>) args;

        return new TTExpressBannerView(context,this.registrar,i,params);
    }
}
