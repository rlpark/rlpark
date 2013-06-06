package rlpark.plugin.rltoys.envio.actions;

import java.util.Arrays;

import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.math.vector.implementations.Vectors;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;


public class ActionArray implements Action {
  private static final long serialVersionUID = 6468757011578627902L;
  @Monitor
  final public double[] actions;

  public ActionArray(double... actions) {
    this.actions = actions == null ? null : actions.clone();
    assert actions == null || Vectors.checkValues(new PVector(actions));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (super.equals(obj))
      return true;
    ActionArray other = (ActionArray) obj;
    if (actions == other.actions)
      return true;
    if (actions == null || other.actions == null)
      return false;
    if (actions.length != other.actions.length)
      return false;
    for (int i = 0; i < actions.length; i++)
      if (actions[i] != other.actions[i])
        return false;
    return true;
  }

  static public Action merge(Action... actions) {
    if (actions.length == 1)
      return actions[0];
    int size = 0;
    double[][] actionData = new double[actions.length][];
    for (int i = 0; i < actions.length; i++) {
      ActionArray action = (ActionArray) actions[i];
      actionData[i] = action.actions;
      size += actionData[i].length;
    }
    double[] result = new double[size];
    int index = 0;
    for (double[] data : actionData) {
      System.arraycopy(data, 0, result, index, data.length);
      index += data.length;
    }
    return new ActionArray(result);
  }

  public Action[] decompose() {
    Action[] result = new Action[actions.length];
    for (int i = 0; i < result.length; i++)
      result[i] = new ActionArray(actions[i]);
    return result;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(actions);
  }

  @Override
  public String toString() {
    return Arrays.toString(actions);
  }

  public ActionArray copy() {
    return new ActionArray(actions);
  }

  static public double toDouble(Action a) {
    final ActionArray action = (ActionArray) a;
    assert action.actions.length == 1;
    return action.actions[0];
  }

  static public ActionArray getDim(Action a, int i) {
    return new ActionArray(((ActionArray) a).actions[i]);
  }

  public static boolean checkAction(Action a) {
    return ((ActionArray) a).actions != null;
  }

  public static Action toAction(double a) {
    return new ActionArray(a);
  }
}
