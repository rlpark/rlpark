package rlpark.plugin.rltoys.junit.math;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.math.History;

public class HistoryTest {
  static private final float[] hist00 = { 0.0f, 0.0f, 0.0f };
  static private final float[] hist01 = { 0.0f, 0.0f, 1.0f };
  static private final float[] hist02 = { 0.0f, 1.0f, 2.0f };
  static private final float[] hist03 = { 1.0f, 2.0f, 3.0f };
  static private final float[] hist04 = { 2.0f, 3.0f, 4.0f };

  @Test
  public void testHistory() {
    History h = new History(3);
    testHistory(h);
    h.reset();
    testHistory(h);
  }

  protected void testHistory(History h) {
    assertEquals(hist00, h.toArray());
    h.append(1.0);
    assertEquals(hist01, h.toArray());
    Assert.assertEquals(1.0, h.sum(), 0.0);
    h.append(2.0);
    assertEquals(hist02, h.toArray());
    Assert.assertEquals(3.0, h.sum(), 0.0);
    h.append(3.0);
    assertEquals(hist03, h.toArray());
    Assert.assertEquals(6.0, h.sum(), 0.0);
    h.append(4.0);
    assertEquals(hist04, h.toArray());
    Assert.assertEquals(9.0, h.sum(), 0.0);
    Assert.assertEquals(2.0, h.oldest(), 0.0);
    Assert.assertEquals(4.0, h.newest(), 0.0);
  }

  private void assertEquals(float[] expected, float[] actual) {
    Assert.assertEquals(expected.length, actual.length);
    for (int i = 0; i < expected.length; i++)
      Assert.assertEquals(expected[i], actual[i], 0.0);
  }
}
