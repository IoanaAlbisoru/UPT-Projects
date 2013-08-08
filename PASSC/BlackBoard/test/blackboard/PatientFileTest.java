package blackboard;

import org.junit.Assert;
import org.junit.Test;

import blackboard.knowledge.Condition;
import blackboard.knowledge.MedicalKnowledge;
import blackboard.knowledge.Symptom;

public class PatientFileTest {

  @Test
  public void askAboutSymptom() {
    final PatientFile patient = new PatientFile("bla", MedicalKnowledge.knowledge.get(Condition.AB),
        MedicalKnowledge.knowledge.get(Condition.ABCD));

    Assert.assertTrue(patient.askAboutSympton(Symptom.c));
    Assert.assertFalse(patient.askAboutSympton(Symptom.c));
    Assert.assertFalse(patient.askAboutSympton(Symptom.f));
  }
}
