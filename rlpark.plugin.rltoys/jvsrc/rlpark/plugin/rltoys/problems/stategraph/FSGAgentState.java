package rlpark.plugin.rltoys.problems.stategraph;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;

import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.problems.stategraph.FiniteStateGraph.StepData;

public class FSGAgentState implements StateToStateAction {
  private static final long serialVersionUID = -6312948577339609928L;
  public final int size;
  private final Map<GraphState, Integer> stateIndexes;
  private final FiniteStateGraph graph;
  private final PVector featureState;

  public FSGAgentState(FiniteStateGraph graph) {
    this.graph = graph;
    stateIndexes = indexStates(graph.states());
    size = nbNonAbsorbingState();
    featureState = new PVector(size);
  }

  private Map<GraphState, Integer> indexStates(GraphState[] states) {
    Map<GraphState, Integer> stateIndexes = new LinkedHashMap<GraphState, Integer>();
    int ci = 0;
    for (GraphState state : states) {
      GraphState s = state;
      if (!s.hasNextState())
        continue;
      stateIndexes.put(s, ci);
      ci++;
    }
    return stateIndexes;
  }

  public StepData step() {
    StepData stepData = graph.step();
    if (stepData.s_t != null && stepData.s_t.hasNextState())
      featureState.data[stateIndexes.get(stepData.s_t)] = 0;
    if (stepData.s_tp1 != null && stepData.s_tp1.hasNextState())
      featureState.data[stateIndexes.get(stepData.s_tp1)] = 1;
    return stepData;
  }

  public PVector currentFeatureState() {
    if (graph.currentState() == null)
      return new PVector(size);
    return featureState;
  }

  private RealMatrix createIdentityMatrix(int size) {
    RealMatrix phi = new Array2DRowRealMatrix(size, size);
    for (int i = 0; i < size; i++)
      phi.setEntry(i, i, 1.0);
    return phi;
  }

  public RealMatrix createPhi() {
    RealMatrix result = new Array2DRowRealMatrix(nbStates(), nbNonAbsorbingState());
    for (int i = 0; i < nbStates(); i++)
      result.setRow(i, getFeatureVector(states()[i]).data);
    return result;
  }

  private PVector getFeatureVector(GraphState graphState) {
    PVector result = new PVector(nbNonAbsorbingState());
    int ci = 0;
    for (int i = 0; i < nbStates(); i++) {
      GraphState s = states()[i];
      if (!s.hasNextState())
        continue;
      if (s == graphState)
        result.data[ci] = 1;
      ci++;
    }
    return result;
  }

  public double[] computeSolution(Policy policy, double gamma, double lambda) {
    RealMatrix phi = createPhi();
    RealMatrix p = createTransitionProbablityMatrix(policy);
    ArrayRealVector d = createStateDistribution(p);
    RealMatrix d_pi = createStateDistributionMatrix(d);
    RealMatrix p_lambda = computePLambda(p, gamma, lambda);
    ArrayRealVector r_bar = computeAverageReward(p);

    RealMatrix A = computeA(phi, d_pi, gamma, p_lambda);
    ArrayRealVector b = computeB(phi, d_pi, p, r_bar, gamma, lambda);
    RealMatrix minusAInverse = new LUDecompositionImpl(A).getSolver().getInverse().scalarMultiply(-1);
    return minusAInverse.operate(b).getData();
  }

  private ArrayRealVector computeB(RealMatrix phi, RealMatrix dPi, RealMatrix p, ArrayRealVector rBar, double gamma,
      double lambda) {
    RealMatrix inv = computeIdMinusGammaLambdaP(p, gamma, lambda);
    return (ArrayRealVector) phi.transpose().operate(dPi.operate(inv.operate(rBar)));
  }

  private RealMatrix computeA(RealMatrix phi, RealMatrix dPi, double gamma, RealMatrix pLambda) {
    RealMatrix id = createIdentityMatrix(phi.getRowDimension());
    return phi.transpose().multiply(dPi.multiply(pLambda.scalarMultiply(gamma).subtract(id).multiply(phi)));
  }

  private ArrayRealVector computeAverageReward(RealMatrix p) {
    ArrayRealVector result = new ArrayRealVector(p.getColumnDimension());
    for (int i = 0; i < nbStates(); i++) {
      if (!states()[i].hasNextState())
        continue;
      double sum = 0;
      for (int j = 0; j < nbStates(); j++)
        sum += p.getEntry(i, j) * states()[j].reward;
      result.setEntry(i, sum);
    }
    return result;
  }

  private RealMatrix computePLambda(RealMatrix p, double gamma, double lambda) {
    RealMatrix inv = computeIdMinusGammaLambdaP(p, gamma, lambda);
    return inv.multiply(p).scalarMultiply(1 - lambda);
  }

