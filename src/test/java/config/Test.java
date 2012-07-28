package config;

import config.lua.LuaConfiguration;
import config.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 */
@org.testng.annotations.Test
public class Test {
  private static final Logger LOGGER = LoggerFactory.getLogger(Test.class.getName());
  
  public void test() throws IOException {
    String data = Resources.remoteGet("http://google.com");
    LOGGER.info(data);
  }

  public void lua() {
    LuaConfiguration conf = new LuaConfiguration();
    conf.addResource("NetworkCall.lua");
    
    for(String host : new String[] {"google", "yahoo"}) {
      String data = conf.get(host, null);
      LOGGER.info("{} Data {}", host, data);
    }
  }
}
