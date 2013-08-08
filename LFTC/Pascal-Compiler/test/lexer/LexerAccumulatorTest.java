package lexer;

import static org.junit.Assert.*;
import lexer.LexerAccumulator;

import org.junit.Test;

public class LexerAccumulatorTest {

  @Test
  public void test() {
    LexerAccumulator buffer = new LexerAccumulator();
    char[] buff = new char[] { 'a', 'a', 'a', 'a' };

    for (char c : buff)
      buffer.append(c);
    assertEquals(buffer.get(), new String(buff));
    assertEquals(buffer.empty(), new String(buff));

    buff = new char[] { 'b', 'b', 'b', 'b' };

    for (char c : buff)
      buffer.append(c);
    assertEquals(buffer.get(), new String(buff));

    buffer.consumeLast();

    assertEquals(buffer.get(), "bbb");
  }

}
