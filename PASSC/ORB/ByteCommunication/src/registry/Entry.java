package registry;

import java.io.Serializable;

import commons.Address;

public class Entry implements Address, Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 2267326954006747749L;
  private final String destinationId;
  private final int portNr;

  public Entry(final String theDest, final int thePort) {
    this.destinationId = theDest;
    this.portNr = thePort;
  }

  @Override
  public String dest() {
    return this.destinationId;
  }

  @Override
  public int port() {
    return this.portNr;
  }
}
