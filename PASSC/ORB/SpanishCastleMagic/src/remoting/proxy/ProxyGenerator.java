package remoting.proxy;

import remoting.RemoteInterface;

import commons.Address;

public interface ProxyGenerator {
  RemoteInterface generateStub(String interfaceName, Address addr) throws Exception;

  SkeletonInterface generateSkeleton(RemoteInterface obj) throws Exception;
}
