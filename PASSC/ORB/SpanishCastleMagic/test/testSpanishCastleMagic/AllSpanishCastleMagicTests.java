package testSpanishCastleMagic;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllSpanishCastleMagicTests {

  public static Test suite() {
    final TestSuite suite = new TestSuite(AllSpanishCastleMagicTests.class.getName());
    // $JUnit-BEGIN$
    suite.addTestSuite(TestUtils.class);
    suite.addTestSuite(TestSpanishCastleMagicBind.class);
    suite.addTestSuite(TestSpanishCastleMagicLookup.class);
    suite.addTestSuite(TestSpanishCastleMagicUnbind.class);
    // $JUnit-END$
    return suite;
  }

}
