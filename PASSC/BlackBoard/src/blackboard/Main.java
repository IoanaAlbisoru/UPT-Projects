package blackboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadFactory;

import blackboard.knowledge.Condition;
import blackboard.knowledge.MedicalKnowledge;
import blackboard.knowledge.Symptom;
import blackboard.utils.SimpleThreadFactory;

public final class Main {

  public static void main(final String[] args) {
    final BlackBoard<PatientFile> repo = PatientRepository.getInstance();
    final ThreadFactory factory = new SimpleThreadFactory();

    final Thread[] doctorThreads = new Thread[Condition.values().length];
    int index = 0;

    // we create some knowledge maps for doctors
    for (final Condition condition : Condition.values()) {
      final Random random = new Random();
      final Map<Condition, List<Symptom>> temp = new HashMap<Condition, List<Symptom>>();
      temp.put(condition, MedicalKnowledge.knowledge.get(condition));
      final int pick = random.nextInt(Condition.values().length);
      temp.put(Condition.values()[pick], MedicalKnowledge.knowledge.get(Condition.values()[pick]));
      doctorThreads[index++] = factory.newThread(new Doctor(temp));
    }

    for (final Thread doctorThread : doctorThreads) {
      doctorThread.start();
    }

    try {
      Thread.sleep(1000);
    } catch (final InterruptedException e) {
    }
    System.out.println(repo);

    for (final Thread doctorThread : doctorThreads) {
      doctorThread.interrupt();
    }
  }
}
