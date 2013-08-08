package server;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import server.ContentType;
import server.RequestInterpreter;
import testUtil.Util;

/**
 * This class interprets a GET HTTP request and tokenizez it into: the name of
 * the file being requested the content type
 * 
 * Behaviour: if the GET response is empty it will respond with the fileName=""
 * and contentType=EMPTY if the file requested doesn't have an extension then
 * the interpreter assumes it is ".html"
 * 
 */
public class RequestInterpreterTest {
  /*
   * this is a nice trick to ensure that an eventual rename of the class will be
   * reflected in the path string, thus only the folder name
   * "subjects/RequestInterpreter" has to be modified,not the test code. L2 use
   * refactorings.
   */
  private static final String associatedClassFileName = RequestInterpreter.class.getSimpleName();
  @Rule
  public final TestName currentTestName = new TestName();

  private String request;

  @Before
  public void setUp() throws Exception {
    File tempFile = new File(".");
    String path = tempFile.getAbsolutePath() + "/subjects/" + RequestInterpreterTest.associatedClassFileName + "/" + this.currentTestName.getMethodName();
    tempFile = new File(path);
    this.request = Util.readFileAsString(tempFile);

  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullStringReferenceArgument() {
    @SuppressWarnings("unused")
    RequestInterpreter temp = new RequestInterpreter(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyStringArgument() {
    @SuppressWarnings("unused")
    RequestInterpreter temp = new RequestInterpreter(this.request);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMalformedRequest() {
    @SuppressWarnings("unused")
    RequestInterpreter temp = new RequestInterpreter(this.request);
  }

  @Test
  public void testEmptyRequest() {
    RequestInterpreter requestInterpreter = new RequestInterpreter(this.request);
    assertEquals("", requestInterpreter.getFilename());
    assertEquals(ContentType.EMPTY, requestInterpreter.getContentType());
  }

  private void genericRequestTypeTest(ContentType expectedType, String expectedFileExtension) {
    RequestInterpreter requestInterpreter = new RequestInterpreter(this.request);
    assertEquals(this.currentTestName.getMethodName() + expectedFileExtension, requestInterpreter.getFilename());
    assertEquals(expectedType, requestInterpreter.getContentType());
  }

  private void genericRequestTypeTest(ContentType expectedType) {
    this.genericRequestTypeTest(expectedType, expectedType.toString());
  }

  @Test
  public void testMissingExtension() {
    ContentType expectedType = ContentType.HTML;
    this.genericRequestTypeTest(expectedType);
  }

  @Test
  public void testContentTypeJPEGRealExtensionJPEG() {
    ContentType expectedType = ContentType.JPEG;
    String expectedExtension = ".jpeg";
    this.genericRequestTypeTest(expectedType, expectedExtension);
  }

  @Test
  public void testContentTypeJPEGRealExtensionJPG() {
    ContentType expectedType = ContentType.JPEG;
    String expectedExtension = ".jpg";
    this.genericRequestTypeTest(expectedType, expectedExtension);
  }

  @Test
  public void testContentTypeGIF() {
    ContentType expectedType = ContentType.GIF;
    this.genericRequestTypeTest(expectedType);
  }

  @Test
  public void testContentTypeCSS() {
    ContentType expectedType = ContentType.CSS;
    this.genericRequestTypeTest(expectedType);
  }

  @Test
  public void testContentTypeHTML() {
    ContentType expectedType = ContentType.HTML;
    this.genericRequestTypeTest(expectedType);
  }
  
  @Test
  public void testContentTypeHTMLSpaces(){
    ContentType expectedType = ContentType.HTML;
    RequestInterpreter requestInterpreter = new RequestInterpreter(this.request);
    String fileName = requestInterpreter.getFilename();
    assertEquals("test ContentTypeHTML.html", fileName);
    assertEquals(expectedType, requestInterpreter.getContentType());
  }
}
