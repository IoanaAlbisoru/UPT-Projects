package activator;

import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import registry.Entry;
import remoting.Utils;
import requestReply.ByteStreamTransformer;
import requestReply.Replyer;

import commons.Address;

import errors.ByteMessages;

public class Activator {
  private static final int PORT = 2400;

  @SuppressWarnings("unused")
  private static class ActivatorMessage {
    private final String serviceInterfaceName;
    private final String lookupName;

    public ActivatorMessage(final String interfaceName, final String lookUpName) {
      this.serviceInterfaceName = interfaceName;
      this.lookupName = lookUpName;
    }

    public String getServiceInterfaceName() {
      return this.serviceInterfaceName;
    }

    public String getLookUpName() {
      return this.lookupName;
    }
  }

  private final static ConcurrentHashMap<String, Activable> services = new ConcurrentHashMap<String, Activable>();

  static boolean activate(final String name, final Address where) {
    return false;
  }

  static boolean remove(final String name, final Address where) {
    return false;
  }

  @SuppressWarnings("unused")
  private static class InnerActivator {
    private final Replyer replyer;

    InnerActivator() throws UnknownHostException {
      this.replyer = new Replyer(new Entry(InetAddress.getLocalHost().getHostAddress(), Activator.PORT));
    }

    private final AtomicBoolean terminate = new AtomicBoolean(false);
    Executor runActivator = Executors.newCachedThreadPool();

    private void runReplyer(final ByteStreamTransformer t) {
      while (true) {
        try {
          this.replyer.receive_transform_and_send_feedback(t);
        } catch (final SocketTimeoutException e) {
          if (this.terminate.get()) {
            return;
          }
        }
      }
    }

    private Thread reaperThread;
    private final Executor serviceExecutor = Executors.newCachedThreadPool();
    private final Executor apiExecutor = Executors.newFixedThreadPool(2);

    void start() {
      this.reaperThread = new Thread(new Runnable() {
        @Override
        public void run() {
          while (true) {
            try {
              Thread.sleep(300);
            } catch (final InterruptedException ignore) {
            }

            if (InnerActivator.this.terminate.get()) {
              return;
            }

            for (final java.util.Map.Entry<String, Activable> entry : Activator.services.entrySet()) {
              if ((entry.getValue() != null) && entry.getValue().isDeactivable()) {
                entry.getValue().deactivate();
                Activator.services.replace(entry.getKey(), null);
              }
            }
          }
        }
      });// end reaperThread

      this.apiExecutor.execute(new Runnable() {
        @Override
        public void run() {
          InnerActivator.this.runReplyer(new TransformerActivate());
        }
      });

    }

    void stop() {
      this.terminate.set(true);
    }

    /**
     * @author Lori Ridiculous assuptions that all classes have a standard
     *         constructor. Too lazy to implement otherwise. Thank you for
     *         understanding.
     */
    private class TransformerActivate implements ByteStreamTransformer {

      @Override
      public byte[] transform(final byte[] in) {
        try {
          final ActivatorMessage packedData = (ActivatorMessage) Utils.toObject(in);
          final Class<?> interfaceToActivate = Class.forName(packedData.getServiceInterfaceName());

          // this is the most horrible thing I've done in a long, long
          // time.
          // appauling, ridiculous and outright stupid
          final Constructor<?> ctor = interfaceToActivate.getConstructor(new Class<?>[] {});

          final Activable activable = (Activable) ctor.newInstance(new Object[] {});
          Activator.services.replace(packedData.lookupName, activable);

          return ByteMessages.success();
        } catch (final Exception e) {
          e.printStackTrace();
        }
        return ByteMessages.failure();
      }
    }

    private class TransformerRemove implements ByteStreamTransformer {

      @Override
      public byte[] transform(final byte[] in) {
        try {
          final String name = (String) Utils.toObject(in);
          final Activable toDeactivate = Activator.services.remove(name);
          toDeactivate.deactivate();
          return ByteMessages.success();
        } catch (final Exception e) {
          e.printStackTrace();
          return ByteMessages.failure();
        }
      }

    }
  }

  public static void main(final String args[]) {

  }
}
