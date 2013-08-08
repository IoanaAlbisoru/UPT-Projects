package lexer;

import java.util.ArrayList;

class LexerAccumulator {
  private static final int INITIAL_SIZE = 64;

  private final ArrayList<Character> accumulator = new ArrayList<Character>(LexerAccumulator.INITIAL_SIZE);

  public String get() {
    String temp;
    char[] tempBuff = new char[this.accumulator.size()];

    int i = 0;
    for (char c : this.accumulator)
      tempBuff[i++] = c;
    temp = new String(tempBuff);
    return temp;
  }

  public String empty() {
    String temp = this.get();
    this.accumulator.clear();
    return temp;
  }

  public void append(char c) {
    this.accumulator.add(c);
  }

  public void consumeLast() {
    this.accumulator.remove(this.accumulator.size() - 1);
  }
}
