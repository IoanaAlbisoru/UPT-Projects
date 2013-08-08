package parser;

import java.io.FileNotFoundException;

import lexer.BufferedLexer;
import grammar.Token;

public class Parser {
  private final BufferedLexer lexer;
  private Token currentToken;

  public Parser(String fileName) throws FileNotFoundException {
    this.lexer = new BufferedLexer(fileName);
    this.currentToken = this.lexer.nextToken();
  }

  public Token getCurrentToken() {
    return this.currentToken;
  }

  public void nextToken() {
    this.currentToken = this.lexer.nextToken();
    // System.out.println("\n Current token: " + this.currentToken + "\n");
  }

  public void parse() {
    // TODO set the domino
  }
}
