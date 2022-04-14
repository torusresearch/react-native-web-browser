import * as React from 'react';

import { StyleSheet, View, Text, Button } from 'react-native';
import {
  openBrowserAsync,
  openAuthSessionAsync,
} from 'react-native-web-browser';

export default function App() {
  return (
    <View style={styles.container}>
      <Button
        title="Open browser window to Google.com"
        onPress={() => openBrowserAsync('https://google.com')}
      >
        Open browser window to Google.com
      </Button>
      <Button
        title="Open auth browser window to Google.com"
        onPress={() =>
          openAuthSessionAsync(
            'https://google.com',
            'https://account.google.com'
          )
        }
      >
        Open auth browser window to Google.com
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
