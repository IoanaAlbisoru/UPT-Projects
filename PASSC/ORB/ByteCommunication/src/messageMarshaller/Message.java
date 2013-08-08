package messageMarshaller;

public class Message {
  public String sender;
  public String data;

  public Message(final String theSender, final String rawData) {
    this.sender = theSender;
    this.data = rawData;
  }
}
