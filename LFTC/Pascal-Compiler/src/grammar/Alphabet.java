package grammar;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class Alphabet {
  /**
   * This is somewhat of a hack. Java doesn't have an EOF character, so I just
   * use this one which is the unicode value of the Greek letter for 'pi'. It is
   * very useful to have an EOF character for the transition maps
   */
  public static final char EOF_CHAR = 0x03C0;

  /**
   * The system's text line separator;
   */
  public static final String TLS = System.getProperty("line.separator");

  public static final Set<String> KEY_WORDS = Collections.unmodifiableSet(new HashSet<String>() {
    String[] arr = new String[] { "and", "begin", "case", "char", "const", "div", "do", "downto", "else", "end", "for",
        "function", "if", "integer", "mod", "not", "of", "or", "procedure", "program", "real", "repeat", "record",
        "then", "to", "until", "var", "while" };
    {
      for (String string : this.arr)
        this.add(string);
    }
  });

  public static final Set<Character> DIGITS_NUMERAL = Collections.unmodifiableSet(new HashSet<Character>() {
    {
      for (char c = '0'; c <= '9'; c++)
        this.add(c);
    }
  });

  public static final Set<Character> DIGITS_HEXA = Collections.unmodifiableSet(new HashSet<Character>(
      Alphabet.DIGITS_NUMERAL) {
    {
      for (char c = 'a'; c <= 'f'; c++)
        this.add(c);
      for (char c = 'A'; c <= 'F'; c++)
        this.add(c);
    }
  });

  public static final Set<Character> WHITE_SPACES = Collections.unmodifiableSet(new HashSet<Character>() {
    {
      this.add(' ');
      this.add('\r');
      this.add('\n');
      this.add('\t');
    }
  });

  public static final Set<Character> ALPHABET = Collections.unmodifiableSet(new HashSet<Character>() {
    {
      for (char c = 'A'; c <= 'Z'; c++)
        this.add(c);
      for (char c = 'a'; c <= 'z'; c++)
        this.add(c);
    }
  });

  public static final Set<Character> VALID_IDENTIFIER_CHARS = Collections.unmodifiableSet(new HashSet<Character>(
      Alphabet.ALPHABET) {
    {
      this.add('_');
      for (char c = '0'; c <= '9'; c++)
        this.add(c);
    }
  });

  public static final Set<Character> OPERATOR_CHARACTERS = Collections.unmodifiableSet(new HashSet<Character>() {
    {
      this.add('*');
      this.add('-');
      this.add('=');
      this.add('+');
      this.add('<');
      this.add('>');
      this.add('/');
    }
  });

  public static final Set<Character> DELIMITER_CHARS = Collections.unmodifiableSet(new HashSet<Character>() {
    {
      this.add('.');
      this.add(',');
      this.add(';');
      this.add(':');
      this.add('[');
      this.add(']');
      this.add('(');
      this.add(')');
      this.add('{');
      this.add('}');
    }
  });

  public static final Set<Character> OTHER_CHARS = Collections.unmodifiableSet(new HashSet<Character>() {
    {
      this.add('~');
      this.add('`');
      this.add('!');
      this.add('@');
      this.add('#');
      this.add('$');
      this.add('%');
      this.add('^');
      this.add('&');
    }
  });

  public static final Set<Character> ALL_CHARS = Collections.unmodifiableSet(new HashSet<Character>() {
    {
      for (char c : Alphabet.ALPHABET)
        this.add(c);

      for (char c : Alphabet.DIGITS_NUMERAL)
        this.add(c);

      for (char c : Alphabet.DELIMITER_CHARS)
        this.add(c);

      for (char c : Alphabet.OPERATOR_CHARACTERS)
        this.add(c);

      for (char c : Alphabet.WHITE_SPACES)
        this.add(c);

      for (char c : Alphabet.OTHER_CHARS)
        this.add(c);
    }
  });

}
