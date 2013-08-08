package grammar;

public class Token {
  public final TokenType type;
  public final String content;
  public final int line;
  public final int column;

  public Token(TokenType t, String str, int line, int column) {
    this.type = t;
    this.content = str;
    this.line = line;
    this.column = column;
  }

  @Override
  public String toString() {
    return this.type.toString() + "    " + this.content;
  }
}
