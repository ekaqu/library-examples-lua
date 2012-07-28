package config.lua;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import config.Configuration;
import config.util.Resources;
import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.luajc.LuaJC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;


/**
 *
 */
public class LuaConfiguration implements Configuration {
  private static final Logger LOGGER = LoggerFactory.getLogger(LuaConfiguration.class.getName());

  private static final String SEPARATOR = ".";
  private static final Splitter SPLITTER = Splitter.on(SEPARATOR).omitEmptyStrings().trimResults();

  private final LuaTable table = JsePlatform.standardGlobals();
  
  {
    table.rawset("remoteGet", new VarArgFunction() {
      @Override
      public LuaValue call(final LuaValue luaValue) {
        try {
          return LuaString.valueOf(Resources.remoteGet(luaValue.tojstring()));
        } catch (IOException e) {
          throw Throwables.propagate(e);
        }
      }
    });
  }

  public LuaConfiguration() {  }
  public LuaConfiguration(boolean compiled) {
    if(compiled) {
      LuaJC.install();
    }
  }

  private LuaValue getLua(final String key) {
    LuaValue value = null;

    if(key.indexOf(SEPARATOR) >= 0) {
      LuaValue ret = table;
      for(String tableKey : SPLITTER.split(key)) {
        ret = ret.get(tableKey);
      }
      value = ret;
    } else {
      value = table.get(key);
    }
    return value;
  }

  public String get(final String key, final String defaultValue) {
    LuaValue value = getLua(key);
    return (value.isnil())? defaultValue : value.tojstring();
  }

  public boolean getBoolean(final String key, final boolean defaultValue) {
    LuaValue value = getLua(key);
    return (value.isnil())? defaultValue : value.toboolean();
  }

  public int getInt(final String key, final int defaultValue) {
    LuaValue value = getLua(key);
    return (value.isnil())? defaultValue : value.toint();
  }

  public long getLong(final String key, final long defaultValue) {
    LuaValue value = getLua(key);
    return (value.isnil())? defaultValue : value.tolong();
  }

  public float getFloat(final String key, final float defaultValue) {
    LuaValue value = getLua(key);
    return (value.isnil())? defaultValue : value.tofloat();
  }

  public double getDouble(final String key, final double defaultValue) {
    LuaValue value = getLua(key);
    return (value.isnil())? defaultValue : value.todouble();
  }

  public Configuration addResource(final String resource) {
    return addResource(Resources.getResource(resource));
  }

  public Configuration addResource(final URL url) {
    try {
      LoadState.load(url.openStream(), new File(url.getPath()).getName(), table).call();
    } catch (IOException e) {
      LOGGER.debug("Unable to load resource {}\n", url, e);
    }
    return this;
  }

  public Configuration addResource(final Class<? extends VarArgFunction> clazz) {
    try {
      VarArgFunction instance = clazz.newInstance();
      instance.setfenv(table);
      instance.onInvoke(null);
    } catch (Throwable e) {
      LOGGER.debug("Unable to load resource {}\n{}", clazz, Throwables.getStackTraceAsString(e));
    }
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(table.toString()).append(" [");
    
    for(LuaValue value : table.keys()) {
      sb.append(value.tojstring()).append(",");
    }
    
    sb.append("]");
    return sb.toString();
  }
}
