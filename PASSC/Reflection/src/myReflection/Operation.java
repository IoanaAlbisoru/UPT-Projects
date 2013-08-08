package myReflection;

import myReflection.expressions.Expression;
import myReflection.expressions.ExpressionContext;

public final class Operation {
  private final String name;
  // private final Entity entity;

  private final Expression expression;

  // private ExpressionContext context;

  public Operation(final String name, final Expression expr) {
    this.name = name;
    this.expression = expr;
    // this.entity = entity;
  }

  // TODO add exception eventually
  Object execute(final ExpressionContext context) {
    return this.expression.evaluate(context);
  }

  final String getName() {
    return this.name;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof String) {
      return this.name.equals(obj);
    }

    if (obj instanceof Operation) {
      return this.name.equals(((Operation) obj).name);
    }

    return false;
  }

  @Override
  public String toString() {
    return String.format("operation: %s\n", this.name);
  }

}
