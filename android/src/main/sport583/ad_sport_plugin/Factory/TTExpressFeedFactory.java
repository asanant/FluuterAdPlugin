package sport583.ad_sport_plugin.Factory;

import android.content.Context;

import androidx.core.view.LayoutInflaterFactory;


import java.util.Map;

import io.flutter.plugin.common.MessageCodec;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;
import sport583.ad_sport_plugin.views.TTExpressFeedView;

public class TTExpressFeedFactory extends PlatformViewFactory {
    PluginRegistry.Registrar registrar;
    public TTExpressFeedFactory(PluginRegistry.Registrar registrar) {
        super(StandardMessageCodec.INSTANCE);
        this.registrar = registrar;
    }

    @Override
    public PlatformView create(Context context, int i, Object args) {
        Map<String, Object> params = (Map<String, Object>) args;
        return new TTExpressFeedView(context,registrar,i,params);
    }
}
