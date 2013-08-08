package testutils;

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;

public class TestUtil {
  public static String constructPathFromClass(Class<?> clazz) {
    File tempFile = new File(".");
    String temp = tempFile.getAbsolutePath() + "/subjects/";
    String fullName = clazz.getName();
    temp += fullName.replace('.', '/');
    return temp;
  }

  public static void createTestFiles(String pathname, Collection<String> content, String namePattern) {
    try {
      File f = new File(pathname);

      int index = 0;
      for (String string : content) {
        File tempFile;
        if (index < 10)
          tempFile = new File(f.getAbsolutePath() + "/" + namePattern + "0" + index++);
        else
          tempFile = new File(f.getAbsolutePath() + "/" + namePattern + index++);
        if (!tempFile.createNewFile())
          return;
        FileWriter out = new FileWriter(tempFile);
        out.write(string);
        out.close();
      }
    } catch (Exception e) {
    }
    ;
  }

  public static void checkTestFileForClass(Class<?> clazz, String fileName) {
    String path = TestUtil.constructPathFromClass(clazz);
    path += "/" + fileName;
    File tempFile = new File(path);
    if (!tempFile.exists())
      throw new RuntimeException("Could not find test file: " + path);
  }

  public static void main(String args[]) {
  }
}
