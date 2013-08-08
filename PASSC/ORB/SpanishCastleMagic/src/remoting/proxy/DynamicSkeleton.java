package remoting.proxy;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentSkipListSet;

import registry.Entry;
import remoting.RemoteInterface;
import remoting.Utils;
import requestReply.ByteStreamTransformer;
import requestReply.Replyer;

import commons.Address;

class DynamicSkeleton implements SkeletonInterface {

  private final RemoteInterface wrappedObject;

  private final Address localAddr;

  private static int MIN_PORT_RANGE = 1500;
  private static int MAX_PORT_RANGE = 2000;
  private static int nextPort = DynamicSkeleton.MIN_PORT_RANGE;

  public DynamicSkeleton(final RemoteInterface rem) throws Exception {
    int allOccupiedCounter = 0;
    this.wrappedObject = rem;

    while (true) {
      if (DynamicSkeleton.usedPorts.add(DynamicSkeleton.nextPort)) {
        this.localAddr = new Entry(InetAddress.getLocalHost().getHostAddress(), DynamicSkeleton.nextPort++);
        return;
      } else {

        DynamicSkeleton.nextPort++;
        allOccupiedCounter++;
        if (DynamicSkeleton.nextPort > DynamicSkeleton.MAX_PORT_RANGE) {
          DynamicSkeleton.nextPort = DynamicSkeleton.MIN_PORT_RANGE;
        }
        if (allOccupiedCounter == 200) {
          throw new Exception("No Free Ports");
        }
      }
    }
  }

  private Thread listeningThread;
  private static ConcurrentSkipListSet<Integer> usedPorts = new ConcurrentSkipListSet<Integer>();

  private Replyer replyer;

  private final ByteStreamTransformer transformer = new ByteStreamTransformer() {

    @Override
    public byte[] transform(final byte[] in) {
      try {
        final SerializedMethodCall temp = (SerializedMethodCall) Utils.toObject(in);
        final Object[] arguments = temp.getArguments();

        final Method toInvoke = temp.getMethod(DynamicSkeleton.this.wrappedObject);

        final Object result = toInvoke.invoke(DynamicSkeleton.this.wrappedObject, arguments);

        return Utils.toBytes(result);
      } catch (final Exception e) {
        e.printStackTrace();
        return null;
      }
    }
  };

  private void handleRemoteMethodCall() {
    while (true) {
      try {
        DynamicSkeleton.this.replyer.receive_transform_and_send_feedback(this.transformer);
      } catch (final SocketTimeoutException e) {
        if (Thread.currentThread().isInterrupted()) {
          return;
        }
      }
    }
  }

  public void start() {
    this.listeningThread = new Thread(new Runnable() {
      @Override
      public void run() {
        DynamicSkeleton.this.handleRemoteMethodCall();
      }
    });
    this.replyer = new Replyer(this.localAddr);
    this.listeningThread.start();
  }

  final Address getLocalAddr() {
    return this.localAddr;
  }

  @Override
  protected void finalize() throws Throwable {
    this.listeningThread.interrupt();
    DynamicSkeleton.usedPorts.remove(this.localAddr.port());
  }

  @Override
  public void stop() {
    this.listeningThread.interrupt();
  }

  @Override
  public Address getAddr() {
    return this.localAddr;
  }

}
