package proof.client;

import java.io.Serializable;
import java.util.UUID;

import proof.common.HugeObjI;

public class Huge implements HugeObjI, Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -648218677806519054L;
  private final String[] strings;

  public Huge() {
    this.strings = new String[20];
    for (int i = 0; i < 20; i++) {
      this.strings[i] = UUID.randomUUID().toString();
    }
  }

  @Override
  public void something() {
    for (final String s : this.strings) {
      System.out.println(s);
    }
  }

}
