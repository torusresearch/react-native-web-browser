//
//  WebBrowserOptions.swift
//  react-native-web-browser
//
//  Created by Michael Lee on 23/3/2022.
//

//https://github.com/expo/expo/blob/main/packages/expo-web-browser/ios/WebBrowserOptions.swift

import Foundation
import SafariServices

class WebBrowserOptions: Codable {
    var readerMode: Bool = false

    var enableBarCollapsing: Bool = false

    var dismissButtonStyle: DismissButtonStyle = .done

    var toolbarColor: CodableColor?

    var controlsColor: CodableColor?
    
    var presentationStyle:PresentationStyle = .overFullScreen
    
    struct AuthSessionOptions:Codable{
        var preferEphemeralSession: Bool = false
    }
    
    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        if let readerMode = try container.decodeIfPresent(Bool.self, forKey: .readerMode) {
            self.readerMode = readerMode
        } else {
            self.readerMode = false
        }
        
        if let enableBarCollapsing = try container.decodeIfPresent(Bool.self, forKey: .enableBarCollapsing) {
            self.enableBarCollapsing = enableBarCollapsing
        } else {
            self.enableBarCollapsing = false
        }
        
        if let dismissButtonStyle = try container.decodeIfPresent(DismissButtonStyle.self, forKey: .dismissButtonStyle) {
            self.dismissButtonStyle = dismissButtonStyle
        } else {
            self.dismissButtonStyle = .done
        }
        
        if let toolbarColor = try container.decodeIfPresent(CodableColor.self, forKey: .toolbarColor) {
            self.toolbarColor = toolbarColor
        } else {
            self.toolbarColor = nil
        }
        
        if let controlsColor = try container.decodeIfPresent(CodableColor.self, forKey: .controlsColor) {
            self.controlsColor = controlsColor
        } else {
            self.controlsColor = nil
        }
    }
}

class AuthSessionOptions: Codable {
    let preferEphemeralSession: Bool
    
    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        if let preferEphemeralSession = try container.decodeIfPresent(Bool.self, forKey: .preferEphemeralSession) {
            self.preferEphemeralSession = preferEphemeralSession
        } else {
            self.preferEphemeralSession = false
        }
    }
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

internal enum PresentationStyle: String ,Codable{
  case fullScreen
  case pageSheet
  case formSheet
  case currentContext
  case overFullScreen
  case overCurrentContext
  case popover
  case none
  case automatic

  func toPresentationStyle() -> UIModalPresentationStyle {
    switch self {
    case .fullScreen:
      return .fullScreen
    case .pageSheet:
      return .pageSheet
    case .formSheet:
      return .formSheet
    case .currentContext:
      return .currentContext
    case .overFullScreen:
      return .overFullScreen
    case .overCurrentContext:
      return .overCurrentContext
    case .popover:
      return .popover
    case .none:
      return .none
    case .automatic:
      if #available(iOS 13.0, *) {
        return .automatic
      }
      // default prior iOS 13
      return .fullScreen
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
