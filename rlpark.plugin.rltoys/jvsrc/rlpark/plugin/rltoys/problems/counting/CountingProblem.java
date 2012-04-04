package rlpark.plugin.rltoys.problems.counting;

import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class CountingProblem {
  private final int[] inputState;
  private final int[] targetStates;
  private final BVector input;

  public CountingProblem(int nbInputs, int nbTargets) {
    inputState = new int[nbInputs];
    targetStates = new int[nbTargets];
    input = new BVector(nbInputs);
  }

  public BVector updateInput() {
    input.clear();
    for (int i = 0; i < inputState.length; i++) {
      if (inputState[i] == 0)
        input.setOn(i);
      inputState[i] = (inputState[i] + 1) % (i + 2);
    }
    return input;
  }

  public int[] targets() {
    int[] result = targetStates.clone();
    for (int i = 0; i < targetStates.length; i++)
      targetStates[i] = (targetStates[i] + 1) % (2 + i);
    return result;
  }
}
