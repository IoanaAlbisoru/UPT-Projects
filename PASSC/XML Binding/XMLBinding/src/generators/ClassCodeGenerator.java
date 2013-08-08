package generators;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ClassCodeGenerator {

  public static Class<?> generateClassCode(final String xmlFileName) throws Exception {
    final Document doc = new XMLLoader().loadXML(xmlFileName);
    if (doc == null) {
      throw new Exception();
    }
    return new ClassCodeGenerator().processXML(doc);
  }

  private Class<?> processXML(final Document doc) throws Exception {
    final Element root = doc.getDocumentElement();

    String className = root.getAttribute("className");
    String path = "d:/Workspace/Dropbox/Workspace/School/PASSC/XMLBinding/src/";

    String packageName = "";
    // it means we have to create the packages
    if (className.contains(".")) {
      final String[] strings = className.split("\\.");
      className = strings[strings.length - 1];

      for (int i = 0; i < (strings.length - 1); i++) {
        path += strings[i] + "/";
        packageName = strings[i] + ".";
      }
      packageName = packageName.substring(0, packageName.length() - 1);
    }
    String text = "";
    if (!packageName.equals("")) {
      text += "package " + packageName + ";\n\n";
    }
    text += "public class " + className + "{\n";

    final NodeList fields = root.getElementsByTagName("field");

    for (int i = 0; i < fields.getLength(); i++) {
      final Element field = (Element) fields.item(i);
      text += "public " + field.getAttribute("fieldType") + " " + field.getAttribute("fieldName") + ";\n";

    }
    text += "}";
    try {
      this.createJavaFile(className, text, path);
      return this.compileAndLoad(packageName, className, path);
      // return packageName + "." + className;
    } catch (final Exception e) {
      throw e;
    }
  }

  private void createJavaFile(final String className, final String text, final String path) throws Exception {

    final File tempPath = new File(path);
    if (!tempPath.exists()) {
      tempPath.mkdir();
    }

    try {
      final String fileName = path + className + ".java";
      final FileWriter outputFile = new FileWriter(fileName);
      outputFile.write(text);
      outputFile.close();
    } catch (final Exception e) {
      if (!tempPath.exists()) {
        tempPath.delete();
      }
      throw e;
    }
  }

  private Class<?> compileAndLoad(final String packageName, final String className, final String path)
      throws IllegalStateException, ClassNotFoundException {
    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    if (compiler == null) {
      throw new IllegalStateException("Cannot find the system Java compiler. "
          + "Check that your class path includes tools.jar");
    }

    final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

    final File[] files = new File[] { new File(path + className + ".java") };

    final Iterable<? extends JavaFileObject> compilationUnits1 = fileManager.getJavaFileObjectsFromFiles(Arrays
        .asList(files));

    final String[] splits = packageName.split("\\.");
    String binPath = "";
    for (final String it : splits) {
      final String regEx = "/" + it;
      binPath = path.replaceAll(regEx, "");
    }
    binPath = binPath.replaceAll("src", "bin");
    final String[] options = new String[] { "-d", binPath };

    final CompilationTask task = compiler.getTask(null, fileManager, null, Arrays.asList(options), null,
        compilationUnits1);

    if (task.call()) {
      return this.loadClass(packageName + "." + className);
    }
    ;
    return null;
  }

  private Class<?> loadClass(final String name) throws ClassNotFoundException {
    return ClassLoader.getSystemClassLoader().loadClass(name);
  }

}
