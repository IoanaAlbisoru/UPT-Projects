package myReflection.expressions;

public abstract class Expression {
  protected boolean isBound;

  public Expression() {
    this.isBound = false;
  }

  public final boolean isBound() {
    return this.isBound;
  }

  public final void bind(final Object... arguments) {
    this.innerBind(arguments);
    this.isBound = true;
  }

  private final double ZERO_THRESHOLD = 1.0 * Math.pow(10.0, -14.0);

  public final Object evaluate(final ExpressionContext context) {
    final double result = this.innerEvaluate(context);

    if (Math.abs(result) <= this.ZERO_THRESHOLD) {
      return 0.0;
    } else {
      return result;
    }
  }

  protected abstract void innerBind(Object... arguments);

  protected abstract double innerEvaluate(ExpressionContext context);
}
