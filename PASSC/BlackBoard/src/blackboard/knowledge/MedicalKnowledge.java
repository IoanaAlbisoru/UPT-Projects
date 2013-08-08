package blackboard.knowledge;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public final class MedicalKnowledge {
  public static final HashMap<Condition, List<Symptom>> knowledge;
  static {
    knowledge = new HashMap<Condition, List<Symptom>>();

    List<Symptom> list = new LinkedList<Symptom>();
    list.add(Symptom.a);
    list.add(Symptom.b);
    MedicalKnowledge.knowledge.put(Condition.AB, list);

    list = new LinkedList<Symptom>();
    list.add(Symptom.a);
    list.add(Symptom.b);
    list.add(Symptom.c);
    list.add(Symptom.d);
    MedicalKnowledge.knowledge.put(Condition.ABCD, list);

    list = new LinkedList<Symptom>();
    list.add(Symptom.a);
    list.add(Symptom.b);
    list.add(Symptom.e);
    MedicalKnowledge.knowledge.put(Condition.ABE, list);

    list = new LinkedList<Symptom>();
    list.add(Symptom.a);
    list.add(Symptom.b);
    list.add(Symptom.c);
    list.add(Symptom.e);
    MedicalKnowledge.knowledge.put(Condition.ABCE, list);

    list = new LinkedList<Symptom>();
    list.add(Symptom.b);
    list.add(Symptom.c);
    list.add(Symptom.e);
    MedicalKnowledge.knowledge.put(Condition.BCE, list);

    list = new LinkedList<Symptom>();
    list.add(Symptom.a);
    list.add(Symptom.b);
    list.add(Symptom.c);
    list.add(Symptom.d);
    list.add(Symptom.e);
    MedicalKnowledge.knowledge.put(Condition.ABCDE, list);

    list = new LinkedList<Symptom>();
    list.add(Symptom.a);
    list.add(Symptom.b);
    list.add(Symptom.c);
    list.add(Symptom.d);
    list.add(Symptom.e);
    list.add(Symptom.f);
    MedicalKnowledge.knowledge.put(Condition.ABCDEF, list);

    list = new LinkedList<Symptom>();
    list.add(Symptom.c);
    list.add(Symptom.d);
    list.add(Symptom.e);
    MedicalKnowledge.knowledge.put(Condition.CDE, list);
    list = null;
  };
}
