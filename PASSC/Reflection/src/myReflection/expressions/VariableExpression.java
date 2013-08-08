package myReflection.expressions;

public final class VariableExpression extends Expression {

  private String variableName = "";

  @Override
  protected void innerBind(final Object... arguments) {
    this.variableName = (String) arguments[0];
  }

  @Override
  protected double innerEvaluate(final ExpressionContext context) {
    return context.lookup(this.variableName);
  }

}
