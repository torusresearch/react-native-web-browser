#import <React/RCTBridgeModule.h>


@interface RCT_EXTERN_MODULE(NativeWebBrowser, NSObject)


RCT_EXTERN_METHOD(openBrowserAsync:(NSString*)urlStr
                  withOptionsDict:(NSDictionary*)optionsDict
                  withResolver:(RCTPromiseResolveBlock)resolve
                  withRejector:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(dismissBrowser:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)


RCT_EXTERN_METHOD(openAuthSessionAsync:(NSString*)authUrlStr
                  withRedirectUrl:(NSString*)redirectUrlStr
                  withOptionsDict:(NSDictionary*)optionsDict
                  withResolver:(RCTPromiseResolveBlock)resolve
                  withRejector:(RCTPromiseRejectBlock)reject)


RCT_EXTERN_METHOD(dismissAuthSession:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)


RCT_EXTERN_METHOD(warmUpAsync:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)


RCT_EXTERN_METHOD(coolDownAsync:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)


RCT_EXTERN_METHOD(mayInitWithUrlAsync:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)


RCT_EXTERN_METHOD(getCustomTabsSupportingBrowsers:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)


@end
