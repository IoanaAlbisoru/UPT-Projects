package lexer;

import grammar.Token;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class BufferedLexer extends Lexer {
  private final ArrayList<Token> buffer;
  private int lastIndex;

  public BufferedLexer(String fileName) throws FileNotFoundException {
    super(fileName);
    this.buffer = new ArrayList<>(1024);
    this.lastIndex = 0;
  }

  @Override
  public Token nextToken() {
    if (this.buffer.size() == this.lastIndex) {
      Token nextToken = super.nextToken();
      this.lastIndex++;
      this.buffer.add(nextToken);
      return nextToken;
    } else
      return this.buffer.get(this.lastIndex++);
  }

  public void putBack(Token lastToken) {
    this.lastIndex--;
  }
}
