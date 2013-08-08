package lexer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import grammar.Alphabet;
import grammar.IntegerToken;
import grammar.RealToken;
import grammar.Token;
import grammar.TokenType;

class FiniteStateMachine {
  private States currentState;
  private States previousState;
  private final LexerAccumulator accumulator;

  public FiniteStateMachine(States initialState) {
    this.currentState = initialState;
    this.previousState = initialState;
    this.accumulator = new LexerAccumulator();
  }

  public States transition(char c) {
    this.previousState = this.currentState;
    this.currentState = this.currentState.transition(c, this.previousState);

    // if we are in the case 12.. it means we have to backtrack, remove from
    // '12.' the dot and set it in the proper state to create the appropriate
    // token, i.e. number
    if (this.currentState == States.NUMBER_DOT_TWO) {
      this.previousState = States.NUMBER;
      this.accumulator.consumeLast();
      return this.currentState;
    }

    this.accumulate(c);

    return this.currentState;
  }

  private void accumulate(char c) {
    if (this.currentState == States.START_STATE)
      return;

    if (this.currentState == States.END_STATE && this.previousState != States.COMMENT)
      return;

    if (this.currentState == States.CHAR_CONST_START)
      return;

    if (this.currentState == States.STRING_CONST && c == '"')
      return;

    if (c != Alphabet.EOF_CHAR)
      this.accumulator.append(c);
  }

  public final Token createToken(int line, int column) {
    String string = this.accumulator.empty();
    TokenType tokenType = null;

    // if the finite state machine is in an inconsistent state then we create an
    // error token
    if (this.currentState == States.INCONSISTENT) {
      String errorMessage = FiniteStateMachine.STATE_TO_ERROR_MAP.get(this.previousState);
      if (errorMessage != null)
        return new Token(TokenType.ERROR, errorMessage + string, line, column);
      else
        return new Token(TokenType.ERROR, "Unkown lexer error:" + string, line, column);
    }

    // if it is not we check to see what Token Type we have to create for the
    // given state
    tokenType = FiniteStateMachine.STATE_TO_TOKEN_MAP.get(this.previousState);

    if (this.previousState == States.IDENTIFIER) {
      TokenType supposedKeyword = TokenType.KEYWORD_TO_TOKEN_MAP.get(string);
      if (supposedKeyword != null)
        tokenType = supposedKeyword;
    }

    if (tokenType == null)
      throw new RuntimeException("Forgot to add token for current state: " + this.previousState);

    return this.buildToken(line, column, string, tokenType);
  }

  private Token buildToken(int line, int column, String string, TokenType tokenType) {
    Token tempToken = new Token(tokenType, string, line, column);

    try {
      if (this.previousState == States.NUMBER_START_ZERO_DIGIT || this.previousState == States.NUMBER)
        return new IntegerToken(tempToken);

      if (this.previousState == States.NUMBER_BASE_NOTATION_NUMBER
          || this.previousState == States.NUMBER_BASE_NOTATION_START_ZERO) {

        String[] split = tempToken.content.split("@");
        int baseValue = Integer.parseInt(split[0], 10);
        int integerValue = Integer.parseInt(split[1], baseValue);

        return new IntegerToken(new Token(tokenType, String.valueOf(integerValue), line, column));
      }

      if (this.previousState == States.NUMBER_REAL || this.previousState == States.NUMBER_EXPONENT
          || this.previousState == States.NUMBER_EXPONENT_START_ZERO_DIGIT)
        return new RealToken(tempToken);
    } catch (NumberFormatException e) {
      return new Token(TokenType.ERROR, "Invalid number format", line, column);
    }

    return tempToken;
  }

  @SuppressWarnings("serial")
  private static final Map<States, TokenType> STATE_TO_TOKEN_MAP = Collections
      .unmodifiableMap(new HashMap<States, TokenType>() {
        {
          this.put(States.INCONSISTENT, TokenType.ERROR);
          this.put(States.IDENTIFIER, TokenType.IDENTIFIER);
          this.put(States.EOF_STATE, TokenType.EOF);

          this.put(States.CHAR_CONST_GOT_CHAR, TokenType.CHAR_CONST);
          this.put(States.STRING_CONST, TokenType.STRING_CONST);
          this.put(States.COMMENT, TokenType.COMMENT);

          this.put(States.NUMBER, TokenType.NUMBER_INTEGER);
          this.put(States.NUMBER_START_ZERO_DIGIT, TokenType.NUMBER_INTEGER);

          this.put(States.NUMBER_REAL, TokenType.NUMBER_REAL);
          this.put(States.NUMBER_EXPONENT, TokenType.NUMBER_REAL);
          this.put(States.NUMBER_EXPONENT_START_ZERO_DIGIT, TokenType.NUMBER_REAL);

          this.put(States.NUMBER_BASE_NOTATION_NUMBER, TokenType.NUMBER_INTEGER);
          this.put(States.NUMBER_BASE_NOTATION_START_ZERO, TokenType.NUMBER_INTEGER);

          this.put(States.PLUS, TokenType.PLUS);
          this.put(States.MINUS, TokenType.MINUS);
          this.put(States.DIVIDE, TokenType.DIVIDE);
          this.put(States.MULTIPLY, TokenType.MULTIPLY);
          this.put(States.ROUND_BRACKET_OPEN, TokenType.ROUND_BRACKET_OPEN);
          this.put(States.ROUND_BRACKET_CLOSE, TokenType.ROUND_BRACKET_CLOSE);
          this.put(States.SQUARE_BRACKET_OPEN, TokenType.SQUARE_BRACKET_OPEN);
          this.put(States.SQUARE_BRAKCET_CLOSE, TokenType.SQUARE_BRACKET_CLOSE);
          this.put(States.EQUAL, TokenType.EQUAL);
          this.put(States.ASSIGN, TokenType.ASSIGN);
          this.put(States.DIFFERENT, TokenType.DIFFERENT);
          this.put(States.LESS, TokenType.LESS);
          this.put(States.LESS_OR_EQUAL, TokenType.LESS_OR_EQUAL);
          this.put(States.GREATER, TokenType.GREATER);
          this.put(States.GREATER_OR_EQUAL, TokenType.GREATER_OR_EQUAL);

          this.put(States.COLON, TokenType.COLON);
          this.put(States.SEMICOLON, TokenType.SEMICOLON);
          this.put(States.DOT, TokenType.DOT);
          this.put(States.DOUBLEDOT, TokenType.DOUBLEDOT);
          this.put(States.COMMA, TokenType.COMMA);
        }
      });

  @SuppressWarnings({ "serial" })
  private static final Map<States, String> STATE_TO_ERROR_MAP = Collections
      .unmodifiableMap(new HashMap<States, String>() {
        {
          this.put(States.IDENTIFIER, "Invalid identifier name: ");
          this.put(States.COMMENT, "Did not close comment bracket: ");
        }
      });

  public boolean isConsuming() {

    if (this.currentState == States.INCONSISTENT && this.previousState == States.START_STATE)
      return true;

    if (this.previousState == States.COMMENT || this.previousState == States.CHAR_CONST_GOT_CHAR
        || this.previousState == States.STRING_CONST)
      return true;

    return false;
  }

}
