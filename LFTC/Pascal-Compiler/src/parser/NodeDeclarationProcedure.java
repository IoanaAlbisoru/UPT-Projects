package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeDeclarationProcedure extends AbstractSynthaxNode {

  public NodeDeclarationProcedure(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    this.expect(TokenType.PROCEDURE_KW);
    new NodeSubProgramHeader(this.parser).check();
    this.expect(TokenType.SEMICOLON);
    new NodeBlock(this.parser).check();
  }

}
