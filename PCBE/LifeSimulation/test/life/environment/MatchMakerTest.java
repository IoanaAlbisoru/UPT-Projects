package life.environment;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import life.organic.SexualCell;
import life.organic.SexualCell.SEX;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MatchMakerTest {

  private MatchMaker match;

  @Before
  public void setUp() throws Exception {
    this.match = new MatchMaker(4000, TimeUnit.MILLISECONDS);
  }

  @After
  public void tearDown() throws Exception {
    this.match.shutdown();
    this.match.awaitTermination(1, TimeUnit.SECONDS);
  }

  @Test
  public void testCorrectMatch() {
    final SexualCell maleCell = mock(SexualCell.class);
    when(maleCell.getSex()).thenReturn(SEX.MALE);

    final SexualCell femaleCell = mock(SexualCell.class);
    when(femaleCell.getSex()).thenReturn(SEX.FEMALE);

    final CountDownLatch start = new CountDownLatch(1);
    Thread maleThread = new Thread() {
      @Override
      public void run() {
        try {
          start.await();
          SexualCell femaleFound = MatchMakerTest.this.match.findMatch(maleCell);
          assertSame(femaleCell, femaleFound);

        } catch (Exception e) {
          fail("Threw exception in maleThread");
        }
      }
    };

    Thread femaleThread = new Thread() {
      @Override
      public void run() {
        try {
          start.await();
          SexualCell maleFound = MatchMakerTest.this.match.findMatch(femaleCell);
          assertSame(maleCell, maleFound);
        } catch (Exception e) {
          fail("Threw exception in femaleThread");
        }
      }
    };

    maleThread.start();
    // femaleThread.start();
    start.countDown();
    try {
      femaleThread.join();
      maleThread.join();
    } catch (InterruptedException e) {
      throw new RuntimeException("something's wrong with the test setup");
    }
  }

}
