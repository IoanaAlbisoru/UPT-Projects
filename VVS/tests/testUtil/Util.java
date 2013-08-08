package testUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public final class Util {

  public static final String TEST_SITE_FOLDER = "zTestSiteFolder";
  public static final String TEST_MAINTENENCE_FOLDER = "zTestSiteFolder/Maintenance";
  public static final String TEST_SERVER_NAME = "Deep Thought";
  public static final String TEST_NOT_FOUND_PAGE = "NotFound.html";
  public static final String TEST_MAINTENANCE_PAGE = "Maintenance.html";
  public static final String TEST_INDEX_PAGE = "index.html";
  public static final String TEST_CONFIG_FILE = "subjects/config";

  public final static String readFileAsString(File file) throws IOException {
    byte[] byteStream = readFileAsByteStream(file);
    String tempString = new String(byteStream);
    return tempString;
  }

  public static byte[] readFileAsByteStream(File file) throws IOException {
    if (!file.exists())
      throw new IOException("uknown file:  " + file);

    byte[] buffer = new byte[(int) file.length()];
    BufferedInputStream f;
    f = new BufferedInputStream(new FileInputStream(file));
    f.read(buffer);
    f.close();
    return buffer;
  }

  public static byte[] combineArrays(byte[] arg1, byte[] arg2) {
    byte[] result = new byte[arg1.length + arg2.length];
    System.arraycopy(arg1, 0, result, 0, arg1.length);
    System.arraycopy(arg2, 0, result, arg1.length, arg2.length);
    return result;
  }

  public static void writeToFile(String filename, byte[] newFileBytes) throws Exception {
    File newFile = new File(filename);
    newFile.createNewFile();
    OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(newFile, false));
    for (byte b : newFileBytes)
      out.write(b);
    out.close();
  }
}
