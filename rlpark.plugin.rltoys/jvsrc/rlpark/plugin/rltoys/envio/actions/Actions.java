package rlpark.plugin.rltoys.envio.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rlpark.plugin.rltoys.math.ranges.Range;

public class Actions {
  static private double[][] createActionsAsArrays(Range... actionValues) {
    if (actionValues.length == 0) {
      double[][] result = new double[1][];
      result[0] = new double[0];
      return result;
    }
    Range[] childValues = Arrays.copyOf(actionValues, actionValues.length - 1);
    double[][] childActions = createActionsAsArrays(childValues);
    double[][] result = new double[3 * childActions.length][];
    int actionIndex = 0;
    for (double[] childAction : childActions) {
      double[][] newActions = createNewActions(childAction, actionValues[actionValues.length - 1]);
      for (double[] newAction : newActions) {
        result[actionIndex] = newAction;
        actionIndex++;
      }
    }
    return result;
  }

  private static double[][] createNewActions(double[] childAction, Range actionValue) {
    double[] action = new double[childAction.length + 1];
    for (int i = 0; i < childAction.length; i++)
      action[i] = childAction[i];
    double[][] result = { action.clone(), action.clone(), action.clone() };
    result[0][childAction.length] = actionValue.min();
    result[1][childAction.length] = (actionValue.max() + actionValue.min()) / 2.0;
    result[2][childAction.length] = actionValue.max();
    return result;
  }

  static public Action[] createActions(Range... actionValues) {
    List<ActionArray> result = new ArrayList<ActionArray>();
    double[][] actions = createActionsAsArrays(actionValues);
    for (double[] action : actions)
      result.add(new ActionArray(action));
    Action[] resultArray = new Action[result.size()];
    result.toArray(resultArray);
    return resultArray;
  }

  static public Action[] createActions(double... actionValues) {
    Range[] ranges = new Range[actionValues.length];
    for (int i = 0; i < ranges.length; i++)
      ranges[i] = new Range(-actionValues[i], actionValues[i]);
    return createActions(ranges);
  }

  static public boolean isOneDimension(Action action) {
    return ((ActionArray) action).actions.length == 1;
  }

  static public Map<Action, Integer> createActionIntMap(Action[] actions) {
    Map<Action, Integer> actionToIndex = new LinkedHashMap<Action, Integer>();
    for (int i = 0; i < actions.length; i++)
      actionToIndex.put(actions[i], i);
    return actionToIndex;
  }
}
