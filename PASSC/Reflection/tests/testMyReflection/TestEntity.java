package testMyReflection;

import junit.framework.Assert;
import junit.framework.TestCase;
import myReflection.AttributeType;
import myReflection.Entity;
import myReflection.Instance;
import myReflection.ObjectModel;
import myReflection.exceptions.DuplicateAttributeName;
import myReflection.exceptions.DuplicateEntityException;
import myReflection.exceptions.EntityNotFound;
import myReflection.exceptions.InvalidValueType;
import myReflection.exceptions.NoSuchAttributeException;

public class TestEntity extends TestCase {

  public void testAdd() {
    final String name1 = "Fagballs";
    final String name2 = "Balthazar";

    try {
      final Entity parent = ObjectModel.newEntity(name1);
      final Entity child = ObjectModel.extendEntity(name2, name1);

      final AttributeType type = new AttributeType("LAME", String.class);
      try {
        // we add an attribute
        parent.addAttribute(type);

        try {
          // check if t was added
          Assert.assertEquals(type, parent.getAttribute(type.getName()));
        } catch (final NoSuchAttributeException e) {
          Assert.fail(e.getMessage());
        }
      } catch (final DuplicateAttributeName e) {
        Assert.fail(e.getMessage());
      }

      // we try to add same attribute twice;
      try {
        parent.addAttribute(type);
      } catch (final DuplicateAttributeName e) {
        Assert.assertTrue(true);
      }

      // we check to see if the recursive property works properly.
      try {
        child.addAttribute(type);
      } catch (final DuplicateAttributeName e) {
        Assert.assertTrue(true);
      }
    } catch (final DuplicateEntityException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (final EntityNotFound e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testWhole() {

    try {
      final Entity insurrance = ObjectModel.newEntity("InsurranceTest");
      final Entity medicalInsurrance = ObjectModel.extendEntity("MedicalInsurranceTest", "InsurranceTest");

      insurrance.addAttribute(new AttributeType("Name", String.class));
      insurrance.addAttribute(new AttributeType("Price", Integer.class));

      medicalInsurrance.addAttribute(new AttributeType("History", String.class));

      final Instance arthur = ObjectModel.newInstance(insurrance.getName());
      arthur.setAttribute("Name", "Arthur Dent");
      arthur.setAttribute("Price", new Integer(42));

      final Instance ford = ObjectModel.newInstance(medicalInsurrance.getName());
      ford.setAttribute("Name", "Ford Prefect");
      ford.setAttribute("Price", new Integer(54));
      ford.setAttribute("History", "Welcome to the Hitchhiker's guide to the galaxy");

      System.out.println(arthur);
      System.out.println(ford);

      insurrance.removeAttribute("Name");

      System.out.println("AFTER REMOVAL!!!");

      System.out.println(arthur);
      final String temp = ford.toString();
      System.out.println(temp);
    } catch (final DuplicateAttributeName e) {
      Assert.fail(e.getMessage());
    } catch (final NoSuchAttributeException e) {
      Assert.fail(e.getMessage());
    } catch (final InvalidValueType e) {
      Assert.fail(e.getMessage());
    } catch (final EntityNotFound e) {
      Assert.fail(e.getMessage());
    } catch (final DuplicateEntityException e) {
      Assert.fail(e.getMessage());
    }

  }
}
