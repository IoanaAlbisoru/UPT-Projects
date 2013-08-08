package lexer;

import grammar.Alphabet;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
class TransitionMaps {

  private static final Map<Character, States> BASE_MAP = Collections.unmodifiableMap(new HashMap<Character, States>() {
    {
      this.put(Alphabet.EOF_CHAR, States.END_STATE);

      for (char c : Alphabet.OTHER_CHARS)
        this.put(c, States.END_STATE);

      for (char c : Alphabet.OPERATOR_CHARACTERS)
        this.put(c, States.END_STATE);

      for (char c : Alphabet.DELIMITER_CHARS)
        this.put(c, States.END_STATE);

      for (char c : Alphabet.ALPHABET)
        this.put(c, States.END_STATE);

      for (char c : Alphabet.WHITE_SPACES)
        this.put(c, States.END_STATE);

    }
  });

  @SuppressWarnings("unused")
  private static final Map<Character, States> TEMPLATE = Collections.unmodifiableMap(new HashMap<Character, States>(
      TransitionMaps.BASE_MAP) {
    {
    }
  });

  private static final Map<Character, States> START_STATE_TRANSITIONS = Collections
      .unmodifiableMap(new HashMap<Character, States>() {
        {
          this.put(Alphabet.EOF_CHAR, States.EOF_STATE);
          // white spaces keep you in the starting state;
          for (char c : Alphabet.WHITE_SPACES)
            this.put(c, States.START_STATE);

          {/* States.IDENTIFIER */
            for (char c : Alphabet.ALPHABET)
              this.put(c, States.IDENTIFIER);

            this.put('_', States.IDENTIFIER);
          }

          {/* States.COMMENT */
            this.put('{', States.COMMENT);
          }

          {/* States.CONST_CHAR */
            this.put('\'', States.CHAR_CONST_START);
          }

          {/* States.CONST_STRING */
            this.put('"', States.STRING_CONST);
          }

          {/* States.NUMBERS */
            this.put('0', States.NUMBER_START_ZERO_DIGIT);
            for (char c = '1'; c <= '9'; c++)
              this.put(c, States.NUMBER);
          }

          {/* States.Delimiters */
            this.put(':', States.COLON);
            this.put('.', States.DOT);
            this.put(';', States.SEMICOLON);
            this.put(',', States.COMMA);
          }

          {/* States.Operators */
            this.put('+', States.PLUS);
            this.put('-', States.MINUS);
            this.put('/', States.DIVIDE);
            this.put('*', States.MULTIPLY);
            this.put('(', States.ROUND_BRACKET_OPEN);
            this.put(')', States.ROUND_BRACKET_CLOSE);
            this.put('[', States.SQUARE_BRACKET_OPEN);
            this.put(']', States.SQUARE_BRAKCET_CLOSE);
            this.put('=', States.EQUAL);
            this.put('<', States.LESS);
            this.put('>', States.GREATER);
          }

        }

      });

