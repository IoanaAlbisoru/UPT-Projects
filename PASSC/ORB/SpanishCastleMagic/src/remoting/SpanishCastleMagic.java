package remoting;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import registry.Entry;
import remoting.exceptions.RemotingException;
import remoting.proxy.DynamicProxyGenerator;
import remoting.proxy.ProxyGenerator;
import remoting.proxy.SkeletonInterface;
import requestReply.ByteStreamTransformer;
import requestReply.Replyer;
import requestReply.Requestor;

import commons.Address;

import errors.ByteMessages;

public class SpanishCastleMagic {

  // private static byte[] errorByteStream = new byte[] { 0, 0, 0, 4, -127,
  // -127,
  // -127, -127 };
  // private static byte[] errorByteStreamNoLength = new byte[] { -127, -127,
  // -127, -127 };

  private final static Address getLocalAddress(final int port) {
    Address addr = null;
    try {
      addr = new Entry(InetAddress.getLocalHost().getHostAddress(), port);
    } catch (final UnknownHostException e) {
      e.printStackTrace();
      System.exit(0);
    }
    return addr;
  }

  private static ProxyGenerator proxyGenerator = new DynamicProxyGenerator();

  /**
   * This method uses a {@link ProxyGenerator} to create a proxy for the object
   * By default the proxy generator is the {@link DynamicProxyGenerator}
   * 
   * @param name
   *          name under which the object is to be bound
   * @param toRegister
   *          object to register
   * @param where
   *          the address where the registry is running
   * @return
   */
  public static final boolean bind(final String name, final RemoteInterface toRegister, final Address where) {

    final Requestor requestor = new Requestor();
    SkeletonInterface skel = null;
    byte data[] = null;
    try {

      skel = SpanishCastleMagic.proxyGenerator.generateSkeleton(toRegister);
      final Address addrOfSkel = skel.getAddr();

      final Class<?>[] test = toRegister.getClass().getInterfaces();
      Class<?> implRemInt = null;
      for (final Class<?> clazz : test) {
        if (RemoteInterface.class.isAssignableFrom(clazz)) {
          implRemInt = clazz;
        }
      }

      if (implRemInt == null) {
        throw new RemotingException("Invalid Object Type");
      }

      final PackagedData temp = new PackagedData(name, implRemInt.getName(), addrOfSkel);
      data = Utils.toBytes(temp);
      final byte[] response = requestor.deliver_and_wait_feedback(where, data);

      if (ByteMessages.isError(response)) {
        System.out.println(String.format("Bind failed for '%s'.", name));
        skel.stop();
        return false;
      } else {
        System.out.println(String.format("Bind succeded for '%s'.", name));
        return true;
      }

    } catch (final Exception e) {
      System.out.println(String.format("Bind failed for '%s'.", name));
      try {
        skel.stop();
      } catch (final Exception ignore) {
        return false;
      }
      return false;
    }
  }

  public static final RemoteInterface lookup(final String name, final Address where) {
    final Requestor requestor = new Requestor();
    final Address newAddress = new Entry(where.dest(), where.port() + 1);

    byte[] result;
    try {
      result = requestor.deliver_and_wait_feedback(newAddress, Utils.toBytes(name));

      if (ByteMessages.isError(result)) {
        return null;
      }

      final PackagedData repo = (PackagedData) Utils.toObject(result);
      final String interfaceName = repo.getInterfaceName();
      final Address addrSkel = repo.getAddressOfSkeleton();
      final RemoteInterface stub = SpanishCastleMagic.proxyGenerator.generateStub(interfaceName, addrSkel);
      return stub;
    } catch (final IOException e1) {
      return null;
    } catch (final ClassNotFoundException e) {
      return null;
    } catch (final Exception e) {
      return null;
    }
  }

  public static final boolean unbind(final String name, final Address where) {
    final Requestor requestor = new Requestor();

    final Address newAddress = new Entry(where.dest(), where.port() - 1);
    try {
      final byte[] result = requestor.deliver_and_wait_feedback(newAddress, Utils.toBytes(name));

      if (ByteMessages.isError(result)) {
        return false;
      }
    } catch (final IOException e) {
      return false;
    }

    return true;

  };

  public static final Registry getRegistry(final Address address) {
    return new WrapperRegistry(address);
  }

  private static InnerRegistry reg = null;

