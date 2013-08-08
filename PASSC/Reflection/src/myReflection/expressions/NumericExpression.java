package myReflection.expressions;

public final class NumericExpression extends Expression {

  private double value;

  @Override
  protected void innerBind(final Object... arguments) {
    this.value = (Double) arguments[0];
  }

  @Override
  protected double innerEvaluate(final ExpressionContext context) {
    return this.value;
  }

}
