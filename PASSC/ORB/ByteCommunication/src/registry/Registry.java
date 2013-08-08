package registry;

import java.util.Hashtable;

public class Registry {
  private final Hashtable<String, Entry> hTable = new Hashtable<String, Entry>();
  private static Registry _instance = null;

  private Registry() {
  }

  public static Registry instance() {
    if (Registry._instance == null) {
      Registry._instance = new Registry();
    }
    return Registry._instance;
  }

  public void put(final String theKey, final Entry theEntry) {
    this.hTable.put(theKey, theEntry);
  }

  public Entry get(final String aKey) {
    return this.hTable.get(aKey);
  }
}
