package parser;

import parser.errors.SynthaxError;

class NodeBlock extends AbstractSynthaxNode {

  public NodeBlock(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    try {
      new NodeDeclarationConstantSection(this.parser).check();
    } catch (Exception e) {
      if (e.getMessage().contains("const")) {
      }
    }

    try {
      new NodeDeclarationVariableSection(this.parser).check();
    } catch (Exception e) {
      if (e.getMessage().contains("var")) {
      }
    }

    try {
      new NodeDeclarationSubProgramSection(this.parser).check();
    } catch (Exception e) {
    }

  }

}
