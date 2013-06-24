package rlpark.plugin.rltoys.problems.stategraph02;

import java.io.Serializable;
import java.util.Random;

import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.problems.RLProblem;

public class GraphProblem implements Serializable, RLProblem {
  private static final long serialVersionUID = 6251650836939403789L;
  private final State s0;
  private State currentState;
  private TRStep step;
  private final Legend legend = new Legend("stateIndex");
  private final StateGraph stateGraph;
  private final Random random;
  private final Projector projector;

  public GraphProblem(Random random, State s0, StateGraph stateGraph, Projector projector) {
    this.random = random;
    this.stateGraph = stateGraph;
    this.s0 = s0;
    this.projector = projector;
    assert stateGraph.checkDistribution();
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

  public StateGraph stateGraph() {
    return stateGraph;
  }

  public Projector projector() {
    return projector;
  }
}
