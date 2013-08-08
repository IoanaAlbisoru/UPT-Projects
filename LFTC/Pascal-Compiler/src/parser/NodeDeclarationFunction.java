package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeDeclarationFunction extends AbstractSynthaxNode {

  public NodeDeclarationFunction(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    this.expect(TokenType.FUNCTION_KW);
    new NodeSubProgramHeader(this.parser).check();
    this.expect(TokenType.COLON);
    new NodeTypeSimple(this.parser).check();
    this.expect(TokenType.SEMICOLON);
    new NodeBlock(this.parser).check();
  }

}
