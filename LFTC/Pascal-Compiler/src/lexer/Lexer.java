package lexer;

import grammar.Alphabet;
import grammar.Token;
import java.io.FileNotFoundException;

import lexer.LexerBufferedReader;

/**
 * @author Lori
 * 
 */
class Lexer {
  private final LexerBufferedReader input;

  public Lexer(String fileName) throws FileNotFoundException {
    this.input = new LexerBufferedReader(fileName);
  }

  public Token nextToken() {
    int lineNumber = this.input.getCurrentLine();
    int columnNumber = this.input.getCurrentColumn();

    final FiniteStateMachine fsm = new FiniteStateMachine(States.START_STATE);
    char c = Alphabet.EOF_CHAR;

    while (true) {
      c = this.input.nextChar();
      States newState = fsm.transition(c);

      if (newState == States.NUMBER_DOT_TWO) {
        this.input.putBackLast();
        return fsm.createToken(lineNumber, columnNumber);
      }

      if (newState == States.END_STATE || newState == States.INCONSISTENT) {
        if (fsm.isConsuming())
          this.input.consume();
        return fsm.createToken(lineNumber, columnNumber);
      }

      this.input.consume();

      // if we stayed in the start state that means that we have to update the
      // current location
      if (newState == States.START_STATE) {
        lineNumber = this.input.getCurrentLine();
        columnNumber = this.input.getCurrentColumn();
      }
    }// end while

  }

}
