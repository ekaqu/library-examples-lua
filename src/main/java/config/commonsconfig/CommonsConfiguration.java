package config.commonsconfig;

import com.google.common.io.Files;
import config.Configuration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 *
 */
public class CommonsConfiguration implements Configuration {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommonsConfiguration.class.getName());

  private final CompositeConfiguration conf = new CompositeConfiguration();

  public String get(final String key, final String defaultValue) {
    return conf.getString(key, defaultValue);
  }

  public boolean getBoolean(final String key, final boolean defaultValue) {
    return conf.getBoolean(key, defaultValue);
  }

  public int getInt(final String key, final int defaultValue) {
    return conf.getInt(key, defaultValue);
  }

  public long getLong(final String key, final long defaultValue) {
    return conf.getLong(key, defaultValue);
  }

  public float getFloat(final String key, final float defaultValue) {
    return conf.getFloat(key, defaultValue);
  }

  public double getDouble(final String key, final double defaultValue) {
    return conf.getDouble(key, defaultValue);
  }

  public Configuration addResource(final String resource) {
    String extension = Files.getFileExtension(resource);
    try {
      if("properties".equals(extension)) {
        this.conf.addConfiguration(new PropertiesConfiguration(resource));
      } else if("xml".equals(extension)) {
        this.conf.addConfiguration(new XMLConfiguration(resource));
      } else if("system".equals(extension)) {
        this.conf.addConfiguration(new SystemConfiguration());
      } else {
        throw new IllegalArgumentException("Extension " + extension + " not supported");
      }
    } catch (ConfigurationException e) {
      LOGGER.debug("Unable to load resource {}\n{}", resource, e);
    }
    return this;
  }

  public Configuration addResource(final URL url) {
    return addResource(url.getPath());
  }
}
