package blackboard;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import blackboard.knowledge.Condition;
import blackboard.knowledge.MedicalKnowledge;
import blackboard.knowledge.Symptom;

public class DoctorTest {

  @Test
  public void areaOfExpertise_01() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    HashMap<Condition, List<Symptom>> knowledge;
    Doctor doctor;
    PatientFile patient;

    Method method;

    knowledge = new HashMap<Condition, List<Symptom>>();
    knowledge.put(Condition.AB, MedicalKnowledge.knowledge.get(Condition.AB));

    doctor = new Doctor(knowledge);
    patient = new PatientFile("Arthur Dent", MedicalKnowledge.knowledge.get(Condition.AB),
        MedicalKnowledge.knowledge.get(Condition.ABCD));

    method = TestUtils.getPrivateMethod(Doctor.class, "areaOfExpertise", new Class[] { PatientFile.class });

    final boolean result = (Boolean) method.invoke(doctor, new Object[] { patient });
    Assert.assertTrue(result);

  }

  @Test
  public void areaOfExpertise_02() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    HashMap<Condition, List<Symptom>> knowledge;
    Doctor doctor;
    PatientFile patient;

    Method method;
    knowledge = new HashMap<Condition, List<Symptom>>();
    knowledge.put(Condition.AB, MedicalKnowledge.knowledge.get(Condition.AB));
    doctor = new Doctor(knowledge);
    patient = new PatientFile("Arthur Dent", MedicalKnowledge.knowledge.get(Condition.ABE),
        MedicalKnowledge.knowledge.get(Condition.ABCD));

    method = TestUtils.getPrivateMethod(Doctor.class, "areaOfExpertise", new Class[] { PatientFile.class });

    final boolean result = (Boolean) method.invoke(doctor, new Object[] { patient });
    Assert.assertFalse(result);

  }

  @Test
  public void areaOfExpertise_03() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    HashMap<Condition, List<Symptom>> knowledge;
    Doctor doctor;
    PatientFile patient;

    Method method;

    knowledge = new HashMap<Condition, List<Symptom>>();
    knowledge.put(Condition.AB, MedicalKnowledge.knowledge.get(Condition.AB));
    knowledge.put(Condition.ABCE, MedicalKnowledge.knowledge.get(Condition.ABCE));

    doctor = new Doctor(knowledge);
    patient = new PatientFile("Arthur Dent", MedicalKnowledge.knowledge.get(Condition.ABE),
        MedicalKnowledge.knowledge.get(Condition.ABCD));

    method = TestUtils.getPrivateMethod(Doctor.class, "areaOfExpertise", new Class[] { PatientFile.class });

    final boolean result = (Boolean) method.invoke(doctor, new Object[] { patient });
    Assert.assertTrue(result);
  }

  /**
   * Tests two equal lists
   */
  @Test
  public void computeOverlap_01() {
    List<Symptom> list1 = new LinkedList<Symptom>();
    List<Symptom> list2 = new LinkedList<Symptom>();

    final Method method = TestUtils.getPrivateMethod(Doctor.class, "computeOverlap", new Class[] { List.class,
        List.class });

    try {

      // FIRST TEST BOTH are equal
      list1 = MedicalKnowledge.knowledge.get(Condition.ABCD);
      list2 = MedicalKnowledge.knowledge.get(Condition.ABCD);

      final Doctor doctor = new Doctor(null);
      final double result = (Double) method.invoke(doctor, new Object[] { list1, list2 });

      Assert.assertEquals(1.0, result, TestUtils.DELTA);

    } catch (final Exception e) {
      Assert.fail(e.getClass().getSimpleName() + "::" + e.getMessage());
    }
  }

  /**
   * Test two lists; one contains exactly half of the elements of the other one
   */
  @Test
  public void computeOverlap_02() {
    List<Symptom> list1 = new LinkedList<Symptom>();
    List<Symptom> list2 = new LinkedList<Symptom>();

    final Method method = TestUtils.getPrivateMethod(Doctor.class, "computeOverlap", new Class[] { List.class,
        List.class });

    try {

      // FIRST TEST BOTH are equal
      list1 = MedicalKnowledge.knowledge.get(Condition.AB);
      list2 = MedicalKnowledge.knowledge.get(Condition.ABCD);

      final Doctor doctor = new Doctor(null);
      final double result = (Double) method.invoke(doctor, new Object[] { list1, list2 });

      Assert.assertEquals(0.5, result, TestUtils.DELTA);

    } catch (final Exception e) {
      Assert.fail(e.getClass().getSimpleName() + "::" + e.getMessage());
    }
  }
}
