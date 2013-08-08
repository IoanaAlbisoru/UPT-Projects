package myReflection.expressions;

import java.util.HashMap;

public final class ExpressionContext {
  private final HashMap<String, Double> bindings;

  public ExpressionContext() {
    this.bindings = new HashMap<String, Double>();
  }

  public void bind(final String variable, final double value) {
    this.bindings.put(variable, value);
  }

  public double lookup(final String variable) {
    if (this.bindings.containsKey(variable)) {
      return this.bindings.get(variable);
    } else {
      return Double.NaN;
    }
  }
}
