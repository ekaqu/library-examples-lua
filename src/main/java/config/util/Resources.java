package config.util;

import com.google.common.io.Closeables;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 */
public class Resources {

  public static URL getResource(String file) {
    // check classpath first
    URL url = Resources.class.getClassLoader().getResource(file);
    if(url == null) {
      File f = new File(file);
      try {
        url = f.toURI().toURL();
      } catch (MalformedURLException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
    }
    return url;
  }
  
  public static String remoteGet(String source) throws IOException {
    URL url = new URL(source);

    BufferedReader in = null;
    String str;
    StringBuilder sb = new StringBuilder();
    try {
      in = new BufferedReader(new InputStreamReader(url.openStream()));
      while ((str = in.readLine()) != null) {
        sb.append(str).append("\n");
      }
    } finally {
      Closeables.closeQuietly(in);
    }
    return sb.toString();
  }

}
