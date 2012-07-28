package config;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import config.commonsconfig.CommonsConfiguration;
import config.lua.LuaConfiguration;
import config.properties.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Strings.padEnd;
import static com.google.common.base.Strings.padStart;

public class SpeedTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpeedTest.class.getName());
  private static final int REPS = Integer.parseInt(System.getProperty("reps", "1000"));
  private static final TimeUnit unit = TimeUnit.valueOf(System.getProperty("unit", TimeUnit.MILLISECONDS.toString()));
  
  private final Map<String, Long> initTimes = Maps.newTreeMap();
  private final Map<String, Long> computeTimes = Maps.newTreeMap();

  @Test(dataProvider = "run")
  public void run(Configuration conf, Object resource, String type, int rep) {

    Stopwatch configStopwatch = new Stopwatch().start();
    if(resource instanceof String) {
      conf.addResource((String) resource);
    } else if(resource instanceof Class) {
      ((LuaConfiguration) conf).addResource((Class) resource);
    }
    configStopwatch.stop();

    // make sure everything applied properly
    Assert.assertEquals(conf.get("foo", null), "bar", conf.toString());

    Stopwatch stopwatch = new Stopwatch().start();
    for(int i = 0; i < rep; i++) {
      conf.get("key", "value");
      conf.getBoolean("key", true);
      conf.getDouble("key", 0.0);
      conf.getFloat("key", 0.0f);
      conf.getInt("key", 0);
      conf.getLong("key", 0l);
    }
    stopwatch.stop();
//    LOGGER.info("{} test loaded in {}, ran in {}", new Object[] {
//        Strings.padEnd(type, 30, '-'),
//        padStart(Long.toString(configStopwatch.elapsedTime(unit)), 10, ' '),
//        padStart(Long.toString(stopwatch.elapsedTime(unit)), 10, ' ')});

    initTimes.put(type, configStopwatch.elapsedTime(unit));
    computeTimes.put(type, stopwatch.elapsedTime(unit));
  }
  
  @AfterClass(alwaysRun = true)
  public void stats() {
    int maxSize = Collections.max(initTimes.keySet(), Ordering.natural().onResultOf(new Function<String, Comparable>() {
      public Comparable apply(@Nullable final String input) {
        return input.length();
      }
    })).length();

    ImmutableList<String> initOrdered = Ordering.natural().onResultOf(Functions.forMap(initTimes)).immutableSortedCopy(initTimes.keySet());
    StringBuilder sb = new StringBuilder("Configuration Init Time\n");
    for(String key : initOrdered) {
      sb.append(padEnd(key, maxSize, '-')).append(padStart(Long.toString(initTimes.get(key)), 10, ' ')).append("\n");
    }
    LOGGER.info(sb.toString());
    
    ImmutableList<String> computeOrdered = Ordering.natural().onResultOf(Functions.forMap(computeTimes)).immutableSortedCopy(computeTimes.keySet());
    sb = new StringBuilder("Compute Time (reps ").append(REPS).append(")\n");
    for(String key : computeOrdered) {
      sb.append(padEnd(key, maxSize, '-')).append(padStart(Long.toString(computeTimes.get(key)), 10, ' ')).append("\n");
    }
    LOGGER.info(sb.toString());
  }

  @DataProvider(name = "run")
  public static Object[][] getRun() throws ClassNotFoundException {
    return new Object[][] {
        new Object[] {new LuaConfiguration(), "simple.lua", "Lua Interpret", REPS},
        new Object[] {new LuaConfiguration(), "WithFunctions.lua", "Lua Interpret with Functions", REPS},

        new Object[] {new LuaConfiguration(), "luac.out", "Lua Precompiled Bytecode", REPS},

        new Object[] {new LuaConfiguration(), Class.forName("simple"), "Lua Precompiled Java", REPS},
        new Object[] {new LuaConfiguration(true), Class.forName("simple"), "Lua Precompiled Java With Compiler", REPS},
        new Object[] {new LuaConfiguration(), Class.forName("WithFunctions"), "Lua Precompiled with Functions", REPS},
        new Object[] {new LuaConfiguration(true), Class.forName("WithFunctions"), "Lua Precompiled with Functions with Compiler", REPS},

        new Object[] {new LuaConfiguration(true), "simple.lua", "Lua Compile Java", REPS},
        new Object[] {new LuaConfiguration(true), "WithFunctions.lua", "Lua Compile Java with Functions", REPS},


        new Object[] {new PropertiesConfiguration(), "simple.properties", "Java Properties", REPS},


        new Object[] {new CommonsConfiguration(), "simple.properties", "Commons Config Properties", REPS},
        new Object[] {new CommonsConfiguration(), "simple.xml", "Commons Config XML", REPS},
        new Object[] {new CommonsConfiguration(), "simple.system", "Commons Config System", REPS},
    };
  }
}
