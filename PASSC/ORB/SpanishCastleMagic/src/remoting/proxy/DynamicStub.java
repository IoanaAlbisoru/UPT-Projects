package remoting.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import remoting.RemoteInterface;
import remoting.Utils;
import requestReply.Requestor;

import commons.Address;

class DynamicStub implements InvocationHandler {

  public static RemoteInterface newProxy(final String interfaceName, final Address addr) throws ClassNotFoundException {

    final Class<?> intClass = Class.forName(interfaceName);
    // for some reason intClass.getInterfaces() only return an array
    // containing RemoteInterface
    final Class<?>[] temp = new Class<?>[] { intClass };

    return (RemoteInterface) Proxy.newProxyInstance(intClass.getClassLoader(), temp, new DynamicStub(addr));
  }

  /**
   * The address of the actual object
   */
  private final Address address;
  private final Requestor request;

  private DynamicStub(final Address addr) {
    this.address = addr;
    this.request = new Requestor();
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
    final SerializedMethodCall temp = new SerializedMethodCall(method, args);
    final byte[] serializedMethod = Utils.toBytes(temp);

    final byte[] binaryResult = this.request.deliver_and_wait_feedback(this.address, serializedMethod);
    final Object result = Utils.toObject(binaryResult);
    return result;
  }
}
