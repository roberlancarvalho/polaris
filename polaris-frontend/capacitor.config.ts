import { CapacitorConfig } from "@capacitor/cli";

const config: CapacitorConfig = {
  appId: "com.technorth.polaris",
  appName: "Polaris",
  webDir: "dist",
  server: {
    androidScheme: "http",

    allowNavigation: ["10.0.2.2", "192.168.1.169", "192.168.1.*"],

    cleartext: true,
  },
  android: {
    allowMixedContent: true,
  },
};

export default config;
