package config;

import java.net.URL;

/**
 *
 */
public interface Configuration {
  String get(String key, String defaultValue);
  boolean getBoolean(String key, boolean defaultValue);
  int getInt(String key, int defaultValue);
  long getLong(String key, long defaultValue);
  float getFloat(String key, float defaultValue);
  double getDouble(String key, double defaultValue);
  
  Configuration addResource(String resource);
  Configuration addResource(URL url);
}
