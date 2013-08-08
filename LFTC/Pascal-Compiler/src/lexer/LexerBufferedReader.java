package lexer;

import grammar.Alphabet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.management.RuntimeErrorException;

class LexerBufferedReader {
  private final File file;
  private final FileReader reader;
  private final char[] buffer;
  private char[] secondaryBuffer = null;
  private int secondaryBufferSize = -1;
  private int currentIndex = 0;
  private int currentBufferSize = -1;

  private int currentLine = 1;
  private int currentColumn = 1;

  private boolean stopReading = false;

  private final int MAX_BUFFER_SIZE;
  private int infiniteLoopWard = 0;

  public LexerBufferedReader(String fileName) throws FileNotFoundException {
    this.file = new File(fileName);

    if (!this.file.exists())
      throw new FileNotFoundException(this.file.getAbsolutePath());

    this.MAX_BUFFER_SIZE = 1024;

    this.reader = new FileReader(this.file);
    this.buffer = new char[this.MAX_BUFFER_SIZE];
  }

  public LexerBufferedReader(File file) throws FileNotFoundException {
    this.file = file;
    if (!file.exists())
      throw new FileNotFoundException(file.getAbsolutePath());

    this.MAX_BUFFER_SIZE = 1024;

    this.reader = new FileReader(this.file);
    this.buffer = new char[this.MAX_BUFFER_SIZE];
  }

  private int readIntoBuffer(char[] buffer) throws IOException {

    for (int i = 0; i < buffer.length; i++) {
      int valueRead = this.reader.read();

      if (valueRead == -1)
        if (i == 0)
          return -1;
        else
          return i;

      buffer[i] = (char) valueRead;
    }

    return buffer.length;
  }

  private void readMore() {

    if (this.currentIndex < this.currentBufferSize)
      return;

    if (this.secondaryBuffer == null) {

      try {
        this.currentBufferSize = this.readIntoBuffer(this.buffer);
      } catch (IOException e) {
        throw new RuntimeException("Something's wrong with the IO on the file " + this.file.getAbsolutePath()
            + "\n-----\n" + e.getMessage());
      }

      if (this.currentBufferSize == -1)
        try {
          this.stopReading = true;
          this.reader.close();
        } catch (IOException e) {
          // new EOFException("End of file");
        }

      if (this.currentBufferSize == this.MAX_BUFFER_SIZE) {
        this.secondaryBuffer = new char[this.MAX_BUFFER_SIZE];
        try {
          this.secondaryBufferSize = this.readIntoBuffer(this.secondaryBuffer);
        } catch (IOException e) {
          throw new RuntimeException("Something's wrong with the IO on the file " + this.file.getAbsolutePath()
              + "\n-----\n" + e.getMessage());
        }
        if (this.secondaryBufferSize == -1)
          this.secondaryBuffer = null;
      }
      this.currentIndex = 0;

    } else {
      System.arraycopy(this.secondaryBuffer, 0, this.buffer, 0, this.secondaryBufferSize);
      this.currentBufferSize = this.secondaryBufferSize;
      this.currentIndex = 0;
      this.secondaryBuffer = null;
      this.secondaryBufferSize = -1;
    }

  }

  public char consume() {

    if (this.stopReading) {
      if (this.infiniteLoopWard > 1024)
        throw new RuntimeErrorException(null, "You're probably stuck in an infinite loop while using "
            + this.getClass().getName());
      this.infiniteLoopWard++;
      return Alphabet.EOF_CHAR;
    }
    // throw new EOFException();

    this.readMore();

    if (this.stopReading)
      return Alphabet.EOF_CHAR;

    char c = this.buffer[this.currentIndex++];
    if (c == '\n') {
      this.currentLine++;
      this.currentColumn = 1;
    } else
      this.currentColumn++;

    return c;
  }

  public char nextChar() {

    if (this.stopReading) {
      if (this.infiniteLoopWard > 1024)
        throw new RuntimeErrorException(null, "You're probably stuck in an infinite loop while using "
            + this.getClass().getName());
      this.infiniteLoopWard++;
      return Alphabet.EOF_CHAR;
    }
    this.readMore();

    if (this.stopReading)
      return Alphabet.EOF_CHAR;

    if (this.currentIndex >= this.currentBufferSize)
      if (this.secondaryBuffer == null)
        try {
          this.stopReading = true;
          this.reader.close();
          return ' ';
        } catch (IOException e) {
          return Alphabet.EOF_CHAR;
        }
      else
        return this.secondaryBuffer[0];

    return this.buffer[this.currentIndex];
  }

  public int getCurrentLine() {
    return this.currentLine;
  }

  public void putBackLast() {
    // TODO, make it work properly;
    this.currentIndex--;
    this.currentColumn--;
  }

  public int getCurrentColumn() {
    return this.currentColumn;
  }

}
