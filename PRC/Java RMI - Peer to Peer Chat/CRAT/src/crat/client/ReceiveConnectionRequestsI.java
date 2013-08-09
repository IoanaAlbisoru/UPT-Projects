package crat.client;

import java.io.Serializable;

import crat.common.SendMessagesI;

interface ReceiveConnectionRequestsI {

  public PackagedData getConnectionRequest() throws InterruptedException;

  public class PackagedData implements Serializable {

    private static final long serialVersionUID = 5703170824385269824L;
    public final String name;
    public final SendMessagesI sendingEnd;
    public final ReceiveMessagesI receivingEndI;

    public PackagedData( String name, ReceiveMessagesI receivingEnd,
        SendMessagesI sendingEnd ){
      this.name = name;
      this.sendingEnd = sendingEnd;
      this.receivingEndI = receivingEnd;
    }
  }
}
