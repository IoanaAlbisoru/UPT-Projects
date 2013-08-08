package generators;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ObjectLoader {

  static public Object createObjectFromXMLfile(final String xmlFileName) throws Exception {

    final Document doc = new XMLLoader().loadXML(xmlFileName);
    if (doc == null) {
      throw new Exception();
    }

    return ObjectLoader.processXML(doc);
  }

  private static Object processXML(final Document doc) throws Exception {
    final Element root = doc.getDocumentElement();

    final String className = root.getAttribute("className");
    final Class<?> clazz = Class.forName(className);
    final Object object = clazz.newInstance();

    final NodeList fields = root.getElementsByTagName("field");

    for (int i = 0; i < fields.getLength(); i++) {
      final Element field = (Element) fields.item(i);
      final String fieldName = field.getAttribute("fieldName");
      final String strValue = field.getAttribute("fieldValue");

      final Field objField = object.getClass().getField(fieldName);
      objField.set(object, ObjectLoader.fieldValueFactory(objField.getType(), strValue));
    }

    return object;
  }

  private static HashMap<Class<?>, Class<?>> primitiveTypeMap = new HashMap<Class<?>, Class<?>>();
  static {
    ObjectLoader.primitiveTypeMap.put(int.class, Integer.class);
    ObjectLoader.primitiveTypeMap.put(double.class, Double.class);
    ObjectLoader.primitiveTypeMap.put(float.class, Float.class);
    ObjectLoader.primitiveTypeMap.put(boolean.class, Boolean.class);
  }

  private static Object fieldValueFactory(Class<?> type, final String value) throws Exception {

    // we replace the primitive types with their wrapper classes;
    if (ObjectLoader.primitiveTypeMap.containsKey(type)) {
      type = ObjectLoader.primitiveTypeMap.get(type);
    }

    final Constructor<?> ctor = type.getConstructor(new Class<?>[] { String.class });
    final Object temp = ctor.newInstance(new Object[] { value });
    return temp;
  }
}
