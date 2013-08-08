package remoting.proxy;

import remoting.RemoteInterface;

import commons.Address;

public class DynamicProxyGenerator implements ProxyGenerator {

  @Override
  public RemoteInterface generateStub(final String interfaceName, final Address addr) throws Exception {
    final RemoteInterface stub = DynamicStub.newProxy(interfaceName, addr);
    return stub;
  }

  @Override
  public SkeletonInterface generateSkeleton(final RemoteInterface obj) throws Exception {
    final DynamicSkeleton skel = new DynamicSkeleton(obj);
    skel.start();
    return skel;
  }

}
