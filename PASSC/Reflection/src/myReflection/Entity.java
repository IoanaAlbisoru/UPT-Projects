package myReflection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import myReflection.exceptions.DuplicateAttributeName;
import myReflection.exceptions.DuplicateOperation;
import myReflection.exceptions.InvalidValueType;
import myReflection.exceptions.NoSuchAttributeException;
import myReflection.exceptions.NoSuchOperation;

public final class Entity {
  private final String name;
  private Entity superType = null;
  private final AttributeAndInstanceCollection attributes;
  final LinkedHashMap<String, Operation> operations = new LinkedHashMap<String, Operation>();

  Entity(final String name) {
    this.name = name;
    this.attributes = new AttributeAndInstanceCollection();
  }

  Entity(final String name, final Entity superType) {
    this.name = name;
    this.attributes = new AttributeAndInstanceCollection();
    this.superType = superType;
    superType.attributes.subClasses.add(this);
  }

  /**
   * This method recursively searches through the whole hierarchy.
   * 
   * @param name
   * @return AttributeType corresponding to the name
   * @throws NoSuchAttributeException
   */
  public AttributeType getAttribute(final String name) throws NoSuchAttributeException {
    AttributeType temp = null;

    // we check if we have the attribute
    try {
      temp = this.attributes.get(name);
    } catch (final NoSuchAttributeException e) {
      try {
        // we recursively try with out supers
        if (this.superType != null) {
          temp = this.superType.getAttribute(name);
        } else {
          throw e;
        }
      } catch (final NoSuchAttributeException exFromSuper) {
        throw e;
      }
    }
    return temp;
  }

  /**
   * This method adds the attribute only if it isn't contained by any
   * super-classes
   * 
   * @param temp
   * @throws DuplicateAttributeName
   */
  public void addAttribute(final AttributeType temp) throws DuplicateAttributeName {
    if (this.superType != null) {
      if (this.superType.containsAttribute(temp)) {
        throw new DuplicateAttributeName(temp.getName());
      }
    }

    this.attributes.add(temp);
  }

  /**
   * This method removes the attribute specified by name
   * 
   * @param name
   * @throws NoSuchAttributeException
   */
  public void removeAttribute(final String name) throws NoSuchAttributeException {
    this.attributes.remove(name);
  }

  public void removeAttribute(final AttributeType temp) throws NoSuchAttributeException {
    this.attributes.remove(temp.getName());
  }

  public void addOperation(final Operation op) throws DuplicateOperation {
    if (this.operations.containsKey(op.getName())) {
      throw new DuplicateOperation(op.getName());
    }

    this.operations.put(op.getName(), op);
  }

  public Operation getOperation(final String name) throws NoSuchOperation {
    Operation temp = null;
    if (this.superType != null) {
      try {
        temp = this.superType.getOperation(name);
      } catch (final NoSuchOperation noOp) {
        temp = this.operations.get(name);
        if (temp == null) {
          throw new NoSuchOperation(name, this.name);
        }
      }
    } else {
      temp = this.operations.get(name);
      if (temp == null) {
        throw new NoSuchOperation(name, this.name);
      }
    }
    return temp;
  }

  public void removeOperation(final Operation op) throws NoSuchOperation {
    if (this.operations.remove(op.getName()) == null) {
      throw new NoSuchOperation(op.getName(), this.name);
    }
  }

  public void removeOperation(final String name) throws NoSuchOperation {
    if (this.operations.remove(name) == null) {
      throw new NoSuchOperation(name, this.name);
    }
  }

  /**
   * This method checks recursively whether or not the attribute exists
   * 
   * @param name
   * @return
   */
  public boolean containsAttribute(final String name) {
    if (this.superType != null) {
      if (this.superType.containsAttribute(name)) {
        return true;
      }
      ;
    }
    return this.attributes.contains(name);
  }

  public boolean containsAttribute(final AttributeType temp) {
    if (this.superType != null) {
      if (this.superType.containsAttribute(temp)) {
        return true;
      }
      ;
    }
    return this.attributes.contains(temp);
  }

  void addNewInstance(final Map<AttributeType, Attribute> toAdd, final Instance inst) throws InvalidValueType {
    if (this.superType != null) {
      this.superType.attributes.addNewInstance(toAdd, inst);
    }
    this.attributes.addNewInstance(toAdd, inst);
  }

  public final String getName() {
    return this.name;
  }

  public Collection<AttributeType> getAttributes() {
    return this.attributes.attributeTypes.values();
  }

  @Override
  public boolean equals(final Object arg0) {
    if (arg0 instanceof String) {
      return this.name.equals(arg0);
    }

    if (arg0 instanceof Entity) {
      return this.name.equals(((Entity) arg0).name);
    }

    return false;
  }

