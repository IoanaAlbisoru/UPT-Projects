package remoting.proxy;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;

import remoting.Utils;

class SerializedMethodCall implements Serializable {
  private static final long serialVersionUID = -7806795760832752493L;
  private final byte[] methodName;
  private final byte[] arguments;
  private final byte[] argTypes;

  transient private static HashMap<String, Class<?>> primitiveTypes = new HashMap<String, Class<?>>();
  static {
    SerializedMethodCall.primitiveTypes.put("int", Integer.TYPE);
    SerializedMethodCall.primitiveTypes.put("float", Float.TYPE);
    SerializedMethodCall.primitiveTypes.put("double", Double.TYPE);
    SerializedMethodCall.primitiveTypes.put("byte", Byte.TYPE);
  }

  public SerializedMethodCall(final Method m, final Object[] args) throws Exception {
    try {
      this.methodName = Utils.toBytesNoLength(m.getName());
      this.arguments = Utils.toBytesNoLength(args);

      final Class<?>[] paramTypes = m.getParameterTypes();
      final String[] paramTypeNames = new String[paramTypes.length];
      for (int i = 0; i < paramTypes.length; i++) {
        paramTypeNames[i] = paramTypes[i].getName();
      }
      this.argTypes = Utils.toBytesNoLength(paramTypeNames);
    } catch (final IOException e) {
      throw new Exception("Cannot serialize method call " + m.getName());
    }
  }

  public Object[] getArguments() throws IOException, ClassNotFoundException {
    return (Object[]) Utils.toObject(this.arguments);
  }

  // public String getMethod() throws IOException, ClassNotFoundException {
  // return (String) Utils.toObject( this.methodName );
  // }
  public Method getMethod(final Object obj) throws IOException, ClassNotFoundException, SecurityException,
      NoSuchMethodException {
    final String name = (String) Utils.toObject(this.methodName);
    final Class<?>[] argTypes = this.getArgTypes();
    final Method toInvoke = obj.getClass().getMethod(name, argTypes);
    toInvoke.setAccessible(true);
    return toInvoke;
  }

  public Class<?>[] getArgTypes() throws ClassNotFoundException, IOException {
    final String[] typeNames = (String[]) Utils.toObject(this.argTypes);
    if (typeNames == null) {
      return null;
    }
    final Class<?>[] types = new Class<?>[typeNames.length];

    for (int i = 0; i < typeNames.length; i++) {
      if (SerializedMethodCall.primitiveTypes.containsKey(typeNames[i])) {
        types[i] = SerializedMethodCall.primitiveTypes.get(typeNames[i]);
      } else {
        types[i] = Class.forName(typeNames[i]);
      }
    }

    return types;
  }
}
