//
//  Promise.swift
//  react-native-web-browser
//
//  Created by Michael Lee on 23/3/2022.
//

import Foundation
// Original Implementation:  https://github.com/expo/expo/blob/168ee43f71f005baa11edf98e518593443e1807a/packages/expo-modules-core/ios/Swift/Promise.swift

struct Promise {
    public typealias ResolveClosure = RCTPromiseResolveBlock
    public typealias RejectClosure = RCTPromiseRejectBlock

    public var resolver: ResolveClosure
    public var rejector: RejectClosure

    public func resolve(_ value: Any? = nil) {
        resolver(value)
    }

    public func reject(_ error: Error) {
        rejector(ReactNativeWebBrowserErrorCode, error.localizedDescription, error)
    }
}