  public static void createRegistry() {
    SpanishCastleMagic.reg = new InnerRegistry(2307);
    SpanishCastleMagic.reg.start();
  }

  public static void destroyRegistry() {
    SpanishCastleMagic.reg.stop();
  }

  private static final class WrapperRegistry implements Registry {
    private final Address regAddress;

    private WrapperRegistry(final Address regAddress) {
      this.regAddress = regAddress;
    }

    @Override
    public final boolean bind(final String name, final RemoteInterface toBind) {
      return SpanishCastleMagic.bind(name, toBind, this.regAddress);
    }

    @Override
    public final RemoteInterface lookup(final String name) {
      return SpanishCastleMagic.lookup(name, this.regAddress);
    }

    @Override
    public final boolean unbind(final String name) {
      return SpanishCastleMagic.unbind(name, this.regAddress);
    }
  }

  private static final class PackagedData implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -5634217783088580863L;
    private final byte[] addressOfSkeleton;
    private final byte[] interfaceName;
    private final byte[] registeredName;

    public PackagedData(final String regName, final String intName, final Address addr) throws IOException {
      this.interfaceName = Utils.toBytesNoLength(intName);
      this.addressOfSkeleton = Utils.toBytesNoLength(addr);
      this.registeredName = Utils.toBytesNoLength(regName);
    }

    public Address getAddressOfSkeleton() throws IOException, ClassNotFoundException {
      return (Address) Utils.toObject(this.addressOfSkeleton);
    }

    public String getInterfaceName() throws IOException, ClassNotFoundException {
      return (String) Utils.toObject(this.interfaceName);
    }

