package clientCode;

import generators.ClassCodeGenerator;

import java.lang.reflect.Field;

public class Main {

  public static void main(final String args[]) {
    try {
      Main.loadClass("d:/Workspace/Dropbox/Workspace/School/PASSC/XMLFiles2.0/dots.xsd");
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  private static void loadClass(final String classFileName) throws Exception {
    System.out.println("\n---load class---");
    Class<?> classResult = ClassCodeGenerator.generateClassCode(classFileName);
    System.out.println(String.format("Class succesfully created: '%s'", classResult.getName()));

    Field[] fields = classResult.getFields();
    for (final Field f : fields) {
      System.out.println(f.getGenericType() + " " + f.getName());
    }

    // ---------------------------------------------------
    System.out.println("-----------------------\n");
    classResult = classResult.getDeclaredClasses()[0];
    System.out.println(String.format("Class succesfully created: '%s'", classResult.getName()));

    fields = classResult.getFields();
    for (final Field f : fields) {
      System.out.println(f.getGenericType() + " " + f.getName());
    }
  }
}
