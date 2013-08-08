package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketIOWrapper {

  public static abstract class SocketIOWrapperFactory {
    public abstract SocketIOWrapper createSocketIOWrapper(Socket socket) throws IOException;

    public static SocketIOWrapperFactory getDefault() {
      return new SocketIOWrapperFactory() {
        @Override
        public SocketIOWrapper createSocketIOWrapper(Socket socket) throws IOException {
          return new SocketIOWrapper(socket);
        }
      };
    }
  }

  private final Socket wrappedSocket;
  private final BufferedReader input;

  private SocketIOWrapper(Socket socket) throws IOException {
    this.wrappedSocket = socket;
    this.input = new BufferedReader(new InputStreamReader(this.wrappedSocket.getInputStream()));
  }

  public synchronized void close() throws IOException {
    this.input.close();
    this.wrappedSocket.close();
  }

  public String readLine() throws IOException {
    return this.input.readLine();
  }

  public void write(byte[] array) throws IOException {
    this.wrappedSocket.getOutputStream().write(array);
  }

  public void write(String string) throws IOException {
    this.write(string.getBytes());
  }

}