  private static final Map<Character, States> CHAR_CONST_START_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>() {
        {
          for (char c : Alphabet.ALL_CHARS)
            this.put(c, States.CHAR_CONST_GOT_CHAR);
        }
      });

  private static final Map<Character, States> CHAR_CONST_GOT_CHAR_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>() {
        {
          this.put('\'', States.END_STATE);
        }
      });

  private static final Map<Character, States> IDENTIFIER_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.BASE_MAP) {
        {
          for (char c : Alphabet.VALID_IDENTIFIER_CHARS)
            this.put(c, States.IDENTIFIER);
        }
      });

  private static final Map<Character, States> NUMBER_BASE_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.BASE_MAP) {
        {
          for (char c : Alphabet.ALPHABET)
            this.put(c, States.END_STATE);
        }
      });

  /**
   * If we are in the NUMBER_START_ZERO_DIGIT state that means that we have
   * started out with a '0' and now we can take only '.' or a terminating char
   */
  private static final Map<Character, States> NUMBER_START_ZERO_DIGIT_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.NUMBER_BASE_TRANSITION_MAP) {
        {
          this.put('.', States.NUMBER_DOT_ONE);
        }
      });

  /**
   * When we get to number start we already have ONE digit parsed.
   */
  private static final Map<Character, States> NUMBER_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.NUMBER_BASE_TRANSITION_MAP) {
        {

          this.put('.', States.NUMBER_DOT_ONE);
          this.put('@', States.NUMBER_BASE_NOTATION);

          for (char c : Alphabet.DIGITS_NUMERAL)
            this.put(c, States.NUMBER);

        }
      });

  private static final Map<Character, States> NUMBER_BASE_NOTATION_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>() {
        {

          for (char c : Alphabet.DIGITS_HEXA)
            this.put(c, States.NUMBER_BASE_NOTATION_NUMBER);

          this.put('0', States.NUMBER_BASE_NOTATION_START_ZERO);
        }
      });

  private static final Map<Character, States> NUMBER_BASE_NOTATION_START_ZERO_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.NUMBER_BASE_TRANSITION_MAP) {
        {
          for (char c : Alphabet.DIGITS_HEXA)
            this.put(c, States.INCONSISTENT);
        }
      });

  private static final Map<Character, States> NUMBER_BASE_NOTATION_NUMBER_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.NUMBER_BASE_TRANSITION_MAP) {
        {
          for (char c : Alphabet.DIGITS_HEXA)
            this.put(c, States.NUMBER_BASE_NOTATION_NUMBER);
        }
      });

  private static final Map<Character, States> NUMBER_DOT_ONE_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>() {
        {
          this.put('.', States.NUMBER_DOT_TWO);

          for (char c : Alphabet.DIGITS_NUMERAL)
            this.put(c, States.NUMBER_REAL);
        }
      });

  private static final Map<Character, States> NUMBER_DOT_TWO_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.NUMBER_BASE_TRANSITION_MAP) {
        {
          for (char c : Alphabet.DIGITS_NUMERAL)
            this.put(c, States.END_STATE);

          for (char c : Alphabet.ALPHABET)
            this.put(c, States.END_STATE);
        }
      });

  /**
   * once in this state we are guaranteed to have at least one digit after the
   * '.' character
   */
  private static final Map<Character, States> NUMBER_REAL_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.NUMBER_BASE_TRANSITION_MAP) {
        {
          for (char c : Alphabet.DIGITS_NUMERAL)
            this.put(c, States.NUMBER_REAL);
          this.put('e', States.NUMBER_EXPONENTIAL_NOTATION);
          this.put('E', States.NUMBER_EXPONENTIAL_NOTATION);
        }
      });

  private static final Map<Character, States> NUMBER_REAL_EXPONENTIAL_NOTATION_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>() {
        {
          this.put('+', States.NUMBER_EXPONENTIAL_NOTATION_WITH_SIGN);
          this.put('-', States.NUMBER_EXPONENTIAL_NOTATION_WITH_SIGN);
        }
      });

  private static final Map<Character, States> NUMBER_EXPONENTIAL_NOTATION_WITH_SIGN_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>() {
        {
          this.put('0', States.NUMBER_EXPONENT_START_ZERO_DIGIT);

          for (char c = '1'; c <= '9'; c++)
            this.put(c, States.NUMBER_EXPONENT);
        }
      });

  private static final Map<Character, States> NUMBER_EXPONENT_START_ZERO_DIGIT_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.BASE_MAP));

  private static final Map<Character, States> NUMBER_EXPONENT_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.BASE_MAP) {
        {
          for (char c : Alphabet.DIGITS_NUMERAL)
            this.put(c, States.NUMBER_EXPONENT);
        }
      });

  private static final Map<Character, States> OPERATOR_BASE_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.BASE_MAP) {
        {
          for (char c : Alphabet.DIGITS_NUMERAL)
            this.put(c, States.END_STATE);

          for (char c : Alphabet.ALPHABET)
            this.put(c, States.END_STATE);

          this.put('"', States.END_STATE);
          this.put('\'', States.END_STATE);
        }
      });

  private static final Map<Character, States> PLUS_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP));

  private static final Map<Character, States> MINUS_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP));

  private static final Map<Character, States> MULTIPLY_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP));

  private static final Map<Character, States> DIVIDE_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP));

  private static final Map<Character, States> ROUND_BRACKET_OPEN_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP));

  private static final Map<Character, States> ROUND_BRACKET_CLOSE_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP));

  private static final Map<Character, States> SQUARE_BRACKET_OPEN_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP));

  private static final Map<Character, States> SQUARE_BRACKET_CLOSE_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP));

  private static final Map<Character, States> EQUAL_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP));

  private static final Map<Character, States> LESS_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP) {
        {
          this.put('>', States.DIFFERENT);
          this.put('=', States.LESS_OR_EQUAL);
        }
      });

  private static final Map<Character, States> LESS_OR_EQUAL_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP));

  private static final Map<Character, States> GREATER_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP) {
        {
          this.put('=', States.GREATER_OR_EQUAL);
        }
      });

  private static final Map<Character, States> GREATER_OR_EQUAL_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP));

  private static final Map<Character, States> DIFFERENT_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP));

  private static final Map<Character, States> ASSIGN_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP));

  private static final Map<Character, States> SEMICOLON_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP));

  private static final Map<Character, States> COLON_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP) {
        {
          this.put('=', States.ASSIGN);
        }
      });

  private static final Map<Character, States> DOT_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP) {
        {
          this.put('.', States.DOUBLEDOT);
        }
      });

  private static final Map<Character, States> DOUBLEDOT_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP));

  private static final Map<Character, States> COMMA_TRANSITION_MAP = Collections
      .unmodifiableMap(new HashMap<Character, States>(TransitionMaps.OPERATOR_BASE_TRANSITION_MAP));

  public static final Map<States, Map<Character, States>> STATE_TO_TRANSITIONS_MAP = Collections
      .unmodifiableMap(new EnumMap<States, Map<Character, States>>(States.class) {
        {
          this.put(States.IDENTIFIER, TransitionMaps.IDENTIFIER_TRANSITION_MAP);

          this.put(States.CHAR_CONST_START, TransitionMaps.CHAR_CONST_START_TRANSITION_MAP);
          this.put(States.CHAR_CONST_GOT_CHAR, TransitionMaps.CHAR_CONST_GOT_CHAR_TRANSITION_MAP);

          this.put(States.NUMBER_START_ZERO_DIGIT, TransitionMaps.NUMBER_START_ZERO_DIGIT_TRANSITION_MAP);
          this.put(States.NUMBER, TransitionMaps.NUMBER_TRANSITION_MAP);
          this.put(States.NUMBER_BASE_NOTATION, TransitionMaps.NUMBER_BASE_NOTATION_TRANSITION_MAP);
          this.put(States.NUMBER_BASE_NOTATION_START_ZERO,
              TransitionMaps.NUMBER_BASE_NOTATION_START_ZERO_TRANSITION_MAP);
          this.put(States.NUMBER_BASE_NOTATION_NUMBER, TransitionMaps.NUMBER_BASE_NOTATION_NUMBER_TRANSITION_MAP);
          this.put(States.NUMBER_DOT_ONE, TransitionMaps.NUMBER_DOT_ONE_TRANSITION_MAP);
          this.put(States.NUMBER_DOT_TWO, TransitionMaps.NUMBER_DOT_TWO_TRANSITION_MAP);

          this.put(States.NUMBER_REAL, TransitionMaps.NUMBER_REAL_TRANSITION_MAP);
          this.put(States.NUMBER_EXPONENTIAL_NOTATION, TransitionMaps.NUMBER_REAL_EXPONENTIAL_NOTATION_TRANSITION_MAP);
          this.put(States.NUMBER_EXPONENTIAL_NOTATION_WITH_SIGN,
              TransitionMaps.NUMBER_EXPONENTIAL_NOTATION_WITH_SIGN_TRANSITION_MAP);
          this.put(States.NUMBER_EXPONENT_START_ZERO_DIGIT,
              TransitionMaps.NUMBER_EXPONENT_START_ZERO_DIGIT_TRANSITION_MAP);
          this.put(States.NUMBER_EXPONENT, TransitionMaps.NUMBER_EXPONENT_TRANSITION_MAP);

          this.put(States.START_STATE, TransitionMaps.START_STATE_TRANSITIONS);

          this.put(States.PLUS, TransitionMaps.PLUS_TRANSITION_MAP);
          this.put(States.MINUS, TransitionMaps.MINUS_TRANSITION_MAP);
          this.put(States.MULTIPLY, TransitionMaps.MULTIPLY_TRANSITION_MAP);
          this.put(States.DIVIDE, TransitionMaps.DIVIDE_TRANSITION_MAP);
          this.put(States.ROUND_BRACKET_OPEN, TransitionMaps.ROUND_BRACKET_OPEN_TRANSITION_MAP);
          this.put(States.ROUND_BRACKET_CLOSE, TransitionMaps.ROUND_BRACKET_CLOSE_TRANSITION_MAP);
          this.put(States.SQUARE_BRACKET_OPEN, TransitionMaps.SQUARE_BRACKET_OPEN_TRANSITION_MAP);
          this.put(States.SQUARE_BRAKCET_CLOSE, TransitionMaps.SQUARE_BRACKET_CLOSE_TRANSITION_MAP);

          this.put(States.LESS, TransitionMaps.LESS_TRANSITION_MAP);
          this.put(States.LESS_OR_EQUAL, TransitionMaps.LESS_OR_EQUAL_TRANSITION_MAP);
          this.put(States.GREATER, TransitionMaps.GREATER_TRANSITION_MAP);
          this.put(States.GREATER_OR_EQUAL, TransitionMaps.GREATER_OR_EQUAL_TRANSITION_MAP);
          this.put(States.EQUAL, TransitionMaps.EQUAL_TRANSITION_MAP);
          this.put(States.DIFFERENT, TransitionMaps.DIFFERENT_TRANSITION_MAP);
          this.put(States.ASSIGN, TransitionMaps.ASSIGN_TRANSITION_MAP);

          this.put(States.SEMICOLON, TransitionMaps.SEMICOLON_TRANSITION_MAP);
          this.put(States.COLON, TransitionMaps.COLON_TRANSITION_MAP);
          this.put(States.COMMA, TransitionMaps.COMMA_TRANSITION_MAP);
          this.put(States.DOT, TransitionMaps.DOT_TRANSITION_MAP);
          this.put(States.DOUBLEDOT, TransitionMaps.DOUBLEDOT_TRANSITION_MAP);

          // these don't really need any TransitionMaps assigned.
          this.put(States.END_STATE, TransitionMaps.BASE_MAP);
          this.put(States.EOF_STATE, TransitionMaps.BASE_MAP);
          this.put(States.INCONSISTENT, TransitionMaps.BASE_MAP);

        }
      });

}