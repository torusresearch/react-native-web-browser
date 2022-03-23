import Foundation

// https://github.com/expo/expo/blob/main/packages/expo-web-browser/ios/WebBrowserModule.swift

@objc(WebBrowser)
class WebBrowser: NSObject {

    @objc(multiply:withB:withResolver:withRejecter:)
    func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        resolve(a*b)
    }
    
    func openBrowserAsync(urlStr: String, optionsDict: [String: Any], resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
        guard
            let url = URL.init(string: urlStr),
            let options = try? JSONEncoder().decode(WebBrowserOptions.self, from: JSONSerialization.data(withJSONObject: optionsDict))
        else {
            reject(
        }
    }
}
