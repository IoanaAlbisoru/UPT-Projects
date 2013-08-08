package myReflection.expressions;

public final class ExprSubtract extends BinaryExpression {

  @Override
  protected double innerEvaluate(final ExpressionContext context) {
    return (Double) super.operand1.evaluate(context) - (Double) super.operand2.evaluate(context);
  }

}
