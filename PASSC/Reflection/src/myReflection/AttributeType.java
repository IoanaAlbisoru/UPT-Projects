package myReflection;

public final class AttributeType {
  private final String name;
  private final Class<?> type;

  public AttributeType(final String name, final Class<?> type) {
    this.name = name;
    this.type = type;
  }

  public final String getName() {
    return this.name;
  }

  public final Class<?> getType() {
    return this.type;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object) Can equal either a string or
   * an attribute type
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof AttributeType) {
      return this.name.equals(((AttributeType) obj).name);
    }

    if (obj instanceof String) {
      return this.name.equals(obj);
    }

    return false;

  }

  @Override
  public String toString() {
    return String.format("TYPE: %s | ATTR_NAME: %s", this.type.getSimpleName(), this.name);
  }

}
