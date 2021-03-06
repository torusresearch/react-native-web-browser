//
//  WebBrowserSession.swift
//  react-native-web-browser
//
//  Created by Michael Lee on 23/3/2022.
//

import Foundation
import SafariServices

internal class WebBrowserSession: NSObject, SFSafariViewControllerDelegate {
    let viewController: SFSafariViewController
    var promise: Promise?
    var isOpen: Bool {
        promise != nil
    }

    init(url: URL, options: WebBrowserOptions) {
        let configuration = SFSafariViewController.Configuration()
        configuration.barCollapsingEnabled = options.enableBarCollapsing
        configuration.entersReaderIfAvailable = options.readerMode

        viewController = SFSafariViewController(url: url, configuration: configuration)
        viewController.dismissButtonStyle = options.dismissButtonStyle.toSafariDismissButtonStyle()
        viewController.preferredBarTintColor = options.toolbarColor?.color
        viewController.preferredControlTintColor = options.controlsColor?.color

        super.init()
        viewController.delegate = self

        // By setting the modal presentation style to OverFullScreen, we disable the "Swipe to dismiss"
        // gesture that is causing a bug where sometimes `safariViewControllerDidFinish` is not called.
        // There are bugs filed already about it on OpenRadar.
        viewController.modalPresentationStyle = .overFullScreen
    }

    func open(_ promise: Promise) {
        var currentViewController = UIApplication.shared.keyWindow?.rootViewController
        while currentViewController?.presentedViewController != nil {
            currentViewController = currentViewController?.presentedViewController
        }
        currentViewController?.present(viewController, animated: true, completion: nil)

        self.promise = promise
    }

    func dismiss() {
        viewController.dismiss(animated: true) {
            self.finish(type: "dismiss")
        }
    }

    // MARK: - SFSafariViewControllerDelegate

    func safariViewControllerDidFinish(_: SFSafariViewController) {
        finish(type: "cancel")
    }

    // MARK: - Private

    private func finish(type: String) {
        promise?.resolve(["type": type])
        promise = nil
    }
}
