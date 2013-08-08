package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeLogicalExpression extends AbstractSynthaxNode {

  public NodeLogicalExpression(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    new NodeLogicalComparison(this.parser).check();
    if (this.accept(TokenType.AND_KW) || this.accept(TokenType.OR_KW))
      this.check();
  }

}
