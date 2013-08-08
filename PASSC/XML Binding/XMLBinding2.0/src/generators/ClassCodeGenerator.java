package generators;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ClassCodeGenerator {

  public static Class<?> generateClassCode(final String xmlFileName) throws Exception {
    final Document doc = XMLLoader.loadXML(xmlFileName);
    if (doc == null) {
      throw new Exception();
    }
    return new ClassCodeGenerator().processXML(doc);
  }

  private Class<?> processXML(final Document doc) {

    final NodeList elements = doc.getElementsByTagName("xs:element");
    final DataClassModel classModel = this.createClassModel(elements);
    try {
      final String path = "d:/Workspace/Dropbox/Workspace/School/PASSC/XMLBinding2.0/src/";
      this.createJavaFile(classModel, path);
      return this.compileAndLoad(classModel.getName(), path);
    } catch (final Exception e) {
      e.printStackTrace();
      return null;
    }

  }

  private void createJavaFile(final DataClassModel model, final String path) throws Exception {
    try {
      final String fileName = path + model.getName() + ".java";
      final FileWriter outputFile = new FileWriter(fileName);
      outputFile.write(model.toString());
      outputFile.close();
    } catch (final Exception e) {
      throw e;
    }
  }

  private DataClassModel createClassModel(final NodeList elements) {
    DataClassModel toReturn = null;

    for (int i = 0; i < elements.getLength(); i++) {
      final Node e = elements.item(i);
      final NamedNodeMap attr = e.getAttributes();
      final String className = attr.getNamedItem("name").getNodeValue();

      final DataClassModel clazz = new DataClassModel(className);
      final Node maxBounds = attr.getNamedItem("maxOccurs");
      final Node minBounds = attr.getNamedItem("minOccurs");
      clazz.setMaxOC(this.determineBounds(maxBounds, true));
      clazz.setMinOC(this.determineBounds(minBounds, false));

      final Document doc = e.getOwnerDocument();
      final NodeList fieldNodes = doc.getElementsByTagName("xs:attribute");

      for (int j = 0; j < fieldNodes.getLength(); j++) {
        final Node field = fieldNodes.item(j);
        final NamedNodeMap fieldAttr = field.getAttributes();
        final Node name = fieldAttr.getNamedItem("name");
        final Node type = fieldAttr.getNamedItem("type");

        if (toReturn != null) {
          clazz.addField(name.getNodeValue(), this.determineType(type));
        }
      }

      if (toReturn == null) {
        toReturn = clazz;
      } else {
        toReturn.addNestedClass(clazz);
      }

    }
    return toReturn;
  };

  private final static Map<String, String> XSDtoJavaTypeMap = new HashMap<String, String>();
  static {
    ClassCodeGenerator.XSDtoJavaTypeMap.put("xs:integer", "int");
  }

  private String determineType(final Node nType) {
    return ClassCodeGenerator.XSDtoJavaTypeMap.get(nType.getNodeValue());
  }

  private int determineBounds(final Node bound, final boolean max) {
    if (bound == null) {
      return -1;
    }
    final String value = bound.getNodeValue();
    if (max) {
      if (value.equals("unbounded")) {
        return DataClassModel.UNBOUNDED;
      } else {
        return new Integer(value);
      }
    } else {
      return new Integer(value);
    }
  }

  private Class<?> compileAndLoad(final String className, final String path) throws IllegalStateException,
      ClassNotFoundException {
    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    if (compiler == null) {
      throw new IllegalStateException("Cannot find the system Java compiler. "
          + "Check that your class path includes tools.jar");
    }

    final StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

    final File[] files = new File[] { new File(path + className + ".java") };

    final Iterable<? extends JavaFileObject> compilationUnits1 = fileManager.getJavaFileObjectsFromFiles(Arrays
        .asList(files));

    // String[] splits = packageName.split( "\\." );
    // String binPath = "";
    // for (String it : splits ) {
    // String regEx = "/" + it;
    // binPath = path.replaceAll( regEx, "" );
    // }
    final String binPath = path.replaceAll("src", "bin");
    final String[] options = new String[] { "-d", binPath };

    final CompilationTask task = compiler.getTask(null, fileManager, null, Arrays.asList(options), null,
        compilationUnits1);

    if (task.call()) {
      return this.loadClass(className);
    }
    ;
    return null;
  }

  private Class<?> loadClass(final String name) throws ClassNotFoundException {
    return ClassLoader.getSystemClassLoader().loadClass(name);
  }

}
