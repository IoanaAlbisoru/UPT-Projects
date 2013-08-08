package parser;

import grammar.TokenType;
import parser.errors.SynthaxError;

public class NodeDeclarationVariableSection extends AbstractSynthaxNode {

  public NodeDeclarationVariableSection(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    // TODO
    if (this.accept(TokenType.VAR_KW))
      new NodeDeclarationVariableList(this.parser).check();
  }

}
