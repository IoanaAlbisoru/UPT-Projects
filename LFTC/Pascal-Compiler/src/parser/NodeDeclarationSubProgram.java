package parser;

import parser.errors.SynthaxError;

class NodeDeclarationSubProgram extends AbstractSynthaxNode {

  public NodeDeclarationSubProgram(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    try {
      new NodeDeclarationFunction(this.parser).check();
    } catch (Exception e) {
      new NodeDeclarationProcedure(this.parser).check();
    }
  }

}
