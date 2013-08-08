package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeDeclarationConstant extends AbstractSynthaxNode {

  public NodeDeclarationConstant(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    this.expect(TokenType.IDENTIFIER);
    this.expect(TokenType.ASSIGN);
    new NodeStaticExpression(this.parser).check();
    this.expect(TokenType.SEMICOLON);
  }

}
