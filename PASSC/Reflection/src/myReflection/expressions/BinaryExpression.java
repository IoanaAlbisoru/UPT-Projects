package myReflection.expressions;

abstract class BinaryExpression extends Expression {
  protected Expression operand1;
  protected Expression operand2;

  @Override
  protected final void innerBind(final Object... arguments) {
    this.operand1 = (Expression) arguments[0];
    this.operand2 = (Expression) arguments[1];
  }
}