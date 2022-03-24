import Foundation

// https://github.com/expo/expo/blob/main/packages/expo-web-browser/ios/WebBrowserModule.swift

@objc(WebBrowser)
class WebBrowser: NSObject {
    private var currentWebBrowserSession: WebBrowserSession?
    private var currentAuthSession: WebAuthSession?
    
    @objc(openBrowserAsync:withOptionsDict:withResolver:withRejector:)
    func openBrowserAsync(urlStr: String, optionsDict: [String: Any], resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        guard
            let url = URL(string: urlStr),
            let options = try? JSONEncoder().decode(WebBrowserOptions.self, from: JSONSerialization.data(withJSONObject: optionsDict))
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
    
    @objc(openAuthSessionAsync:withRedirectUrl:withResolver:withRejector:)
    func openAuthSessionAsync(authUrlStr: String, redirectUrlStr: String, resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
        guard
            let authUrl = URL(string: authUrlStr),
            let redirectUrl = URL(string: redirectUrlStr)
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
    func dismissAuthSession(resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
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
    override var methodQueue: DispatchQueue {
        return DispatchQueue.main
    }
}
