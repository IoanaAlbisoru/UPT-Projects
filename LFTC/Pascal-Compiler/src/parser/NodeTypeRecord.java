package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeTypeRecord extends AbstractSynthaxNode {

  public NodeTypeRecord(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    if (this.expect(TokenType.RECORD_KW)) {
      new NodeFieldList(this.parser).check();
      this.expect(TokenType.END_KW);
    }
  }

}
