import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-web-browser' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

export function multiply(a: number, b: number): Promise<number> {
  return WebBrowser.multiply(a, b);
}

export interface WebBrowserOpenOptions {
  controlsColor?: string;
  dismissButtonStyle?: 'done' | 'close' | 'cancel';
  enableBarCollapsing?: boolean;
  readerMode?: boolean;
  toolbarColor?: string;
}

export interface WebBrowserRedirectResult {
  /**
   * Type of the result.
   */
  type: 'success';
  url: string;
}

export type WebBrowserAuthSessionResult = WebBrowserRedirectResult;

export async function openAuthSessionAsync(
  url: string,
  redirectUrl: string
): Promise<WebBrowserAuthSessionResult> {
  return await WebBrowser.openAuthSessionAsync(url, redirectUrl);
}

export async function dismissAuthSession(): Promise<void> {
  return await WebBrowser.dismissAuthSession();
}
