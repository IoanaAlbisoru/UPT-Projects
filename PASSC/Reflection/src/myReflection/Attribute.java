package myReflection;

import myReflection.exceptions.InvalidValueType;

public class Attribute {
  private final AttributeType type;
  private Object value;

  public Attribute(final AttributeType type) {
    this.type = type;
    // so we don't have to worry about null pointer exceptions.
    this.value = null;
  }

  public Attribute(final AttributeType type, final Object Value) throws InvalidValueType {
    this.type = type;
    if (!this.type.getType().equals(Value.getClass())) {
      throw new InvalidValueType("Attribute constructor");
    }
    this.value = Value;
  }

  public final AttributeType getType() {
    return this.type;
  }

  public final Object getValue() {
    return this.value;
  }

  public final void setValue(final Object value) throws InvalidValueType {
    {
      if (!this.type.getType().equals(value.getClass())) {
        throw new InvalidValueType("Attribute.setValue");
      }
      this.value = value;
    }
  }

  @Override
  public String toString() {
    if (this.value != null) {
      return this.type.toString() + " | VALUE=" + this.value.toString();
    }

    return this.type.toString() + " | VALUE=null";
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof String) {
      return this.type.equals(obj);
    }
    if (obj instanceof Attribute) {
      return this.type.equals(obj);
    }
    return false;
  }
}