  @Override
  public String toString() {
    String temp = String.format("-----Entity-----\nName: %s\n", this.name);

    if (this.superType != null) {
      temp += "========SUPER========\n";
      temp += this.superType.toString();
      temp += "======END SUPER======\n";

    }

    temp += "Attribute types:\n";
    for (final AttributeType type : this.attributes.attributeTypes.values()) {
      temp += String.format("        %s\n", type.toString());
    }
    return temp;
  }

  public String forInterpreter() {
    String temp = String.format("entity %s\n", this.name);
    if (this.superType == null) {
      temp += String.format("superType: none\n\n");
    } else {
      temp += String.format("superType: %s\n\n", this.superType.getName());
    }

    temp += this.attributes.forInterpreter();

    temp += this.operationsForInterpreter();
    return temp;
  }

  private String operationsForInterpreter() {
    String temp = "";
    if (this.superType != null) {

      final String tempSuper = this.superType.operationsForInterpreter();
      if (!tempSuper.equals("")) {
        temp += String.format("Ops Inherited from: %s\n", this.superType.getName());
      }
      temp += tempSuper;
    }
    for (final Operation op : this.operations.values()) {
      temp += op;
    }
    return temp;
  }

  public Collection<Instance> getInstances() {
    return this.attributes.getInstances();
  }

  private class AttributeAndInstanceCollection {
    private final LinkedHashMap<String, AttributeType> attributeTypes = new LinkedHashMap<String, AttributeType>();

    private final Map<Instance, Map<AttributeType, Attribute>> instanceValues = new LinkedHashMap<Instance, Map<AttributeType, Attribute>>(
        10);

    private final Collection<Entity> subClasses = new ArrayList<Entity>();

    /**
     * This method is called after all the checking whether the type already
     * existed or not
     * 
     * @param type
     */
    private void updateInstanceAdd(final AttributeType type) {
      for (final Map<AttributeType, Attribute> map : this.instanceValues.values()) {
        map.put(type, new Attribute(type));
      }

    }

    /**
     * This method is called after all the checking whether the type already
     * existed or not
     * 
     * @param type
     */
    private void updateInstanceRemove(final AttributeType type) {
      for (final Map<AttributeType, Attribute> map : this.instanceValues.values()) {
        map.remove(type);
      }

    }

    private void updateSubClassesAdd(final AttributeType toAdd) {
      for (final Entity subclass : this.subClasses) {
        subclass.attributes.updateInstanceAdd(toAdd);
      }
    }

    private void updateSubClassesRemove(final AttributeType toRemove) {
      for (final Entity subclass : this.subClasses) {
        subclass.attributes.updateInstanceRemove(toRemove);
      }
    }

    /**
     * We make sure that we copy everything in this function.
     * 
     * @param toAdd
     * @throws InvalidValueType
     */
    void addNewInstance(final Map<AttributeType, Attribute> toAdd, final Instance inst) throws InvalidValueType {
      final Collection<AttributeType> temp = this.attributeTypes.values();

      for (final AttributeType type : temp) {
        if (type.getType().equals(String.class)) {
          toAdd.put(type, new Attribute(type, ""));
        } else if (type.getType().equals(Double.class)) {
          toAdd.put(type, new Attribute(type, new Double(0.0)));
        } else if (type.getType().equals(Integer.class)) {
          toAdd.put(type, new Attribute(type, new Integer(0)));
        }
      }

      this.instanceValues.put(inst, toAdd);
    }

    void add(final AttributeType temp) throws DuplicateAttributeName {
      if (this.attributeTypes.containsKey(temp.getName())) {
        throw new DuplicateAttributeName(temp.getName());
      }

      this.attributeTypes.put(temp.getName(), temp);
      this.updateInstanceAdd(temp);
      this.updateSubClassesAdd(temp);
    }

    AttributeType get(final String name) throws NoSuchAttributeException {
      final AttributeType temp = this.attributeTypes.get(name);
      if (temp == null) {
        throw new NoSuchAttributeException(name, "Entity.get");
      }
      return temp;
    }

    void remove(final String name) throws NoSuchAttributeException {
      final AttributeType temp = this.attributeTypes.remove(name);
      if (null == temp) {
        throw new NoSuchAttributeException(name, "Entity.remove");
      } else {
        this.updateInstanceRemove(temp);
      }
      this.updateSubClassesRemove(temp);
    }

    boolean contains(final String name) {
      return this.attributeTypes.containsKey(name);
    }

    boolean contains(final AttributeType value) {
      return this.attributeTypes.containsValue(value);
    }

    Collection<Instance> getInstances() {
      return this.instanceValues.keySet();
    }

    String forInterpreter() {
      String temp = "";
      for (final AttributeType type : this.attributeTypes.values()) {
        temp += String.format("%s %s\n", type.getType().getSimpleName(), type.getName());
      }
      if (Entity.this.superType != null) {
        temp += String.format("\nInherited from %s:\n\n", Entity.this.superType.getName());
        temp += Entity.this.superType.attributes.forInterpreter();
      }
      return temp;
    }

  };// END EntityAttributesCollection
}
