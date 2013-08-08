package parser.errors;

import grammar.Token;

public class SynthaxError extends Exception {

  public SynthaxError(String arg0, Token token) {
    super("Error @:\nline:" + token.line + "\ncolumn: " + token.column + "\nMessage:\n" + arg0);
  }

  private static final long serialVersionUID = 1L;

}
