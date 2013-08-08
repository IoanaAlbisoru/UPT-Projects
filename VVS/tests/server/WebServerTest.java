package server;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ServerSocketFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import server.SocketIOWrapper.SocketIOWrapperFactory;
import testUtil.Util;

public class WebServerTest {

  private static final String associatedClassFileName = WebServer.class.getSimpleName();
  private static String currentPath;
  private static String inputPath;
  private static String outputPath;

  @Rule
  public final TestName currentTestName = new TestName();

  private ServerSocket mockServerSocket;
  private WebServer webServer;
  private SocketIOWrapperFactory mockSocketIOWrapperFactory;

  @BeforeClass
  public static void before() throws Exception {
    File tempFile = new File(".");
    WebServerTest.currentPath = tempFile.getAbsolutePath() + "/subjects/" + WebServerTest.associatedClassFileName;
    WebServerTest.inputPath = WebServerTest.currentPath + "/input";
    WebServerTest.outputPath = WebServerTest.currentPath + "/output";
  }

  @Before
  public void setUp() throws Exception {
    ServerSocketFactory mockServerSocketFactory = mock(ServerSocketFactory.class);
    this.mockServerSocket = mock(ServerSocket.class);
    when(mockServerSocketFactory.createServerSocket(anyInt())).thenReturn(this.mockServerSocket);
    this.mockSocketIOWrapperFactory = mock(SocketIOWrapperFactory.class);

    this.webServer = WebServer.createWebServer(Util.TEST_SERVER_NAME, Util.TEST_CONFIG_FILE, mockServerSocketFactory, this.mockSocketIOWrapperFactory);
    this.webServer.setRootFolder("subjects/" + Util.TEST_SITE_FOLDER);
    this.webServer.setMaintanenceFolder("subjects/" + Util.TEST_MAINTENENCE_FOLDER);
  }

  @After
  public void tearDown() throws Exception {
    this.webServer.stop();
  }

  @Test
  public void testTwoConsecutiveRequests() {
    try {
      SocketIOWrapper mockSocketIOWrapper1 = mock(SocketIOWrapper.class);
      SocketIOWrapper mockSocketIOWrapper2 = mock(SocketIOWrapper.class);

      Socket mockSocket = mock(Socket.class);
      when(this.mockServerSocket.accept()).thenReturn(mockSocket);

      String in1FileName = WebServerTest.inputPath + "/" + this.currentTestName.getMethodName() + "1";
      String in2FileName = WebServerTest.inputPath + "/" + this.currentTestName.getMethodName() + "2";

      String out1FileName = WebServerTest.outputPath + "/" + this.currentTestName.getMethodName() + "1";
      String out2FileName = WebServerTest.outputPath + "/" + this.currentTestName.getMethodName() + "2";

      String request1 = Util.readFileAsString(new File(in1FileName));
      String request2 = Util.readFileAsString(new File(in2FileName));

      byte[] response1 = Util.readFileAsByteStream(new File(out1FileName));
      byte[] response2 = Util.readFileAsByteStream(new File(out2FileName));

      // this is how we simulate 2 requests;
      when(mockSocketIOWrapper1.readLine()).thenReturn(request1, "");
      when(mockSocketIOWrapper2.readLine()).thenReturn(request2, "");

      when(this.mockSocketIOWrapperFactory.createSocketIOWrapper((Socket) anyObject())).thenReturn(mockSocketIOWrapper1, mockSocketIOWrapper2).thenThrow(
          new IOException());

      this.webServer.start(12342);
      Thread.sleep(500);

      this.webServer.stop();
      verify(mockSocketIOWrapper1).write(response1);
      verify(mockSocketIOWrapper2).write(response2);

    } catch (Exception e) {
      fail("problem with test setup: " + e.getMessage());
    }
  }

  @Test
  public void testMaintanenceMode() {
    try {
      SocketIOWrapper mockSocketIOWrapper = mock(SocketIOWrapper.class);

      when(this.mockSocketIOWrapperFactory.createSocketIOWrapper((Socket) anyObject())).thenReturn(mockSocketIOWrapper);
      Socket mockSocket = mock(Socket.class);
      when(this.mockServerSocket.accept()).thenReturn(mockSocket);

      String inputFileName = WebServerTest.inputPath + "/" + this.currentTestName.getMethodName();
      String outputFileName = WebServerTest.outputPath + "/" + this.currentTestName.getMethodName();

      String request = Util.readFileAsString(new File(inputFileName));
      byte[] response = Util.readFileAsByteStream(new File(outputFileName));

      when(mockSocketIOWrapper.readLine()).thenReturn(request, "");

      this.webServer.start(10023);
      this.webServer.enterMaintenance();
      Thread.sleep(500);
      this.webServer.stop();
      verify(mockSocketIOWrapper, atLeast(1)).write(response);

    } catch (Exception e) {
      fail("problem with test setup: " + e.getMessage());
    }
  }

  @Test
  public void stressTest() {
    try {
      when(this.mockServerSocket.accept()).thenReturn(mock(Socket.class));
      final int capacity = 10000;
      final SocketIOWrapper mockIO[] = new SocketIOWrapper[capacity];
      for (int i = 0; i < mockIO.length; i++)
        mockIO[i] = mock(SocketIOWrapper.class);

      String inputFileName = WebServerTest.inputPath + "/" + this.currentTestName.getMethodName();
      String outputFileName = WebServerTest.outputPath + "/" + this.currentTestName.getMethodName();

      String request = Util.readFileAsString(new File(inputFileName));
      byte[] response = Util.readFileAsByteStream(new File(outputFileName));

      for (SocketIOWrapper mock : mockIO) {
        when(mock.readLine()).thenReturn(request, "");
      }

      when(this.mockSocketIOWrapperFactory.createSocketIOWrapper((Socket) anyObject())).then(new Answer<SocketIOWrapper>() {
        AtomicInteger count = new AtomicInteger(0);

        @Override
        public SocketIOWrapper answer(InvocationOnMock invocation) throws Throwable {
          try {
            return mockIO[this.count.getAndIncrement()];
          } catch (Exception e) {
            throw new SocketTimeoutException();
          }
        }
      });
      this.webServer.start(12345);
      Thread.sleep(5000);
      for (SocketIOWrapper socketIOWrapper : mockIO)
        verify(socketIOWrapper, atLeast(1)).write(response);
      this.webServer.terminate();

    } catch (Exception e) {
      fail(e.toString());
    }

  }

}
