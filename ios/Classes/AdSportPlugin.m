#import "AdSportPlugin.h"
#import <ad_sport_plugin/ad_sport_plugin-Swift.h>

@implementation AdSportPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAdSportPlugin registerWithRegistrar:registrar];
}
@end
