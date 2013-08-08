package server;

final class RequestInterpreter {

  private final String request;
  private String fileName = null;
  private ContentType contentType = null;

  public RequestInterpreter(final String request) throws IllegalArgumentException {
    if (request == null)
      throw new IllegalArgumentException("Request cannot be null");

    if (request.equals(""))
      throw new IllegalArgumentException("Request cannot be void");

    this.request = request;
    this.tokenizeRequest(this.request);

  }

  private void tokenizeRequest(final String request) throws IllegalArgumentException {
    String getString = "GET";
    int getIndex = request.indexOf(getString) + getString.length();
    // minus one because of the space we'd expect
    int firstCharacter = request.indexOf("/", getIndex) + 1;
    int lastCharacter = request.indexOf("HTTP", firstCharacter) - 1;

    if (firstCharacter > lastCharacter)
      throw new IllegalArgumentException("Malformed Request");

    String tempFileName = request.substring(firstCharacter, lastCharacter);

    // This is the case of an empty request;
    if (tempFileName.equals("")) {
      this.fileName = tempFileName;
      this.contentType = ContentType.getContentFromExtension(tempFileName);
      return;
    }

    this.fileName = tempFileName;

    String[] split = this.fileName.split("\\.");

    if (split.length < 1)
      throw new IllegalArgumentException("Cannot determine ContentType");

    ContentType tempContentType = null;
    if (split.length == 1) {
      tempContentType = ContentType.getContentFromExtension("html");
      this.fileName += tempContentType.toString();
    } else if (split.length >= 2) {
      String tempExtension = split[split.length - 1];
      tempContentType = ContentType.getContentFromExtension(tempExtension);

    }

    if (tempContentType == null)
      throw new IllegalArgumentException("You forgot to update the extension map, idiot.");

    this.contentType = tempContentType;
  }

  /**
   * @return the name of the file the requests wants; null if the request is
   *         malformed
   */
  public String getFilename() {
    return this.fileName;
  }

  public ContentType getContentType() {
    return this.contentType;
  }

}