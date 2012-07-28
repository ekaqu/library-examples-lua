package config.lua;

import config.Configuration;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.google.common.io.Resources.*;

/**
 *
 */
@Test(groups = "Experiment")
public class LuaPlay {
  private static final Logger LOGGER = LoggerFactory.getLogger(LuaPlay.class.getName());

  private static final String LUA_PROPERTIES = "simple.lua";

  public void simpleProperties() throws IOException {
    LuaTable table = JsePlatform.standardGlobals();
    LoadState.load(newInputStreamSupplier(getResource(LUA_PROPERTIES)).getInput(), LUA_PROPERTIES, table).call();

    LuaValue value = table.get("foo");
    LOGGER.info("Table {}", table);
    for(LuaValue key : table.keys()) {
      LOGGER.info("Table Keys {}", key);
    }
    LOGGER.info("Value {}", value);

    value = table.get("t.bar");
    LOGGER.info("Value {}", value);
  }
  
  public void simpleConfig() {
    Configuration conf = new LuaConfiguration();
    conf.addResource(LUA_PROPERTIES);
    
    String value = conf.get("t.bar", null);
    LOGGER.info("Value of t.bar {}", value);

    value = conf.get("t.bar.plus", null);
    LOGGER.info("Value of t.bar.plus {}", value);
  }
}
