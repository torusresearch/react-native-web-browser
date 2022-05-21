import * as React from 'react';

import {StyleSheet, View, Button} from 'react-native';
import {
  openBrowserAsync,
  openAuthSessionAsync,
  dismissAuthSession,
  dismissBrowser,
} from 'react-native-web-browser';

export default function App() {
  return (
    <View style={styles.container}>
      <Button
        title="Open browser window to Google.com"
        onPress={() => openBrowserAsync('https://google.com')}>
        Open browser window to Google.com
      </Button>
      <Button
        title="Open auth browser window to Google.com"
        onPress={() =>
          openAuthSessionAsync(
            'https://google.com',
            'https://account.google.com',
          )
        }>
        Open auth browser window to Google.com
      </Button>
      <Button title="Dismiss Auth Session" onPress={() => dismissAuthSession()}>
        Dismiss Auth Session
      </Button>
      <Button title="Dismiss Browser" onPress={() => dismissBrowser()}>
        Dismiss Browser
      </Button>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
