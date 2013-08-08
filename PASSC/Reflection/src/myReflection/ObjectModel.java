package myReflection;

import java.util.Collection;
import java.util.LinkedHashMap;

import myReflection.exceptions.DuplicateAttributeName;
import myReflection.exceptions.DuplicateEntityException;
import myReflection.exceptions.EntityNotFound;
import myReflection.exceptions.InvalidValueType;

public class ObjectModel {
  private static final LinkedHashMap<String, Entity> entities = new LinkedHashMap<String, Entity>();

  // static {
  // loadEntities();
  // };

  /**
   * 
   */
  @SuppressWarnings("unused")
  private static void loadEntities() {

    try {
      Entity tempEntity = ObjectModel.newEntity("Insurance");

      tempEntity.addAttribute(new AttributeType("Name", String.class));
      tempEntity.addAttribute(new AttributeType("Adress", String.class));
      tempEntity.addAttribute(new AttributeType("ID", String.class));
      tempEntity.addAttribute(new AttributeType("Education", String.class));
      tempEntity.addAttribute(new AttributeType("Workplace", String.class));

      tempEntity = ObjectModel.extendEntity("MedicalInsurance", "Insurance");
      tempEntity.addAttribute(new AttributeType("MedicalHistory", String.class));
      tempEntity.addAttribute(new AttributeType("Price1", Integer.class));
      tempEntity.addAttribute(new AttributeType("Price2", Integer.class));
      tempEntity.addAttribute(new AttributeType("Price3", Integer.class));

      tempEntity = ObjectModel.extendEntity("HouseInsurance", "Insurance");
      tempEntity.addAttribute(new AttributeType("HouseStuff", String.class));
      tempEntity.addAttribute(new AttributeType("HousePrice1", Integer.class));
      tempEntity.addAttribute(new AttributeType("HousePrice2", Integer.class));
      tempEntity.addAttribute(new AttributeType("HousePrice3", Integer.class));

      tempEntity = ObjectModel.extendEntity("SpecialHouseInsurance", "HouseInsurance");
      tempEntity.addAttribute(new AttributeType("SpecialHouseStuff", String.class));
      tempEntity.addAttribute(new AttributeType("SpecialHousePrice1", Integer.class));
      tempEntity.addAttribute(new AttributeType("SpecialHousePrice2", Integer.class));
      tempEntity.addAttribute(new AttributeType("SpecialHousePrice3", Integer.class));

    } catch (final DuplicateAttributeName e) {
      System.out.println(e.getMessage());
    } catch (final DuplicateEntityException e) {
      System.out.println(e.getMessage());
    } catch (final EntityNotFound e) {
      System.out.println(e.getMessage());
    }
  }

  public static Entity getEntity(final String name) throws EntityNotFound {
    final Entity temp = ObjectModel.entities.get(name);

    if (temp == null) {
      throw new EntityNotFound(name);
    }
    return temp;
  }

  public static boolean contains(final String name) {
    return ObjectModel.entities.containsKey(name);
  }

  public static void printEntities() {
    for (final Entity ent : ObjectModel.entities.values()) {
      System.out.println(ent);
    }
  }

  public static void printInstances() {
    for (final Entity ent : ObjectModel.entities.values()) {
      for (final Instance instance : ent.getInstances()) {
        System.out.println(instance);
      }
    }
  }

  public static Collection<Entity> getEntities() {
    return ObjectModel.entities.values();
  }

  public static Instance newInstance(final String entityName) throws EntityNotFound, InvalidValueType {
    final Entity temp = ObjectModel.getEntity(entityName);
    return new Instance(temp);
  }

  /**
   * This method creates and Entity with the name 'name', stores it and returns
   * it
   * 
   * @param name
   * @return
   * @throws DuplicateEntityException
   */
  public static Entity newEntity(final String name) throws DuplicateEntityException {
    if (ObjectModel.contains(name)) {
      throw new DuplicateEntityException(name);
    }

    final Entity toAdd = new Entity(name);
    ObjectModel.entities.put(name, toAdd);
    return toAdd;
  }

  public static Entity extendEntity(final String name, final String superType) throws DuplicateEntityException,
      EntityNotFound {
    if (ObjectModel.contains(name)) {
      throw new DuplicateEntityException(name);
    }

    final Entity toAdd = new Entity(name, ObjectModel.getEntity(superType));
    ObjectModel.entities.put(name, toAdd);
    return toAdd;
  }
}
