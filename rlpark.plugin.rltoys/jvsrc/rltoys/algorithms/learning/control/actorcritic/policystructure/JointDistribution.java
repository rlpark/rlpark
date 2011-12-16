package rltoys.algorithms.learning.control.actorcritic.policystructure;

import java.util.ArrayList;
import java.util.List;

import rltoys.algorithms.representations.acting.PolicyDistribution;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class JointDistribution implements PolicyDistribution {
  private static final long serialVersionUID = -7545331400083047916L;
  private final PolicyDistribution[] distributions;
  @IgnoreMonitor
  private int[] weightsToAction;

  public JointDistribution(PolicyDistribution[] distributions) {
    this.distributions = distributions;
  }

  @Override
  public double pi(RealVector s, Action a) {
    double product = 1.0;
    for (PolicyDistribution distribution : distributions)
      product *= distribution.pi(s, a);
    return product;
  }

  @Override
  public ActionArray decide(RealVector s) {
    List<ActionArray> actions = new ArrayList<ActionArray>();
    int nbDimension = 0;
    for (PolicyDistribution distribution : distributions) {
      ActionArray a = (ActionArray) distribution.decide(s);
      nbDimension += a.actions.length;
      actions.add(a);
    }
    double[] result = new double[nbDimension];
    int currentPosition = 0;
    for (ActionArray a : actions) {
      System.arraycopy(a.actions, 0, result, currentPosition, a.actions.length);
      currentPosition += a.actions.length;
    }
    return new ActionArray(result);
  }

  @Override
  public PVector[] createParameters(int nbFeatures) {
    List<PVector> parameters = new ArrayList<PVector>();
    List<Integer> parametersToAction = new ArrayList<Integer>();
    for (int i = 0; i < distributions.length; i++)
      for (PVector parameterVector : distributions[i].createParameters(nbFeatures)) {
        parameters.add(parameterVector);
        parametersToAction.add(i);
      }
    PVector[] result = new PVector[parameters.size()];
    parameters.toArray(result);
    weightsToAction = new int[parameters.size()];
    for (int i = 0; i < weightsToAction.length; i++)
      weightsToAction[i] = parametersToAction.get(i);
    return result;
  }

  @Override
  public RealVector[] getGradLog(RealVector x_t, Action a_t) {
    List<RealVector> gradLogs = new ArrayList<RealVector>();
    for (PolicyDistribution distribution : distributions)
      for (RealVector parameterVector : distribution.getGradLog(x_t, a_t))
        gradLogs.add(parameterVector);
    RealVector[] result = new RealVector[gradLogs.size()];
    gradLogs.toArray(result);
    return result;
  }

  public int weightsIndexToActionIndex(int i) {
    return weightsToAction[i];
  }

  public PolicyDistribution policy(int actionIndex) {
    return distributions[actionIndex];
  }
}
