package testInterpreter;

import interpreter.Parser;
import junit.framework.Assert;
import junit.framework.TestCase;

public class ParserTest extends TestCase {
  public void testParser() {
    final Parser parser = new Parser();
    final String text = "entity name\nstr asf;\n  double asd;\nint asc;\n  def name asd+asc+asf;";
    try {
      parser.parse(text);
    } catch (final Exception e) {
      Assert.fail();
    }
  }
}
