package rlpark.plugin.rltoysview.maze;

import rlpark.plugin.rltoys.envio.actions.Action;


public class PolicyData {
  final public int resolutionX;
  final public int resolutionY;
  private final double[][][] policyData;
  private final Action[] actions;

  public PolicyData(int resolutionX, int resolutionY, Action[] actions) {
    this.resolutionX = resolutionX;
    this.resolutionY = resolutionY;
    this.actions = actions;
    this.policyData = createPolicyData();
  }

  private double[][][] createPolicyData() {
    double[][][] policyData = new double[resolutionX][][];
    for (int i = 0; i < policyData.length; i++) {
      policyData[i] = new double[resolutionY][];
      for (int j = 0; j < policyData[i].length; j++) {
        policyData[i][j] = new double[actions.length];
      }
    }
    return policyData;
  }

  public double[] probabilities(int i, int j) {
    return policyData[i][j];
  }

  public Action[] actions() {
    return actions;
  }

  public void set(int i, int j, double[] probabilities) {
    System.arraycopy(probabilities, 0, policyData[i][j], 0, actions.length);
  }
}
