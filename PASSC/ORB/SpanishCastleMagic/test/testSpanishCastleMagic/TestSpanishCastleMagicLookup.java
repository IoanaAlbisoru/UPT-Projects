package testSpanishCastleMagic;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

import registry.Entry;
import remoting.Registry;
import remoting.RemoteInterface;
import remoting.SpanishCastleMagic;

import commons.Address;

public class TestSpanishCastleMagicLookup extends TestCase {

  @Override
  @Before
  public void setUp() throws Exception {
    SpanishCastleMagic.createRegistry();
  }

  @Override
  @After
  public void tearDown() throws Exception {
    SpanishCastleMagic.destroyRegistry();
  }

  public void testLookup() {

    final Address local = new Entry("127.0.0.1", 2307);
    Registry registry = SpanishCastleMagic.getRegistry(local);
    registry = SpanishCastleMagic.getRegistry(local);
    final MockTestObject test = new MockTestObject();

    Assert.assertTrue(registry.bind("mock", test));
    final RemoteInterface result = registry.lookup("mock");

    Assert.assertEquals(test, result);
  }
}
