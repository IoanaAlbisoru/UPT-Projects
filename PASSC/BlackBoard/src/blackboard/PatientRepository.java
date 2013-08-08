package blackboard;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import blackboard.knowledge.Symptom;

/**
 * 
 *
 */
public class PatientRepository implements BlackBoard<PatientFile> {
  private static final int MAX_NUMBER_OF_CONSULTATIONS = 5;

  private static PatientRepository instance = null;

  private final List<PatientFile> undiagnosedPatiens;
  private final List<PatientFile> diagnosedPatiens;
  private final List<PatientFile> deadPeopleWalking;
  private PatientGenerator generator;

  private PatientRepository() {
    this.undiagnosedPatiens = new CopyOnWriteArrayList<PatientFile>();
    this.diagnosedPatiens = new CopyOnWriteArrayList<PatientFile>();
    this.deadPeopleWalking = new CopyOnWriteArrayList<PatientFile>();
  }

  public static PatientRepository getInstance() {
    if (PatientRepository.instance == null) {
      PatientRepository.instance = new PatientRepository();
      PatientRepository.instance.startGenerator();
      return PatientRepository.instance;
    }
    return PatientRepository.instance;
  }

  private void startGenerator() {
    this.generator = new PatientGenerator();
    new Thread(this.generator).start();
  }

  /**
   * This class randomly generate NUMBER_OF_PATIENTS and adds them to the
   * PatientRepository
   */
  private class PatientGenerator implements Runnable {

    private final Random random;
    private final int NUMBER_OF_PATIENTS = 20;

    public PatientGenerator() {
      this.random = new Random();
    }

    @Override
    public void run() {
      int pick;
      for (int i = 0; i < this.NUMBER_OF_PATIENTS; i++) {
        final List<Symptom> partial = new LinkedList<Symptom>();
        final List<Symptom> complete = new LinkedList<Symptom>();

        final int size = this.random.nextInt(Symptom.values().length - 4) + 3;
        while (complete.size() < size) {
          pick = this.random.nextInt(Symptom.values().length);
          if (complete.contains(Symptom.values()[pick]) || (Symptom.values()[pick] == Symptom.f)) {
            continue;
          }
          complete.add(Symptom.values()[pick]);
        }

        partial.add(complete.get(0));

        final PatientFile tempPatient = new PatientFile(UUID.randomUUID().toString(), partial, complete);

        PatientRepository.instance.addPatient(tempPatient);
      }
    }
  }/*
    * END Class PatientGenerator.
    * ----------------------------------------------------------------------
    */

  private void addPatient(final PatientFile file) {
    this.undiagnosedPatiens.add(file);
  }

  /*
   * (non-Javadoc) This is where the Repository has to check in which category
   * it will place the new updated PatientFile. There is a limit on the maximum
   * number of doctors that a patient can visit, if that limit is reached then
   * he is removed from the list of diagnosable patients.
   */
  @Override
  public void update(final PatientFile newInfo) {
    if (newInfo.isDiagnosed()) {
      this.diagnosedPatiens.add(newInfo);
      return;
    }

    if (newInfo.timesCheckedOut() == PatientRepository.MAX_NUMBER_OF_CONSULTATIONS) {
      this.deadPeopleWalking.add(newInfo);
      return;
    }

    this.undiagnosedPatiens.add(newInfo);
  }

  /*
   * ALL NECESSARY SYNCHRONIZATION OF THREADS IS DONE HERE. IF I AM CORRECT NO
   * TWO THREADS SHOULD EVER BE ABLE TO GET HOLD OF THE SAME PatientFile, thus
   * no synchronization is ever needed when trying to mutate a PatientFile. The
   * knowledge parameter supplies the algorithm for determining what object
   * should be used by the knowledge source, but the responsibility of ENSURING
   * THAT THE OBJECT IS NOT given to two separate threads rests on the
   * PatientRepository.
   */
  @Override
  public PatientFile inspect(final Inspector<PatientFile> knowledge) {
    final PatientFile temp = knowledge.inspect(this.undiagnosedPatiens);
    /*
     * if remove() returns true then it means that the Patient was still in the
     * collection, hence it wasn't removed by another doctor. This operation is
     * always executed atomically, so there is no danger of some bollocks result
     * in case of two Doctors trying to remove the same patient at the same time
     */
    if (this.undiagnosedPatiens.remove(temp)) {
      temp.checkout();
      return temp;
    }

    return null;
  }

  /*
   * Led Zeppelin - The Rover There can be no denyin', that the wind will shake
   * 'em down.
   */
  @Override
  public String toString() {
    String temp = "----------\nundiagnosed patients:\n----------\n";

    temp += "Total number of undiagnosed patients: " + this.undiagnosedPatiens.size() + "\n";
    Iterator<PatientFile> it = this.undiagnosedPatiens.iterator();
    while (it.hasNext()) {
      final PatientFile patientFile = it.next();
      temp += patientFile + "\n-\n";
    }

    temp += "----------\ndiagnosed patients:\n----------\n";
    temp += "Total number: " + this.diagnosedPatiens.size() + "\n";
    it = this.diagnosedPatiens.iterator();
    while (it.hasNext()) {
      final PatientFile patientFile = it.next();
      temp += patientFile + "\n-\n";
    }

    // And the wind is crying!! Of a love that won't grow cold.
    temp += "----------\ndead people walkings:\n----------\n";
    temp += "Total number: " + this.deadPeopleWalking.size() + "\n";
    it = this.deadPeopleWalking.iterator();
    while (it.hasNext()) {
      final PatientFile patientFile = it.next();
      temp += patientFile + "\n-\n";
    }

    return temp;
  }

}
