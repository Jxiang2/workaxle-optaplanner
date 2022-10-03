package org.workaxle.util.threadLocal;

import org.workaxle.domain.Settings;

public class SettingsThreadLocal {

  private static final ThreadLocal<Settings> LOCAL = new ThreadLocal<>();

  private SettingsThreadLocal() {
  }

  public static void put(Settings settings) {
    LOCAL.set(settings);
  }

  public static Settings get(Settings settings) {
    return LOCAL.get();
  }

  public static void remove() {
    LOCAL.remove();
  }

}
