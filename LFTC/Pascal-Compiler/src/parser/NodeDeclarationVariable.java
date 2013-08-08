package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeDeclarationVariable extends AbstractSynthaxNode {

  public NodeDeclarationVariable(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    new NodeIdentifierList(this.parser).check();
    this.expect(TokenType.COLON);
    new NodeType(this.parser).check();
    // expect(TokenType.SEMICOLON);
  }

}
