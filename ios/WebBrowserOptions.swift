//
//  WebBrowserOptions.swift
//  react-native-web-browser
//
//  Created by Michael Lee on 23/3/2022.
//

import Foundation
import SafariServices

struct WebBrowserOptions: Codable {
    var readerMode: Bool = false

    var enableBarCollapsing: Bool = false

    var dismissButtonStyle: DismissButtonStyle = .done

    var toolbarColor: CodableColor?

    var controlsColor: CodableColor?
}

enum DismissButtonStyle: String, Codable {
    case done
    case close
    case cancel

    func toSafariDismissButtonStyle() -> SFSafariViewController.DismissButtonStyle {
        switch self {
        case .done:
            return .done
        case .close:
            return .close
        case .cancel:
            return .cancel
        }
    }
}

// https://github.com/expo/expo/blob/168ee43f71f005baa11edf98e518593443e1807a/packages/expo-modules-core/ios/Swift/Arguments/Convertibles.swift
// https://stackoverflow.com/questions/50928153/make-uicolor-codable

public struct CodableColor: Encodable, Decodable {
    let color: UIColor

    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(Conversions.fromColor(uiColor: color))
    }

    public init(from decoder: Decoder) throws {
        let value = try decoder.singleValueContainer()
        let uiColor = try Conversions.toColor(hexString: value.decode(String.self))
        color = uiColor
    }
}
