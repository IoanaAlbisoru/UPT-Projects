package clientCode;

import generators.ClassCodeGenerator;
import generators.ObjectLoader;

import java.lang.reflect.Field;

public class Main {

  public static void main(final String args[]) {
    try {
      Main.loadClass("d:/Workspace/Dropbox/Workspace/School/PASSC/XMLFiles/DataClass.xml");
      Main.loadObject("d:/Workspace/Dropbox/Workspace/School/PASSC/XMLFiles/SomeObject.xml");
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  private static void loadClass(final String classFileName) throws Exception {
    System.out.println("\n---load class---");
    final Class<?> classResult = ClassCodeGenerator.generateClassCode(classFileName);
    System.out.println(String.format("Class succesfully created: '%s'", classResult.getName()));

    final Field[] fields = classResult.getFields();
    for (final Field f : fields) {
      System.out.println(f.getGenericType() + " " + f.getName());
    }
  }

  private static void loadObject(final String objFileName) throws Exception {
    System.out.println("\n---load object---");
    final Object objResult = ObjectLoader.createObjectFromXMLfile(objFileName);
    final Field[] fields = objResult.getClass().getFields();

    for (final Field f : fields) {
      System.out.println(f.getName() + "=" + f.get(objResult));
    }

  }
}
