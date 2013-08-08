package lexer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import grammar.Alphabet;
import grammar.Token;
import grammar.TokenType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import testutils.TestUtil;

@RunWith(value = Parameterized.class)
public class LexerTest {

  private static final Class<?> TARGET_CLASS = Lexer.class;
  private final String fileName;
  private final TokenType[] tokenTypes;
  private final String[] values;
  private final PositionInFile[] positions;

  public LexerTest(String fileName, TokenType[] tokenTypes, String[] values, PositionInFile[] positions) {
    this.fileName = fileName;
    this.tokenTypes = tokenTypes;
    this.values = values;
    this.positions = positions;
  }

  private Lexer lexer;

  @Before
  public void setUp() throws Exception {
    String path = TestUtil.constructPathFromClass(LexerTest.TARGET_CLASS) + "/" + this.fileName;
    this.lexer = new Lexer(path);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testCompare() {
    String messageString = "-- Test name: " + this.fileName + " --";

    for (int i = 0; i < this.tokenTypes.length; i++) {
      Token nextToken = this.lexer.nextToken();

      assertEquals(messageString, this.tokenTypes[i], nextToken.type);

      if (this.values != null && this.values[i] != null)
        assertEquals(this.fileName, this.values[i], nextToken.content);

      if (this.positions != null && this.positions[i] != null) {
        assertEquals(messageString, this.positions[i].line, nextToken.line);
        assertEquals(messageString, this.positions[i].column, nextToken.column);
      }
    }

    Token lastToken = this.lexer.nextToken();
    assertEquals(this.fileName, TokenType.EOF, lastToken.type);
  }

  @SuppressWarnings("rawtypes")
  @Parameterized.Parameters
  public static Collection testData() {
    final FactoryPiF pif = PositionInFile.getFactory();
    String fileName;
    TokenType[] tokenTypes;
    String[] values;
    PositionInFile[] positions;
    List<Object[]> testValues = new ArrayList<>(100);

    {/*
      * =====================================
      * 
      * @note comment01
      * 
      * =====================================
      */
      fileName = "comment01";
      tokenTypes = new TokenType[] { TokenType.COMMENT, TokenType.ERROR };
      values = new String[] { "{comment}", null };
      positions = new PositionInFile[] { pif.n(1, 1), pif.n(1, 10) };
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note comment02MultiLine
      * 
      * =====================================
      */
      fileName = "comment02MultiLine";
      tokenTypes = new TokenType[] { TokenType.COMMENT };
      values = new String[] { "{line1" + Alphabet.TLS + "line2" + Alphabet.TLS + Alphabet.TLS + "line4}" };
      positions = null;
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note constCharString
      * 
      * =====================================
      */
      fileName = "constCharString";
      tokenTypes = new TokenType[] { TokenType.CHAR_CONST, TokenType.STRING_CONST, TokenType.ERROR, TokenType.ERROR };
      values = new String[] { "a", "aa", null, null };
      positions = null;
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note constString
      * 
      * =====================================
      */
      fileName = "constString";
      tokenTypes = new TokenType[] { TokenType.COMMA, TokenType.STRING_CONST, TokenType.ROUND_BRACKET_CLOSE };
      values = new String[] { ",", "aaa", ")" };
      positions = null;
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note delimiters01
      * 
      * =====================================
      */
      fileName = "delimiters01";
      tokenTypes = new TokenType[] { TokenType.DOUBLEDOT, TokenType.DOT, TokenType.COMMA, TokenType.SEMICOLON,
          TokenType.COLON };
      values = new String[] { "..", ".", ",", ";", ":" };
      positions = new PositionInFile[] { pif.n(1, 1), pif.n(1, 3), pif.n(1, 4), pif.n(1, 5), pif.n(1, 6) };
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note empty
      * 
      * =====================================
      */
      fileName = "empty";
      tokenTypes = new TokenType[] { TokenType.EOF };
      values = new String[] { null };
      positions = new PositionInFile[] { pif.n(1, 1) };
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note error
      * 
      * =====================================
      */
      fileName = "error";
      tokenTypes = new TokenType[] { TokenType.ERROR, TokenType.ERROR };
      values = null;
      positions = null;
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note identifier01
      * 
      * =====================================
      */
      fileName = "identifier01";
      tokenTypes = new TokenType[] { TokenType.IDENTIFIER };
      values = new String[] { "identifier01" };
      positions = new PositionInFile[] { pif.n(1, 1) };
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note identifier02
      * 
      * =====================================
      */
      fileName = "identifier02";
      tokenTypes = new TokenType[] { TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.IDENTIFIER,
          TokenType.IDENTIFIER, TokenType.IDENTIFIER };
      values = new String[] { "identifier01", "identifier02", "identifier_03", "_i4", "forA" };
      positions = new PositionInFile[] { pif.n(1, 1), pif.n(1, 14), pif.n(2, 1), pif.n(3, 1), null };
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note identifier03
      * 
      * =====================================
      */
      fileName = "identifier03";
      tokenTypes = new TokenType[] { TokenType.IDENTIFIER, TokenType.ERROR, TokenType.IDENTIFIER };
      values = new String[] { "identi", null, "fier03" };
      positions = new PositionInFile[] { pif.n(1, 1), pif.n(1, 7), pif.n(1, 8) };
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note identifier04
      * 
      * =====================================
      */
      fileName = "identifier04";
      tokenTypes = new TokenType[] { TokenType.IDENTIFIER, TokenType.COMMENT, TokenType.IDENTIFIER, TokenType.COMMENT };
      values = new String[] { "a", "{c}", "b", "{c}" };
      positions = new PositionInFile[] { pif.n(1, 1), pif.n(1, 2), pif.n(1, 5), pif.n(1, 6) };
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note keywords
      * 
      * =====================================
      */
      fileName = "keywords";
      Object[] array = EnumSet.range(TokenType.AND_KW, TokenType.READ_KW).toArray();
      tokenTypes = new TokenType[array.length];
      for (int i = 0; i < array.length; i++)
        tokenTypes[i] = (TokenType) array[i];

      values = new String[] { "and", "array", "begin", "case", "char", "const", "div", "do", "downto", "else", "end",
          "for", "function", "if", "integer", "mod", "not", "of", "or", "procedure", "program", "real", "repeat",
          "record", "then", "to", "until", "var", "while", "print", "read" };
      positions = null;
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note number01SingleDigits
      * 
      * =====================================
      */
      fileName = "number01SingleDigits";
      tokenTypes = new TokenType[10];
      values = new String[10];
      positions = new PositionInFile[10];
      int tempCounter = 1;
      for (int i = 0; i < tokenTypes.length; i++) {
        tokenTypes[i] = TokenType.NUMBER_INTEGER;
        values[i] = String.valueOf(i);
        positions[i] = pif.n(1, tempCounter);
        tempCounter += 2;
      }
      positions[0] = pif.n(1, 1);
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note number012ingleDigitInvalid
      * 
      * =====================================
      */
      fileName = "number02SingleDigitInvalid";
      tokenTypes = new TokenType[] { TokenType.ERROR, TokenType.NUMBER_INTEGER, TokenType.NUMBER_INTEGER };
      values = new String[] { null, "12", "3" };
      positions = new PositionInFile[] { pif.n(1, 1), pif.n(1, 2), pif.n(1, 5) };
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note number03
      * 
      * =====================================
      */
      fileName = "number03";
      tokenTypes = new TokenType[] { TokenType.NUMBER_INTEGER, TokenType.NUMBER_INTEGER };
      values = new String[] { "1234", "567" };
      positions = new PositionInFile[] { pif.n(1, 1), pif.n(1, 6) };
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note number04Real
      * 
      * =====================================
      */
      fileName = "number04Real";
      tokenTypes = new TokenType[] { TokenType.NUMBER_REAL };
      values = new String[] { "1234.567" };
      positions = new PositionInFile[] { pif.n(1, 1) };
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note number05DoubleDot
      * 
      * =====================================
      */
      fileName = "number05DoubleDot";
      tokenTypes = new TokenType[] { TokenType.NUMBER_INTEGER, TokenType.DOUBLEDOT, TokenType.NUMBER_INTEGER };
      values = new String[] { "1", "..", "5" };
      positions = new PositionInFile[] { pif.n(1, 1), pif.n(1, 2), pif.n(1, 4) };
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note number06Exponent
      * 
      * =====================================
      */
      fileName = "number06Exponent";
      tokenTypes = new TokenType[] { TokenType.NUMBER_REAL, TokenType.NUMBER_REAL, TokenType.NUMBER_REAL,
          TokenType.NUMBER_REAL };
      values = new String[] { "1234.5e+10", "1234.5e-10", "1234.5E+10", "1234.5E-10" };
      positions = null;
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note number07Exponent
      * 
      * =====================================
      */
      fileName = "number07ExponentError";
      tokenTypes = new TokenType[] { TokenType.ERROR, TokenType.ERROR };
      values = null;/* new String[] { "1.5e", "1." }; */
      positions = null;
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note number08Base
      * 
      * =====================================
      */
      fileName = "number08Base";
      tokenTypes = new TokenType[] { TokenType.NUMBER_INTEGER, TokenType.NUMBER_INTEGER };
      values = new String[] { "26", "1" };
      positions = null;
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note number09BaseError
      * 
      * =====================================
      */
      fileName = "number09BaseError";
      tokenTypes = new TokenType[] { TokenType.ERROR, TokenType.ERROR, TokenType.NUMBER_INTEGER };
      values = new String[] { null, null, "0" };
      positions = null;
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note operators01
      * 
      * =====================================
      */
      fileName = "operators01";
      tokenTypes = new TokenType[] { TokenType.PLUS, TokenType.MINUS, TokenType.DIVIDE, TokenType.MULTIPLY,
          TokenType.ROUND_BRACKET_OPEN, TokenType.ROUND_BRACKET_CLOSE, TokenType.SQUARE_BRACKET_OPEN,
          TokenType.SQUARE_BRACKET_CLOSE, TokenType.EQUAL, TokenType.ASSIGN, TokenType.DIFFERENT, TokenType.GREATER,
          TokenType.LESS, TokenType.LESS_OR_EQUAL, TokenType.GREATER_OR_EQUAL, TokenType.LESS_OR_EQUAL, TokenType.EQUAL };
      values = new String[] { "+", "-", "/", "*", "(", ")", "[", "]", "=", ":=", "<>", ">", "<", "<=", ">=", "<=", "=" };
      positions = null;
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note complex01
      * 
      * =====================================
      */
      fileName = "complex01";
      tokenTypes = new TokenType[] { TokenType.FOR_KW, TokenType.IDENTIFIER, TokenType.ASSIGN,
          TokenType.NUMBER_INTEGER, TokenType.TO_KW, TokenType.NUMBER_INTEGER, TokenType.DO_KW };
      values = new String[] { "for", "i", ":=", "1", "to", "33", "do" };
      positions = null;
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    {/*
      * =====================================
      * 
      * @note complex02
      * 
      * =====================================
      */
      fileName = "complex02";
      tokenTypes = new TokenType[] { TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.IDENTIFIER,
          TokenType.NUMBER_INTEGER, TokenType.NUMBER_INTEGER, TokenType.NUMBER_INTEGER, TokenType.DOUBLEDOT,
          TokenType.DOT, TokenType.NUMBER_INTEGER, TokenType.IDENTIFIER, TokenType.END_KW, TokenType.IDENTIFIER,
          TokenType.IDENTIFIER, TokenType.NUMBER_INTEGER, TokenType.IDENTIFIER, TokenType.IDENTIFIER, TokenType.IF_KW,
          TokenType.IDENTIFIER, TokenType.NUMBER_INTEGER };
      values = new String[] { "test", "de", "test", "38", "4094", "1", "..", ".", "1", "beg3begin", "end", "endif",
          "alfa", "99", "de", "teste", "if", "b3gin", "10" };
      positions = new PositionInFile[] { pif.n(1, 1), pif.n(1, 6), pif.n(1, 9), pif.n(2, 1), pif.n(3, 1), pif.n(4, 1),
          pif.n(4, 2), pif.n(4, 4), pif.n(4, 5), pif.n(5, 1), pif.n(6, 1), pif.n(6, 5), pif.n(7, 5), pif.n(8, 1),
          pif.n(8, 4), pif.n(8, 7), pif.n(8, 13), pif.n(9, 1), pif.n(10, 1) };
      testValues.add(new Object[] { fileName, tokenTypes, values, positions });
    }

    return testValues;
  }

  private static final class PositionInFile {
    public final int line;
    public final int column;

    public PositionInFile(int line, int column) {
      this.line = line;
      this.column = column;
    }

    public static FactoryPiF getFactory() {
      return new FactoryPiF() {

        @Override
        public PositionInFile n(int l, int c) {
          return new PositionInFile(l, c);
        }
      };
    }

  }

  private static interface FactoryPiF {
    PositionInFile n(int l, int c);
  }
}
