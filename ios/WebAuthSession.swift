//
//  WebAuthSession.swift
//  react-native-web-browser
//
//  Created by Michael Lee on 23/3/2022.
//

import AuthenticationServices
import Foundation

@available(iOS 12.0, *)
private class PresentationContextProvider: NSObject, ASWebAuthenticationPresentationContextProviding {
    func presentationAnchor(for _: ASWebAuthenticationSession) -> ASPresentationAnchor {
        return UIApplication.shared.keyWindow ?? ASPresentationAnchor()
    }
}

@available(iOS 12.0, *)
internal final class WebAuthSession {
    var authSession: ASWebAuthenticationSession?
    var promise: Promise?
    var isOpen: Bool {
        promise != nil
    }

    // It must be initialized before hand as `ASWebAuthenticationSession` holds it as a weak property
    private var presentationContextProvider = PresentationContextProvider()

    init(authUrl: URL, redirectUrl: URL, options: AuthSessionOptions) {
        authSession = ASWebAuthenticationSession(
            url: authUrl,
            callbackURLScheme: redirectUrl.scheme,
            completionHandler: { callbackUrl, error in
                self.finish(with: [
                    "type": callbackUrl != nil ? "success" : "cancel",
                    "url": callbackUrl?.absoluteString,
                    "error": error?.localizedDescription,
                ])
            }
        )
        if #available(iOS 13.0, *) {
          self.authSession?.prefersEphemeralWebBrowserSession = options.preferEphemeralSession
        }
    }

    func open(_ promise: Promise) {
        if #available(iOS 13.0, *) {
            authSession?.presentationContextProvider = presentationContextProvider
        }
        authSession?.start()
        self.promise = promise
    }

    func dismiss() {
        authSession?.cancel()
        finish(with: ["type": "dismiss"])
    }

    // MARK: - Private

    private func finish(with result: [String: String?]) {
        promise?.resolve(result)
        promise = nil
        authSession = nil
    }
}
