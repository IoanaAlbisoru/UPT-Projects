package parser;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import parser.errors.SynthaxError;

import testutils.TestUtil;

@RunWith(value = Parameterized.class)
public class ParserTest {
  private static final Class<?> TARGET_CLASS = Parser.class;
  private final String fileName;
  private AbstractSynthaxNode node;
  private final Class<?> className;

  public ParserTest(String fileName, Class<?> className) {
    this.fileName = fileName;
    this.className = className;
  }

  @Before
  public void setUp() throws Exception {
    String path = TestUtil.constructPathFromClass(ParserTest.TARGET_CLASS) + "/" + this.fileName;
    Parser parser = new Parser(path);
    Constructor<?> constructor = this.className.getConstructor(Parser.class);
    this.node = (AbstractSynthaxNode) constructor.newInstance(parser);
  }

  @Test
  public void testNodeStructure() {
    if (!this.fileName.contains("Error"))
      try {
        this.node.check();
      } catch (Exception e) {
        fail(this.fileName + e.getMessage());
      }
    else {
      try {
        this.node.check();
      } catch (SynthaxError e) {
        assertTrue(true);
        return;
      }
      fail(this.fileName + " did not throw exception");
    }
  }

  @SuppressWarnings("rawtypes")
  @Parameterized.Parameters
  public static Collection testData() {
    final List<Object[]> testValues = new ArrayList<>(100);

    testValues.add(new Object[] { "NodeTypeSimple1", NodeTypeSimple.class });
    testValues.add(new Object[] { "NodeTypeSimple2", NodeTypeSimple.class });
    testValues.add(new Object[] { "NodeTypeSimple3", NodeTypeSimple.class });
    testValues.add(new Object[] { "NodeTypeSimpleError", NodeTypeSimple.class });

    testValues.add(new Object[] { "NodeStaticFactor1", NodeStaticFactor.class });
    testValues.add(new Object[] { "NodeStaticFactor2", NodeStaticFactor.class });
    testValues.add(new Object[] { "NodeStaticFactor3", NodeStaticFactor.class });
    testValues.add(new Object[] { "NodeStaticFactor4", NodeStaticFactor.class });
    testValues.add(new Object[] { "NodeStaticFactorError", NodeStaticFactor.class });

    testValues.add(new Object[] { "NodeStaticTerm1", NodeStaticTerm.class });
    testValues.add(new Object[] { "NodeStaticTerm2", NodeStaticTerm.class });
    testValues.add(new Object[] { "NodeStaticTerm3", NodeStaticTerm.class });
    testValues.add(new Object[] { "NodeStaticTermError", NodeStaticTerm.class });

    testValues.add(new Object[] { "NodeStaticExpression1", NodeStaticExpression.class });
    testValues.add(new Object[] { "NodeStaticExpression2", NodeStaticExpression.class });
    testValues.add(new Object[] { "NodeStaticExpressionError", NodeStaticExpression.class });

    testValues.add(new Object[] { "NodeTypeArray1", NodeTypeArray.class });
    testValues.add(new Object[] { "NodeTypeArrayError", NodeTypeArray.class });

    testValues.add(new Object[] { "NodeIdentifierList1", NodeIdentifierList.class });
    testValues.add(new Object[] { "NodeIdentifierList2", NodeIdentifierList.class });
    testValues.add(new Object[] { "NodeIdentifierListError", NodeIdentifierList.class });

    testValues.add(new Object[] { "NodeFieldList1", NodeFieldList.class });
    testValues.add(new Object[] { "NodeFieldList2", NodeFieldList.class });
    testValues.add(new Object[] { "NodeFieldListError", NodeFieldList.class });

    testValues.add(new Object[] { "NodeTypeRecord1", NodeTypeRecord.class });
    testValues.add(new Object[] { "NodeTypeRecord2", NodeTypeRecord.class });
    testValues.add(new Object[] { "NodeTypeRecordError", NodeTypeRecord.class });

    testValues.add(new Object[] { "NodeType1", NodeType.class });
    testValues.add(new Object[] { "NodeType2", NodeType.class });
    testValues.add(new Object[] { "NodeType3", NodeType.class });
    testValues.add(new Object[] { "NodeTypeError", NodeType.class });

    testValues.add(new Object[] { "NodeDeclarationVariable1", NodeDeclarationVariable.class });
    testValues.add(new Object[] { "NodeDeclarationVariable2", NodeDeclarationVariable.class });
    testValues.add(new Object[] { "NodeDeclarationVariable3", NodeDeclarationVariable.class });
    testValues.add(new Object[] { "NodeDeclarationVariableError", NodeDeclarationVariable.class });

    testValues.add(new Object[] { "NodeVarFactor1", NodeVarFactor.class });
    testValues.add(new Object[] { "NodeVarFactor2", NodeVarFactor.class });
    testValues.add(new Object[] { "NodeVarFactor3", NodeVarFactor.class });
    testValues.add(new Object[] { "NodeVarFactor4", NodeVarFactor.class });
    testValues.add(new Object[] { "NodeVarFactor5", NodeVarFactor.class });
    testValues.add(new Object[] { "NodeVarFactorError", NodeVarFactor.class });

    testValues.add(new Object[] { "NodeVarTerm1", NodeVarTerm.class });
    testValues.add(new Object[] { "NodeVarTerm2", NodeVarTerm.class });
    testValues.add(new Object[] { "NodeVarTermError", NodeVarTerm.class });

    testValues.add(new Object[] { "NodeVarExpression1", NodeVarExpression.class });
    testValues.add(new Object[] { "NodeVarExpressionError", NodeVarExpression.class });

    testValues.add(new Object[] { "NodeVariable1", NodeVariable.class });
    testValues.add(new Object[] { "NodeVariable2", NodeVariable.class });
    testValues.add(new Object[] { "NodeVariable3", NodeVariable.class });
    testValues.add(new Object[] { "NodeVariableError", NodeVariable.class });

    testValues.add(new Object[] { "NodeLogicalComparison1", NodeLogicalComparison.class });
    testValues.add(new Object[] { "NodeLogicalComparison2", NodeLogicalComparison.class });
    testValues.add(new Object[] { "NodeLogicalComparisonError", NodeLogicalComparison.class });

    testValues.add(new Object[] { "NodeLogicalExpression1", NodeLogicalExpression.class });
    testValues.add(new Object[] { "NodeLogicalExpression2", NodeLogicalExpression.class });
    testValues.add(new Object[] { "NodeLogicalExpressionError", NodeLogicalExpression.class });

    testValues.add(new Object[] { "NodeCondition1", NodeCondition.class });
    testValues.add(new Object[] { "NodeCondition2", NodeCondition.class });
    testValues.add(new Object[] { "NodeConditionError", NodeCondition.class });

    testValues.add(new Object[] { "NodeInstructionAssign", NodeInstructionAssign.class });
    testValues.add(new Object[] { "NodeInstructionIf1", NodeInstructionIf.class });
    testValues.add(new Object[] { "NodeInstructionIf2", NodeInstructionIf.class });
    testValues.add(new Object[] { "NodeInstructionIf3", NodeInstructionIf.class });
    testValues.add(new Object[] { "NodeInstructionIfError", NodeInstructionIf.class });

    testValues.add(new Object[] { "NodeInstructionWhile1", NodeInstructionWhile.class });
    testValues.add(new Object[] { "NodeInstructionWhile2", NodeInstructionWhile.class });

    testValues.add(new Object[] { "NodeInstructionFor1", NodeInstructionFor.class });
    testValues.add(new Object[] { "NodeInstructionFor2", NodeInstructionFor.class });

    testValues.add(new Object[] { "NodeInstructionRepeat1", NodeInstructionRepeat.class });

    testValues.add(new Object[] { "NodeElement1", NodeElement.class });
    testValues.add(new Object[] { "NodeElement2", NodeElement.class });

    testValues.add(new Object[] { "NodeElementList1", NodeElementList.class });

    testValues.add(new Object[] { "NodeInstructionPrint1", NodeInstructionPrint.class });

    testValues.add(new Object[] { "NodeInstructionRead1", NodeInstructionRead.class });

    testValues.add(new Object[] { "NodeInstructionList", NodeInstructionList.class });

    testValues.add(new Object[] { "NodeBasicBlock1", NodeBasicBlock.class });
    testValues.add(new Object[] { "NodeBasicBlock2", NodeBasicBlock.class });
    testValues.add(new Object[] { "NodeBasicBlockError", NodeBasicBlock.class });

    testValues.add(new Object[] { "NodeDeclarationConstant1", NodeDeclarationConstant.class });

    testValues.add(new Object[] { "NodeSubProgramHeader1", NodeSubProgramHeader.class });
    testValues.add(new Object[] { "NodeSubProgramHeader2", NodeSubProgramHeader.class });

    testValues.add(new Object[] { "NodeDeclarationFunction1", NodeDeclarationFunction.class });
    testValues.add(new Object[] { "NodeDeclarationFunction2", NodeDeclarationFunction.class });
    testValues.add(new Object[] { "NodeDeclarationFunctionError", NodeDeclarationFunction.class });

    testValues.add(new Object[] { "NodeDeclarationProcedure1", NodeDeclarationProcedure.class });
    testValues.add(new Object[] { "NodeDeclarationProcedure2", NodeDeclarationProcedure.class });
    testValues.add(new Object[] { "NodeDeclarationProcedureError", NodeDeclarationProcedure.class });

    testValues.add(new Object[] { "NodeDeclarationConstantSection1", NodeDeclarationConstantSection.class });

    testValues.add(new Object[] { "NodeDeclarationVariableSection1", NodeDeclarationVariableSection.class });

    testValues.add(new Object[] { "NodeMainProgram1", NodeMainProgram.class });

    return testValues;
  }

}
