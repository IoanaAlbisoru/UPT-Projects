package server;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import testUtil.Util;
import static org.mockito.Mockito.*;

public class ClientRequestHandlerTest {

  private static final String EXPECTED_OUTPUT_FOLDER_NAME = "output";
  private static final String INPUT_FOLDER_NAME = "input";
  private static final String associatedClassFileName = ClientRequestHandler.class.getSimpleName();

  @Rule
  public final TestName currentTestName = new TestName();

  private String request;
  private byte[] expectedResponse;

  @Before
  public void setUp() throws Exception {
    File tempFile = new File(".");
    String inputFilePath = tempFile.getAbsolutePath() + "/subjects/" + ClientRequestHandlerTest.associatedClassFileName + "/"
        + ClientRequestHandlerTest.INPUT_FOLDER_NAME + "/" + this.currentTestName.getMethodName();
    File requestFile = new File(inputFilePath);
    this.request = Util.readFileAsString(requestFile);

    String expectedFilePath = tempFile.getAbsolutePath() + "/subjects/" + ClientRequestHandlerTest.associatedClassFileName + "/"
        + ClientRequestHandlerTest.EXPECTED_OUTPUT_FOLDER_NAME + "/" + this.currentTestName.getMethodName();

    File responseFile = new File(expectedFilePath);
    this.expectedResponse = Util.readFileAsByteStream(responseFile);
  }

  @Test
  public void testEmptyRequest() {
    this.basicTestRequestResponse();
  }

  @Test
  public void testCSSRequest() {
    this.basicTestRequestResponse();
  }

  @Test
  public void testHTMLRequest() {
    this.basicTestRequestResponse();
  }

  @Test
  public void testHTMLRequestFromDeeperFolderLevel() {
    this.basicTestRequestResponse();
  }

  @Test
  public void testNonExistentPageRequestWithStandardNotFoundPage() {
    this.basicTestRequestResponse();
  }

  @Test
  public void testNonExistentPageRequestWithCustomNotFoundPage() {
    this.basicTestRequestResponse(Util.TEST_SITE_FOLDER + "/CustomNotFoundPage");
  }

  // this test fails because the comparison is done without ignoring
  // whitespaces. Damn mixed, binary/text files
  @Test
  public void testJPEGRequest() {
    this.basicTestRequestResponse();
  }

  // this test fails because the comparison is done without ignoring
  // whitespaces. Damn mixed, binary/text files
  @Test
  public void testGIFRequest() {
    this.basicTestRequestResponse();
  }

  @Test
  public void testBadRequestResponse1() {
    this.basicTestRequestResponse();
  }

  @Test
  public void testBadRequestResponse2() {
    this.basicTestRequestResponse();
  }

  // currently I can only verify that socket.close() was actually called
  @Test
  public void testCloseSocketThrowsException() {
    try {
      SocketIOWrapper mockedSocketIOWrapper = mock(SocketIOWrapper.class);
      stub(mockedSocketIOWrapper.readLine()).toReturn(this.request).toReturn("");
      doThrow(new RuntimeException()).when(mockedSocketIOWrapper).close();
      ClientRequestHandler clientRequestHandler = new ClientRequestHandler(new AtomicReference<ServerStates>(ServerStates.RUNNING), mockedSocketIOWrapper,
          "subjects/" + Util.TEST_SITE_FOLDER, Util.TEST_MAINTENENCE_FOLDER, Util.TEST_SERVER_NAME, Util.TEST_NOT_FOUND_PAGE, Util.TEST_MAINTENANCE_PAGE,
          Util.TEST_INDEX_PAGE);
      Thread thread = new Thread(clientRequestHandler);
      thread.start();
      thread.join();
      verify(mockedSocketIOWrapper).close();

    } catch (IOException ignore) {
      fail("failure in test setup, not in tested code");
    } catch (InterruptedException e) {
      fail("did not end execution of request");
    }
  }

  private void basicTestRequestResponse() {
    this.basicTestRequestResponse(Util.TEST_SITE_FOLDER);
  }

  private void basicTestRequestResponse(String siteFolderPath) {
    this.basicTestRequestResponse(siteFolderPath, ServerStates.RUNNING);
  }

  private void basicTestRequestResponse(String siteFolderPath, ServerStates state) {
    try {
      SocketIOWrapper mockedSocketIOWrapper = mock(SocketIOWrapper.class);
      stub(mockedSocketIOWrapper.readLine()).toReturn(this.request).toReturn("");
      ClientRequestHandler clientRequestHandler = new ClientRequestHandler(new AtomicReference<ServerStates>(state), mockedSocketIOWrapper, "subjects/"
          + siteFolderPath, Util.TEST_MAINTENENCE_FOLDER, Util.TEST_SERVER_NAME, Util.TEST_NOT_FOUND_PAGE, Util.TEST_MAINTENANCE_PAGE, Util.TEST_INDEX_PAGE);
      Thread thread = new Thread(clientRequestHandler);
      thread.start();
      thread.join();
      verify(mockedSocketIOWrapper).write(this.expectedResponse);

    } catch (IOException ignore) {
      fail("failure in test setup, not in tested code");
    } catch (InterruptedException e) {
      fail("did not end execution of request");
    }
  }

}