    public String getRegisteredName() throws IOException, ClassNotFoundException {
      return (String) Utils.toObject(this.registeredName);
    }
  }// end packaged data

  private static final class InnerRegistry {
    private final Address bindAddress;
    private final Address unbindAddress;
    private final Address lookupAddress;
    private final Replyer bindReplier;
    private final Replyer unbindReplier;
    private final Replyer lookupReplier;

    private final ConcurrentHashMap<String, PackagedData> objectMap = new ConcurrentHashMap<String, PackagedData>();
    private final Hashtable<Replyer, ByteStreamTransformer> replyerTransformMap = new Hashtable<Replyer, ByteStreamTransformer>();

    private static int SPANISH_CASTLE_MAGIC_PORT_BIND;
    private static int SPANISH_CASTLE_MAGIC_PORT_LOOKUP;
    private static int SPANISH_CASTLE_MAGIC_PORT_UNBIND;

    private InnerRegistry() {
      InnerRegistry.SPANISH_CASTLE_MAGIC_PORT_BIND = 2307;
      InnerRegistry.SPANISH_CASTLE_MAGIC_PORT_LOOKUP = InnerRegistry.SPANISH_CASTLE_MAGIC_PORT_BIND + 1;
      InnerRegistry.SPANISH_CASTLE_MAGIC_PORT_UNBIND = InnerRegistry.SPANISH_CASTLE_MAGIC_PORT_BIND - 1;

      this.bindAddress = SpanishCastleMagic.getLocalAddress(InnerRegistry.SPANISH_CASTLE_MAGIC_PORT_BIND);
      this.unbindAddress = SpanishCastleMagic.getLocalAddress(InnerRegistry.SPANISH_CASTLE_MAGIC_PORT_UNBIND);
      this.lookupAddress = SpanishCastleMagic.getLocalAddress(InnerRegistry.SPANISH_CASTLE_MAGIC_PORT_LOOKUP);

      this.bindReplier = new Replyer(this.bindAddress);
      this.unbindReplier = new Replyer(this.unbindAddress);
      this.lookupReplier = new Replyer(this.lookupAddress);

      this.replyerTransformMap.put(this.bindReplier, new TransformerBind());
      this.replyerTransformMap.put(this.lookupReplier, new TransformerLookup());
      this.replyerTransformMap.put(this.unbindReplier, new TransformerUnbind());

    }

    private InnerRegistry(final int port) {
      InnerRegistry.SPANISH_CASTLE_MAGIC_PORT_BIND = port;
      InnerRegistry.SPANISH_CASTLE_MAGIC_PORT_UNBIND = InnerRegistry.SPANISH_CASTLE_MAGIC_PORT_BIND - 1;
      InnerRegistry.SPANISH_CASTLE_MAGIC_PORT_LOOKUP = InnerRegistry.SPANISH_CASTLE_MAGIC_PORT_BIND + 1;

      this.bindAddress = SpanishCastleMagic.getLocalAddress(InnerRegistry.SPANISH_CASTLE_MAGIC_PORT_BIND);
      this.unbindAddress = SpanishCastleMagic.getLocalAddress(InnerRegistry.SPANISH_CASTLE_MAGIC_PORT_UNBIND);
      this.lookupAddress = SpanishCastleMagic.getLocalAddress(InnerRegistry.SPANISH_CASTLE_MAGIC_PORT_LOOKUP);

      this.bindReplier = new Replyer(this.bindAddress);
      this.unbindReplier = new Replyer(this.unbindAddress);
      this.lookupReplier = new Replyer(this.lookupAddress);

      this.replyerTransformMap.put(this.bindReplier, new TransformerBind());
      this.replyerTransformMap.put(this.lookupReplier, new TransformerLookup());
      this.replyerTransformMap.put(this.unbindReplier, new TransformerUnbind());

    }

    private void runReplyer(final Replyer replyer) {
      final ByteStreamTransformer trans = this.replyerTransformMap.get(replyer);
      while (true) {

        if (this.terminate.get()) {
          return;
        }

        try {
          replyer.receive_transform_and_send_feedback(trans);
        } catch (final Exception e) {
        }
      }
    }

    private Executor threadExecutor = Executors.newFixedThreadPool(3);
    private final AtomicBoolean terminate = new AtomicBoolean(false);

    private void stop() {
      this.terminate.set(true);
      this.threadExecutor = null;
    }

    private void start() {
      this.threadExecutor.execute(new Runnable() {
        @Override
        public void run() {
          InnerRegistry.this.runReplyer(InnerRegistry.this.bindReplier);
        }
      });

      this.threadExecutor.execute(new Runnable() {
        @Override
        public void run() {
          InnerRegistry.this.runReplyer(InnerRegistry.this.unbindReplier);
        }
      });

      this.threadExecutor.execute(new Runnable() {
        @Override
        public void run() {
          InnerRegistry.this.runReplyer(InnerRegistry.this.lookupReplier);
        }
      });

    }

    private class TransformerBind implements ByteStreamTransformer {

      @Override
      public byte[] transform(final byte[] in) {
        try {

          final PackagedData remoteObject = (PackagedData) Utils.toObject(in);
          // String name = (String) Utils.toObject(
          // remoteObject.interfaceName()
          // );
          // RemoteInterface object = (RemoteInterface) Utils
          // .toObject( remoteObject.getRemoteObject() );

          final String name = remoteObject.getRegisteredName();

          if (InnerRegistry.this.objectMap.containsKey(name)) {
            return ByteMessages.failure();
          }

          InnerRegistry.this.objectMap.putIfAbsent(name, remoteObject);
        } catch (final IOException e) {
          return ByteMessages.failure();
        } catch (final ClassNotFoundException e) {
          return ByteMessages.failure();
        }

        return ByteMessages.success();
      }
    }

    private class TransformerLookup implements ByteStreamTransformer {

      @Override
      public byte[] transform(final byte[] in) {
        String name = "";
        try {
          name = (String) Utils.toObject(in);
          if (InnerRegistry.this.objectMap.containsKey(name)) {
            return Utils.toBytes(InnerRegistry.this.objectMap.get(name));
          } else {
            System.out.println(String.format("Lookup failed: '%s' no such name.", name));
            return ByteMessages.failure();
          }
        } catch (final Exception e) {
          System.out.println(String.format("Lookup failed for '%s'.", name));
          return ByteMessages.failure();
        }
      }
    }

    private class TransformerUnbind implements ByteStreamTransformer {

      @Override
      public byte[] transform(final byte[] in) {
        try {
          final String name = (String) Utils.toObject(in);

          if (InnerRegistry.this.objectMap.remove(name) != null) {
            return ByteMessages.success();
          } else {
            return ByteMessages.failure();
          }
        } catch (final Exception e) {
          return ByteMessages.failure();
        }

      }
    }

    @Override
    protected void finalize() throws Throwable {
      super.finalize();
      this.terminate.set(true);

    }
  }// end registry

  public static void main(final String args[]) {
    final InnerRegistry harvesterOfSorrow = new InnerRegistry();
    System.out.println("Registry is now up and running on port 2307\n");
    harvesterOfSorrow.start();
  }

}
