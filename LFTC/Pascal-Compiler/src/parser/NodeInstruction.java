package parser;

import parser.errors.SynthaxError;

class NodeInstruction extends AbstractSynthaxNode {

  public NodeInstruction(Parser parser) {
    super(parser);
  }

  @Override
  public void check() throws SynthaxError {
    AbstractSynthaxNode[] instructions = new AbstractSynthaxNode[] { new NodeInstructionAssign(this.parser),
        new NodeInstructionIf(this.parser), new NodeBasicBlock(this.parser), new NodeInstructionWhile(this.parser),
        new NodeInstructionFor(this.parser), new NodeInstructionRead(this.parser), new NodeInstructionPrint(this.parser),
        new NodeInstructionRead(this.parser), new NodeInstructionFunctionCall(this.parser) };
    boolean flag = false;

    for (int i = 0; i < instructions.length; i++) {
      AbstractSynthaxNode instr = instructions[i];
      try {
        instr.check();
        flag = true;
      } catch (SynthaxError e) {
      }
    }

    if (!flag)
      throw new SynthaxError("Instruction expected", this.parser.getCurrentToken());
  }

}
