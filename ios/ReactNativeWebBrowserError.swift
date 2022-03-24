//
//  ReactNativeWebBrowserError.swift
//  react-native-web-browser
//
//  Created by Michael Lee on 23/3/2022.
//

import Foundation

@objc(ReactNativeWebBrowserError)
enum ReactNativeWebBrowserError: Error {
    case invalidHexColor(String)
    case hexColorOverflow(UInt64)
    case alreadyOpen
    case invalidArgument(String)
}

let ReactNativeWebBrowserErrorCode = "ReactNativeWebBrowserError"