  private RealMatrix computeIdMinusGammaLambdaP(RealMatrix p, double gamma, double lambda) {
    RealMatrix id = createIdentityMatrix(p.getColumnDimension());
    return new LUDecompositionImpl(id.subtract(p.scalarMultiply(lambda * gamma))).getSolver().getInverse();
  }

  private RealMatrix createStateDistributionMatrix(ArrayRealVector d) {
    RealMatrix d_pi = new Array2DRowRealMatrix(nbStates(), nbStates());
    int ci = 0;
    for (int i = 0; i < nbStates(); i++) {
      GraphState s = states()[i];
      if (!s.hasNextState())
        continue;
      d_pi.setEntry(i, i, d.getEntry(ci));
      ci++;
    }
    return d_pi;
  }

  private ArrayRealVector createStateDistribution(RealMatrix p) {
    RealMatrix p_copy = p.copy();
    p_copy = removeColumnAndRow(p_copy, absorbingStatesSet());
    assert p_copy.getColumnDimension() == p_copy.getRowDimension();
    RealMatrix id = createIdentityMatrix(p_copy.getColumnDimension());
    RealMatrix inv = new LUDecompositionImpl(id.subtract(p_copy)).getSolver().getInverse();
    RealMatrix mu = createInitialStateDistribution();
    RealMatrix visits = mu.multiply(inv);
    double sum = 0;
    for (int i = 0; i < visits.getColumnDimension(); i++)
      sum += visits.getEntry(0, i);
    return (ArrayRealVector) visits.scalarMultiply(1 / sum).getRowVector(0);
  }

  private Set<Integer> absorbingStatesSet() {
    Set<Integer> endStates = new LinkedHashSet<Integer>();
    for (int i = 0; i < nbStates(); i++)
      if (!states()[i].hasNextState())
        endStates.add(i);
    return endStates;
  }

  private int nbNonAbsorbingState() {
    return stateIndexes.size();
  }

  private RealMatrix removeColumnAndRow(RealMatrix m, Set<Integer> absorbingState) {
    RealMatrix result = new Array2DRowRealMatrix(nbNonAbsorbingState(), nbNonAbsorbingState());
    int ci = 0;
    for (int i = 0; i < m.getRowDimension(); i++) {
      if (absorbingState.contains(i))
        continue;
      int cj = 0;
      for (int j = 0; j < m.getColumnDimension(); j++) {
        if (absorbingState.contains(j))
          continue;
        result.setEntry(ci, cj, m.getEntry(i, j));
        cj++;
      }
      ci++;
    }
    return result;
  }

  private RealMatrix createInitialStateDistribution() {
    double[] numbers = new double[nbNonAbsorbingState()];
    int ci = 0;
    for (int i = 0; i < nbStates(); i++) {
      GraphState s = states()[i];
      if (!s.hasNextState())
        continue;
      if (s != graph.initialState())
        numbers[ci] = 0.0;
      else
        numbers[ci] = 1.0;
      ci++;
    }
    RealMatrix result = new Array2DRowRealMatrix(1, numbers.length);
    for (int i = 0; i < numbers.length; i++)
      result.setEntry(0, i, numbers[i]);
    return result;
  }

  private RealMatrix createTransitionProbablityMatrix(Policy policy) {
    RealMatrix p = new Array2DRowRealMatrix(nbStates(), nbStates());
    for (int si = 0; si < nbStates(); si++) {
      GraphState s_t = states()[si];
      policy.update(s_t.v());
      for (Action a : graph.actions()) {
        double pa = policy.pi(a);
        GraphState s_tp1 = s_t.nextState(a);
        if (s_tp1 != null)
          p.setEntry(si, graph.indexOf(s_tp1), pa);
      }
    }
    for (Integer absorbingState : absorbingStatesSet())
      p.setEntry(absorbingState, absorbingState, 1.0);
    return p;
  }

  private int nbStates() {
    return graph.nbStates();
  }

  private GraphState[] states() {
    return graph.states();
  }

  public Map<GraphState, Integer> stateIndexes() {
    return stateIndexes;
  }

  public FiniteStateGraph graph() {
    return graph;
  }

  public PVector featureState(GraphState s) {
    PVector result = new PVector(size);
    if (s != null && s.hasNextState())
      result.data[stateIndexes.get(s)] = 1;
    return result;
  }

  @Override
  public PVector stateAction(RealVector s, Action a) {
    PVector sa = new PVector(nbNonAbsorbingState() * graph.actions().length);
    if (s == null)
      return sa;
    GraphState sg = graph.state(s);
    for (int ai = 0; ai < graph.actions().length; ai++)
      if (graph.actions()[ai] == a) {
        sa.setEntry(ai * nbNonAbsorbingState() + stateIndexes.get(sg), 1);
        return sa;
      }
    return null;
  }

  @Override
  public int vectorSize() {
    return size;
  }

  @Override
  public double vectorNorm() {
    return 1;
  }
}
