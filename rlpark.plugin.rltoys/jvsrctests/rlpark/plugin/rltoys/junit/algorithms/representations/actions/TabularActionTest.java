package rlpark.plugin.rltoys.junit.algorithms.representations.actions;

import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.functions.stateactions.TabularAction;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.junit.math.vector.testing.VectorsTestsUtils;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;

@SuppressWarnings("serial")
public class TabularActionTest {
  static private Action a0 = new Action() {
  };

  static private Action a1 = new Action() {
  };

  @Test
  public void testTabularAction() {
    TabularAction tabularAction = new TabularAction(new Action[] { a0, a1 }, 5.0, 2);
    PVector s = new PVector(2.0, 3.0);
    VectorsTestsUtils.assertEquals(new PVector(new double[] { 2.0, 3.0, 0.0, 0.0 }), tabularAction.stateAction(s, a0));
    VectorsTestsUtils.assertEquals(new PVector(new double[] { 0.0, 0.0, 2.0, 3.0 }), tabularAction.stateAction(s, a1));
  }
}
