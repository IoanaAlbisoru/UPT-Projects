package testProxy;

import junit.framework.Assert;
import junit.framework.TestCase;
import registry.Entry;
import remoting.RemoteInterface;
import remoting.proxy.DynamicProxyGenerator;
import remoting.proxy.ProxyGenerator;

public class TestDynamicProxys extends TestCase {

  public void testArgMethod() {
    try {
      final ProxyGenerator generator = new DynamicProxyGenerator();

      final MockRemoteInterface targetObj = new MockRemoteObject();

      generator.generateSkeleton(targetObj);

      final Class<?>[] test = targetObj.getClass().getInterfaces();
      Class<?> implRemInt = null;
      for (final Class<?> clazz : test) {
        if (RemoteInterface.class.isAssignableFrom(clazz)) {
          implRemInt = clazz;
        }
      }

      final MockRemoteInterface stub = (MockRemoteInterface) generator.generateStub(implRemInt.getName(), new Entry(
          "127.0.0.1", 1500));

      stub.methodWithNoReturn();
      Assert.assertTrue(true);

      Assert.assertEquals(MockRemoteObject.RETURN_VALUE, stub.methodWithPrimitiveReturn());

      final String str = "testString";
      final int testInt = 10;
      final String result = "testString10";

      Assert.assertEquals(result, stub.methodWithReturn(str, testInt));
      Assert.assertEquals("null0", stub.methodWithReturn(null, 0));
    } catch (final Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }
}
