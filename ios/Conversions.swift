//
//  Conversions.swift
//  react-native-web-browser
//
//  Created by Michael Lee on 23/3/2022.
//

// https://github.com/expo/expo/blob/168ee43f71f005baa11edf98e518593443e1807a/packages/expo-modules-core/ios/Swift/Conversions.swift

import Foundation

internal enum Conversions {
    /**
     Converts hex string to `UIColor` or throws an exception if the string is corrupted.
     */
    static func toColor(hexString hex: String) throws -> UIColor {
        var hexStr = hex
            .trimmingCharacters(in: .whitespacesAndNewlines)
            .replacingOccurrences(of: "#", with: "")

        // If just RGB, set alpha to maximum
        if hexStr.count == 6 { hexStr += "FF" }
        if hexStr.count == 3 { hexStr += "F" }

        // Expand short form (supported by Web)
        if hexStr.count == 4 {
            let chars = Array(hexStr)
            hexStr = [
                String(repeating: chars[0], count: 2),
                String(repeating: chars[1], count: 2),
                String(repeating: chars[2], count: 2),
                String(repeating: chars[3], count: 2),
            ].joined(separator: "")
        }

        var rgba: UInt64 = 0

        guard hexStr.range(of: #"^[0-9a-fA-F]{8}$"#, options: .regularExpression) != nil,
              Scanner(string: hexStr).scanHexInt64(&rgba)
        else {
            throw ReactNativeWebBrowserError.invalidHexColor(hex)
        }
        return try toColor(rgba: rgba)
    }

    /**
     Converts an integer for ARGB color to `UIColor`. Since the alpha channel is represented by first 8 bits,
     it's optional out of the box. React Native converts colors to such format.
     */
    static func toColor(argb: UInt64) throws -> UIColor {
        guard argb <= UInt32.max else {
            throw ReactNativeWebBrowserError.hexColorOverflow(argb)
        }
        let alpha = CGFloat((argb >> 24) & 0xFF) / 255.0
        let red = CGFloat((argb >> 16) & 0xFF) / 255.0
        let green = CGFloat((argb >> 8) & 0xFF) / 255.0
        let blue = CGFloat(argb & 0xFF) / 255.0
        return UIColor(red: red, green: green, blue: blue, alpha: alpha)
    }

    /**
     Converts an integer for RGBA color to `UIColor`.
     */
    static func toColor(rgba: UInt64) throws -> UIColor {
        guard rgba <= UInt32.max else {
            throw ReactNativeWebBrowserError.hexColorOverflow(rgba)
        }
        let red = CGFloat((rgba >> 24) & 0xFF) / 255.0
        let green = CGFloat((rgba >> 16) & 0xFF) / 255.0
        let blue = CGFloat((rgba >> 8) & 0xFF) / 255.0
        let alpha = CGFloat(rgba & 0xFF) / 255.0
        return UIColor(red: red, green: green, blue: blue, alpha: alpha)
    }

    static func fromColor(uiColor: UIColor) throws -> String {
        let cgColorInRGB = uiColor.cgColor.converted(to: CGColorSpace(name: CGColorSpace.sRGB)!, intent: .defaultIntent, options: nil)!
        let colorRef = cgColorInRGB.components
        let r = colorRef?[0] ?? 0
        let g = colorRef?[1] ?? 0
        let b = ((colorRef?.count ?? 0) > 2 ? colorRef?[2] : g) ?? 0
        let a = uiColor.cgColor.alpha

        var color = String(
            format: "#%02lX%02lX%02lX",
            lroundf(Float(r * 255)),
            lroundf(Float(g * 255)),
            lroundf(Float(b * 255))
        )

        if a < 1 {
            color += String(format: "%02lX", lroundf(Float(a * 255)))
        }

        return color
    }
}
