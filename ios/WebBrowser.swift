import Foundation

// https://github.com/expo/expo/blob/main/packages/expo-web-browser/ios/WebBrowserModule.swift

@objc(WebBrowser)
class WebBrowser: NSObject {
    private var currentWebBrowserSession: WebBrowserSession?
    private var currentAuthSession: WebAuthSession?
    
    @objc(openBrowserAsync:withOptionsDict:withResolver:withRejector:)
    func openBrowserAsync(urlStr: String, optionsDict: [String: Any], resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        guard
            let url = URL(string: urlStr),
            let options = try? JSONDecoder().decode(WebBrowserOptions.self, from: JSONSerialization.data(withJSONObject: optionsDict))
        else {
            reject(ReactNativeWebBrowserErrorCode, "Invalid Argument: url or options is invalid.", ReactNativeWebBrowserError.invalidArgument("url or options"))
            return
        }
        let promise = Promise(resolver: resolve, rejector: reject)
        
        guard self.currentWebBrowserSession?.isOpen != true else {
            reject(ReactNativeWebBrowserErrorCode, "WebBrowser is already opened.", ReactNativeWebBrowserError.alreadyOpen)
            return
        }
        self.currentWebBrowserSession = WebBrowserSession(url: url, options: options)
        self.currentWebBrowserSession?.open(promise)
    }
    
    @objc(dismissBrowser:withRejector:)
    func dismissBrowser(resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        self.currentWebBrowserSession?.dismiss()
        self.currentWebBrowserSession = nil
        resolve(nil)
    }
    
//    @objc(openAuthSessionAsync:withRedirectUrl:withResolver:withRejector:)
//    func openAuthSessionAsync(_ authUrlStr: String, _ redirectUrlStr: String, _ resolve: @escaping RCTPromiseResolveBlock, _ reject: @escaping RCTPromiseRejectBlock) {
//        guard
//            let authUrl = URL(string: authUrlStr),
//            let redirectUrl = URL(string: redirectUrlStr)
//        else {
//            reject(ReactNativeWebBrowserErrorCode, "Invalid Argument: authUrl or redirectUrl is invalid.", ReactNativeWebBrowserError.invalidArgument("authUrl or redirectUrl"))
//            return
//        }
//        let promise = Promise(resolver: resolve, rejector: reject)
//
//        guard self.currentAuthSession?.isOpen != true else {
//            reject(ReactNativeWebBrowserErrorCode, "AuthSession is already opened.", ReactNativeWebBrowserError.alreadyOpen)
//            return
//        }
//        self.currentAuthSession = WebAuthSession(authUrl: authUrl, redirectUrl: redirectUrl)
//        self.currentAuthSession?.open(promise)
//    }
    
    @objc(openAuthSessionAsync:withRedirectUrl:withResolver:withRejector:)
    func openAuthSessionAsync(_ authUrlStr: NSString, redirectUrlStr: NSString, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        guard
            let authUrl = URL(string: authUrlStr as String),
            let redirectUrl = URL(string: redirectUrlStr as String)
        else {
            reject(ReactNativeWebBrowserErrorCode, "Invalid Argument: authUrl or redirectUrl is invalid.", ReactNativeWebBrowserError.invalidArgument("authUrl or redirectUrl"))
            return
        }
        let promise = Promise(resolver: resolve, rejector: reject)
        
        guard self.currentAuthSession?.isOpen != true else {
            reject(ReactNativeWebBrowserErrorCode, "AuthSession is already opened.", ReactNativeWebBrowserError.alreadyOpen)
            return
        }
        self.currentAuthSession = WebAuthSession(authUrl: authUrl, redirectUrl: redirectUrl)
        self.currentAuthSession?.open(promise)
    } 
    
    @objc(dismissAuthSession:withRejector:)
    func dismissAuthSession(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        self.currentAuthSession?.dismiss()
        self.currentAuthSession = nil
        resolve(nil)
    }
    
    @objc(warmUpAsync:withRejector:)
    func warmUpAsync(resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        resolve(nil)
    }
    
    @objc(coolDownAsync:withRejector:)
    func coolDownAsync(resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        resolve(nil)
    }
    
    @objc(mayInitWithUrlAsync:withRejector:)
    func mayInitWithUrlAsync(resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        resolve(nil)
    }
    
    @objc(getCustomTabsSupportingBrowsers:withRejector:)
    func getCustomTabsSupportingBrowsers(resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        resolve([])
    }
    
    @objc
    var methodQueue: DispatchQueue {
        return DispatchQueue.main
    }
}
