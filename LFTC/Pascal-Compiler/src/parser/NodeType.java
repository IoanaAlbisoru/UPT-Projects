package parser;

import parser.errors.SynthaxError;

public class NodeType extends AbstractSynthaxNode {

  public NodeType(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    try {
      new NodeTypeSimple(this.parser).check();
      return;
    } catch (SynthaxError e) {
      try {
        new NodeTypeArray(this.parser).check();
        return;
      } catch (SynthaxError e1) {
        new NodeTypeRecord(this.parser).check();
      }
    }

  }

}
