package blackboard;

import java.lang.reflect.Method;

import junit.framework.Assert;

final class TestUtils {

  static final double DELTA = 0.01;

  static Method getPrivateMethod(final Class<?> clazz, final String name, final Class<?>[] pTypes) {
    Method method = null;

    try {
      method = clazz.getDeclaredMethod(name, pTypes);
      method.setAccessible(true);
      return method;
    } catch (final Exception e) {
      Assert.fail("@getPrivateMethod:: " + clazz.getClass().getName() + "." + name + "()");
    }
    return null;
  }
}
