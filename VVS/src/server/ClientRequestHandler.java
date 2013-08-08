package server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

final class ClientRequestHandler implements Runnable {
  private final SocketIOWrapper socketIO;
  private final String rootFolder;
  private final String serverName;
  private final AtomicReference<ServerStates> currentServerState;
  private final String maintenanceFolder;
  private final String maintenancePage;
  private final String notFoundPage;
  private final String indexPage;

  public ClientRequestHandler(AtomicReference<ServerStates> serverState, SocketIOWrapper socket, String rootFolder, String maintenanceFolder,
      String serverName, String notFoundPage, String maintenancePage, String indexPage) {
    this.currentServerState = serverState;
    this.socketIO = socket;
    this.rootFolder = rootFolder;
    this.maintenanceFolder = maintenanceFolder;
    this.serverName = serverName;
    this.maintenancePage = maintenancePage;
    this.notFoundPage = notFoundPage;
    this.indexPage = indexPage;
  }

  @Override
  public final void run() {
    try {
      String request = this.readRequest();
      byte[] response = this.createResponse(request);
      this.socketIO.write(response);
    } catch (Exception ioe) {
      // TODO log;
    } finally {
      try {
        this.socketIO.close();
      } catch (Exception e) {
        // TODO log;
        // this shouldn't happen very often. But if it does there isn't much we
        // must do.
      }
    }
  }

  private String readRequest() throws IOException {
    String inputLine = this.socketIO.readLine();
    String request = "";
    while (inputLine != null) {
      if (inputLine.trim().equals(""))
        break;
      request += inputLine + "\r\n";
      inputLine = this.socketIO.readLine();
    }
    return request;
  }

  private final byte[] createResponse(String request) {
    if (this.currentServerState.compareAndSet(ServerStates.MAINTENANCE, ServerStates.MAINTENANCE)) {
      String header = this.createHTTPHeader(Http.HTTP_UNAVAILABLE, ContentType.HTML);
      byte[] maintenancePageContent = this.returnFileAsByteStream(this.maintenanceFolder, this.maintenancePage);
      if (maintenancePageContent == null) {
        String temp = header + "<html><body><h2>Under maintenance, EXTERMINATE, EXTERMINATE!</h2></body></html>";
        return temp.getBytes();
      } else {
        byte[] combined = this.combineArrays(header.getBytes(), maintenancePageContent);
        return combined;
      }
    }

    RequestInterpreter reqInterpreter = null;
    try {
      reqInterpreter = new RequestInterpreter(request);
    } catch (IllegalArgumentException e) {
      String temp = this.createHTTPHeader(Http.HTTP_BAD_REQUEST, ContentType.HTML) + "<html><body><h2>Bad Request</h2></body></html>";
      return temp.getBytes();
    }
    byte[] fileContents = null;
    ContentType effectiveType = reqInterpreter.getContentType();
    if (effectiveType == ContentType.EMPTY) {
      fileContents = this.returnFileAsByteStream(this.indexPage);
      effectiveType = ContentType.HTML;
    } else
      fileContents = this.returnFileAsByteStream(reqInterpreter.getFilename());

    if (fileContents == null) {
      byte[] pageNotFoundContents = this.returnFileAsByteStream(this.notFoundPage);
      String header = this.createHTTPHeader(Http.HTTP_NOT_FOUND, ContentType.HTML);
      if (pageNotFoundContents == null) {
        String temp = header + "<html><body><h2>Not Found</h2></body></html>";
        return temp.getBytes();
      } else {
        byte[] combined = this.combineArrays(header.getBytes(), pageNotFoundContents);
        return combined;
      }
    }
    byte[] result = this.combineArrays(this.createHTTPHeader(Http.HTTP_OK, effectiveType).getBytes(), fileContents);
    return result;
  }

  private byte[] combineArrays(byte[] arg1, byte[] arg2) {
    byte[] result = new byte[arg1.length + arg2.length];
    System.arraycopy(arg1, 0, result, 0, arg1.length);
    System.arraycopy(arg2, 0, result, arg1.length, arg2.length);
    return result;
  }

  private byte[] returnFileAsByteStream(String fileName) {
    return this.returnFileAsByteStream(this.rootFolder, fileName);
  }

  private byte[] returnFileAsByteStream(String folder, String fileName) {
    File file = new File(folder + "/" + fileName);

    if (!file.exists())
      return null;

    try {
      byte[] buffer = new byte[(int) file.length()];
      BufferedInputStream f;
      f = new BufferedInputStream(new FileInputStream(file));
      f.read(buffer);
      f.close();
      return buffer;
    } catch (Exception e) {
      return null;
    }
  }

  private String createHTTPHeader(int code, ContentType content) {
    String header = "HTTP/1.1 ";
    header += Http.codeMessageMap.get(code);
    header = header + "\r\n";
    header = header + "Connection: close\r\n";
    header = header + "Server: " + this.serverName + " server v0\r\n";

    if (content != null)
      header += content.getContentTypeFormatting();

    header = header + "\r\n";
    return header;
  }

}
