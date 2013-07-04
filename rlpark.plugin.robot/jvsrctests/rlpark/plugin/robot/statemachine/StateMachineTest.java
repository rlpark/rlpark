package rlpark.plugin.robot.statemachine;

import org.junit.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.robot.internal.statemachine.StateMachine;
import rlpark.plugin.robot.internal.statemachine.TimedState;


public class StateMachineTest {
  static private class StateNodeTest extends TimedState {
    protected int v = 0;

    public StateNodeTest(int nbTimeStep) {
      super(nbTimeStep);
    }

    @Override
    public void step(TRStep step) {
      super.step(step);
      v++;
    }
  }

  private final StateNodeTest a = new StateNodeTest(20);
  private final StateNodeTest b = new StateNodeTest(10);

  @Test
  public void testStateMachine() {
    @SuppressWarnings("unchecked")
    StateMachine<TRStep> stateMachine = new StateMachine<TRStep>(a, b);
    for (int i = 0; i < 30; i++)
      stateMachine.step(null);
    Assert.assertEquals(a.v, 20);
    Assert.assertEquals(b.v, 10);
  }
}
