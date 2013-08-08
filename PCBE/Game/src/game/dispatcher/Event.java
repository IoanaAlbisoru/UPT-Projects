package game.dispatcher;

public final class Event {
  public final String header;
  public final String[] message;

  public Event(String header, String[] message) {
    this.header = header;
    this.message = message;
  }

  @Override
  public String toString() {
    String temp = "------\n" + this.header + "\n";
    for (String s : this.message)
      temp += s + "\n";
    return temp;
  }
}
