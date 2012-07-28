package config.properties;

import com.google.common.io.Resources;
import config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 *
 */
public class PropertiesConfiguration implements Configuration {
  private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesConfiguration.class.getName());

  private final Properties properties = new Properties();

  public String get(final String key, final String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  public boolean getBoolean(final String key, final boolean defaultValue) {
    String value = properties.getProperty(key);
    return (value == null)? defaultValue : Boolean.parseBoolean(value);
  }

  public int getInt(final String key, final int defaultValue) {
    String value = properties.getProperty(key);
    return (value == null)? defaultValue : Integer.parseInt(value);
  }

  public long getLong(final String key, final long defaultValue) {
    String value = properties.getProperty(key);
    return (value == null)? defaultValue : Long.parseLong(value);
  }

  public float getFloat(final String key, final float defaultValue) {
    String value = properties.getProperty(key);
    return (value == null)? defaultValue : Float.parseFloat(value);
  }

  public double getDouble(final String key, final double defaultValue) {
    String value = properties.getProperty(key);
    return (value == null)? defaultValue : Double.parseDouble(value);
  }

  public Configuration addResource(final String resource) {
    return addResource(Resources.getResource(resource));
  }

  public Configuration addResource(final URL url) {
    try {
      properties.load(Resources.newInputStreamSupplier(url).getInput());
    } catch (IOException e) {
      LOGGER.debug("Unable to load resource {}\n{}", url, e);
    }
    return this;
  }
}
