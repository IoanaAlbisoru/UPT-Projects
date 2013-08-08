package parser;

import parser.errors.SynthaxError;

class NodeDeclarationSubProgramSection extends AbstractSynthaxNode {

  public NodeDeclarationSubProgramSection(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    // TODO
    new NodeDeclarationSubProgram(this.parser).check();
  }

}
