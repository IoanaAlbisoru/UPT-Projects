package lexer;

import grammar.Alphabet;

import java.util.Map;

enum States {
  START_STATE,
    END_STATE,
    INCONSISTENT,
    EOF_STATE,

    IDENTIFIER,
    COMMENT {
      @Override
      public States transition(char c, States previousState) {
        if (c == Alphabet.EOF_CHAR)
          return States.INCONSISTENT;

        if (c == '}')
          return States.END_STATE;

        return States.COMMENT;
      }
    },

    STRING_CONST {
      @Override
      public States transition(char c, States previousState) {
        if (c == Alphabet.EOF_CHAR)
          return States.INCONSISTENT;

        if (c == '"')
          return States.END_STATE;

        return States.STRING_CONST;
      }
    },
    CHAR_CONST_START,
    CHAR_CONST_GOT_CHAR,
    NUMBER_START_ZERO_DIGIT,
    NUMBER,
    NUMBER_DOT_ONE,
    NUMBER_DOT_TWO,
    NUMBER_BASE_NOTATION,
    NUMBER_BASE_NOTATION_START_ZERO,
    NUMBER_BASE_NOTATION_NUMBER,
    NUMBER_REAL,
    NUMBER_EXPONENTIAL_NOTATION,
    NUMBER_EXPONENTIAL_NOTATION_WITH_SIGN,
    NUMBER_EXPONENT_START_ZERO_DIGIT,
    NUMBER_EXPONENT,

    SEMICOLON,
    COLON,
    DOT,
    DOUBLEDOT,
    COMMA,

    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    ROUND_BRACKET_OPEN,
    ROUND_BRACKET_CLOSE,
    SQUARE_BRACKET_OPEN,
    SQUARE_BRAKCET_CLOSE,
    EQUAL,
    LESS,
    LESS_OR_EQUAL,
    GREATER,
    GREATER_OR_EQUAL,
    DIFFERENT,
    ASSIGN;

  private Map<Character, States> transitionMap = null;

  private final void lazyInitializationOfMap() {
    if (this.transitionMap == null) {
      this.transitionMap = TransitionMaps.STATE_TO_TRANSITIONS_MAP.get(this);
      if (this.transitionMap == null)
        throw new RuntimeException("Forgot to associate transition map for: " + this);
    }
  }

  public States transition(char c, States previousState) {
    this.lazyInitializationOfMap();
    States state = this.transitionMap.get(c);
    if (state == null)
      return INCONSISTENT;
    return state;
  }

}
