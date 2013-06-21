package rlpark.plugin.rltoys.problems.stategraph02;

import java.util.Random;

import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.problems.RLProblem;

public class GraphProblem implements Projector, RLProblem {
  private static final long serialVersionUID = 6251650836939403789L;
  private final State s0;
  private State currentState;
  private final BVector stateVector;
  private TRStep step;
  private final Legend legend = new Legend("stateIndex");
  private final StateGraph stateGraph;
  private final Random random;

  public GraphProblem(Random random, State s0, StateGraph stateGraph) {
    this.random = random;
    this.stateGraph = stateGraph;
    stateVector = new BVector(stateGraph.nbStates());
    this.s0 = s0;
  }

  @Override
  public TRStep initialize() {
    currentState = s0;
    step = new TRStep(toObs(currentState), currentState.reward);
    return step;
  }

  private double[] toObs(State s) {
    return new double[] { stateGraph.indexOf(s) };
  }

  @Override
  public TRStep step(Action action) {
    currentState = stateGraph.sampleNextState(random, currentState, action);
    step = new TRStep(step, action, toObs(currentState), currentState.reward);
    if (stateGraph.isTerminal(currentState))
      step = step.createEndingStep();
    return step;
  }

  @Override
  public TRStep forceEndEpisode() {
    step = step.createEndingStep();
    return step;
  }

  @Override
  public TRStep lastStep() {
    return step;
  }

  @Override
  public Legend legend() {
    return legend;
  }

  @Override
  public RealVector project(double[] obs) {
    stateVector.clear();
    stateVector.setOn((int) obs[0]);
    return stateVector;
  }

  @Override
  public int vectorSize() {
    return stateVector.getDimension();
  }

  @Override
  public double vectorNorm() {
    return 1;
  }

  public StateGraph stateGraph() {
    return stateGraph;
  }
}
