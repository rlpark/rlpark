package rlpark.plugin.rltoys.junit.envio.actions;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.envio.actions.ActionArray;

public class ActionArrayTest {
  final static double[] a1 = { 1.0, 2.0 };
  final static double[] a2 = { 1.0, 2.0 };
  final static double[] a3 = { 3.0, 2.0 };

  @Test
  public void testToString() {
    new ActionArray(a1).toString();
  }

  @Test
  public void testEqual() {
    Assert.assertFalse(new ActionArray(null).equals(null));
    Assert.assertTrue(new ActionArray(null).equals(new ActionArray(null)));
    Assert.assertFalse(new ActionArray(a1).equals(null));
    Assert.assertFalse(new ActionArray(null).equals(new ActionArray(a1)));
    Assert.assertTrue(new ActionArray(a1).equals(new ActionArray(a1)));
    Assert.assertTrue(new ActionArray(a1).equals(new ActionArray(a2)));
    Assert.assertFalse(new ActionArray(a1).equals(new ActionArray(a3)));
  }

  @Test
  public void testHashCode() {
    Set<ActionArray> actions = new LinkedHashSet<ActionArray>();
    actions.add(new ActionArray(a1));
    actions.add(new ActionArray(a2));
    actions.add(new ActionArray(a3));
    Assert.assertEquals(2, actions.size());
  }

}
