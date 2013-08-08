package myReflection;

import interpreter.exceptions.UninitializedVariables;

import java.util.LinkedHashMap;
import java.util.Map;

import myReflection.exceptions.InvalidValueType;
import myReflection.exceptions.NoSuchAttributeException;
import myReflection.exceptions.NoSuchOperation;
import myReflection.expressions.ExpressionContext;

public class Instance {
  private final Entity entity;
  private final Map<AttributeType, Attribute> attributes;

  private final Long ID;

  final Long getID() {
    return this.ID;
  }

  private static long INSTANCE_ID = 0;

  Instance(final Entity entity) throws InvalidValueType {
    this.entity = entity;
    this.ID = Instance.INSTANCE_ID++;

    this.attributes = new LinkedHashMap<AttributeType, Attribute>();
    this.entity.addNewInstance(this.attributes, this);
  }

  public Attribute getAttribute(final String name) throws NoSuchAttributeException {
    if (!this.attributes.containsKey(name)) {
      throw new NoSuchAttributeException(name, "Instance.getAttribute");
    }

    final Attribute toReturn = this.attributes.get(name);
    return toReturn;
  }

  public AttributeType getAttributeType(final String name) throws NoSuchAttributeException {
    final AttributeType temp = this.entity.getAttribute(name);
    return temp;
  }

  public void setAttribute(final String name, final Object newValue) throws NoSuchAttributeException, InvalidValueType {
    final AttributeType temp = this.entity.getAttribute(name);

    if (!temp.getType().equals(newValue.getClass())) {
      throw new InvalidValueType("Instance.setAttribute");
    }

    this.attributes.put(temp, new Attribute(temp, newValue));
  }

  @Override
  public String toString() {
    String temp = String.format("-----ID=%s :Instance of:-----\n", this.ID.toString());

    temp += String.format("      %s\n", this.entity.getName());
    temp += "-----Attributes-----\n";
    for (final Attribute attr : this.attributes.values()) {
      temp += String.format("        %s\n", attr.toString());
    }
    return temp;
  }

  public String forInterpreter() {
    String temp = String.format("ID=%d instanceof: %s", this.ID, this.entity.getName());

    for (final Attribute attr : this.attributes.values()) {
      if (attr.getValue() != null) {
        temp += String.format("\n%s %s=%s", attr.getType().getType().getSimpleName(), attr.getType().getName(), attr
            .getValue().toString());
      } else {
        temp += String.format("\n%s %s=%s", attr.getType().getType().getSimpleName(), attr.getType().getName(), "null");
      }
    }

    return temp;
  }

  // TODO this will fail.
  public Object executeOperation(final String name) throws NoSuchOperation, UninitializedVariables {
    final Operation op = this.entity.getOperation(name);
    final ExpressionContext context = new ExpressionContext();

    for (final Map.Entry<AttributeType, Attribute> entry : this.attributes.entrySet()) {
      final Attribute attr = entry.getValue();
      if (attr.getType().getType().equals(Double.class)) {

        if (attr.getValue() != null) {
          context.bind(attr.getType().getName(), (Double) attr.getValue());
        } else {
          throw new UninitializedVariables("name");
        }
      }
    }

    return op.execute(context);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof Instance) {
      return ((Instance) obj).getID().equals(this.ID);
    }
    return false;
  }

  public final Entity getEntity() {
    return this.entity;
  }
}
