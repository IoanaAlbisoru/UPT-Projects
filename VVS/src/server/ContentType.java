package server;

import java.util.HashMap;

enum ContentType {
  JPEG {
    @Override
    public String getContentTypeFormatting() {
      return ContentType.CONTENT_TYPE + " image/jpeg\r\n";
    }

    @Override
    public String toString() {
      return ".jpg";
    }
  },
  GIF {
    @Override
    public String getContentTypeFormatting() {
      return ContentType.CONTENT_TYPE + " image/gif\r\n";
    }

    @Override
    public String toString() {
      return ".gif";
    }

  },
  HTML {
    @Override
    public String getContentTypeFormatting() {
      return ContentType.CONTENT_TYPE + " text/html\r\n";
    }

    @Override
    public String toString() {
      return ".html";
    }
  },
  CSS {
    @Override
    public String getContentTypeFormatting() {
      return ContentType.CONTENT_TYPE + " text/html\r\n";
    }

    @Override
    public String toString() {
      return ".css";
    }
  },
  EMPTY {

    @Override
    public String getContentTypeFormatting() {
      return ContentType.CONTENT_TYPE + " ";
    }
  };

  private static final String CONTENT_TYPE = "Content-Type:";

  public abstract String getContentTypeFormatting();

  private static HashMap<String, ContentType> extensionMap = new HashMap<String, ContentType>();
  static {
    ContentType.extensionMap.put("gif", ContentType.GIF);
    ContentType.extensionMap.put("jpg", ContentType.JPEG);
    ContentType.extensionMap.put("jpeg", ContentType.JPEG);
    ContentType.extensionMap.put("css", ContentType.CSS);
    ContentType.extensionMap.put("html", ContentType.HTML);
    ContentType.extensionMap.put("txt", ContentType.HTML);
    ContentType.extensionMap.put("", ContentType.EMPTY);
  }

  /**
   * @param extension
   *          is given WITHOUT the ".", Repeat after me: NO DOT!
   * @return the ContentType enum corresponding to the extension
   */
  public static ContentType getContentFromExtension(String extension) {
    return ContentType.extensionMap.get(extension);
  }
}