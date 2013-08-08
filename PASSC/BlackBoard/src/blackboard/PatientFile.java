package blackboard;

import java.util.LinkedList;
import java.util.List;

import blackboard.knowledge.Condition;
import blackboard.knowledge.Symptom;

public class PatientFile {
  private final String name;
  private int timesCheckedOut = 0;

  private final List<Symptom> revealedSymptoms;
  private final List<Symptom> allSymptoms;
  private Condition diagnosedCondition = null;
  private List<Condition> suspectConditions = null;

  public PatientFile(final String name, final List<Symptom> revealedSymptoms, final List<Symptom> allSymptoms) {
    this.name = name;
    this.revealedSymptoms = revealedSymptoms;
    this.allSymptoms = allSymptoms;
  }

  @Override
  public String toString() {
    String temp = "" + this.name;

    if (this.diagnosedCondition != null) {
      temp += "\ndiagnosed with:" + this.diagnosedCondition;
    } else {
      temp += "\nundiagnosed";
    }

    if (this.suspectConditions != null) {
      temp += "\nsuspected with:";
      for (final Condition condition : this.suspectConditions) {
        temp += "\n" + condition;
      }
    }

    temp += "\nComplete list of symptoms ";
    for (final Symptom symptom : this.allSymptoms) {
      temp += " " + symptom;
    }

    temp += "\nRevealed symptoms: ";
    for (final Symptom symptom : this.revealedSymptoms) {
      temp += " " + symptom;
    }

    return temp;
  }

  boolean isDiagnosed() {
    return (this.diagnosedCondition != null);
  }

  /**
   * @param question
   *          specifies the symptom about which the client wants to inquire
   * @return true: if patient has specified symptom, also updates the list of
   *         symptoms for the patient false: if the patient doesn't exhibit the
   *         symptom
   */
  public boolean askAboutSympton(final Symptom question) {
    if (this.allSymptoms.contains(question) && !this.revealedSymptoms.contains(question)) {
      this.revealedSymptoms.add(question);
      return true;
    }
    return false;
  }

  public void giveDiagnosis(final Condition condition) {
    this.diagnosedCondition = condition;
  }

  public void giveSuspectedDiagnosis(final Condition condition) {
    if (this.suspectConditions == null) {
      this.suspectConditions = new LinkedList<Condition>();
    }

    if (!this.suspectConditions.contains(condition)) {
      this.suspectConditions.add(condition);
    }
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof PatientFile) {
      return (this.name == ((PatientFile) obj).name);
    }
    return false;
  }

  /**
   * synchronized shouldn't be necessary because this method is called only in
   * one place, which is already in a "synchronized"(it's synchronized by the
   * atomicity of the "remove" operation of CopyOnWriteArray) block
   */
  public void checkout() {
    this.timesCheckedOut++;
  }

  /**
   * given that the value is never written in any non-thread safe context(check
   * comments for void checkout() ) a synchronized would be redundant.
   */
  public int timesCheckedOut() {
    return this.timesCheckedOut;
  }

  /*
   * will only be used for reading, NEVER WRITING
   */
  public List<Symptom> getSymptoms() {
    return this.revealedSymptoms;
  }
}
