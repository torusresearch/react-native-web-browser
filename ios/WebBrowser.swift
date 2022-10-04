import Foundation

// https://github.com/expo/expo/blob/main/packages/expo-web-browser/ios/WebBrowserModule.swift

@objc(NativeWebBrowser)
class NativeWebBrowser: NSObject {
    private var currentWebBrowserSession: WebBrowserSession?
    private var currentAuthSession: WebAuthSession?
    
    @objc(openBrowserAsync:withOptionsDict:withResolver:withRejector:)
    func openBrowserAsync(_ urlStr: NSString, optionsDict: [String: Any], resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        guard
            let url = URL(string: urlStr as String),
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
    func dismissBrowser(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        self.currentWebBrowserSession?.dismiss()
        self.currentWebBrowserSession = nil
        resolve(nil)
    }
    
    @objc(openAuthSessionAsync:withRedirectUrl:withOptionsDict:withResolver:withRejector:)
    func openAuthSessionAsync(_ authUrlStr: NSString, redirectUrlStr: NSString?, optionsDict: [String: Any], resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
        guard
            let authUrl = URL(string: authUrlStr as String),
            let options = try? JSONDecoder().decode(AuthSessionOptions.self, from: JSONSerialization.data(withJSONObject: optionsDict))
        else {
            reject(ReactNativeWebBrowserErrorCode, "Invalid Argument: authUrl or redirectUrl or options is invalid.", ReactNativeWebBrowserError.invalidArgument("authUrl or redirectUrl or options"))
            return
        }
        
        let promise = Promise(resolver: resolve, rejector: reject)
        
        guard self.currentAuthSession?.isOpen != true else {
            reject(ReactNativeWebBrowserErrorCode, "AuthSession is already opened.", ReactNativeWebBrowserError.alreadyOpen)
            return
        }
        let redirectUrl = URL(string: redirectUrlStr as? String ?? "")
        self.currentAuthSession = WebAuthSession(authUrl: authUrl, redirectUrl: redirectUrl, options: options)
        self.currentAuthSession?.open(promise)
    } 
    
    @objc(dismissAuthSession:withRejector:)
    func dismissAuthSession(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        self.currentAuthSession?.dismiss()
        self.currentAuthSession = nil
        resolve(nil)
    }
    
    @objc(warmUpAsync:withRejector:)
    func warmUpAsync(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        resolve(nil)
    }
    
    @objc(coolDownAsync:withRejector:)
    func coolDownAsync(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        resolve(nil)
    }
    
    @objc(mayInitWithUrlAsync:withRejector:)
    func mayInitWithUrlAsync(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        resolve(nil)
    }
    
    @objc(getCustomTabsSupportingBrowsers:withRejector:)
    func getCustomTabsSupportingBrowsers(_ resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        resolve([])
    }
    
    @objc
    var methodQueue: DispatchQueue {
        return DispatchQueue.main
    }
    
    @objc
    static func requiresMainQueueSetup() -> Bool {
      return true
    }
}
