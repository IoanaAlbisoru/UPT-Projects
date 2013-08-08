package life.tools;

public abstract class Event implements Comparable<Event> {
  public final String message;
  private final long time;
  private static final long initialTime = System.nanoTime();

  protected Event(String message) {
    this.message = message;
    this.time = System.nanoTime();
  }

  protected long getTime() {
    return this.time;
  }

  @Override
  public String toString() {
    long tempTime = this.time - Event.initialTime;
    return "Time=" + tempTime + ":\n" + "Event description: " + this.message + "\n";
  }

  @Override
  public int compareTo(Event o) {
    if (o.time == this.time)
      return 0;

    if (o.time < this.time)
      return 1;

    return -1;
  }
}
