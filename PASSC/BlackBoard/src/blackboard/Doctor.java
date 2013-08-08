package blackboard;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import blackboard.knowledge.Condition;
import blackboard.knowledge.Symptom;

/**
 */
public class Doctor implements Runnable {
  private final Map<Condition, List<Symptom>> knowledge;
  private final Inspector<PatientFile> inspector = new House();

  public Doctor(final Map<Condition, List<Symptom>> knowledge) {
    this.knowledge = knowledge;
  }

  /**
   * A patient is considered to be in the area of expertise of the doctor if the
   * doctor can associate ALL the Symptoms of the patient to different
   * Conditions of his knowledge. Example: if the patient has symptoms a,b the
   * doctor need to have knowledge of at least one Condition that exhibits
   * symptom a AND at least one Condition that exhibits symptom b; the symptom
   * can be associated to different Conditions
   * 
   * @param patient
   * @return true: if 'patient' is in the area of expertise of the doctor false:
   *         otherwise
   */
  private boolean areaOfExpertise(final PatientFile patient) {
    final List<Symptom> flatList = new LinkedList<Symptom>();

    // we pool all the symptoms known to this doctor in one list
    // then compare it with the list of known symptoms that the
    // patient is exhibiting.
    for (final List<Symptom> symptomList : this.knowledge.values()) {
      flatList.addAll(symptomList);
    }

    try {
      return flatList.containsAll(patient.getSymptoms());
    } catch (final Exception e) {
      return false;
    }
  }

  private class House implements Inspector<PatientFile> {

    /*
     * Here we have an algorithm that determines whether or not a patient can be
     * diagnosed with our expertise THIS SHOULD EXECUTE ATOMICALLY, BAD IDEEA
     * FOR SCALABILITY
     */
    @Override
    public PatientFile inspect(final Collection<PatientFile> data) {
      final Iterator<PatientFile> it = data.iterator();

      while (it.hasNext()) {
        final PatientFile patient = it.next();
        if (Doctor.this.areaOfExpertise(patient)) {
          return patient;
        }
      }
      return null;
    }
  }

  private static final int MAX_NUMBER_OF_QUESTION = 3;

  /**
   * @param patient
   */
  private void diagnose(final PatientFile patient) {
    List<Symptom> bestGuess = null;
    double maxOverlap = 0.0;
    double tempOverlap;

    for (int i = 0; i < this.knowledge.size(); i++) {
      // we calculate the best overlap between the patients symptoms and
      // the doctors knowledge. We store the best overlaping list of
      // symptoms
      // for
      // later use.
      for (final List<Symptom> drKnowledge : this.knowledge.values()) {
        tempOverlap = this.computeOverlap(drKnowledge, patient.getSymptoms());
        if ((tempOverlap > maxOverlap) && (bestGuess != drKnowledge)) {
          bestGuess = drKnowledge;
          maxOverlap = tempOverlap;
        }
      }

      // we weed out all the symptoms that the patient already has,
      // and ask only about those that he doesn't have.
      int numberOfQuestions = 0;
      for (final Symptom question : bestGuess) {
        if (!patient.getSymptoms().contains(question)) {
          patient.askAboutSympton(question);
          numberOfQuestions++;
          if (numberOfQuestions == Doctor.MAX_NUMBER_OF_QUESTION) {
            return;
          }
        }
      }
      // we check the overlap between the doctor's best guess and the
      // patient's
      // symptoms if the overlap is complete then the doctor will go on
      // and
      // diagnose the
      // patient if not he will write down the best guess as his
      // suspicion.
      for (final Condition condition : this.knowledge.keySet()) {
        if (this.knowledge.get(condition) == bestGuess) {
          if (this.computeOverlap(bestGuess, patient.getSymptoms()) == 1) {
            patient.giveDiagnosis(condition);
          } else {
            patient.giveSuspectedDiagnosis(condition);
          }
        }
      }
    }

  }

  /**
   * @param list1
   * @param list2
   * @return a double value ranging from 0.0 to 1 which indicates the degree of
   *         overlap between list1 and list2. 1: meaning total overlap; and 0:
   *         none.
   */
  private double computeOverlap(final List<Symptom> list1, final List<Symptom> list2) {
    int contained = 0;
    double overlap = 0.0;

    if ((list1 == null) || (list2 == null)) {
      return 0.0;
    }

    if (list1.isEmpty() || list2.isEmpty()) {
      return 0.0;
    }

    for (final Symptom s : list1) {
      if (list2.contains(s)) {
        contained++;
      }
    }

    final int max = (list1.size() > list2.size()) ? list1.size() : list2.size();

    overlap = ((double) contained) / max;

    return overlap;
  }

  @Override
  public void run() {
    PatientFile patient = null;
    final BlackBoard<PatientFile> repository = PatientRepository.getInstance();

    for (;;) {

      patient = repository.inspect(this.inspector);
      if (patient == null) {

        if (Thread.currentThread().isInterrupted()) {
          return;
        }

        continue;
      }
      this.diagnose(patient);
      repository.update(patient);

    }// END INFINITE LOOP
  }
}
