package life.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class FileLogger implements Logger {
  private final ConcurrentLinkedQueue<Event> events = new ConcurrentLinkedQueue<Event>();

  @Override
  public void log(Event event) {
    this.events.add(event);
  }

  public void dump(String filename) {
    try {
      FileWriter fstream = new FileWriter(filename, false);
      BufferedWriter out = new BufferedWriter(fstream);
      Event[] sortedEvents = this.events.toArray(new Event[this.events.size()]);
      Arrays.sort(sortedEvents);

      for (Event iterable_element : sortedEvents)
        out.write(iterable_element.toString() + "\n----\n");
      out.close();
    } catch (Exception e) {// Catch exception if any
      System.err.println("Error: " + e.getMessage());
    }
  }

}
