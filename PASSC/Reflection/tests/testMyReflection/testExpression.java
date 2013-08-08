package testMyReflection;

import junit.framework.Assert;
import junit.framework.TestCase;
import myReflection.expressions.ExprAdd;
import myReflection.expressions.Expression;
import myReflection.expressions.ExpressionContext;
import myReflection.expressions.VariableExpression;

public class testExpression extends TestCase {

  public void testBinary() {
    final Expression add = new ExprAdd();
    final VariableExpression var1 = new VariableExpression();
    final VariableExpression var2 = new VariableExpression();
    var1.bind("A");
    var2.bind("B");

    add.bind(var1, var2);
    final ExpressionContext context = new ExpressionContext();
    context.bind("A", new Double(3));
    context.bind("B", new Double(3));
    final double result = (Double) add.evaluate(context);
    Assert.assertEquals(6, result, 0.0);
  }
}
