package lexer;

import static org.junit.Assert.*;

import grammar.Alphabet;

import java.io.EOFException;

import lexer.LexerBufferedReader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import testutils.TestUtil;

public class LexerBufferedReaderTest {

  @Rule
  public TestName name = new TestName();

  private static final Class<?> TARGET_CLASS = LexerBufferedReader.class;
  private LexerBufferedReader reader;

  @Before
  public void setUp() throws Exception {
    String path = TestUtil.constructPathFromClass(LexerBufferedReaderTest.TARGET_CLASS) + "/"
        + this.name.getMethodName();
    this.reader = new LexerBufferedReader(path);
  }

  @Test
  public void test() {
    assertEquals('a', this.reader.consume());
    assertEquals('a', this.reader.consume());
    assertEquals('a', this.reader.consume());
    char nextChar = this.reader.consume();
    assertEquals(Alphabet.EOF_CHAR, nextChar);

  }

  @Test
  public void testSimpleRead() {
    assertEquals('a', this.reader.consume());
    assertEquals('a', this.reader.consume());
    assertEquals('a', this.reader.consume());
    assertEquals(Alphabet.EOF_CHAR, this.reader.consume());

  }

  @Test
  public void testSimpleReadLookAhead() {
    assertEquals('a', this.reader.consume());
    assertEquals('b', this.reader.nextChar());
    assertEquals('b', this.reader.consume());
    assertEquals('a', this.reader.consume());
    assertEquals(Alphabet.EOF_CHAR, this.reader.nextChar());
    assertEquals(Alphabet.EOF_CHAR, this.reader.consume());
  }

  @Test
  public void testBufferReReading() {
    // this file should contain 1024 'a' and 3 'b';
    for (int i = 0; i < 1024; i++)
      assertEquals('a', this.reader.consume());

    assertEquals('b', this.reader.consume());
    assertEquals('b', this.reader.consume());
    assertEquals('b', this.reader.consume());
    assertEquals(Alphabet.EOF_CHAR, this.reader.consume());
  }

  @Test
  public void testBufferReReadingThenLookAhead() throws EOFException {
    // this file should contain 1024 'a' and 3 'b';
    for (int i = 0; i < 1024; i++)
      assertEquals('a', this.reader.consume());

    // first we look ahead after we've exhausted the buffer, only then do we
    // continue;
    assertEquals('b', this.reader.nextChar());

    assertEquals('b', this.reader.consume());
    assertEquals('b', this.reader.consume());
    assertEquals('b', this.reader.consume());
    assertEquals(Alphabet.EOF_CHAR, this.reader.consume());
  }

  @Test
  public void testSimpleMultipleLine() throws EOFException {
    assertEquals('a', this.reader.consume());
    assertEquals(2, this.reader.getCurrentColumn());
    assertEquals('a', this.reader.consume());
    assertEquals('a', this.reader.consume());
    assertEquals(' ', this.reader.consume());
    assertEquals(5, this.reader.getCurrentColumn());
    assertEquals(1, this.reader.getCurrentLine());

    assertEquals('\r', this.reader.consume());
    assertEquals('\n', this.reader.consume());
    assertEquals('\r', this.reader.consume());
    assertEquals('\n', this.reader.consume());

    assertEquals('a', this.reader.consume());
    assertEquals(3, this.reader.getCurrentLine());
    assertEquals(2, this.reader.getCurrentColumn());

    assertEquals('\r', this.reader.consume());
    assertEquals('\n', this.reader.consume());
    assertEquals('\r', this.reader.consume());
    assertEquals('\n', this.reader.consume());

    assertEquals('a', this.reader.consume());
    assertEquals(5, this.reader.getCurrentLine());

    assertEquals(Alphabet.EOF_CHAR, this.reader.consume());
  }

  @Test
  public void testSimpleLookAhead() throws EOFException {
    assertEquals('a', this.reader.nextChar());
  }

}
